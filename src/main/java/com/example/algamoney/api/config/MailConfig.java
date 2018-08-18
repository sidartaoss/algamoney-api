/**
 * 
 */
package com.example.algamoney.api.config;

import java.util.Properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import com.example.algamoney.api.config.property.AlgamoneyApiProperty;

/**
 * @author SEMPR
 *
 * Aula 22.16. Configurando o Envio de E-mail.

 * Nesta aula, vamos aprender como fazemos o envio de e-mail, utilizando alguns recursos do Spring. Basicamente, uma interface chamada 
 * JavaMailSender, que facilita a nossa vida quanto ao envio de e-mail. E nos vamos precisar dessa funcionalidade de envio de e-mail com a 
 * ajuda do Spring, porque nos vamos juntar com a funcionalidade de agendamento de tarefas do Scheduler para que a gente coloque uma 
 * funcionalidade na nossa Aplicacao. Que funcionalidade que eh essa? Nos vamos avisar os nossos Usuarios sobre os Lancamentos que ja 
 * estao vencidos, mas que, obviamente, ainda nao foram pagos ou recebidos. Entao, esse eh o objetivo final. 

 * O objetivo desta aula eh fazermos a configuracao para o envio de e-mail, para podermos fazer o envio de e-mail.

 * Entao, vamos abrir o STS. A primeira coisa que vamos fazer eh mexer no POM.XML, porque a interface que mencionamos (JavaMailSender) 
 * ainda nao esta no nosso PATH. Sendo assim, vamos colar a dependencia que possui essa interface:

		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-mail</artifactId>
		</dependency>	
		
 * para podermos fazer uso da Classe JavaMailSenderImpl.

 * Nos vamos configurar essa Classe em uma classe que criaremos dentro do pacote config chamada de MailConfig.
 */
/**
 *	A primeira coisa que faremos eh anotar com @Configuration
 */
@Configuration
public class MailConfig {
	
	/**  * Aula 22.16. Configurando o Envio de E-mail. 
	 * Injetar uma propriedade do tipo AlgamoneyApiProperty
	 * **/
	@Autowired
	private AlgamoneyApiProperty property;

	/** A segunda coisa que faremos eh configurar um metodo javaMailSender que vai devolver JavaMailSender.
	 * Vamos anotar com @Bean **/
	@Bean
	public JavaMailSender javaMailSender() {
		/** Agora vamos comecar a definir as configuracoes.
		 * 		Vamos utilizar a Classe java.util.Properties.
		 * 		Dentro dessa Classe Properties, nos vamos definir algumas configuracoes referentes ao envio do Email, como, por exemplo,
		 * 		se sera autenticado ou nao, se vai utilizar o Transport-Lay-Security ou nao, etc. Vamos comecar definindo qual que sera o 
		 * 		protocolo que vamos utilizar. Fazemos isso a partir da proprieade mail.transport.protocol, que vai ser definido com o 
		 * 		valor 'smtp'.
		 * 		A propriedade mail.smtp.auth define se vai ser autenticado ou nao. 
		 * 		A propriedade mail.smtp.starttls.enable define se vai habilitar Transport Layer Security ou nao. 
		 * 		starttls (TLS - Transport Layer Security) **/
		Properties props = new Properties();
		props.put("mail.transport.protocol", "smtp");
		props.put("mail.smtp.auth", true);
		props.put("mail.smtp.starttls.enable", true);
		/** Aqui vamos tratar do timeout: quanto tempo que vamos esperar uma conexao para o envio de email.
		 * 	  Definir 10 segundos. Define-se em milissegundos, entao 10 * 1000 **/
		props.put("mail.smtp.connectiontimeout", 10000);
		
		/** Criar uma instancia de JavaMailSender **/
		JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
		/** Jogar as configuracoes para dentro da instancia. **/
		mailSender.setJavaMailProperties(props);
		/**  
		 * 		Aula 22.16. Configurando o Envio de E-mail.
		 *
		 * 		Ainda precisamos de outras propriedades, que sao:
		 * 		o Host, a Porta, o Usuario e a Senha do nosso E-mail.
		 * 		Para isso, nos vamos configurar essas propriedades no application.properties.
		 * 		Vamos, primeiramente, definir as propriedades em AlgamoneyApiProperty.
		 * 		Ver a Classe AlgamoneyApiProperty.
		 *   
		 *		Voltando aqui no metodo javaMailSender(),
		 *		Configurar o host. **/
		mailSender.setHost(property.getMail().getHost());
		/** Configurar a porta. **/
		mailSender.setPort(property.getMail().getPort());
		/** Configurar o Usuario. **/
		mailSender.setUsername(property.getMail().getUsername());
		/** Configurar a Senha. **/ 
		mailSender.setPassword(property.getMail().getPassword());
		/** Com isso, a configuracao de Email esta pronta. Na proxima aula, nos vamos implementar o metodo de envio de Email, o qual
		 * vai enviar um Email simples ou em HTML e faremos o teste do envio desse Email. **/
		return mailSender;
	}
}
