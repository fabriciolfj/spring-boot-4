package com.github.fabriciolfj.study.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

public class SalesAggregator {

    @JsonProperty("total_amount")
    private double totalAmount;

    @JsonProperty("count")
    private long count;

    @JsonProperty("min_amount")
    private double minAmount;

    @JsonProperty("max_amount")
    private double maxAmount;

    @JsonProperty("first_sale_timestamp")
    private long firstSaleTimestamp;

    @JsonProperty("last_sale_timestamp")
    private long lastSaleTimestamp;

    // Construtor padrão
    public SalesAggregator() {
        this.totalAmount = 0.0;
        this.count = 0;
        this.minAmount = Double.MAX_VALUE;
        this.maxAmount = Double.MIN_VALUE;
        this.firstSaleTimestamp = Long.MAX_VALUE;
        this.lastSaleTimestamp = Long.MIN_VALUE;
    }

    // Construtor de cópia
    public SalesAggregator(SalesAggregator other) {
        this.totalAmount = other.totalAmount;
        this.count = other.count;
        this.minAmount = other.minAmount;
        this.maxAmount = other.maxAmount;
        this.firstSaleTimestamp = other.firstSaleTimestamp;
        this.lastSaleTimestamp = other.lastSaleTimestamp;
    }

    // Método principal para adicionar uma nova venda
    public SalesAggregator addSale(Double amount) {
        return addSale(amount, System.currentTimeMillis());
    }

    public SalesAggregator addSale(double amount, long timestamp) {
        this.totalAmount += amount;
        this.count++;

        // Atualiza min/max
        this.minAmount = Math.min(this.minAmount, amount);
        this.maxAmount = Math.max(this.maxAmount, amount);

        // Atualiza timestamps
        this.firstSaleTimestamp = Math.min(this.firstSaleTimestamp, timestamp);
        this.lastSaleTimestamp = Math.max(this.lastSaleTimestamp, timestamp);

        return this;
    }

    // Método para combinar dois agregadores (útil para merge de janelas)
    public SalesAggregator merge(SalesAggregator other) {
        if (other == null || other.count == 0) {
            return this;
        }

        if (this.count == 0) {
            return new SalesAggregator(other);
        }

        SalesAggregator merged = new SalesAggregator();
        merged.totalAmount = this.totalAmount + other.totalAmount;
        merged.count = this.count + other.count;
        merged.minAmount = Math.min(this.minAmount, other.minAmount);
        merged.maxAmount = Math.max(this.maxAmount, other.maxAmount);
        merged.firstSaleTimestamp = Math.min(this.firstSaleTimestamp, other.firstSaleTimestamp);
        merged.lastSaleTimestamp = Math.max(this.lastSaleTimestamp, other.lastSaleTimestamp);

        return merged;
    }

    // Método para subtrair (útil para sliding windows)
    public SalesAggregator subtract(double amount, long timestamp) {
        this.totalAmount -= amount;
        this.count--;

        // Nota: min/max e timestamps não podem ser facilmente revertidos
        // sem manter histórico completo. Para casos complexos, considere
        // usar estruturas de dados mais sofisticadas.

        return this;
    }

    // Métrica: Média
    public double getAverage() {
        return count > 0 ? totalAmount / count : 0.0;
    }

    // Métrica: Taxa de vendas (vendas por segundo)
    public double getSalesRate() {
        if (count <= 1 || firstSaleTimestamp >= lastSaleTimestamp) {
            return 0.0;
        }

        long timeSpanMs = lastSaleTimestamp - firstSaleTimestamp;
        double timeSpanSeconds = timeSpanMs / 1000.0;

        return count / timeSpanSeconds;
    }

    // Métrica: Variância (aproximação simples)
    public double getVariance() {
        if (count <= 1) return 0.0;

        double avg = getAverage();
        // Esta é uma aproximação. Para variância exata, seria necessário
        // manter soma dos quadrados
        return Math.pow(maxAmount - minAmount, 2) / 4.0;
    }

    // Métrica: Range de valores
    public double getRange() {
        return count > 0 ? maxAmount - minAmount : 0.0;
    }

    // Verifica se o agregador está vazio
    public boolean isEmpty() {
        return count == 0;
    }

    // Reset do agregador
    public void reset() {
        this.totalAmount = 0.0;
        this.count = 0;
        this.minAmount = Double.MAX_VALUE;
        this.maxAmount = Double.MIN_VALUE;
        this.firstSaleTimestamp = Long.MAX_VALUE;
        this.lastSaleTimestamp = Long.MIN_VALUE;
    }

    // Getters
    public double getTotalAmount() {
        return totalAmount;
    }

    public long getCount() {
        return count;
    }

    public double getMinAmount() {
        return count > 0 ? minAmount : 0.0;
    }

    public double getMaxAmount() {
        return count > 0 ? maxAmount : 0.0;
    }

    public long getFirstSaleTimestamp() {
        return firstSaleTimestamp != Long.MAX_VALUE ? firstSaleTimestamp : 0;
    }

    public long getLastSaleTimestamp() {
        return lastSaleTimestamp != Long.MIN_VALUE ? lastSaleTimestamp : 0;
    }

    // Setters (para deserialização)
    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public void setCount(long count) {
        this.count = count;
    }

    public void setMinAmount(double minAmount) {
        this.minAmount = minAmount;
    }

    public void setMaxAmount(double maxAmount) {
        this.maxAmount = maxAmount;
    }

    public void setFirstSaleTimestamp(long firstSaleTimestamp) {
        this.firstSaleTimestamp = firstSaleTimestamp;
    }

    public void setLastSaleTimestamp(long lastSaleTimestamp) {
        this.lastSaleTimestamp = lastSaleTimestamp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SalesAggregator that = (SalesAggregator) o;
        return Double.compare(that.totalAmount, totalAmount) == 0 &&
                count == that.count &&
                Double.compare(that.minAmount, minAmount) == 0 &&
                Double.compare(that.maxAmount, maxAmount) == 0 &&
                firstSaleTimestamp == that.firstSaleTimestamp &&
                lastSaleTimestamp == that.lastSaleTimestamp;
    }

    @Override
    public int hashCode() {
        return Objects.hash(totalAmount, count, minAmount, maxAmount, firstSaleTimestamp, lastSaleTimestamp);
    }

    @Override
    public String toString() {
        return "SalesAggregator{" +
                "totalAmount=" + totalAmount +
                ", count=" + count +
                ", average=" + getAverage() +
                ", minAmount=" + getMinAmount() +
                ", maxAmount=" + getMaxAmount() +
                ", salesRate=" + getSalesRate() +
                ", firstSaleTimestamp=" + firstSaleTimestamp +
                ", lastSaleTimestamp=" + lastSaleTimestamp +
                '}';
    }

    // Factory methods para casos específicos
    public static SalesAggregator empty() {
        return new SalesAggregator();
    }

    public static SalesAggregator fromSingleSale(double amount) {
        return new SalesAggregator().addSale(amount);
    }

    public static SalesAggregator fromSingleSale(double amount, long timestamp) {
        return new SalesAggregator().addSale(amount, timestamp);
    }

    // Builder pattern para casos mais complexos
    public static class Builder {
        private SalesAggregator aggregator = new SalesAggregator();

        public Builder addSale(double amount) {
            aggregator.addSale(amount);
            return this;
        }

        public Builder addSale(double amount, long timestamp) {
            aggregator.addSale(amount, timestamp);
            return this;
        }

        public SalesAggregator build() {
            return aggregator;
        }
    }

    public static Builder builder() {
        return new Builder();
    }
}