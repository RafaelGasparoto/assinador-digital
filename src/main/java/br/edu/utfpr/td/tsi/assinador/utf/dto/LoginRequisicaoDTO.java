package br.edu.utfpr.td.tsi.assinador.utf.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LoginRequisicaoDTO(@NotBlank @Size(min = 3, max = 100) String senha, @NotBlank @Email String email) {
}
