package com.example.algamoney.api.config;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.security.oauth2.provider.token.TokenEnhancerChain;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

import com.example.algamoney.api.config.token.CustomTokenEnhancer;

/**
 * Classe para configurar o Authorization Server.
 * 
 * Primeiro eh necessario solicitar o ACCESS_TOKEN com o endpoint /oauth/token, passando uma autorizacao basica (Basic Auth: Usuario / Senha).
 * Verbo POST.
 * Essa autorizacao eh da Aplicacao Cliente (Angular) e nao do Usuario.
 * 	Passar o Header da Autorizacao com o Usuario e Senha da Aplicacao Angular:
 * Authorization Basic Auth. Username: angular; Password: @ngul@r0
 * Content Type: application/x-www-form-urlencoded
 * Em body, passar os par�metros:  
 * client - angular;
 * username (do Usuario) - admin; password - admin (Configurados em ResourceServerConfig);
 * grant_type - password 
 * 
 * No Header das requisicoes OAuth2, definir:
 * Authorization - Bearer (token_type) + ESPACO + access_token
 * 
 * 
 * Recuperar um Access Token que eh do tipo JWT:
 * 
 * JWT - Json Web Token
 * Dividio em tres partes: 1. Header: o algoritmo e o tipo de token; 2. Payload: os dados em formato JSON. Atributos no Payload 
 * saa chamados de Claims;
 * 3. Assinatura para validar o que tem nesse token. Eh um composto do Header + Payload + Uma palavra secreta (senha), 
 * a qual valida se o token esta valido ou nao.
 *   
 * Com isso, JWT oferece mais seguran�a, a aplicacao torna-se mais escal�vel, pois torna-se desnecess�rio
 * armazenar o token no lado do servidor, nao precisa nem colocar em banco de dados, porque, no Payload,
 * ja eh carregado tudo que eh necessario. O servidor fica totalmente Stateless quando usa JWT.
 * Como dentro do token ja tem tudo o que n�s precisamos, nao eh necessario armazenar nada no servidor. 
 * 
 * Para adicionar JWT ao projeto, primeiramente adicionar depend�ncia no POM.xml.
 * 
 * 
 * Obter um novo ACCESS_TOKEN atraves do REFRESH_TOKEN
 * Utiliza a mesma URL: /oauth/token.
 * Nao utiliza grant_type - password;
 * Nao passar username / password novamente. 
 * Nao precisa armazenar username / password do Usuario na minha aplicacao, pois vai usar o Refresh Token.
 * Body: x-www-form-urlencoded. 
 * Parametros:
 * grant_type - refresh_token;
 * refresh_token - {valor_do_refresh_token} 
 * 
 * Problema: As boas praticas dizem que nao e bom que a Aplicacao Cliente (Angular) tenha acesso ao Refresh Token.
 * Solucao: 
 * Colocar o Refresh Token em um Cookie Http. Quando estivermos em Produ��o, esse Cookie Http, sendo seguro, so vai funcionar
 * em Https, o Javascript nao tera acesso a ele.
 * Tirar o Refresh Token do Corpo da Resposta e coloca-lo em um Cookie.
 * Nao existe uma forma que o Spring Security ja nos forneca essa solucao, entao vamos usar outros mecanismos do Spring para
 * fazer isso: colocar no Cookie Http.
 * O que usar? Ha uma interface no Spring, ResponseBodyAdvice, que intercepta a Requisicao, eh um processador que vai pegar a Requisicao antes de 
 * processar de volta a quem chamou a Requisicao, para trabalhar em cima dela no Servidor do Spring: Tirar o Refresh Token da Requisicao e
 * adicionar no Cookie.
 * 
 * Cookie refreshToken é seguro: Só será enviado pelo Browser quando tiver HTTPS. 
 * Não é enviado se tiver HTTP apenas. HTTP Only, ou seja, Javascrit não consegue acessar.
 * 
 * @author SEMPR
 *
 */
