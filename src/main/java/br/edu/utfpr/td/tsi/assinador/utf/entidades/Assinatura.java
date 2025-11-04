package br.edu.utfpr.td.tsi.assinador.utf.entidades;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "assinatura")
public class Assinatura {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    // Hash do PDF assinado — garante integridade
    @Column(name = "hash_pdf_assinado", length = 128, nullable = false)
    private String hashPdfAssinado;

    // Hash do certificado usado (opcional)
    @Column(name = "hash_certificado", length = 128)
    private String hashCertificado;

    @Column(name = "data_assinatura", nullable = false)
    private LocalDateTime dataAssinatura = LocalDateTime.now();
}