package br.edu.utfpr.td.tsi.assinador.utf.configuracao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import br.edu.utfpr.td.tsi.assinador.utf.seguranca.JwtFiltro;
import br.edu.utfpr.td.tsi.assinador.utf.tratamento.JwtAutenticacaoExcecao;

@Configuration
@EnableWebSecurity
public class ConfigSeguranca {

	@Autowired
	private JwtAutenticacaoExcecao jwtEntryPoint;

	@Autowired
	private JwtFiltro jwtFiltro;

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http.csrf(csrf -> csrf.disable())
				.authorizeHttpRequests(auth -> auth.requestMatchers(HttpMethod.POST, "/usuarios").permitAll()
						.requestMatchers("/autenticacao/**").permitAll().anyRequest().authenticated())
				.exceptionHandling(exception -> exception.authenticationEntryPoint(jwtEntryPoint))
				.addFilterBefore(jwtFiltro, UsernamePasswordAuthenticationFilter.class);

		return http.build();
	}
}