package br.edu.utfpr.td.tsi.assinador.utf.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UsuarioRequisicaoDTO(
        @NotBlank @Size(min = 3, max = 100) String nome,
        @NotBlank @Email String email,
        @NotBlank @Size(min = 6, max = 100) String senha
) {}
