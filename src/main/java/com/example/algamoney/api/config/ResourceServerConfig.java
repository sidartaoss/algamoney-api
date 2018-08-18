package com.example.algamoney.api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
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
/**
 * Aula 25.03. Modificacoes para o Spring Security 5
 * 
 *  10.1. Agora, vamos na classe ResourceServerConfig e vamos pegar algumas anotacoes tambem que queremos deixar somente na classe
 *  OAuthSecurityConfig: @EnableWebSecurity, @EnableResourceServer, @EnableGlobalMethodSecurity.
 *  
 *  10.2. As anotacoes que ficam definidas para a classe ResourceServerConfig sao apenas @Profile e @Configuration.
 *  Voltar para a classe OAuthSecurityConfig.java. 
 */
@Profile("oauth-security")
@Configuration
/**@EnableWebSecurity
@EnableResourceServer*/
/** Adicionar anotacao @EnableGlobalMethodSecurity para configurar permissoes de acesso, definindo prePostEnabled = true 
 * para habilitar a seguranca nos metodos.
 * 
 * Eh necessario adicionar tambem um novo Bean para permitir funcionar as permissoes de acesso com OAuth2.
 * Com isso, ja eh possivel ir, por exemplo, em CategoriaResource e adicionar as permissoes.
 * **/
