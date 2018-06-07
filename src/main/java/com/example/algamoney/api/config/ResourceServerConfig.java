package com.example.algamoney.api.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.expression.OAuth2MethodSecurityExpressionHandler;

/**
 * Classe para adicionar customizacao para a configuracao da dependencia de seguranca do Spring.
 * Classe para definir configuracoes de seguranca do OAuth2.
 * Classe para configurar o Resource Server.
 * 
 * Vamos trabalhar na Autorizacao, ou seja, adicionar as permissoes de acesso. 
 * Para tanto, precisa fazer configuracoes tanto no Authorization Server quanto no Resource Server.
 * 
 * @author SEMPR
 *
 */
/** Anotar com @EnableWebSecurity para habilitar a seguranca. Essa anotacao ja conta com @Configuration definida dentro dela.
 * @Configuration define que esta eh uma classe de configuracao. **/
@Profile("oauth-security")
@Configuration
@EnableWebSecurity
@EnableResourceServer
/** Adicionar anotacao @EnableGlobalMethodSecurity para configurar permissoes de acesso, definindo prePostEnabled = true 
 * para habilitar a seguranca nos metodos.
 * 
 * Eh necessario adicionar tambem um novo Bean para permitir funcionar as permissoes de acesso com OAuth2.
 * Com isso, ja eh possivel ir, por exemplo, em CategoriaResource e adicionar as permissoes.
 * 
 * **/
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class ResourceServerConfig extends ResourceServerConfigurerAdapter {

	/** Eh necessario uma classe de implementacao de UserDetailsService para definir-se como buscar um Usu�rio.
	 * Dentro do pacote security, classe AppUserDetailsService. **/
	@Autowired
	private UserDetailsService userDetailsService;
	
	/**
	 * 		Iniciar a configuracao.
	 */
	/** Como nao eh definido na classe que estende, injetar AuthenticationManagerBuilder. **/
	@Autowired
	/** @Override **/
	/** protected void configure(AuthenticationManagerBuilder auth) throws Exception { **/
	public void configure(AuthenticationManagerBuilder auth) throws Exception {
		/** 1. De onde vai validar o usuario e senha que ira informar na API. **/
		/** Poderia buscar do banco de dados. Como eh so um teste inicial, vai deixar so em memoria.
		 * Autenticacao em memoria significa que vai passar o usuario e a senha. E precisa passar tambem uma ROLE que o 
		 * usuario teria, ou seja, uma permissao que o usuario poderia ter. A ROLE seria utilizada para fazer a autorizacao.
		 * Usuario e Senha utilizados para fazer a autenticacao. **/
		/**
		auth.inMemoryAuthentication()
				.withUser("admin")
				.password("admin")
				.roles("ROLE");
		 **/
		
		/** Ao inves de usar Usuario e Senha como acima com inMemoryAuthentication(), vai usar o metodo userDetailsService(), 
		 * injetando um objeto UserDetailsService. **/
		/** Para validar se a senha esta correta, encodado da forma como foi inserido na base de dados, 
		 * Eh necessario falar para o Spring como foi encodado. Para isso, chamar o metodo passwordEncoder(). So assim, o Spring consegue ler a 
		 * senha encodada na base de dados. **/
		auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
	}
	

	/**
	 * Iniciar a configuracao de autorizacao das nossas requisicoes.
	 */
	@Override
	/** protected void configure(HttpSecurity http) throws Exception { **/
	public void configure(HttpSecurity http) throws Exception {
		/** Para quaisquer requisicao, precisa estar autenticado: precisa que o usuario e senha estejam validados. **/
		http.authorizeRequests()
			/** Com excecao das requisicoes em /categorias. Requisicoes /categorias estao abertas. Para /categorias, qualquer um 
			 * pode acessar. Para o restante das requisicoes, precisa estar autenticado. **/
			.antMatchers("/categorias").permitAll()
			.anyRequest()
			.authenticated()
			.and()
			/** E desabilitar a criacao de sessao no servidor.
			 * Definir que a API REST nao mantenha estado de nada; sem estado, nao ter sessao nenhuma no servidor. **/
			.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
			.and()
			/** E desabilitar o Cross-Site Request Forgery. 
			 * Cross-Site Request Forgery: se conseguisse fazer um JavaScript injection dentro do servico web. **/
			.csrf().disable();
	}
	
	/**
	 * Metodo para configurar como STATELESS.
	 * Nao manter estado nenhum no servidor.
	 */
	@Override
	public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
		resources.stateless(true);
	}

	private PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	/****
	 * Injetar um Bean que adiciona um Handler para conseguir fazer a seguranca dos metodos com OAuth2.
	 * @return
	 */
	@Bean
	public MethodSecurityExpressionHandler createExpressionHandler() {
		return new OAuth2MethodSecurityExpressionHandler();
	}
}
