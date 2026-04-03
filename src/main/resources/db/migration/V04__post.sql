-- =============================================================
-- Schema: demo_db
-- Abordagem: LEFT JOIN FETCH via Record Projection (Vlad Mihalcea)
-- =============================================================

CREATE TABLE IF NOT EXISTS users (
                                     id         BIGSERIAL    PRIMARY KEY,
                                     first_name VARCHAR(100) NOT NULL,
    last_name  VARCHAR(100) NOT NULL
    );

CREATE TABLE IF NOT EXISTS post (
                                    id    BIGINT       PRIMARY KEY,
                                    title VARCHAR(255) NOT NULL
    );

-- PostDetails compartilha o mesmo PK do Post (@MapsId / @OneToOne)
-- A coluna created_by_user_id é nullable pois User é opcional
CREATE TABLE IF NOT EXISTS post_details (
                                            id                  BIGINT       PRIMARY KEY,
                                            created_on          TIMESTAMP    NOT NULL DEFAULT NOW(),
    created_by_user_id  BIGINT,
    CONSTRAINT fk_post_details_post FOREIGN KEY (id)                 REFERENCES post(id) ON DELETE CASCADE,
    CONSTRAINT fk_post_details_user FOREIGN KEY (created_by_user_id) REFERENCES users(id)
    );

-- =============================================================
-- Dados de exemplo
-- =============================================================

INSERT INTO users (first_name, last_name) VALUES
                                              ('John', 'Doe'),
                                              ('Jane', 'Smith');

-- Post com PostDetails e User associado
INSERT INTO post (id, title) VALUES (1, 'First post');
INSERT INTO post_details (id, created_on, created_by_user_id) VALUES (1, NOW(), 1);

-- Post com PostDetails mas sem User
INSERT INTO post (id, title) VALUES (2, 'Second post');
INSERT INTO post_details (id, created_on, created_by_user_id) VALUES (2, NOW(), NULL);

-- Post sem PostDetails (orphan)
INSERT INTO post (id, title) VALUES (3, 'Third post - no details');
