package com.example.algamoney.api.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Adicionar seguranca do tipo Basic tambem. Para que? Para facilitar o desenvolvimento do Front-end com Angular. 
 * Para trabalhar as partes iniciais com seguranca basica para facilitar o desenvolvimento.
 * 
 * Pode-se contar com o auxilio dos Profiles do Spring para trocar: agora quero usar seguranca Basic ou Oauth2, 
 * sem precisar fazer nada programaticamente. 
 * 
 * Quando a aplicacao inteira estiver pronta, o JAR estiver pronto, vamos entregar esse JAR para o front-end. 
 * E lah, na hora de subir a Aplicacao, pode-se escolher subir com Basic ou subir com OAuth2.
 * 
 * Na Classe BasicSecurityConfig, adicionar a Anotacao @Profile(basic-security).
 * Na Classe AuthorizationServerConfig, adicionar a Anotacao @Profile(oauth-security). Anotar todas as outras classes relacionadas a Seguranca Oauth
 * com @Profile(oauth-security).
 * Neste momento, tem duas segurancas configuradas no sistema: Basic-Security e Oauth2-Security. 
 * Como que escolhe? Atraves da propriedade spring.profiles.active.
 *
 *
* Com a anotacao @Profile, pode-se definir um nome de um Profile que quero que esteja ativo na hora que estiver subindo a Aplicacao.
 * Se definir que quero que basic-security esteja ativo, este objeto do Spring serah carregado. Se nao tiver o Profile basic-security
 * este objeto nao serah carregado.
 * 
 * @author SEMPR
 *
 */
@Profile("basic-security")
@EnableWebSecurity
public class BasicSecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	private UserDetailsService userDetailsService;
	
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
	}

	/**
	 * Criptografia continua com BCrypt.
	 * @return
	 */
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	/**
	 * Configurando o acesso, falando que eh por HTTP Basic.
	 */
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests()
			.anyRequest().authenticated()
			.and()
			.httpBasic()
			.and()
			.sessionManagement()
				.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
			.and()
			.csrf().disable();
	}
}
