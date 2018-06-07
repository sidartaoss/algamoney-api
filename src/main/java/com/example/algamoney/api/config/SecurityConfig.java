//package com.example.algamoney.api.config;
//
//import org.springframework.context.annotation.Configuration;
//import org.springframework.context.annotation.Profile;
//import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
//import org.springframework.security.config.http.SessionCreationPolicy;
//
///**
// * Classe para adicionar customizacao para a configuracao da dependencia de seguranca do Spring.
// * Classe para definir usuario/senha, autenticacao HTTP Basic, etc.
// * @author SEMPR
// * 
// **/
///** Anotar com @EnableWebSecurity para habilitar a seguranca. Essa anotacao ja conta com @Configuration definida dentro dela.
// * @Configuration define que esta eh uma classe de configuracao. **/
//@Configuration
//@EnableWebSecurity
//public class SecurityConfig extends WebSecurityConfigurerAdapter {
//
//	/**
//	 * 		Iniciar a configuracao.
//	 */
//	@Override
//	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
//		/** 1. De onde vai validar o usuario e senha que ira informar na API. **/
//		/** Poderia buscar do banco de dados. Como eh soh um teste inicial, vai deixar soh em memoria.
//		 * Autenticacao em memoria significa que vai passar o usuario e a senha. E precisa passar tambem uma ROLE que o 
//		 * usuario teria, ou seja, uma permissao que o usuario poderia ter. A ROLE seria utilizada para fazer a autorizacao.
//		 * Usuario e Senha utilizados para fazer a autenticacao. **/
//		auth.inMemoryAuthentication()
//				.withUser("admin")
//				.password("admin")
//				.roles("ROLE");
//	}
//	
//	/**
//	 * Iniciar a configuracao de autorizacao das nossas requisicoes.
//	 */
//	@Override
//	protected void configure(HttpSecurity http) throws Exception {
//		/** Para quaisquer requisicao, precisa estar autenticado: precisa que o usuario e senha estejam validados. **/
//		http.authorizeRequests()
//			/** Com excecao das requisicoes em /categorias. Requisicoes /categorias estao abertas. Para /categorias, qualquer um 
//			 * pode acessar. Para o restante das requisicoes, precisa estar autenticado. **/
//			.antMatchers("/categorias").permitAll()
//			.anyRequest()
//			.authenticated()
//			.and()
//			/** E qual eh o tipo de autenticacao que vou fazer? Http Basic.
//			 * Configuracao de Seguranca Basica: Autenticacao HTTP Basic: Para cada requisicao, envia usuario e senha encodado no Cabecalho HTTP. **/
//			.httpBasic()
//			.and()
//			/** E desabilitar a criacao de sessao no servidor.
//			 * Definir que a API REST nao mantenha estado de nada; sem estado, nao ter sessao nenhuma no servidor. **/
//			.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
//			.and()
//			/** E desabilitar o Cross-Site Request Forgery. 
//			 * Cross-Site Request Forgery: se conseguisse fazer um JavaScript injection dentro do servico web. **/
//			.csrf().disable();
//		
//	}
//}
