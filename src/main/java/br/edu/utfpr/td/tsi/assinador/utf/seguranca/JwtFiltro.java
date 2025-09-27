package br.edu.utfpr.td.tsi.assinador.utf.seguranca;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtFiltro extends OncePerRequestFilter {

	@Autowired
	private JwtUtil jwtUtil;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
	        throws ServletException, IOException {

	    String header = request.getHeader("Authorization");
	    if (header != null && header.startsWith("Bearer ")) {
	        String token = header.substring(7);
	        String email = jwtUtil.obterUsuarioDoToken(token);
	        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(email, null, null);
	        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
	        SecurityContextHolder.getContext().setAuthentication(authToken);
	    }

	    filterChain.doFilter(request, response);
	}

}
