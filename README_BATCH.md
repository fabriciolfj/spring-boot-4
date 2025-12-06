# Spring Batch - Projeto Demonstrativo

Projeto completo demonstrando os conceitos e componentes do Spring Batch através de um caso prático de processamento de transações financeiras.

## 📋 Índice

1. [O que é Spring Batch](#o-que-é-spring-batch)
2. [Componentes Principais](#componentes-principais)
3. [Arquitetura do Projeto](#arquitetura-do-projeto)
4. [Como Executar](#como-executar)
5. [Testando o Projeto](#testando-o-projeto)
6. [Entendendo o Fluxo](#entendendo-o-fluxo)

## 🎯 O que é Spring Batch

Spring Batch é um framework para processamento em lote (batch) de grandes volumes de dados. Ele fornece:

- **Chunk-oriented processing**: Processa dados em blocos (chunks)
- **Transaction management**: Gerencia transações automaticamente
- **Job repository**: Mantém histórico de execuções
- **Restart/Retry**: Permite reiniciar jobs e retentar operações falhas
- **Skip logic**: Pula registros com erro sem parar o processamento
- **Parallel processing**: Suporta processamento paralelo

## 🔧 Componentes Principais

### 1. Job
```
Job = Orquestra todo o processo batch
  └── Step 1
  └── Step 2
  └── Step N
```

**Responsabilidades:**
- Define a sequência de steps
- Configura listeners globais
- Define políticas de restart
- Gerencia parâmetros de execução

### 2. Step
```
Step = Uma fase do processamento
  └── Chunk (tamanho configurável)
      ├── Read (ItemReader)
      ├── Process (ItemProcessor)
      └── Write (ItemWriter)
```

**Responsabilidades:**
- Define o tamanho do chunk
- Configura reader, processor, writer
- Define políticas de erro (skip, retry)
- Gerencia transações

### 3. ItemReader
```java
interface ItemReader<T> {
    T read() throws Exception;
}
```

**Implementações comuns:**
- `FlatFileItemReader`: Lê arquivos CSV, TXT
- `JdbcCursorItemReader`: Lê de banco usando cursor
- `JpaPagingItemReader`: Lê com paginação JPA
- `KafkaItemReader`: Lê de tópicos Kafka

**No projeto:**
- Lê arquivo CSV com transações
- Mapeia cada linha para objeto TransacaoCSV
- Pula primeira linha (cabeçalho)

### 4. ItemProcessor
```java
interface ItemProcessor<I,O> {
    O process(I item) throws Exception;
}
```

**Responsabilidades:**
- Transformar dados (I → O)
- Validar dados
- Enriquecer informações
- Filtrar (retorna null)

**No projeto:**
- Valida duplicatas
- Valida valores máximos por tipo
- Calcula taxas
- Converte tipos de dados
- Filtra registros inválidos

### 5. ItemWriter
```java
interface ItemWriter<T> {
    void write(Chunk<? extends T> items);
}
```

**Implementações comuns:**
- `JpaItemWriter`: Grava usando JPA
- `JdbcBatchItemWriter`: Grava com JDBC batch
- `FlatFileItemWriter`: Grava em arquivo
- `KafkaItemWriter`: Envia para Kafka

**No projeto:**
- Grava transações no banco H2
- Usa JPA para batch inserts
- Adiciona logging customizado

### 6. JobOperator (Spring Batch 5.x)

```java
interface JobOperator {
    Long start(String jobName, String parameters);
    boolean stop(Long executionId);
    Long restart(Long executionId);
    Set<Long> getRunningExecutions(String jobName);
}
```

**Vantagens sobre JobLauncher:**
- API de alto nível para gerenciamento de jobs
- Suporta operações de controle (start, stop, restart)
- Integração com JobRegistry para descoberta automática
- Melhor para aplicações de produção e APIs REST

**No projeto:**
- Usado no `BatchController` para iniciar jobs via REST API
- Permite parar execuções em andamento
- Facilita restart de jobs que falharam
- Fornece listagem de execuções

### 7. JobRepository

Armazena metadados sobre execuções:
- `BATCH_JOB_INSTANCE`: Instâncias únicas do job
- `BATCH_JOB_EXECUTION`: Cada execução do job
- `BATCH_STEP_EXECUTION`: Cada execução de step
- `BATCH_JOB_EXECUTION_PARAMS`: Parâmetros usados

### 8. Chunk Processing

```
┌─────────────────────────────────────────┐
│  Chunk Size = 100                       │
├─────────────────────────────────────────┤
│  1. Read 100 items                      │
│  2. Process each item                   │
│  3. Write all 100 items                 │
│  4. Commit transaction                  │
└─────────────────────────────────────────┘
```

**Vantagens:**
- Processamento eficiente de grandes volumes
- Commits periódicos (não sobrecarrega memória)
- Rollback granular em caso de erro

## 🏗️ Arquitetura do Projeto

```
src/main/java/com/exemplo/batch/
├── SpringBatchApplication.java          # Classe principal
├── config/
│   └── BatchJobConfig.java              # Configuração do Job e Step
├── controller/
│   └── BatchController.java             # API REST para executar job
├── model/
│   ├── TransacaoCSV.java                # DTO de entrada (CSV)
│   └── Transacao.java                   # Entidade JPA (saída)
├── repository/
│   └── TransacaoRepository.java         # Repository JPA
├── reader/
│   └── TransacaoItemReaderConfig.java   # Configuração do Reader
├── processor/
│   └── TransacaoItemProcessor.java      # Lógica de processamento
├── writer/
│   └── TransacaoItemWriterConfig.java   # Configuração do Writer
└── listener/
    ├── JobCompletionNotificationListener.java
    └── StepNotificationListener.java

src/main/resources/
├── application.yml                      # Configurações
└── data/
    └── transacoes.csv                   # Arquivo de entrada
```

## 🚀 Como Executar

### Pré-requisitos
- Java 21+
- Maven 3.6+

### Passo 1: Compilar o projeto
```bash
cd spring-batch-demo
mvn clean package
```

### Passo 2: Executar a aplicação
```bash
mvn spring-boot:run
```

### Passo 3: Executar o job
```bash
curl -X POST http://localhost:8080/api/batch/processar
```

Ou acesse o H2 Console para verificar os dados:
- URL: http://localhost:8080/h2-console
- JDBC URL: jdbc:h2:mem:batchdb
- User: sa
- Password: (deixe em branco)

## 🧪 Testando o Projeto

### Teste 1: Execução Normal
```bash
# Executar o job
curl -X POST http://localhost:8080/api/batch/processar

# Resposta esperada:
# {
#   "executionId": 1,
#   "jobName": "processarTransacoesJob",
#   "status": "STARTED",
#   "startTime": "2024-12-05T10:00:00",
#   "parameters": "executadoEm=1733400000000,requisitante=API-REST"
# }

# Verificar status da execução (substitua {executionId} pelo ID retornado)
curl http://localhost:8080/api/batch/status/1

# Listar execuções recentes
curl http://localhost:8080/api/batch/executions?limit=10

# Parar uma execução (se necessário)
curl -X POST http://localhost:8080/api/batch/stop/1

# Reiniciar uma execução falha
curl -X POST http://localhost:8080/api/batch/restart/1
```

### Teste 2: Verificar Dados no Banco
```sql
-- Conecte ao H2 Console
SELECT * FROM transacoes;

-- Verificar taxas aplicadas
SELECT tipo, AVG(taxa_aplicada) as taxa_media 
FROM transacoes 
GROUP BY tipo;

-- Verificar total por tipo
SELECT tipo, COUNT(*), SUM(valor) as total
FROM transacoes 
GROUP BY tipo;
```

### Teste 3: Verificar Metadados do Batch
```sql
-- Histórico de execuções
SELECT * FROM BATCH_JOB_EXECUTION;

-- Detalhes dos steps
SELECT * FROM BATCH_STEP_EXECUTION;

-- Parâmetros usados
SELECT * FROM BATCH_JOB_EXECUTION_PARAMS;
```

## 📊 Entendendo o Fluxo

### Fluxo Completo de Execução

```
1. API recebe POST /api/batch/processar
   ↓
2. JobLauncher.run(job, parameters)
   ↓
3. JobListener.beforeJob()
   ↓
4. Step é executado:
   ├── StepListener.beforeStep()
   ├── Loop de Chunks:
   │   ├── Read 100 items (FlatFileItemReader)
   │   ├── Process each item (TransacaoItemProcessor)
   │   │   ├── Valida duplicata
   │   │   ├── Valida valor
   │   │   ├── Calcula taxa
   │   │   └── Retorna Transacao (ou null para filtrar)
   │   ├── Write chunk (JpaItemWriter)
   │   └── Commit transaction
   └── StepListener.afterStep()
   ↓
5. JobListener.afterJob()
   ↓
6. Retorna JobExecution com estatísticas
```

### Processamento de um Item

```java
// 1. LEITURA (ItemReader)
TransacaoCSV csv = reader.read();
// csv = {id="TRX-001", tipo="PIX", valor="R$ 150,00", ...}

// 2. PROCESSAMENTO (ItemProcessor)
Transacao transacao = processor.process(csv);
// - Parse valor: "R$ 150,00" → BigDecimal(150.00)
// - Calcula taxa PIX: 0% → BigDecimal(0.00)
// - Converte data: "2024-12-01 10:30:00" → LocalDateTime
// - Retorna: Transacao{valor=150.00, taxa=0.00, liquido=150.00}

// 3. ESCRITA (ItemWriter após acumular 100 itens)
writer.write(chunk); // Grava 100 transações de uma vez
```

### Tratamento de Erros

```
Item com erro durante processamento:
  ↓
Tentativa 1: FALHOU
  ↓
Tentativa 2: FALHOU (retry)
  ↓
Tentativa 3: FALHOU (retry)
  ↓
Skip item (se skip count < maxSkipCount)
  ↓
Continua processamento
```

## 📝 Configurações Importantes

### application.yml

```yaml
batch:
  chunk-size: 100              # Itens por chunk
  max-skip-count: 10           # Máximo de erros permitidos
  input-file: classpath:data/transacoes.csv

spring:
  batch:
    job:
      enabled: false           # Não executa automaticamente
    jdbc:
      initialize-schema: always  # Cria tabelas do batch
```

### Chunk Size

**Muito pequeno (10):**
- ✅ Commits frequentes (menos dados perdidos em falha)
- ❌ Overhead de transações
- ❌ Performance reduzida

**Muito grande (10000):**
- ✅ Menos overhead
- ✅ Melhor performance
- ❌ Mais dados perdidos em falha
- ❌ Maior uso de memória

**Ideal (100-1000):**
- Balanceia performance e segurança
- Depende do tamanho dos objetos
- Testar com dados reais

## 🎓 Conceitos Avançados

### Parallel Processing
```java
@Bean
public Step parallelStep() {
    return stepBuilderFactory.get("step")
        .partitioner("slaveStep", partitioner())
        .taskExecutor(taskExecutor())
        .build();
}
```

### Conditional Flow
```java
@Bean
public Job conditionalJob() {
    return jobBuilderFactory.get("job")
        .start(step1)
        .on("COMPLETED").to(step2)
        .from(step1).on("FAILED").to(step3)
        .end()
        .build();
}
```

### Multiple Data Sources
```java
@Bean
public Step multiSourceStep() {
    return stepBuilderFactory.get("step")
        .chunk(100)
        .reader(compositeItemReader())  // Lê de múltiplas fontes
        .processor(processor)
        .writer(writer)
        .build();
}
```

## 📚 Próximos Passos

1. **Adicionar scheduling**: Use `@Scheduled` para executar automaticamente
2. **Implementar partitioning**: Processe em paralelo
3. **Adicionar notificações**: Email/Slack ao terminar job
4. **Métricas**: Integrar com Micrometer/Prometheus
5. **Testes**: Adicionar testes unitários e de integração

## 🔗 Referências

- [Spring Batch Docs](https://spring.io/projects/spring-batch)
- [Spring Batch Reference](https://docs.spring.io/spring-batch/docs/current/reference/html/)
- [Baeldung Spring Batch](https://www.baeldung.com/spring-batch)

## 🤝 Dúvidas Comuns

**P: Quando usar Spring Batch vs processamento síncrono?**
R: Use batch para volumes grandes (>1000 registros), processamento agendado, ou quando não precisa de resposta imediata.

**P: Como reiniciar um job que falhou?**
R: Spring Batch mantém o estado. Basta executar novamente com os mesmos parâmetros.

**P: Posso processar em paralelo?**
R: Sim! Use partitioning ou multi-threaded steps.

**P: Como monitorar jobs em produção?**
R: Use Spring Batch Admin, métricas do Actuator, ou integre com ferramentas de monitoring.
