CREATE EXTENSION IF NOT EXISTS vector;

CREATE TABLE IF NOT EXISTS book (
                                    id     BIGSERIAL PRIMARY KEY,
                                    content TEXT         NOT NULL,
                                    embedding VECTOR(768)  NOT NULL,
    year_published VARCHAR(10) NOT NULL
    );

CREATE INDEX IF NOT EXISTS idx_book_embedding
    ON book USING hnsw (embedding vector_cosine_ops);