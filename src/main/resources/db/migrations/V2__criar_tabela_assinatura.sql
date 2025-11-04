CREATE TABLE assinatura (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    usuario_id BIGINT NOT NULL,
    hash_pdf_assinado VARCHAR(128) NOT NULL,
    hash_certificado VARCHAR(128),
    data_assinatura TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_assinatura_usuario FOREIGN KEY (usuario_id)
        REFERENCES usuario (id)
        ON DELETE CASCADE
);
