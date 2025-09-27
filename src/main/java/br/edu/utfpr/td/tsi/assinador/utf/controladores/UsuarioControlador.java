package br.edu.utfpr.td.tsi.assinador.utf.controladores;

import br.edu.utfpr.td.tsi.assinador.utf.dto.UsuarioRequisicaoDTO;
import br.edu.utfpr.td.tsi.assinador.utf.dto.UsuarioRespostaDTO;
import br.edu.utfpr.td.tsi.assinador.utf.servicos.UsuarioServico;
import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/usuarios")
public class UsuarioControlador {

	@Autowired
    private UsuarioServico usuarioServico;

    @PostMapping
    public ResponseEntity<UsuarioRespostaDTO> criar(@RequestBody @Valid UsuarioRequisicaoDTO dto) {
        UsuarioRespostaDTO usuario = usuarioServico.criarUsuario(dto);
        return ResponseEntity.ok(usuario);
    }
}
