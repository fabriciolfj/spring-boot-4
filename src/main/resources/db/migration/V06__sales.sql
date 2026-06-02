CREATE TABLE sales (
                       id        BIGSERIAL NOT NULL,
                       sale_date DATE NOT NULL,
                       amount    DECIMAL(10, 2),
                       PRIMARY KEY (id, sale_date)
) PARTITION BY RANGE (sale_date);

CREATE TABLE sales_2024_q1 PARTITION OF sales
    FOR VALUES FROM ('2024-01-01') TO ('2024-04-01');

CREATE TABLE sales_2024_q2 PARTITION OF sales
    FOR VALUES FROM ('2024-04-01') TO ('2024-07-01');
