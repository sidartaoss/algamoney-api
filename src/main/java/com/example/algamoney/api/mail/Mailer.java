/**
 * 
 */
package com.example.algamoney.api.mail;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import com.example.algamoney.api.model.Lancamento;
import com.example.algamoney.api.model.Usuario;
import com.example.algamoney.api.repository.LancamentoRepository;

/**
 * @author SEMPR
 *
 *	Aula 22.17. Enviando um Email Simples

 * Nesta aula, a gente vai implementar o metodo que vai, de fato, fazer o envio de Email. 
 * Esse metodo sera implementado no pacote chamado mail e a Classe se chamara Mailer.
 * 
 * A primeira coisa que faremos eh anotar a Classe com @Component. (@Component indica que a Classe anotada eh um "Componente". Tais
 * classes sao consideradas candidatas para auto-deteccao quando utiliza-se configuracao baseada em anotacoes e leitura do classpath.
 * 
 * A segunda coisa que faremos eh injetar a nossa Classe JavaMailSender, porque eh atraves dela que nos faremos o envio de Email.
 * 
 * Em seguida, nos criaremos o metodo chamado de enviarEmail.
 */
@Component
public class Mailer {
	
	@Autowired
	private JavaMailSender mailSender;
	
	/** Aula 22.19. Processando o Template e Enviando o Email.
	 * 	Antes de implementarmos o metodo enviarEmail(), devemos injetar o TemplateEngine do Thymeleaf. **/
	@Autowired
	private TemplateEngine thymeleaf;
	
	/** Aula 22.19. Processando o Template e Enviando o Email. 
	 * 		Injetar a variavel de instancia de Repositorio de Lancamento
	 * 		
	 * 		Vamos comentar lancamentoRepository porque sera utilizado apenas para o metodo teste(), que
	 * 		serve apenas para testarmos a funcionalidade de envio de Email.
	 * **
	@Autowired
	private LancamentoRepository lancamentoRepository;
	**/

	/** Aula 22.17. Enviando um Email Simples 
	 * 	  Metodo para testar o envio de Email.
	 * 
	 * Primeiramente, anotar este metodo com a anotacao org.springframework.context.event.EventListener. O metodo deve receber um objeto 
	 * ApplicationReadyEvent, caso contrario este metodo sera invocado a todo evento que o Spring for disparando e, para nos, neste caso, so
	 * interessa o Evento dizendo que a Aplicacao esta pronta para ser utilizada.
	 * 
	 * Vamos comentar este metodo, porque eh um metodo somente para testar a funcionalidade de envio de Email.
	 * **
	@EventListener
	private void teste(ApplicationReadyEvent event) {
		/** Aqui faremos a chamada para o metodo enviarEmail(). **
		this.enviarEmail("testes.algaworks.sidarta@gmail.com", 
				Arrays.asList("semprebono@gmail.com"), "Testando", "Olá!<br>Teste ok.");
		/** Agora, nos vamos iniciar a Aplicacao e, assim que a Aplicacao iniciar, este metodo teste() tem que ser disparado. **
		/** Vamos logar com sysout, porque eh um metodo simples de teste que estamos definindo para envio de Email. **
		
		/** Eh necessario tambem alterar a opcao, nas Configuracoes do Gmail, para a conta do Remetente testes.algaworks.sidarta@gmail.com: 
		 * Permitir aplicativos menos seguros: ATIVADA
		 * **
		System.out.println("Terminado o envio de e-mail...");
	}
	**/
	
	/** Aula 22.19. Processando o Template e Enviando o Email. 
	 * Para testarmos, eh so subir a Aplicacao. A anotacao @EventListener escuta o evento ApplicationReadyEvent. Esse evento eh 
	 * disparado quando a Aplicacao esta pronta para ser utilizada. Entao, quando a Aplicacao estiver pronta para ser utilizada, o 
	 * nosso metodo teste() vai ser executado e a gente consegue testar o envio de Email. 
	 * 
	 * Agora, iremos comentar o metodo teste(), porque esse metodo eh apenas para testar a funcionalide de envio de Email.
	 * **
	 @EventListener
	private void teste(ApplicationReadyEvent event) {
		 /** A primeira coisa que faremos eh declarar uma variavel chamada template, que vai apontar para o nosso template. 
		  * Nao eh necessario definir a extensao. Tambem nao eh necessario definir a primeira pasta do PATH: templates. O Thymeleaf
		  * vai assumir que ja existe essa pasta. **
		 String template = "mail/aviso-lancamentos-vencidos";
		 
		 /** Agora, vamos injetar o repositorio de Lancamento **
		 
		 /** Agora, vamos obter a Lista de Lancamentos, todos os registros. Nao precisamos de criterios, porque so queremos
		  * testar o envio do Email. **
		 List<Lancamento> lista = this.lancamentoRepository.findAll(); 
		 
		 /** Agora, vamos criar o nosso Map com as variaveis. **
		 Map<String, Object> variaveis = new HashMap<>();
		 variaveis.put("lancamentos", lista);
		 
		this.enviarEmail("testes.algaworks.sidarta@gmail.com", 
				Arrays.asList("semprebono@gmail.com"), "Testando", template, variaveis);
		System.out.println("Terminado o envio de e-mail...");
		
		/** O Envio de Email ocorreu com sucesso. Vamos apenas ajustar o layout do Email: o que eh Receita deve ficar em Azul e 
		 * o que eh Despesa deve ficar em Vermelho. Essa mudanca eh no nosso template HTML.
		 * Ver aviso-lancamentos-vencidos.html. **
	}
	**/
	
