package br.edu.utfpr.td.tsi.assinador.utf.configuracao;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class ConfigSeguranca {

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		 http
         .csrf(csrf -> csrf.disable()) // desabilita CSRF
         .authorizeHttpRequests(authorize -> authorize.anyRequest().permitAll() // Libera todos os endpoints, deixar assim enquanto não tem autenticação
         );

     return http.build();
	}
}