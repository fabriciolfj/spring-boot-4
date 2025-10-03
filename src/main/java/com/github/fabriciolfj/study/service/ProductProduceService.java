package com.github.fabriciolfj.study.service;

import com.github.fabriciolfj.study.dto.ProductDTO;
import com.study.details.Detalhes;
import com.study.produto.Produto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.util.Optional;

@Slf4j
@Service
public class ProductProduceService {

    @Value("${topic.product}")
    private String topic;
    @Value("${topic.details}")
    private String topicDetails;
    private final KafkaTemplate<String, Produto> kafkaTemplate;
    private final KafkaTemplate<String, Detalhes> kafkaTemplateDetails;
    @Qualifier("productCacheManager")
    private final CacheManager cacheManager;

    public ProductProduceService(KafkaTemplate<String, Produto> kafkaTemplate,
                                 KafkaTemplate<String, Detalhes> kafkaTemplateDetails,
                                 @Qualifier("productCacheManager")
                                 CacheManager cacheManager) {
        this.kafkaTemplate = kafkaTemplate;
        this.kafkaTemplateDetails = kafkaTemplateDetails;
        this.cacheManager = cacheManager;
    }

    public void send(final ProductDTO dto) {
        var avro = Produto.newBuilder()
                .setId(dto.getId())
                .setNome(dto.getName())
                .setCusto(dto.getCost())
                .build();

        kafkaTemplate.send(topic, dto.getId().toString(), avro).whenComplete((value, ex) -> {
            if (ex == null) {
                log.info("mensagem enviada com sucesso para topic {}, produto {}", topic, dto.getId());
            }
        });


        var avroDetails = Detalhes.newBuilder()
                .setId(dto.getId())
                .setDescricao(dto.getDescription())
                .build();

        kafkaTemplateDetails.send(topicDetails, dto.getId().toString(), avroDetails)
                .whenComplete((value, ex) -> {
            if (ex == null) {
                log.info("mensagem enviada com sucesso para topic {}, produto {}", topicDetails, dto.getId());
            }
        });

        var cache = cacheManager.getCache("product");
        if (cache != null) {
            cache.put(dto.getId(), dto);
        }
    }

    public Optional<ProductDTO> getProduct(final Long id) {
        var cache = cacheManager.getCache("product");
        if (cache != null) {
            Cache.ValueWrapper wrapper = cache.get(id);
            if (wrapper != null) {
                //val product = objectMapper.readValue(wrapper.get().toString(), ProductDTO.class);
                return Optional.ofNullable((ProductDTO) wrapper.get());
            }
        }
        return Optional.empty();
    }

    public void evictProduct(final Long id) {
        Cache cache = cacheManager.getCache("products");
        if (cache != null) {
            cache.evict(id);
        }
    }
}
