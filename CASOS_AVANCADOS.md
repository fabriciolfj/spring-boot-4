# Spring Batch - Casos de Uso Avançados

Este documento complementa o README principal com exemplos práticos de cenários mais complexos.

## 📚 Índice

1. [Multiple Steps em Sequência](#1-multiple-steps-em-sequência)
2. [Conditional Flow](#2-conditional-flow)
3. [Parallel Processing](#3-parallel-processing)
4. [Partitioning](#4-partitioning)
5. [Multiple Data Sources](#5-multiple-data-sources)
6. [Retry e Skip Strategies](#6-retry-e-skip-strategies)
7. [Scheduling com Cron](#7-scheduling-com-cron)
8. [Notificações e Alertas](#8-notificações-e-alertas)

---

## 1. Multiple Steps em Sequência

Execute múltiplos passos em ordem, onde cada step depende do anterior.

```java
@Bean
public Job multiStepJob(JobRepository jobRepository) {
    return new JobBuilder("multiStepJob", jobRepository)
            .start(importarDadosStep())
            .next(validarDadosStep())
            .next(processarDadosStep())
            .next(gerarRelatorioStep())
            .build();
}

@Bean
public Step validarDadosStep(JobRepository jobRepository, 
                             PlatformTransactionManager transactionManager) {
    return new StepBuilder("validarDadosStep", jobRepository)
            .tasklet((contribution, chunkContext) -> {
                log.info("Validando dados importados...");
                
                // Buscar dados no banco
                long totalTransacoes = transacaoRepository.count();
                
                // Validação
                if (totalTransacoes == 0) {
                    throw new IllegalStateException("Nenhuma transação encontrada!");
                }
                
                log.info("Validação concluída: {} transações", totalTransacoes);
                return RepeatStatus.FINISHED;
            }, transactionManager)
            .build();
}

@Bean
public Step gerarRelatorioStep(JobRepository jobRepository,
                               PlatformTransactionManager transactionManager) {
    return new StepBuilder("gerarRelatorioStep", jobRepository)
            .tasklet((contribution, chunkContext) -> {
                log.info("Gerando relatório...");
                
                // Agregar dados
                Map<String, BigDecimal> totalPorTipo = transacaoRepository
                        .findAll()
                        .stream()
                        .collect(Collectors.groupingBy(
                                t -> t.getTipo().name(),
                                Collectors.reducing(BigDecimal.ZERO, 
                                        Transacao::getValor, 
                                        BigDecimal::add)
                        ));
                
                // Salvar em arquivo
                String relatorio = "Relatório de Processamento\n" +
                        "===========================\n" +
                        totalPorTipo.entrySet().stream()
                                .map(e -> e.getKey() + ": R$ " + e.getValue())
                                .collect(Collectors.joining("\n"));
                
                Files.writeString(Path.of("/tmp/relatorio.txt"), relatorio);
                
                return RepeatStatus.FINISHED;
            }, transactionManager)
            .build();
}
```

**Quando usar:** Processos ETL complexos, pipelines de dados, workflows sequenciais.

---

## 2. Conditional Flow

Execute steps diferentes baseado no resultado de steps anteriores.

```java
@Bean
public Job conditionalJob(JobRepository jobRepository) {
    return new JobBuilder("conditionalJob", jobRepository)
            .start(validarArquivoStep())
            .on("VALIDO").to(processarStep())
            .from(validarArquivoStep())
            .on("INVALIDO").to(notificarErroStep())
            .from(processarStep())
            .on("COMPLETED").to(gerarRelatorioStep())
            .from(processarStep())
            .on("FAILED").to(reverterProcessamentoStep())
            .end()
            .build();
}

@Bean
public Step validarArquivoStep(JobRepository jobRepository,
                               PlatformTransactionManager transactionManager) {
    return new StepBuilder("validarArquivoStep", jobRepository)
            .tasklet((contribution, chunkContext) -> {
                String arquivo = "/data/transacoes.csv";
                
                if (!Files.exists(Path.of(arquivo))) {
                    contribution.setExitStatus(new ExitStatus("INVALIDO"));
                    return RepeatStatus.FINISHED;
                }
                
                long linhas = Files.lines(Path.of(arquivo)).count();
                if (linhas < 2) { // Header + pelo menos 1 linha
                    contribution.setExitStatus(new ExitStatus("INVALIDO"));
                    return RepeatStatus.FINISHED;
                }
                
                contribution.setExitStatus(new ExitStatus("VALIDO"));
                return RepeatStatus.FINISHED;
            }, transactionManager)
            .build();
}
```

**Quando usar:** Workflows com validações, processamento condicional, tratamento de erros diferenciado.

---

## 3. Parallel Processing

Execute múltiplos steps em paralelo para melhorar performance.

```java
@Bean
public Job parallelJob(JobRepository jobRepository) {
    Flow flow1 = new FlowBuilder<Flow>("flow1")
            .start(processarTransacoesCredito())
            .build();
    
    Flow flow2 = new FlowBuilder<Flow>("flow2")
            .start(processarTransacoesDebito())
            .build();
    
    Flow parallelFlow = new FlowBuilder<Flow>("parallelFlow")
            .split(new SimpleAsyncTaskExecutor())
            .add(flow1, flow2)
            .build();
    
    return new JobBuilder("parallelJob", jobRepository)
            .start(parallelFlow)
            .end()
            .build();
}

@Bean
public TaskExecutor taskExecutor() {
    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    executor.setCorePoolSize(4);
    executor.setMaxPoolSize(8);
    executor.setQueueCapacity(100);
    executor.setThreadNamePrefix("batch-");
    executor.initialize();
    return executor;
}
```

**Quando usar:** Processar diferentes tipos de dados independentemente, maximizar throughput.

---

## 4. Partitioning

Divida grandes volumes em partições processadas em paralelo.

```java
@Bean
public Step masterStep(JobRepository jobRepository,
                       Partitioner partitioner,
                       Step slaveStep,
                       TaskExecutor taskExecutor) {
    return new StepBuilder("masterStep", jobRepository)
            .partitioner("slaveStep", partitioner)
            .step(slaveStep)
            .gridSize(10)  // 10 partições
            .taskExecutor(taskExecutor)
            .build();
}

@Component
class DataPartitioner implements Partitioner {
    
    @Autowired
    private TransacaoRepository repository;
    
    @Override
    public Map<String, ExecutionContext> partition(int gridSize) {
        long totalRegistros = repository.count();
        long tamanhoPagina = (totalRegistros / gridSize) + 1;
        
        Map<String, ExecutionContext> result = new HashMap<>();
        
        for (int i = 0; i < gridSize; i++) {
            ExecutionContext context = new ExecutionContext();
            context.putLong("startIndex", i * tamanhoPagina);
            context.putLong("endIndex", (i + 1) * tamanhoPagina);
            result.put("partition" + i, context);
        }
        
        return result;
    }
}

@Bean
@StepScope
public JpaPagingItemReader<Transacao> partitionedReader(
        @Value("#{stepExecutionContext['startIndex']}") Long startIndex,
        @Value("#{stepExecutionContext['endIndex']}") Long endIndex,
        EntityManagerFactory entityManagerFactory) {
    
    JpaPagingItemReader<Transacao> reader = new JpaPagingItemReader<>();
    reader.setEntityManagerFactory(entityManagerFactory);
    reader.setQueryString(
            "SELECT t FROM Transacao t WHERE t.id BETWEEN :start AND :end");
    
    Map<String, Object> params = new HashMap<>();
    params.put("start", startIndex);
    params.put("end", endIndex);
    reader.setParameterValues(params);
    
    return reader;
}
```

**Quando usar:** Volumes muito grandes (milhões de registros), necessidade de maximizar paralelismo.

---

## 5. Multiple Data Sources

Leia de múltiplas fontes e combine os dados.

```java
@Bean
public Step multiSourceStep(JobRepository jobRepository,
                            PlatformTransactionManager transactionManager) {
    return new StepBuilder("multiSourceStep", jobRepository)
            .<Transacao, Transacao>chunk(100, transactionManager)
            .reader(compositeReader())
            .processor(processor)
            .writer(writer)
            .build();
}

@Bean
public CompositeItemReader<Transacao> compositeReader() {
    CompositeItemReader<Transacao> reader = new CompositeItemReader<>();
    reader.setDelegates(Arrays.asList(
            csvReader(),
            databaseReader(),
            apiReader()
    ));
    return reader;
}

@Bean
public RestItemReader<Transacao> apiReader() {
    return new RestItemReaderBuilder<Transacao>()
            .name("apiReader")
            .baseUrl("https://api.example.com")
            .jsonObjectMapper(new ObjectMapper())
            .targetType(Transacao.class)
            .build();
}
```

**Quando usar:** Migração de dados, consolidação de múltiplas fontes, integração de sistemas.

---

## 6. Retry e Skip Strategies

Configure estratégias sofisticadas de tratamento de erros.

```java
@Bean
public Step resilientStep(JobRepository jobRepository,
                         PlatformTransactionManager transactionManager) {
    return new StepBuilder("resilientStep", jobRepository)
            .<TransacaoCSV, Transacao>chunk(100, transactionManager)
            .reader(reader)
            .processor(processor)
            .writer(writer)
            .faultTolerant()
            // Configuração de Skip
            .skip(ValidationException.class)
            .skip(DataIntegrityViolationException.class)
            .skipLimit(100)
            .skipPolicy(customSkipPolicy())
            // Configuração de Retry
            .retry(TransientException.class)
            .retry(DeadlockLoserDataAccessException.class)
            .retryLimit(3)
            .retryPolicy(customRetryPolicy())
            // Listeners
            .listener(customSkipListener())
            .listener(customRetryListener())
            .build();
}

@Component
class CustomSkipPolicy implements SkipPolicy {
    
    @Override
    public boolean shouldSkip(Throwable t, long skipCount) {
        if (t instanceof ValidationException) {
            log.warn("Validação falhou, pulando item #{}", skipCount);
            return true;
        }
        if (t instanceof DuplicateKeyException) {
            log.warn("Registro duplicado, pulando item #{}", skipCount);
            return true;
        }
        return false;
    }
}

@Component
class CustomRetryPolicy implements RetryPolicy {
    
    private static final int MAX_ATTEMPTS = 3;
    
    @Override
    public boolean canRetry(RetryContext context) {
        return context.getRetryCount() < MAX_ATTEMPTS;
    }
    
    @Override
    public void registerThrowable(RetryContext context, Throwable throwable) {
        if (throwable instanceof DeadlockLoserDataAccessException) {
            log.warn("Deadlock detectado, tentativa {}", 
                    context.getRetryCount() + 1);
        }
    }
}

@Component
class CustomSkipListener implements SkipListener<TransacaoCSV, Transacao> {
    
    @Override
    public void onSkipInRead(Throwable t) {
        log.error("Erro ao ler item: {}", t.getMessage());
    }
    
    @Override
    public void onSkipInProcess(TransacaoCSV item, Throwable t) {
        log.error("Erro ao processar item {}: {}", item.getId(), t.getMessage());
        // Salvar em tabela de erros
        saveToErrorTable(item, t);
    }
    
    @Override
    public void onSkipInWrite(Transacao item, Throwable t) {
        log.error("Erro ao escrever item {}: {}", item.getId(), t.getMessage());
    }
}
```

**Quando usar:** Processos críticos que precisam continuar mesmo com erros, integração com sistemas instáveis.

---

## 7. Scheduling com Cron

Agende execuções automáticas do batch.

```java
@Configuration
@EnableScheduling
public class BatchSchedulerConfig {
    
    @Autowired
    private JobLauncher jobLauncher;
    
    @Autowired
    private Job processarTransacoesJob;
    
    // Executa todo dia às 2h da manhã
    @Scheduled(cron = "0 0 2 * * *")
    public void executarProcessamentoNoturno() {
        try {
            JobParameters params = new JobParametersBuilder()
                    .addLocalDateTime("timestamp", LocalDateTime.now())
                    .addString("tipo", "AGENDADO")
                    .toJobParameters();
            
            JobExecution execution = jobLauncher.run(processarTransacoesJob, params);
            
            log.info("Job agendado executado. Status: {}", execution.getStatus());
        } catch (Exception e) {
            log.error("Erro ao executar job agendado", e);
            enviarAlerta("Falha no processamento noturno: " + e.getMessage());
        }
    }
    
    // Executa a cada 4 horas
    @Scheduled(fixedRate = 4, timeUnit = TimeUnit.HOURS)
    public void processamentoPeriodico() {
        // Lógica similar
    }
    
    // Executa 30 minutos após a aplicação iniciar, e depois a cada 2 horas
    @Scheduled(initialDelay = 30, fixedRate = 120, timeUnit = TimeUnit.MINUTES)
    public void processamentoRecorrente() {
        // Lógica similar
    }
}
```

**Quando usar:** Processamentos noturnos, relatórios periódicos, sincronizações regulares.

---

## 8. Notificações e Alertas

Implemente notificações por email, Slack, etc.

```java
@Component
@RequiredArgsConstructor
class NotificationJobListener implements JobExecutionListener {
    
    private final EmailService emailService;
    private final SlackService slackService;
    
    @Override
    public void afterJob(JobExecution jobExecution) {
        String jobName = jobExecution.getJobInstance().getJobName();
        BatchStatus status = jobExecution.getStatus();
        
        if (status == BatchStatus.COMPLETED) {
            enviarNotificacaoSucesso(jobExecution);
        } else if (status == BatchStatus.FAILED) {
            enviarNotificacaoFalha(jobExecution);
        }
    }
    
    private void enviarNotificacaoSucesso(JobExecution execution) {
        long totalProcessado = execution.getStepExecutions().stream()
                .mapToLong(StepExecution::getWriteCount)
                .sum();
        
        String mensagem = String.format(
                "✅ Job %s concluído com sucesso!\n" +
                "Total processado: %d registros\n" +
                "Tempo: %s",
                execution.getJobInstance().getJobName(),
                totalProcessado,
                Duration.between(execution.getStartTime(), execution.getEndTime())
        );
        
        slackService.enviarMensagem("#batch-jobs", mensagem);
    }
    
    private void enviarNotificacaoFalha(JobExecution execution) {
        String erros = execution.getAllFailureExceptions().stream()
                .map(Throwable::getMessage)
                .collect(Collectors.joining("\n"));
        
        String mensagem = String.format(
                "❌ ALERTA: Job %s FALHOU!\n" +
                "Erros:\n%s",
                execution.getJobInstance().getJobName(),
                erros
        );
        
        // Email para administradores
        emailService.enviar(
                "admin@empresa.com",
                "Falha no Job Batch",
                mensagem
        );
        
        // Alerta no Slack
        slackService.enviarMensagem("#alerts", mensagem);
    }
}

@Service
class SlackService {
    
    public void enviarMensagem(String canal, String mensagem) {
        // Implementação usando Slack Webhook
        RestTemplate restTemplate = new RestTemplate();
        
        Map<String, Object> payload = Map.of(
                "channel", canal,
                "text", mensagem
        );
        
        restTemplate.postForEntity(
                "https://hooks.slack.com/services/YOUR/WEBHOOK/URL",
                payload,
                String.class
        );
    }
}
```

**Quando usar:** Monitoramento proativo, alertas críticos, relatórios de execução.

---

## 💡 Dicas de Performance

### 1. Otimize o Chunk Size
```java
// Testar diferentes tamanhos
@Value("${batch.chunk-size:100}")
private int chunkSize;

// Regra geral: 100-1000 para dados pequenos, 10-50 para objetos grandes
```

### 2. Use Batch Inserts do JPA
```java
spring:
  jpa:
    properties:
      hibernate:
        jdbc:
          batch_size: 100
        order_inserts: true
        order_updates: true
```

### 3. Configure Connection Pool
```java
spring:
  datasource:
    hikari:
      maximum-pool-size: 10
      minimum-idle: 5
      connection-timeout: 30000
```

### 4. Use Cursor para Grandes Volumes
```java
@Bean
public JdbcCursorItemReader<Transacao> cursorReader() {
    return new JdbcCursorItemReaderBuilder<Transacao>()
            .name("cursorReader")
            .dataSource(dataSource)
            .sql("SELECT * FROM transacoes WHERE processado = false")
            .rowMapper(new BeanPropertyRowMapper<>(Transacao.class))
            .fetchSize(1000)  // Fetch em lotes
            .build();
}
```

---

## 📊 Monitoramento e Métricas

### Integração com Actuator
```yaml
management:
  endpoints:
    web:
      exposure:
        include: health,metrics,prometheus
  metrics:
    export:
      prometheus:
        enabled: true
```

### Métricas Customizadas
```java
@Component
@RequiredArgsConstructor
class BatchMetricsListener implements StepExecutionListener {
    
    private final MeterRegistry meterRegistry;
    
    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        meterRegistry.counter("batch.items.read",
                "step", stepExecution.getStepName())
                .increment(stepExecution.getReadCount());
        
        meterRegistry.counter("batch.items.written",
                "step", stepExecution.getStepName())
                .increment(stepExecution.getWriteCount());
        
        meterRegistry.timer("batch.step.duration",
                "step", stepExecution.getStepName())
                .record(Duration.between(
                        stepExecution.getStartTime(),
                        stepExecution.getEndTime()
                ));
        
        return stepExecution.getExitStatus();
    }
}
```

---

Estes são os principais casos de uso avançados do Spring Batch. Combine-os conforme necessário para criar pipelines robustos e escaláveis!