	/**
	 * Aula 22.21. Agendando o envio de e-mail.
	 * Este metodo vai receber uma lista de Lancamentos vencidos e uma lista de Usuarios, que serao os destinatarios.
	 */
	public void avisarSobreLancamentosVencidos(
			List<Lancamento> vencidos, List<Usuario> destinatarios) {
		/** Agora, o que nos iremos fazer eh preparar os parametros para podermos, na sequencia, chamar o metodo enviarEmail, que faz o processamento
		 * do template HTML.
		 * A primeira coisa a fazer eh configurar as variaveis do template. Vamos, entao, declarar um Map<String, Object>, chamando de variaveis.
		 * A variavel lancamentos tem que ser a mesma definida em th:each no template HTML. **/
		Map<String, Object> variaveis = new HashMap<>();
		variaveis.put("lancamentos", vencidos);
		/** A partir da nossa lista de Usuarios, a gente vai configurar uma lista de Emails. Vamos declarar, entao, List<String>, chamando de emails.
		 * Vamos utilizar os metodos do Java8 para poder transformar uma Lista de Usuarios em uma Lista de Strings: stream.map(). O metodo map tera 
		 * como parametro uma expressao Lambda: u de Usuario. O corpo vai ficar como u.getEmail(). Depois disso, eh preciso transformar essa stream em
		 * uma lista com o metodo collect(). No parametro do metodo collect(), definir Collectors.toList(). Assim, a gente transforma uma Lista de Usuarios
		 * em uma Lista de Strings que, no nosso caso, eh uma lista de Emails. **/
		List<String> emails = destinatarios.stream().map(u -> u.getEmail())
				.collect(Collectors.toList());
		/** Agora, podemos chamar o nosso metodo enviarEmail() **/
		this.enviarEmail("testes.algaworks.sidarta@gmail.com", 
				emails, "Lançamentos vencidos", 
				"mail/aviso-lancamentos-vencidos", 
				variaveis);
		/** Agora, podemos voltar em LancamentoService.avisarSobreLancamentosVencidos() para concluir a definicao do metodo. 
		 * Ver LancamentoService.avisarSobreLancamentosVencidos() **/
	}
	
