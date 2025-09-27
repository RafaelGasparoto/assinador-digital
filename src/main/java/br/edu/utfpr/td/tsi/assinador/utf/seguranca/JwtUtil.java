package br.edu.utfpr.td.tsi.assinador.utf.seguranca;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtUtil {
	@Value("${JWT_SEGREDO}")
	private String chaveSegredo;

	@Value("${JWT_EXPIRACAO}")
	private long expiracao;

	private SecretKey getChaveAssinatura() {
		return Keys.hmacShaKeyFor(chaveSegredo.getBytes());
	}

	public String gerarToken(String email) {
		return Jwts.builder().setSubject(email).setExpiration(new Date(System.currentTimeMillis() + expiracao))
				.signWith(getChaveAssinatura(), SignatureAlgorithm.HS256).compact();
	}

	public String obterUsuarioDoToken(String token) {
		Claims claims = Jwts.parserBuilder().setSigningKey(getChaveAssinatura()).build().parseClaimsJws(token)
				.getBody();
		return claims.getSubject();
	}

	public boolean validarToken(String token) {
		try {
			Jwts.parserBuilder().setSigningKey(getChaveAssinatura()).build().parseClaimsJws(token);
			return true;
		} catch (Exception e) {
			return false;
		}
	}
}
