package com.github.fabriciolfj.study.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Objects;

public class SalesEvent {

    @JsonProperty("event_id")
    private String eventId;

    @JsonProperty("user_id")
    private String userId;

    @JsonProperty("product_id")
    private String productId;

    @JsonProperty("amount")
    private Double amount;

    @JsonProperty("quantity")
    private Integer quantity;

    @JsonProperty("event_time")
    private LocalDateTime eventTime;

    @JsonProperty("store_id")
    private String storeId;

    @JsonProperty("category")
    private String category;

    // Construtor padrão
    public SalesEvent() {}

    // Construtor completo
    public SalesEvent(String eventId, String userId, String productId,
                      Double amount, Integer quantity, LocalDateTime eventTime,
                      String storeId, String category) {
        this.eventId = eventId;
        this.userId = userId;
        this.productId = productId;
        this.amount = amount;
        this.quantity = quantity;
        this.eventTime = eventTime;
        this.storeId = storeId;
        this.category = category;
    }

    // Construtor de conveniência
    public SalesEvent(String userId, String productId, Double amount, LocalDateTime eventTime) {
        this.userId = userId;
        this.productId = productId;
        this.amount = amount;
        this.eventTime = eventTime;
    }

    // Método para obter timestamp em milissegundos (usado pelo TimestampExtractor)
    public long getEventTime() {
        return eventTime != null ? eventTime.toInstant(ZoneOffset.UTC).toEpochMilli() : 0L;
    }

    // Método para obter LocalDateTime
    public LocalDateTime getEventDateTime() {
        return eventTime;
    }

    // Getters e Setters
    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public void setEventTime(LocalDateTime eventTime) {
        this.eventTime = eventTime;
    }

    public String getStoreId() {
        return storeId;
    }

    public void setStoreId(String storeId) {
        this.storeId = storeId;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    // Métodos auxiliares
    public boolean isValidEvent() {
        return userId != null && !userId.isEmpty() &&
                amount != null && amount > 0 &&
                eventTime != null;
    }

    public String getWindowKey() {
        return storeId + "_" + category;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SalesEvent that = (SalesEvent) o;
        return Objects.equals(eventId, that.eventId) &&
                Objects.equals(userId, that.userId) &&
                Objects.equals(productId, that.productId) &&
                Objects.equals(amount, that.amount) &&
                Objects.equals(quantity, that.quantity) &&
                Objects.equals(eventTime, that.eventTime) &&
                Objects.equals(storeId, that.storeId) &&
                Objects.equals(category, that.category);
    }

    @Override
    public int hashCode() {
        return Objects.hash(eventId, userId, productId, amount, quantity, eventTime, storeId, category);
    }

    @Override
    public String toString() {
        return "SalesEvent{" +
                "eventId='" + eventId + '\'' +
                ", userId='" + userId + '\'' +
                ", productId='" + productId + '\'' +
                ", amount=" + amount +
                ", quantity=" + quantity +
                ", eventTime=" + eventTime +
                ", storeId='" + storeId + '\'' +
                ", category='" + category + '\'' +
                '}';
    }

    // Builder pattern para facilitar criação de instâncias
    public static class Builder {
        private String eventId;
        private String userId;
        private String productId;
        private Double amount;
        private Integer quantity;
        private LocalDateTime eventTime;
        private String storeId;
        private String category;

        public Builder eventId(String eventId) {
            this.eventId = eventId;
            return this;
        }

        public Builder userId(String userId) {
            this.userId = userId;
            return this;
        }

        public Builder productId(String productId) {
            this.productId = productId;
            return this;
        }

        public Builder amount(Double amount) {
            this.amount = amount;
            return this;
        }

        public Builder quantity(Integer quantity) {
            this.quantity = quantity;
            return this;
        }

        public Builder eventTime(LocalDateTime eventTime) {
            this.eventTime = eventTime;
            return this;
        }

        public Builder storeId(String storeId) {
            this.storeId = storeId;
            return this;
        }

        public Builder category(String category) {
            this.category = category;
            return this;
        }

        public SalesEvent build() {
            return new SalesEvent(eventId, userId, productId, amount, quantity, eventTime, storeId, category);
        }
    }

    public static Builder builder() {
        return new Builder();
    }
}