	/**
	 * Aula 22.19. Processando o Template e Enviando o E-mail.

		Nesta aula, nos vamos configurar o Thymeleaf para processar o template HTML que nos configuramos na aula passada e fazer o envio de Email com HTML. A primeira coisa para nos realizarmos essa tarefa eh colocar o Thymeleaf no nosso PATH. Entao, vamos abrir o pom.xml e vamos colar 2 dependencias do Thymeleaf:

		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-thymeleaf</artifactId>
		</dependency>		
		<dependency>
			<groupId>org.thymeleaf.extras</groupId>
			<artifactId>thymeleaf-extras-java8time</artifactId>
		</dependency>
		
		A segunda dependencia eh para podermos formatar as datas no Java8. Precisamos dessa dependencia para a funcao de formatacao do Thymeleaf.
		
		Outra coisa que iremos definir no pom.xml sao as versoes do Thymeleaf que iremos utilizar.
		
		<thymeleaf.version>3.0.9.RELEASE</thymeleaf.version>
		<thymeleaf-layout-dialect.version>2.2.2</thymeleaf-layout-dialect.version>
		<thymeleaf-extras-data-attribute.version>2.0.1</thymeleaf-extras-data-attribute.version>
		<thymeleaf-extras-java8time.version>3.0.0.RELEASE</thymeleaf-extras-java8time.version>
				
		Temos duas dependencias e 4 versoes. Por que?
		
		A versao <thymeleaf-extras-java8time.version>3.0.0.RELEASE</thymeleaf-extras-java8time.version> eh para podermos formatar o LocalDate do Java8. As outras 3 versoes eh porque elas ja estao como dependencias do Spring Boot e so queremos mudar a versao.
		
		Agora, nos vamos construir o nosso metodo na Classe Mailer. Sera bastante parecido com o metodo ja existente enviarEmail. Vamos, inclusive, 
		copiar a assinatura.
		Em relacao ao metodo original enviarEmail, iremos alterar o parametro mensagem para template e iremos adicionar um parametro do tipo 
		Map, que serao as propriedades, as variaveis desse template. Nesse nosso caso, eh a variavel lancamentos que tem la no template HTML 
		aviso-lancamentos-vencidos.html que criamos na aula passada.
	 */
	public void enviarEmail(String remetente, 
			List<String> destinatarios, String assunto, String template, 
			Map<String, Object> variaveis) {
		/** Antes de implementarmos o metodo, devemos injetar uma variavel de instancia TemplateEngine do Thymeleaf. 
		 * Nos iremos utiliza-la para processar o template HTML. **/
		
		/** Primeiramente, iremos instanciar uma variavel do tipo Context do Thymeleaf que vai ser o contexto do template onde iremos
		 * colocar as variaveis. Vamos passar, no Construtor, um Locale. 
		 * **/
		Context context = new Context(new Locale("pt", "BR"));
		
		/** Agora, iremos passar as variaveis para o contexto utilizando Lambda. Definimos e -> como entrada (item do mapa) **/
		variaveis.entrySet().forEach(
				e -> context.setVariable(e.getKey(), e.getValue()));
		
		/** Agora, iremos processar o HTML e receber de volta um HTML processado, que sera a nossa mensagem.
		 * Nos fazemos isso atraves da nossa variavel de instancia thymeleaf, com o metodo process(), passando, como
		 * parametro, o template (recebido por parametro na assinatura deste metodo) e o contexto que acabamos de criar. **/
		String mensagem = this.thymeleaf.process(template, context);
		/** Depois de processar, ja podemos chamar o metodo enviarEmail, o qual ja implementamos em aula anterior. **/
		this.enviarEmail(remetente, destinatarios, assunto, mensagem);
		/** Para testarmos o envio de e-mail, vamos utilizar, novamente, o metodo teste(). **/
	}
	
	/** Para o envio de Email, nos precisamos definir um Remetente, uma Lista de Destinatarios, o Assunto e a Mensagem.
	 *  **/
	public void enviarEmail(String remetente, 
			List<String> destinatarios, String assunto, String mensagem) {
		try {
			/** Agora, nos vamos configurar a mensagem de Email. Para isso, nos vamos utilizar a Classe javax.mail.internet.MimeMessage. 
			 * 		Vamos utilizar mailSender para criar uma instancia dessa classe. **/
			MimeMessage mimeMessage = mailSender.createMimeMessage();
			/** Agora, nos vamos utilizar outra classe, que eh uma classe de ajuda que se chama MimeMessageHelper para configurarmos
			 *  o Remetente, o Destinatario e algumas outras configuracoes. 
			 *  Vamos criar uma instancia de MimeMessageHelper, passando dois parametros: mimeMessage e a segunda eh a codificacao de
			 *  caracteres que iremos utilizar, que vai ser a UTF-8 **/
			MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "UTF-8");
			/** A partir da Classe MimeMessageHelper, nos vamos configurar Remetente **/
			helper.setFrom(remetente);
			/** Destinatario. Vamos utilizar a sobrecarga do metodo setTo() passando um array de String. **/
			helper.setTo(destinatarios.toArray(new String[destinatarios.size()]));
			/** Assunto **/
			helper.setSubject(assunto);
			/** Mensagem. O segundo parametro do metodo setText() indica se eh HTML ou nao. **/
			helper.setText(mensagem, true);
			/** Agora faremos, de fato, o envio do Email. **/
			mailSender.send(mimeMessage);
			
			/** Para testarmos, agora, nos vamos utilizar um Recurso do Spring, que eh: 
			 * Escutar um Evento que o proprio Spring lanca para quando a Aplicacao estiver pronta para ser utilizada, para atender Requisicoes.
			 * No nosso caso, nos vamos escutar esse Evento dizendo que a Aplicacao ja esta pronta, ela ja foi iniciada e, quando esse Evento for
			 * disparado, a gente escuta e faz o teste do envio de Email.
			 * Para tanto, vamos definir um novo metodo chamado teste().
			 *   **/
		} catch (MessagingException e) {
			/** Vamos re-lancar a Excecao. **/
			throw new RuntimeException("Problemas com o envio de e-mail!", e);
		}
	}
}
