package br.edu.utfpr.td.tsi.assinador.utf.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record VerificarLoginRequisicaoDTO(@NotBlank @Size(min = 3, max = 100) String codigo, @NotBlank @Email String email) {

}