@Profile("oauth-security")
@Configuration
/**
 * Aula 25.03. Modificacoes para o Spring Security 5
 * 
 *  8.1. Agora, vamos abrir as classes AuthorizatonServerConfig e ResourceServerConfig. O que vamos fazer? Nos vamos pegar
 *  e recortar as anotacoes, por exemplo, essa anotacao de @EnableAuthorizationServer em AuthorizationServerConfig e vamos colar em 
 *  OAuthSecurityConfig, nos vamos concentrar tudo nessa classe, porque, ja que a gente tem uma classe aqui que vai concentrar o 
 *  que eh comum nas configuracoes do OAuth, entao nos vamos pegar as anotacoes e vamos colocar em OAuthSecurityConfig.
 *  
 *  8.2. As anotacoes que ficam definidas para a classe AuthorizationServerConfig sao apenas @Profile("oauth-security") e @Configuration.
 *  Voltar para a classe OAuthSecurityConfig.java. 
 */
/** @EnableAuthorizationServer **/
public class AuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {

	/**
	 * Aula 25.03. Modificacoes para o Spring Security 5
	 *
	 * Nesta aula aqui, o que a gente vai fazer aqui eh a adaptacao para a nova API do Spring Security, junto com o Oauth, para as novas versoes, porque, do 
	 * jeito que esta aqui, a gente esta com o profile Basic-Security ainda configurado no application-properties, spring.profiles.active=basic-security. 
	 *
	 * 1. Com ele, vai funcionar beleza, vamos levantar a Aplicacao e abrir uma nova Aba no Postman. Selecionar Authorization Basic Auth, Username 
	 * admin@algamoney.com, Password admin, URL http://localhost:8080/lancamentos, se fizermos uma Requisicao, vem o Resultado, com Status 200 Ok.
	 *
	 * 2. Com Basic-Security, esta tudo okay, mas, se definirmos, em application-properties, spring.profiles.active como oauth-security, a gente vai comecar 
	 * a ter problemas, 
	 *
	 *	***************************
	 *	APPLICATION FAILED TO START
	 *	***************************
	 *	
	 *	Description:
	 *	
	 *	Field authenticationManager in com.example.algamoney.api.config.AuthorizationServerConfig required a bean of type 
	 *'org.springframework.security.authentication.AuthenticationManager' that could not be found.
	 *	
	 *	Action:
	 *	
	 *	Consider defining a bean of type 'org.springframework.security.authentication.AuthenticationManager' in your configuration.
	 *	
	 *	, esta falando que o Campo authenticationManager la na nossa classe AuthorizationServerConfig eh requerido, mas ele nao foi encontrado. Por que? 
	 * Teve algumas mudancas aqui. Nao sabemos muito ao certo o que esta acontecendo, pesquisamos isso na Internet e vimos algumas discussoes sobre isso. 
	 * Vimos, ate, algumas pessoas reclamando o fato do authenticationManager nao estar mais sendo provido de forma automatica. Entao, a gente precisa dar 
	 * um jeito nisso agora.
	 *	
	 *	3. Nos vamos abrir a nossa classe AuthorizationServerConfig. O campo authenticationManager nao esta mais sendo provido de uma forma mais automatica.
	 * Nos vamos ter que fazer, aqui, uma pequena modificacao para que ele seja provido. 
	 * 
	 * 4. Antes de mexer nisso, verificamos que o nosso Projeto, na raiz, esta apontando um erro. Vamos clicar com o botao Direito em algamoney-api, opcao 
	 * Maven - Update Project... - clicar em OK. Okay, sumiu o erro na raiz do Projeto. 
	 * 
	 *  5. Agora, voltando para a nossa Correcao do campo authenticationManager, vamos criar uma nova classe no pacote .config,
	 *  que vai ser chamanda de OauthSecurityConfig. 
	 *  Ver OauthSecurityConfig.java.
	 *  
	 *  15.1. Em AuthorizationServerConfig, vamos injetar mais uma propriedade, variavel de instancia de UserDetailsService,
	 *  porque a gente vai precisar dessa instancia no metodo configure(AuthorizationServerEndpointsConfigurer endpoints).
	 *  
	 *  16. Antes, era o Spring Security que pegava UserDetailsService por detras dos panos e, agora, isso nao esta acontecendo e a gente vai
	 *  precisar prover explicitamente. Sem essa instancia, a gente ate consegue gerar o token e utiliza-lo, mas a gente nao consegue dar um
	 *  Refresh no Token. Quando a gente vai buscar a atualizacao do nosso Token de forma automatica, a gente nao consegue se a gente nao 
	 *  tiver essa propriedade de UserDetailsService configurada. Entao, por isso que estamos configurando UserDetailsService.
	 *   Voltar em ResourceServerConfig.java.
	 */
	/** 1. Injetar o AuthenticationManager 
	 * Vai gerenciar a autenticacao, pegar usuario / senha da aplicacao. **/
	@Autowired
	private AuthenticationManager authenticationManager;
	