/** @EnableGlobalMethodSecurity(prePostEnabled = true) */
public class ResourceServerConfig extends ResourceServerConfigurerAdapter {
	/**
	 * Aula 25.03. Modificacoes para o Spring Security 5
	 * 
	 * 12.1. Na classe ResourceServerConfig, ate tinha essa configuracao do AuthenticationManagerBuilder, 
     * 	@Autowired
		public void configure(AuthenticationManagerBuilder auth) throws Exception {
			auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
		}
	 *  que era para poder fazer a configuracao do AuthenticationManager, para fazer a configuracao de uma implementacao da interface 
	 *  do AuthenticationManager, mas, como vimos com a Migracao para o Spring Boot 2, quando levantamos a Aplicacao, nao funcionou. Entao,
	 *  iremos, simplesmente, remover esse metodo da classe ResourceServerConfig.
	 *  
	 *  13. Inclusive, nos vamos pegar o metodo passwordEncoder(), que estava sendo usado no metodo configure(AuthenticationManagerBuilder auth) 
	 *  da classe ResourceServerConfig e vamos colar na classe OAuthSecurityConfig.java.
	 *  Ver OAuthSecurityConfig.java.
	 *  
	 *  17. Em ResourceServerConfig, as alteracoes ja foram feitas, vamos apertar CTRL+SHIFT+O para corrigir as importacoes e, aqui, a gente
	 *  nao precisa mudar mais nada alem do que ja foi feito.
	 *  
	 *  18. Inclusive, a gente ja pode fazer um teste, vamos iniciar a nossa Aplicacao. Enquanto ela se inicia, vamos abrir o Postman.
	 *  Verificamos que a Aplicacao levantou sem problemas.
	 *  
	 *  19. Vamos fazer uma Requisicao do tipo POST para http://localhost:8080/oauth/token. Retornou Status 401 Unauthorized,
	 *  {
		    "error": "unauthorized",
		    "error_description": "Full authentication is required to access this resource"
		}
	 * 
	 * 20. Isso se deve porque a senha definida em AuthorizationServerConfig, metodo configure(ClientDetailsServiceConfigurer clients), precisa ficar
	 * criptografada de acordo com o Encoder que a gente esta utilizando para Senha, que eh definido em OauthSecurityConfig, metodo
	 * passwordEncoder(), que devolver uma instancia de BCryptPasswordEncoder.
	 * 
	 * 21. Entao, a gente vai fazer isso agora. Vamos copiar a senha definida em AuthorizationServerConfig, metodo 
	 * configure(ClientDetailsServiceConfigurer clients), .secret("@ngul@r0"), e nos vamos em .security.util.GeradorSenha e vamos colar a 
	 * senha no metodo main() e vamos mandar rodar essa classe. Gerou o valor:
	 * $2a$10$Jm/bFsrb9bpF.USxf.EiYeMaA/GJhGmHEKeYeHJQN.bQKA4pUFQXO
	 * 
	 *  22. Copiar o valor gerado em .security.util.GeradorSenha e colar em AuthorizationServerConfig, metodo 
	 * configure(ClientDetailsServiceConfigurer clients), .secret().
	 * Ver AuthorizationServerConfig.java.
	 * 
	 *  25. Vamos, agora, fazer, novamente, a tentativa de buscar o Token no Postman.
	 *  
	 *  26. Okay, agora retornou Status: 200 Ok,
	 *  {
		    "access_token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX25hbWUiOiJhZG1pbkBhbGdhbW9uZXkuY29tIiwic2NvcGUiOlsicmVhZCIsIndyaXRlIl0sIm5vbWUiOiJBZG1pbmlzdHJhZG9yIiwiZXhwIjoxNTMzODE0MzI5LCJhdXRob3JpdGllcyI6WyJST0xFX0NBREFTVFJBUl9DQVRFR09SSUEiLCJST0xFX1BFU1FVSVNBUl9QRVNTT0EiLCJST0xFX1JFTU9WRVJfUEVTU09BIiwiUk9MRV9DQURBU1RSQVJfTEFOQ0FNRU5UTyIsIlJPTEVfUEVTUVVJU0FSX0xBTkNBTUVOVE8iLCJST0xFX1JFTU9WRVJfTEFOQ0FNRU5UTyIsIlJPTEVfQ0FEQVNUUkFSX1BFU1NPQSIsIlJPTEVfUEVTUVVJU0FSX0NBVEVHT1JJQSJdLCJqdGkiOiI1YzQyMWM4OS0xOWU4LTQ4ZjktOTk1Yy1hODdmNGFjYmQ5ZDIiLCJjbGllbnRfaWQiOiJhbmd1bGFyIn0.6bw5bN-7BDt3lnpb6EJB6MFO-UNPWCCNTkBo2wz4cmY",
		    "token_type": "bearer",
		    "expires_in": 1799,
		    "scope": "read write",
		    "nome": "Administrador",
		    "jti": "5c421c89-19e8-48f9-995c-a87f4acbd9d2"
		}
	 *  
	 *  27. Agora, vamos fazer uma Requisicao para /lancamentos, GET, http://localhost:8080/lancamentos, Bearer Token, Colar
	 *  o access_token do Passo 26, enviar a Requisicao.
	 *  
	 *  28. Okay, a Requisicao foi feita com sucesso.
	 *  
	 *  29. Agora, vamos fazer uma Requisicao para obter o Refresh Token, porque ja temos o Cookie configurado, entao ele vai
	 *  enviar esse Cookie e a gente vai conseguir dar um Refresh para atualizar o nosso Token.
	 *  Definir: http://localhost:8080/oauth/token, POST.
	 *  Em Body, definir x-www-form-urlencoded, Key como grant_type, Value como refresh_token. 
	 *  Em Authorization, continua Basic Auth. Username angular, Senha @ngul@r0. 
	 *  
	 *  30. Okay, ao enviar, retornou Status 200 Ok,
	 *  {
		    "access_token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX25hbWUiOiJhZG1pbkBhbGdhbW9uZXkuY29tIiwic2NvcGUiOlsicmVhZCIsIndyaXRlIl0sIm5vbWUiOiJBZG1pbmlzdHJhZG9yIiwiZXhwIjoxNTMzODE1MDM2LCJhdXRob3JpdGllcyI6WyJST0xFX0NBREFTVFJBUl9DQVRFR09SSUEiLCJST0xFX1BFU1FVSVNBUl9QRVNTT0EiLCJST0xFX1JFTU9WRVJfUEVTU09BIiwiUk9MRV9DQURBU1RSQVJfTEFOQ0FNRU5UTyIsIlJPTEVfUEVTUVVJU0FSX0xBTkNBTUVOVE8iLCJST0xFX1JFTU9WRVJfTEFOQ0FNRU5UTyIsIlJPTEVfQ0FEQVNUUkFSX1BFU1NPQSIsIlJPTEVfUEVTUVVJU0FSX0NBVEVHT1JJQSJdLCJqdGkiOiJmZmQzMDJkZS1jMjUyLTQ3OWUtODUzZS1kMmM5OTFlNWUzMzkiLCJjbGllbnRfaWQiOiJhbmd1bGFyIn0.evt1iu0UFa3MOLh5Uv6R6-yifzBbk6tpVb0_PJJ6DOQ",
		    "token_type": "bearer",
		    "expires_in": 1799,
		    "scope": "read write",
		    "nome": "Administrador",
		    "jti": "ffd302de-c252-479e-853e-d2c991e5e339"
		}
	 *
	 * 31. Vamos copiar o Refresh Token do Passo 30 para testa-lo tambem. Vamos testar na Requisicao de /lancamentos, conforme 
	 * Passo 27.
	 * 
	 * 32. Ao enviar a Requisicao, os dados foram retornados com sucesso.
	 * 
	 * 33. Fim da Aula 25.03. Modificacoes para o Spring Security 5.
	 */
	
	/** Eh necessario uma classe de implementacao de UserDetailsService para definir-se como buscar um Usu�rio.
	 * Dentro do pacote security, classe AppUserDetailsService. **/
	/**@Autowired
	private UserDetailsService userDetailsService;*/
	
	/**
	 * 		Iniciar a configuracao.
	 */
	/** Como nao eh definido na classe que estende, injetar AuthenticationManagerBuilder. **/
	/**@Autowired*/
	/** @Override **/
	/** protected void configure(AuthenticationManagerBuilder auth) throws Exception { **/
	/**public void configure(AuthenticationManagerBuilder auth) throws Exception {
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
		 * senha encodada na base de dados. **
		auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
	}
	*/

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

	/**
	private PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	*/
	
	/****
	 * Injetar um Bean que adiciona um Handler para conseguir fazer a seguranca dos metodos com OAuth2.
	 * @return
	 */
	@Bean
	public MethodSecurityExpressionHandler createExpressionHandler() {
		return new OAuth2MethodSecurityExpressionHandler();
	}
}
