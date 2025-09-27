package br.edu.utfpr.td.tsi.assinador.utf.configuracao;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class ConfigSeguranca {

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http.csrf(csrf -> csrf.disable())
		.authorizeHttpRequests(auth -> auth.requestMatchers(HttpMethod.POST, "/usuarios").permitAll()
                .requestMatchers("/autenticacao/**").permitAll()
				.anyRequest().authenticated() // Todas as outras rotas exigem autenticação
		);

		return http.build();
	}
}