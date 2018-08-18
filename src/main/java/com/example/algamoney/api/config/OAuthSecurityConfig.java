/**
 * 
 */
package com.example.algamoney.api.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * Aula 25.03. Modificacoes para o Spring Security 5
 *
 * 6. Esta vai ser uma Classe de Configuracao e vai estender a classe
 * WebSecurityConfigurerAdapter, pois nos vamos pegar o metodo super() da classe WebSecurityConfigurerAdapter para ajudar
 * a prover o AuthentiticationManager na classe AuthorizationServerConfig, para evitar-se o erro identificado na Migracao para o 
 * Spring Boot 2, Field authenticationManager in com.example.algamoney.api.config.AuthorizationServerConfig required a bean of 
 * type 'org.springframework.security.authentication.AuthenticationManager' that could not be found.
 * 
 * 7. A primeira coisa que a gente vai fazer eh anotar esta classe com @Configuration, que indica que a classe declara um ou mais 
 * metodos de @Bean e podem ser processados pelo Spring container.
 * 
 *  8. Agora, vamos abrir as classes AuthorizatonServerConfig e ResourceServerConfig. O que vamos fazer? Nos vamos pegar
 *  e recortar as anotacoes, por exemplo, essa anotacao de @EnableAuthorizationServer e vamos colar em OAuthSecurityConfig, 
 *  nos vamos concentrar tudo nessa classe, porque, ja que a gente tem uma classe aqui que vai concentrar o que eh comum nas 
 *  configuracoes do OAuth, entao nos vamos pegar as anotacoes e vamos colocar em OAuthSecurityConfig.
 *  Ver AuthorizationServerConfig.java. 
 *  
 *  9. Inclusive, nos vamos precisar definir a anotacao @Profile("oauth-security") tambem na classe OAuthSecurityConfig.
 *  
 *  10. Agora, vamos na classe ResourceServerConfig e vamos pegar algumas anotacoes tambem que queremos deixar somente na classe
 *  OAuthSecurityConfig: @EnableWebSecurity, @EnableResourceServer, @EnableGlobalMethodSecurity.
 *  Ver ResourceServerConfig.java.
 *  
 *  11. A segunda alteracao aqui nesta classe eh a gente sobrescreve o metodo authenticationManager(). Ele vai continuar chamando super(),
 *  soh que nos vamos anota-lo com @Bean, que indica que o metodo produz um bean para ser gerenciado pelo container do Spring. Assim,
 *  dessa forma, nos temos de onde pegar e prover o AuthenticationManager.
 *  
 *  12. Na classe ResourceServerConfig, ate tinha essa configuracao do AuthenticationManagerBuilder, 
 * 	@Autowired
	public void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
	}
 *  
 *  que era para poder fazer a configuracao do AuthenticationManager, para fazer a configuracao de uma implementacao da interface 
 *  do AuthenticationManager, mas, como vimos com a Migracao para o Spring Boot 2, quando levantamos a Aplicacao, nao funcionou. Entao,
 *  iremos, simplesmente, remover esse metodo da classe ResourceServerConfig.
 *  Ver classe ResourceServerConfig.java.
 *  
 *  13.1. Inclusive, nos vamos pegar o metodo passwordEncoder(), que estava sendo usado no metodo configure(AuthenticationManagerBuilder auth) 
 *  da classe ResourceServerConfig e vamos colar na classe OAuthSecurityConfig.java.
 *  
 *  14. Vamos colar esse metodo aqui nesta classe porque a gente ainda continua precisando desse metodo, apesar de que nao o usarmos 
 * explicitamente, a gente vai continuar precisando dele.
 * 
 * 15. Em AuthorizationServerConfig, vamos injetar mais uma propriedade, variavel de instancia de UserDetailsService.
 * Ver AuthorizationServerConfig.java.
 *  
 * 
 * @author SEMPR
 *
 */
@Profile("oauth-security")
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@EnableAuthorizationServer
@EnableResourceServer
public class OAuthSecurityConfig extends WebSecurityConfigurerAdapter {

	@Bean
	@Override
	protected AuthenticationManager authenticationManager() throws Exception {
		return super.authenticationManager();
	}
	
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}	
	
}
