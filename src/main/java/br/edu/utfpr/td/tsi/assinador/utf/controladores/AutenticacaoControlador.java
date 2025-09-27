package br.edu.utfpr.td.tsi.assinador.utf.controladores;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import br.edu.utfpr.td.tsi.assinador.utf.dto.VerificarLoginRequisicaoDTO;
import br.edu.utfpr.td.tsi.assinador.utf.dto.LoginRequisicaoDTO;
import br.edu.utfpr.td.tsi.assinador.utf.servicos.AutenticacaoServico;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/autenticacao")
public class AutenticacaoControlador {

	@Autowired
    private AutenticacaoServico autenticacaoServico;

    // valida email/senha e envia código por email
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody @Valid LoginRequisicaoDTO dto) {
    	autenticacaoServico.loginPasso1(dto.email(), dto.senha());
        return ResponseEntity.ok("Código enviado para o seu email");
    }

    // valida código verificação de 2 fatores, retorna JWT
    @PostMapping("/verificar")
    public ResponseEntity<String> verificarLogin(@RequestBody @Valid VerificarLoginRequisicaoDTO dto) {
        String token = autenticacaoServico.loginPasso2(dto.email(), dto.codigo());
        return ResponseEntity.ok(token);
    }
}