	@Autowired
	private UserDetailsService userDetailsService;
	
	/** 2. Configurar a aplicacao; o Cliente. A aplicacao eh o cliente, eh quem o usuario esta usando. Vai autorizar esse Cliente
	 * a acessar o Authorization Server. **/
	@Override
	public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
		/** Pode-se colocar este Cliente em JDBC, em um banco de dados (jdbc()), ou em memoria (inMemory()).
		 * Vamos deixar em memoria porque estamos criando uma API REST que vai servir para o nosso Cliente Angular e isso
		 * nao vai mudar:  vai servir apenas para o Cliente Angular. **/
		clients.inMemory()
						/** Qual eh o nome do Cliente? CLIENTE_ID**/
						.withClient("angular")
						/** Qual eh a senha deste Cliente? CLIENTE_SECRET **/
						/** .secret("@ngul@r0") */
						/** 
						 * Aula 25.03. Modificacoes para o Spring Security 5
						 * 
						 * 22.1. Copiar o valor gerado em .security.util.GeradorSenha e colar no lugar da senha descriptografada em 
						 * AuthorizationServerConfig, metodo configure(ClientDetailsServiceConfigurer clients), na chamada do 
						 * metodo .secret().
						 * **/
						.secret("$2a$10$Jm/bFsrb9bpF.USxf.EiYeMaA/GJhGmHEKeYeHJQN.bQKA4pUFQXO")						
						/** Qual eh o escopo deste Clinete? (Read/Write). 
						 * Com a definicao de escopo, consegue limitar o acesso deste cliente (Aplicacao Angular). 
						 * Quando vai usar a definicao de escopo? No momento de definir a autorizacao dos metodos no ResourceServerConfig:
						 * @PreAuthorize("hasAuthority('ROLE_PESQUISAR_CATEGORIA')" and #oauth2.hasScope('read')) **/
						.scopes("read", "write")
						/** Qual eh o Grant Type que vamos utilizar? 
						 * Nos vamos utilizar o Password Credentials Flow, que eh o fluxo onde o Angular recebe o Usuario / Senha do Usuario; 
						 * o Usuario informa Usuario / Senha na tela do Angular e o Angular vai enviar para receber o ACCESS_TOKEN **/
						/** Refresh Token sera utilizado para obter um novo Access Token **/
						.authorizedGrantTypes("password", "refresh_token")	
						/** Quantos segundos esse token vai ficar ativo? 1800 = 30 minutos. **/
						.accessTokenValiditySeconds(1800)
						/** Definir o tempo de vida do Refresh Token. 1 dia inteiro para expirar. **/
						.refreshTokenValiditySeconds(3600 * 24)
				.and()
						/** Adicionar um novo Cliente para tratar o escopo.
						 * Criando uma Aplicacao Mobile **/
						.withClient("mobile")
						/** .secret("m0b1l30") **/
						/**
						 * Aula 25.03. Modificacoes para o Spring Security 5
						 * 
						 * 23. A mesma coisa feita no item 22.1 fazer para a senha descriptografada abaixo: m0b1l30.
						 * 
						 * 24. Por uma questao didatica, estamos definindo a senha aqui, mas a intencao eh que nao se guarde aqui a senha.
						 * Deve-se ter a senha em algum outro lugar seguro que nao no codigo-fonte, porque, se o pessoal do Spring, agora,
						 * mudou o Spring para que a gente defina a criptografia da senha aqui no codigo-fonte, eh porque o pessoal do Spring
						 * aconselha que nao armazenemos a senha aqui no codigo-fonte de forma transparente.
						 * Voltar para ResourceServerConfig.java.
						 */
						.secret("$2a$10$q6x.9u.YgZonVsZsmhuWLOa5DDlp.EyUoweda64kX5IvBhgzYHhku")
						/** Com escopo somente de Leitura. **/
						.scopes("read")
						.authorizedGrantTypes("password", "refresh_token")	
						.accessTokenValiditySeconds(1800)
						.refreshTokenValiditySeconds(3600 * 24)				
						;
	}
	
	/** 3. **/
	@Override
	public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
		/**
		 *  Adicionar no Token JWT o nome do usuário. Para que? Para que, no front-end, no Angular, possa mostrar o nome do Usuario logado.
	     * Há uma forma de incrementar as informacoes do Token com as nossas informacoes. NO JWT, pode-se colocar o que se desejar.
	     * No AuthorizationServer, em suas configurações, criar um objeto TokenEnhancedChain, ou seja, um Token com mais detalhes.
	     * 
	     * Para o Token retornado na URL: http://localhost:8080/oauth/token:
	     * 		{
				    "access_token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX25hbWUiOiJhZG1pbkBhbGdhbW9uZXkuY29tIiwic2NvcGUiOlsicmVhZCIsIndyaXRlIl0sIm5vbWUiOiJBZG1pbmlzdHJhZG9yIiwiZXhwIjoxNTI1MTgwNzYyLCJhdXRob3JpdGllcyI6WyJST0xFX0NBREFTVFJBUl9DQVRFR09SSUEiLCJST0xFX1BFU1FVSVNBUl9QRVNTT0EiLCJST0xFX1JFTU9WRVJfUEVTU09BIiwiUk9MRV9DQURBU1RSQVJfTEFOQ0FNRU5UTyIsIlJPTEVfUEVTUVVJU0FSX0xBTkNBTUVOVE8iLCJST0xFX1JFTU9WRVJfTEFOQ0FNRU5UTyIsIlJPTEVfQ0FEQVNUUkFSX1BFU1NPQSIsIlJPTEVfUEVTUVVJU0FSX0NBVEVHT1JJQSJdLCJqdGkiOiIxMDI4N2NkZC1lNWZmLTQ0OWMtOTIxYi04ZTI2NjZlYTk5ODUiLCJjbGllbnRfaWQiOiJhbmd1bGFyIn0.ktC2FmdbzFl68W-k_bOkUB8aAf8Ur4rQv6jZMpjyz8Y",
				    "token_type": "bearer",
				    "expires_in": 1799,
				    "scope": "read write",
				    "nome": "Administrador",
				    "jti": "10287cdd-e5ff-449c-921b-8e2666ea9985"
				}
		 * Ir em jwt.io e verificar no Payload decodificado o nome do Usuario:
		 * 
		 *{
			  "user_name": "admin@algamoney.com",
			  "scope": [
			    "read",
			    "write"
			  ],
			  "nome": "Administrador",
			  "exp": 1525180762,
			  "authorities": [
			    "ROLE_CADASTRAR_CATEGORIA",
			    "ROLE_PESQUISAR_PESSOA",
			    "ROLE_REMOVER_PESSOA",
			    "ROLE_CADASTRAR_LANCAMENTO",
			    "ROLE_PESQUISAR_LANCAMENTO",
			    "ROLE_REMOVER_LANCAMENTO",
			    "ROLE_CADASTRAR_PESSOA",
			    "ROLE_PESQUISAR_CATEGORIA"
			  ],
			  "jti": "10287cdd-e5ff-449c-921b-8e2666ea9985",
			  "client_id": "angular"
			}
		 *	Com isso, a Aplicação Angular vai poder pegar o Token e pegar o nome do Usuario logado e mostrar na tela. 	  
	     * 
		 */		
		TokenEnhancerChain tokenEnhancerChain = new TokenEnhancerChain();
		/** Adicionar ao Token uma cadeia de objetos que trabalham com tokens: setTokenEnhancers()  
		 * Podem haver vários objetos que incrementam funcionalidades ao Token. **/
		tokenEnhancerChain.setTokenEnhancers(Arrays.asList(this.tokenEnhancer(), this.accessTokenConverter()));
		endpoints
				/** Precisa armazenar o token em algum lugar, porque a Aplicacao Angular vai: 1. buscar o token; 2. mandar de volta para poder
				 * acessar a API. Entao o token tem que estar armazenado em algum lugar no servidor para o AuthorizationServer validar: esse token
				 * eh valido ou esse token nao eh valido.
				 * 
				 * Armazenar em um TokenStore.  **/
				.tokenStore(tokenStore())
				/** Passar para o Endpoint tambem o Token Enhancer, nao soh o Access Token. **/
				.tokenEnhancer(tokenEnhancerChain)
				/** Adicionar um conversor de token no nosso endpoint. **/
				.accessTokenConverter(accessTokenConverter())
				/** Sempre que pedir um novo Access Token usando um Refresh Token, um novo Refresh Token sera enviado.
				 * Se nao setar reuseRefreshTokens(false), o Refresh Token ter� o tempo de 24h apenas, ate buscar um novo Refresh Token
				 * usando Password Credentials Flow. **/
				.reuseRefreshTokens(false)
				.userDetailsService(this.userDetailsService)
				/** Utilizar o AuthenticationManager injetado para ele poder validar o Usu�rio / Senha. **/
				.authenticationManager(authenticationManager);
	}

	/**
	 * Bean que adiciona um AccessTokenConverter do tipo JWT.
	 * @return
	 */
	@Bean
	public JwtAccessTokenConverter accessTokenConverter() {
		JwtAccessTokenConverter accessTokenConverter = new JwtAccessTokenConverter();
		/** Eh nesse AccessTokenConverter que nos vamos setar a chave (Secret) que valida um Token. Eh essa a senha que valida o token. **/
		accessTokenConverter.setSigningKey("algaworks");
		return accessTokenConverter;
	}

	/** 1. TokenStore em memoria. **/
	/**
	@Bean
	public TokenStore tokenStore() {
		/** Armazenar em Memoria. 		
		/** Poderia colocar o token em um banco de dados, mas como vai mudar para JWT (JSON Web Token), onde nao precisa ser armazenado
		 * vamos deixar assim por enquanto. 
		return new InMemoryTokenStore();
	}
	**/
	
	/**
	 * 2. O TokenStore nao vai mais ser em memoria. Como, dentro do token ja tem tudo o que nos precisamos, nao eh necessario
	 * armazenar mais nada.
	 * O TokenStore vai ser um JWT TokenStore. 
	 * @return
	 */
	@Bean
	public TokenStore tokenStore() {
		return new JwtTokenStore(accessTokenConverter());
	}
	
	/**
	 * Incrementar customizacao ao token.
	 * @return
	 */
	private TokenEnhancer tokenEnhancer() {
		return new CustomTokenEnhancer();
	}	
	
}
