package br.edu.utfpr.td.tsi.assinador.utf.servicos;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import br.edu.utfpr.td.tsi.assinador.utf.dto.UsuarioRequisicaoDTO;
import br.edu.utfpr.td.tsi.assinador.utf.dto.UsuarioRespostaDTO;
import br.edu.utfpr.td.tsi.assinador.utf.entidades.Usuario;
import br.edu.utfpr.td.tsi.assinador.utf.repositorios.UsuarioRepositorio;

@Service
public class UsuarioServico {
	@Autowired
	private UsuarioRepositorio usuarioRepositorio;
	
    private final BCryptPasswordEncoder senhaCodificador = new BCryptPasswordEncoder();
	
	public UsuarioRespostaDTO criarUsuario(UsuarioRequisicaoDTO dto) {
        if (usuarioRepositorio.findByEmail(dto.email()).isPresent()) {
            throw new RuntimeException("E-mail já cadastrado!");
        }

        Usuario usuario = new Usuario();
        usuario.setNome(dto.nome());
        usuario.setEmail(dto.email());
        usuario.setSenha(senhaCodificador.encode(dto.senha()));

        Usuario novoUsuario = usuarioRepositorio.save(usuario);

        return new UsuarioRespostaDTO(novoUsuario.getId(), novoUsuario.getNome(), novoUsuario.getEmail());
    }
}

