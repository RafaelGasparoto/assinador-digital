package br.edu.utfpr.td.tsi.assinador.utf.servicos;

import java.time.LocalDateTime;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import br.edu.utfpr.td.tsi.assinador.utf.entidades.Usuario;
import br.edu.utfpr.td.tsi.assinador.utf.repositorios.UsuarioRepositorio;
import br.edu.utfpr.td.tsi.assinador.utf.seguranca.JwtUtil;

@Service
public class AutenticacaoServico {

	@Autowired
	private UsuarioRepositorio usuarioRepositorio;
	
	@Autowired
	private JwtUtil jwtUtil;

	@Autowired
	private EmailServico emailServico;

	private final BCryptPasswordEncoder codificadorSenha = new BCryptPasswordEncoder();

	public void validarEmailSenha(String email, String senha) {
		Usuario usuario = usuarioRepositorio.findByEmail(email)
				.orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

		if (!codificadorSenha.matches(senha, usuario.getSenha())) {
			throw new RuntimeException("Senha inválida");
		}

		// gera código 2 fatores
		String codigo2fa = String.format("%06d", new Random().nextInt(999999));
		usuario.setCodigo2fa(codigo2fa);
		usuario.setExpiracao2fa(LocalDateTime.now().plusMinutes(5));
		usuarioRepositorio.save(usuario);

		// envia por email
		emailServico.enviarEmail(usuario.getEmail(), "Seu código de verificação",
				"Use este código para entrar: " + codigo2fa);
	}

	public String validarCodigoAutenticacao(String email, String otp) {
		Usuario usuario = usuarioRepositorio.findByEmail(email)
				.orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
		
		if (usuario.getExpiracao2fa().isBefore(LocalDateTime.now())) {
			throw new RuntimeException("Código expirado");
		}
		
		if (!usuario.getCodigo2fa().equals(otp)) {
			throw new RuntimeException("Código inválido");
		}


		// Limpa código 2 fatores depois de usar
		usuario.setCodigo2fa(null);
		usuario.setExpiracao2fa(null);
		usuarioRepositorio.save(usuario);

		// Aqui você gera e retorna o JWT
		return jwtUtil.gerarToken(usuario.getEmail());
	}
}
