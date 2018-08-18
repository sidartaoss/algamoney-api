package com.example.algamoney.api.service;

import java.io.InputStream;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.example.algamoney.api.dto.LancamentoEstatisticaPessoa;
import com.example.algamoney.api.mail.Mailer;
import com.example.algamoney.api.model.Lancamento;
import com.example.algamoney.api.model.Pessoa;
import com.example.algamoney.api.model.Usuario;
import com.example.algamoney.api.repository.LancamentoRepository;
import com.example.algamoney.api.repository.PessoaRepository;
import com.example.algamoney.api.repository.UsuarioRepository;
import com.example.algamoney.api.service.exception.PessoaInexistenteOuInativaException;
import com.example.algamoney.api.storage.S3;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

@Service
public class LancamentoService {

	/** 
	 * Aula 22.22. Incluindo Logs no Agendamento do Envio de Email.

	 * Ainda na nossa Classe LancamentoService, uma coisa que queremos fazer eh o seguinte: quando nos temos agendamentos rodando no plano 
	 * de fundo da nossa Aplicacao, como no caso do metodo avisarSobreLancamentosVencidos(), eh legal a gente colocar logs dentro desse 
	 * metodo para a gente conseguir rastrear caso tenha essa necessidade, porque como esse metodo nao interage com o Usuario diretamente, 
	 * entao, se estiver acontecendo algum problema, principalmente problema de regra de negocio, coisas do tipo, fica mais dificil rastrear se 
	 * nao tivermos logs para a gente poder fazer uma consulta caso exista uma desconfianca de que o funcionamento dele nao esta do jeito que 
	 * queremos. 

	 * Entao, vamos colocar logs no metodo avisarSobreLancamentosVencidos(). Para colocar logs no metodo, vamos criar uma variavel de 
	 * instancia de Log, a partir da classe org.slf4j.Logger. **/
	private static final Logger LOGGER = LoggerFactory.getLogger(LancamentoService.class);
	
	/** Aula 22.21. Agendando o envio de e-mail. 
	 * Definicao da constante DESTINATARIOS com a Permissao que os Usuarios precisarao ter para o recebimento de Email. **/
	private static final String DESTINATARIOS = "ROLE_PESQUISAR_LANCAMENTO";
	
	@Autowired
	private LancamentoRepository lancamentoRepository;

	@Autowired
	private PessoaRepository pessoaRepository;
	
	/** Aula 22.21. Agendando o envio de e-mail. **/
	@Autowired
	private UsuarioRepository usuarioRepository;
	
	/** Aula 22.21. Agendando o envio de e-mail. **/
	@Autowired
	private Mailer mailer;
	
	/**
	 * Aula 22.34. Anexando Arquivo no Lancamento
	 * 6. Vamos injetar uma instancia da classe S3 para podermos salvar o arquivo permanentemente. Ate este 
	 * momento seria um arquivo temporario, mas, no momento em que formos juntar o arquivo com o 
	 * Lancamento, ele vai virar um anexo e vai ser tornar tambem um arquivo permanente.
	 * 
	 * 7. Agora, vamos ajustar o metodo salvar(). 
	 * **/
	@Autowired
	private S3 s3;
	
	/**
	 * Aula 22.13. Gerando os Bytes do Relatorio

	 * Nesta aula, vamos gerar os bytes do nosso relatorio. Vamos comecar a implementar o metodo que vai ler o arquivo do nosso Relatorio, passar os dados para ele, 
	 * gerar os bytes para a gente poder devolver isso nas respostas das nossas Requisicoes. Mas, antes de comecar a implementar o metodo em si, nos vamos clicar com o botao 
	 * direito no Projeto da API, no STS, vamos criar uma nova pasta e chamar de docs/relatorios. O que vamos colar na pasta docs/relatorios? Nos vamos ir no Jaspersoft e 
	 * vamos copiar o arquivo lancamentos-por-pessoa.jrxml. Botao direito em lancamentos-por-pessoa.jrxml, selecionar a opcao Copy, voltar no STS, CTRL+V na pasta docs/relatorios. 
	 * Isso eh apenas para guardarmos o arquivo jrxml como referencia dentro do nosso proprio Projeto da API.

	 * Agora, nos vamos, no STS, na pasta resources e vamos criar uma pasta chamada relatorios. E, dentro dessa pasta, nos vamos colar o Relatorio compilado. 
	 * Vamos voltar no Jaspersoft e vamos clicar no botao Compile Report. Foi gerado o arquivo lancamentos-por-pessoa.jasper. Clicar com o Botao direito no arquivo 
	 * lancamentos-por-pessoa.jasper, Copy. Vamos voltar no STS e vamos colar dentro da pasta resources/relatorios. Sendo assim, o arquivo lancamentos-por-pessoa.jasper 
	 * esta dentro da pasta resources/relatorios. E, tambem, nos criarmos a pasta docs/relatorios somente para guardarmos o arquivo lancamentos-por-pessoa.jrxml como referencia, 
	 * caso comitemos em um repositorio e, depois, para utilizarmos esse arquivo para podermos fazer as alteracoes se forem necessarias dentro do Jaspersoft. 
	 * O arquivo que iremos ler no Java eh o lancamentos-por-pessoa.jasper.

	 * Vamos, agora, criar o nosso metodo dentro da pasta service, no Servico de Lancamento, ou seja, Classe LancamentoService. Vamos criar o metodo que chamaremos de 
	 * relatorioPorPessoa(). Esse metodo vai devolver um array de byte, passando, como parametro, LocalDate inicio e fim, que sao os parametros que teremos que passar para a 
	 * nossa Consulta.
	 * 
	 * Este metodo:
	 * Faz a pesquisa dos dados.
	 * A definicao dos parametros sao os parametros que vamos precisar passar para o Relatorio.
	 * Le o arquivo que esta na pasta src/main/resources/relatorios.
	 * Usamos o metodo fillReport() para poder gerar o JasperPrint. A partir desse JasperPrint, atraves do metodo exportReportToPdf(), geramos os
	 * bytes para serem retornados pelo metodo.
	 * 
	 * @throws JRException 
	 */
	public byte[] relatorioPorPessoa(LocalDate inicio, LocalDate fim) throws JRException {
		/** A primeira coisa que vamos fazer eh declarar uma Lista de LancamentoEstatisticaPessoa, nomeada de dados, que sao os dados que nos iremos enviar para o nosso
		 * relatorio. Os dados serao obtidos do nosso Repositorio.
		 * **/
		List<LancamentoEstatisticaPessoa> dados =  this.lancamentoRepository.porPessoa(inicio, fim);
		
		/** Agora, nos vamos configurar um Mapa com os parametros que nos queremos passar para o nosso Relatorio. O nosso Relatorio recebe Data Inicio e Data Fim. 
		 * Nos iremos, atraves de um Map, passar esses parametros para o Relatorio. **/
		Map<String, Object> parametros = new HashMap<>();
		/** Agora, iremos popular. Precisamos criar um java.util.Date a partir do LocalDate. Como fazer? Vamos utilizar java.sql.Date.valueOf()
		 * Como o tipo java.sql.Date estende de java.util.Date, entao nao vai ter problema.   **/
		parametros.put("DT_INICIO", java.sql.Date.valueOf(inicio));
		parametros.put("DT_FIM", java.sql.Date.valueOf(fim));
		/** O parametro REPORT_LOCALE eh nativo do Jaspersoft. Nesse parametro, vamos passar o Locale pt-BR. 
		 * Por que passar esses parametro? O nosso Campo Total, no Relatorio, esta sendo formatado.
		 * Entao, se nao passarmos aqui que queremos o padrao Portugues Brasileiro, o Relatorio vai formatar no padrao norte-americano.  **/
		parametros.put("REPORT_LOCALE", new Locale("pt", "BR"));
		
		/** Agora, nos vamos ler o nosso arquivo.
		 * Iremos criar um InputStream baseado no nosso arquivo de extensao .jasper. **/
		InputStream inputStream = this.getClass().getResourceAsStream(
				"/relatorios/lancamentos-por-pessoa.jasper");
		
		/** Depois de pegar o InputStream, precisamos utilizar Classes do proprio Jasper. Para tanto, precisamos importa-las. 
		 * Vamos abrir o nosso pom.xml e vamos colar as dependencias. Ver pom.xml. 
		 * 		
		<dependency>
		    <groupId>net.sf.jasperreports</groupId>
		    <artifactId>jasperreports</artifactId>
		    <version>6.5.1</version>
		</dependency>
		
		<dependency>
			<groupId>net.sf.jasperreports</groupId>
			<artifactId>jasperreports-functions</artifactId>
			<version>6.5.1</version>
		</dependency>
		
		<dependency>
			<groupId>net.sf.jasperreports</groupId>
			<artifactId>jasperreports-fonts</artifactId>
			<version>6.0.0</version>
		</dependency>
		
	* Apos o STS baixar as dependencias para as Classes que iremos precisar, vamos continuar a alteracao.
	**/
		/** 
		 * JasperPrint eh um documento orientado a pagina (page-oriented document) que pode ser visualizado, impresso ou exportado.
		 * 
		 * Vamos chamar o metodo fillReport(), passando, como parametro, o Relatorio, ou seja, o InputStream do Relatorio,
		 * o segundo parametro sao os parametros do Map e o terceiro parametro sao os nossos dados. 
		 * Nos passamos os nossos dados atraves de uma Classe que se chama JRBeanCollectionDataSource. 
		 * Para o metodo fillReport(), vamos definir throws JRException na assinatura deste metodo. Quem chamar este metodo, vai ter que
		 * tratar ou relancar essa Excecao. **/
		JasperPrint jasperPrint = JasperFillManager.fillReport(inputStream, parametros,
			new JRBeanCollectionDataSource(dados));
		
	/** Para retornar, vamos utilizar JasperExportManager, utilizando o metodo exportReportToPdf() e passando, como parametro,
	 * 		o jasperPrint. Dessa forma, nos retornamos os bytes do nosso Relatorio para que, posteriormente, o nosso Recurso retorne esses
	 * 		bytes na Requisicao que for feita para ele. 
	 * 
	 * Na proxima aula, iremos construir o Recurso no Controlador.
	 * **/
		return JasperExportManager.exportReportToPdf(jasperPrint);
	}

	public Lancamento salvar(Lancamento lancamento) {
		this.validarPessoa(lancamento);
		/**
		 * Aula 22.34. Anexando Arquivo no Lancamento
		 * 7.1. Apos validar pessoa, vamos definir um if. Vamos utilizar a Classe StringUtils do proprio Spring.
		 * Se tem texto no anexo, quer dizer que tem alguem tentando anexar um arquivo em Lancamento.
		 * O que vamos fazer, entao? Nos vamos la no S3 e nos vamos salvar esse anexo.
		 * Agora, nos vamos criar o metodo salvar() na classe S3. Ver classe S3, metodo salvar().
		 * */
		if (StringUtils.hasText(lancamento.getAnexo())) {
			this.s3.salvar(lancamento.getAnexo());
		}
		return this.lancamentoRepository.save(lancamento);
	}

	public Lancamento atualizar(Long codigo, Lancamento lancamento) {
		Lancamento lancamentoSalvo = this.buscarLancamentoExistente(codigo);
		/** Se estiver trocando a Pessoa do Lancamento, vai validar a Pessoa. **/
		if (!lancamento.getPessoa().equals(lancamentoSalvo.getPessoa())) {
			this.validarPessoa(lancamento);
		}
		
	/**
	 * Aula 22.35. Atualizando e Removendo Anexo
		* 1. Como comentamos na aula passada, nesta aula o que nos iremos fazer eh alterar o 
		* metodo atualizar() da classe LancamentoService para ele tambem levar em consideracao 
		* a possibilidade de se incluir um anexo na nossa entidade Lancamento no nosso registro de 
		* Lancamento. E ele vai ter, aqui, alguma diferenca do metodo salvar(), que eh o 
		* seguinte: a gente vai colocar, aqui, dois ifs, basicamente. Um que vai verificar se a 
		* propriedade anexo de lancamento sendo passado como parametro 
		* esta vazia e o lancamento que esta na base 
		* de dados (lancamentoSalvo) tem um anexo? Entao, o que a gente vai fazer eh 
		* remover esse anexo que esta salvo. Porque, se, no lancamento que esta sendo passado 
		* como parametro, a propriedade anexo esta vazia, 
		* a gente vai entender como uma intencao de remocao desse 
		* anexo, entao a gente vai remover. 
		* No entanto, se lancamento sendo passado como parametro tiver um anexo e ele 
		* nao for igual ao anexo que ja esta salvo, o que a gente vai fazer eh substituir o anexo.
		* Entao, nesta aula, nos vamos cobrir esses dois casos com esses dois ifs que iremos
		* fazer agora com a ajuda, inclusive, da classe org.springframeworkString.util.StringUtils 
		* do Spring, onde a gente primeiramente vai ver se lancamento que esta vindo do parametro
		* tem anexo E o atributo anexo do lancamentoSalvo tem algum texto, porque, se ele tiver,
		* nos iremos remover.​
	 * **/
		if (StringUtils.isEmpty(lancamento.getAnexo())
				&& StringUtils.hasText(lancamentoSalvo.getAnexo())) {
			 /** Aula 22.35. Atualizando e Removendo Anexo 
			  * 2. Vamos utilizar, entao, s3.remover(), passando lancamentoSalvo.getAnexo().
			  * Nos iremos criar o metodo remover() agora na classe S3.
			  * Ver classe S3.java.
			  * **/
			s3.remover(lancamentoSalvo.getAnexo());
		} else if (StringUtils.hasText(lancamento.getAnexo())
					&& !lancamento.getAnexo().equals(lancamentoSalvo.getAnexo())) {
			/** Aula 22.35. Atualizando e Removendo Anexo
			 * 5. Vamos fazer, aqui, um else if e nos vamos fazer aquela outra
			 * verificacao: StringUtils.hasText(), se o lancamento que esta
			 * vindo no parametro tem texto, se tem um arquivo para ser anexado
			 * no lancamento esta para ser atualizado E esse arquivo que esta para ser atualizado
			 * que esta em lancamento.getAnexo() nao for igual ao que esta no banco de dados:
			 * lancamentoSalvo.getAnexo(), entao ira substituir.
			 * 6. Vamos chamar o metodo s3.substituir(), passando o antigo: 
			 * lancamentoSalvo.getAnexo() e o novo para substituir: lancamento.getAnexo().
			 * Esse metodo a gente vai criar agora na classe S3.
			 * Ver classe S3.
			 */
			s3.substituir(lancamentoSalvo.getAnexo(), lancamento.getAnexo());
		}
		/** Aula 22.35. Atualizando e Removendo Anexo
		 * 9. Com isso, ja eh possivel fazermos um teste. Nos vamos abrir o Postman, agora.
		 * A primeira coisa que faremos no Postman eh uma Requisicao para o lancamento de
		 * identificador 1:
		 * GET, http://localhost:8080/lancamentos/1
		 * 9.1. Vamos pegar o JSON de reposta dele:
		 * {
			    "codigo": 1,
			    "descricao": "Salario mensal",
			    "dataVencimento": "2018-06-10",
			    "dataPagamento": null,
			    "valor": 6500,
			    "observacao": "Distribuicao de lucros",
			    "tipo": "RECEITA",
			    "categoria": {
			        "codigo": 1,
			        "nome": "Lazer"
			    },
			    "pessoa": {
			        "codigo": 1,
			        "nome": "Joao Silva",
			        "endereco": {
			            "logradouro": "Rua do Abacaxi",
			            "numero": "10",
			            "complemento": null,
			            "bairro": "Brasil",
			            "cep": "38.400-12",
			            "cidade": "Uberlandia",
			            "estado": "MG"
			        },
			        "ativo": true
			    },
			    "anexo": null,
			    "urlAnexo": null
			}
			9.2. Vamos criar uma nova Requisicao de atualizacao:
			* PUT, http://localhost:8080/lancamentos/1, porque nos vamos
			* atualizar o registro de identificador 1.
			* Body: raw, Text: JSON (application/json)
			* Colar o JSON que acabamos de Copiar.
			* {
			    "codigo": 1,
			    "descricao": "Salario mensal",
			    "dataVencimento": "2018-06-10",
			    "dataPagamento": null,
			    "valor": 6500,
			    "observacao": "Distribuicao de lucros",
			    "tipo": "RECEITA",
			    "categoria": {
			        "codigo": 1
			    },
			    "pessoa": {
			        "codigo": 1
			    },
			    "anexo": null
			}
			* Remover o atributo urlAnexo do JSON.
			* Remover os dados do atributo pessoa do JSON, so eh necessario o codigo da
			* pessoa.
			* Remover os dados do atributo categoria, so eh necessario o codigo da categoria.
			* Agora, nos precisamos de um anexo.
			* Entao, nos vamos da Requisicao de anexo e nos vamos anexar um arquivo, entao:
			* POST, http://localhost:8080/lancamentos/anexo,
			* Body: form-data; Key: anexo, File: Choose Files: CodeConventions.pdf
			* Clicar em Send
			* Retornou Status: 200 OK
			* No Corpo da resposta (Body):
			* {
				    "nome": "d9eae6c2-b4c6-449f-b413-94110c0cebe7_CodeConventions.pdf",
				    "url": "\\sidarta.silva-algamoney-arquivos.s3.amazonaws.com/d9eae6c2-b4c6-449f-b413-94110c0cebe7_CodeConventions.pdf"
				}
			* Copiar o nome do anexo criado la no S3: d9eae6c2-b4c6-449f-b413-94110c0cebe7_CodeConventions.pdf
			* Voltar na Requisicao de Atualizacao de Lancamento. No atributo anexo 
			* do JSON, colar o nome do anexo que foi copiado:
			* {
				    "codigo": 1,
				    "descricao": "Salario mensal",
				    "dataVencimento": "2018-06-10",
				    "dataPagamento": null,
				    "valor": 6500,
				    "observacao": "Distribuicao de lucros",
				    "tipo": "RECEITA",
				    "categoria": {
				        "codigo": 1
				    },
				    "pessoa": {
				        "codigo": 1
				    },
				    "anexo": "d9eae6c2-b4c6-449f-b413-94110c0cebe7_CodeConventions.pdf"
				}
			* Agora, nos vamos enviar a Requisicao.
			* Retornou Status: 200 OK, ou seja, sucesso.
			* No Corpo da resposta:
			* {
				    "codigo": 1,
				    "descricao": "Salario mensal",
				    "dataVencimento": "2018-06-10",
				    "dataPagamento": null,
				    "valor": 6500,
				    "observacao": "Distribuicao de lucros",
				    "tipo": "RECEITA",
				    "categoria": {
				        "codigo": 1,
				        "nome": null
				    },
				    "pessoa": {
				        "codigo": 1,
				        "nome": null,
				        "endereco": null,
				        "ativo": null
				    },
				    "anexo": "d9eae6c2-b4c6-449f-b413-94110c0cebe7_CodeConventions.pdf",
				    "urlAnexo": null
				}
			* Para conferirmos, vem em:
			* GET, http://localhost:8080/lancamentos/1 se a propriedade anexo vai voltar
			* preenchida. Ok, voltou preenchida:
			* {
			    "codigo": 1,
			    "descricao": "Salario mensal",
			    "dataVencimento": "2018-06-10",
			    "dataPagamento": null,
			    "valor": 6500,
			    "observacao": "Distribuicao de lucros",
			    "tipo": "RECEITA",
			    "categoria": {
			        "codigo": 1,
			        "nome": "Lazer"
			    },
			    "pessoa": {
			        "codigo": 1,
			        "nome": "Joao Silva",
			        "endereco": {
			            "logradouro": "Rua do Abacaxi",
			            "numero": "10",
			            "complemento": null,
			            "bairro": "Brasil",
			            "cep": "38.400-12",
			            "cidade": "Uberlandia",
			            "estado": "MG"
			        },
			        "ativo": true
			    },
			    "anexo": "d9eae6c2-b4c6-449f-b413-94110c0cebe7_CodeConventions.pdf",
			    "urlAnexo": null
			}
			* Agora, vamos conferir no Browser, no Servico S3 do AWS Console, no
			* nosso bucket sidarta.silva-algamoney-arquivos. Okay, o arquivo
			* d9eae6c2-b4c6-449f-b413-94110c0cebe7_CodeConventions.pdf esta
			* la (Aba Overview) e nao pode estar com data de expiracao. Clicando no link 
			* do arquivo, verificamos que esta sem data de expiracao.
			* Ou seja, nos conseguimos anexar um arquivo em um lancamento que ja 
			* existia.
			*  
			* 10. Outro teste que nos faremos eh remover esse anexo. Vamos abrir o 
			* Postman, abrir a Requisicao de PUT, que atualiza o nosso Lancamento
			* de identificador 1 e, da mesma forma que nos incluimos o anexo, nos, agora
			* iremos remove-lo, ou seja, vamos setar a propriedade anexo para null:
			* {
				    "codigo": 1,
				    "descricao": "Salario mensal",
				    "dataVencimento": "2018-06-10",
				    "dataPagamento": null,
				    "valor": 6500,
				    "observacao": "Distribuicao de lucros",
				    "tipo": "RECEITA",
				    "categoria": {
				        "codigo": 1
				    },
				    "pessoa": {
				        "codigo": 1
				    },
				    "anexo": null
				}
			* A propriedade vai, entao, ser nula e iremos enviar essa Requisicao agora:
			* Retornou Status: 200 OK, ou seja, sucesso, anexo esta nulo:
			* {
				    "codigo": 1,
				    "descricao": "Salario mensal",
				    "dataVencimento": "2018-06-10",
				    "dataPagamento": null,
				    "valor": 6500,
				    "observacao": "Distribuicao de lucros",
				    "tipo": "RECEITA",
				    "categoria": {
				        "codigo": 1,
				        "nome": null
				    },
				    "pessoa": {
				        "codigo": 1,
				        "nome": null,
				        "endereco": null,
				        "ativo": null
				    },
				    "anexo": null,
				    "urlAnexo": null
				}
			* Para conferir se o resultado esta correto, fazer uma nova Requisicao GET
			* para o identificador 1. Retornou, no Corpo da Resposta, a propriedade
			* anexo como nula.
			* Vamos, agora, olhar la no Browser, o Servico S3 no Console da AWS  
			* para sabermos se o arquivo 
			* d9eae6c2-b4c6-449f-b413-94110c0cebe7_CodeConventions.pdf 
			* foi removido.
			* Voltar no bucket e clicar no link-imagem de atualizar no canto direito da tela.
			* Verificamos que o arquivo foi removido. Entao, esta funcionando.
			* Tanto nos conseguimos atualizar o Lancamento e anexar nesse Lancamento
			* um arquivo novo como tambem nos removemos o anexo do Lancamento e 
			* ele foi removido do Servico S3 no AWS da Amazon.
			* Fim da Aula 22.35. Atualizando e Removendo Anexo.
			* 
		 */
		BeanUtils.copyProperties(lancamento, lancamentoSalvo, "codigo");
		return this.lancamentoRepository.save(lancamentoSalvo);
	}
	
	/**
	 *
	 * Aula 22.15. Criando um Agendamento de Tarefa (Scheduler)

	 * Nesta aula, nos vamos trabalhar com Agendamento de Tarefa. Eh um recurso que nos precisamos muito dentro das nossas aplicacoes, 
	 * que sao, basicamente, aqueles servicos que ficam rodando em background na sua Aplicacao para, por exemplo, fazer uma verificacao no 
	 * seu banco de dados, alguma validacao no banco de dados, para fazer envio de e-mails de alerta sobre alguma coisa. Tem varias funcoes em 
	 * que podemos utilizar o Agendamento de Tarefas, que, basicamente, eh um Servico, eh um metodo que vai rodar no plano de fundo da 
	 * Aplicacao de tempos em tempos.

	 * O bacana eh que o Spring ajuda muito a gente nesse sentido. Ele tem alguns recursos que facilitam muito a nossa vida. Vamos apresentar 
	 * como que funciona e vamos mostrar duas formas de configurar um agendamento: uma que eh com Delay Fixo e a outra eh utilizando o 
	 * Padrao Cron. 

	 * Vamos abrir o STS e vamos abrir a Classe LancamentoService. Vamos fazer o agendamento nessa Classe porque, no final desta aula, nos 
	 * ja vamos deixar o agendamento de servico configurado para a gente poder utilizar quando a gente quiser enviar um e-mail de alerta 
	 * sobre os lancamentos vencidos: os lancamentos que tem no nosso Sistema e que estao com a Data de Pagamento vencida, ou seja, que esta 
	 * vencido e ainda nao foi pago ou recebido. Entao, ja vamos deixar preparado. Mas o envio de e-mail em si a gente vai fazer na 
	 * proxima aula. 

	 * Vamos criar um metodo simples, chamado de avisarSobreLancamentosVencidos().
	 */
	/** @Scheduled(fixedDelay = 1000 * 5) 
	@Scheduled(fixedDelay = 1000 * 2)
	public void avisarSobreLancamentosVencidos() {
		/** Como que fazemos o agendamento com o Spring? 
		 * 		Primeiramente, precisamos anotar o metodo com @Scheduled. Temos algumas opcoes nessa anotacao.
		 * 		fixedDelay: 5 segundos. Como vai funcionar a configuracao com Delay Fixo?
		 * 		Logo que iniciarmos a Aplicacao, uma chamada para este metodo sera feita, independente do horario, porque foi definido
		 * 		a anotacao @Scheduled. O que vai acontecer? Depois que este metodo terminar a execucao, supondo que seja um metodo que 
		 * 		demore muito tempo, que faca uma validacao que demande muito processamento, depois que for encerrado, ai entao vai comecar
		 * 		a contar o tempo definido em fixedDelay para a proxima execucao. Por que isso eh bom? Quando que isso eh vantajoso?
		 * 		Supondo que tenhamos, na nossa Aplicacao, um metodo que vai precisar rodar em plano de fundo e que tenha um processamento
		 * 		que varia muito de tempo, às vezes eh pouco tempo e às vezes eh muito tempo e tambem nao podemos ficar esperando muito tempo
		 * 		para poder executar esse servico. Qual eh o problema que podemos ter nesse tipo de situacao?
		 * 		
		 * 		Vamos supor que coloquemos a rodar de 5 em 5 segundos, mas de acordo com o relogio. Por exemplo, supondo que estejamos no 
		 * 		segundo zero de um minuto qualquer. Ai mandamos executar o metodo. Se este metodo demorar menos de 5 segundos, no momento
		 * 		que estiver no segundo 5, a chamada anterior vai ter sido encerrada e, no segundo 5, comeca uma proxima execucao desse mesmo
		 * 		metodo. So que se, no segundo 0, executamos o metodo que demanda muito processamento, quer dizer, pode ou nao demandar 
		 * 		muito processamento, eh variavel. Vamos supor que essa execucao do segundo 0 demore mais que 5 segundos, 
		 * 		vamos supor 7 segundos. O que vai acontecer? Enquanto esta sendo executada, no momento que chegar no segundo 5, vai iniciar
		 * 		outra execucao enquanto a anterior ainda nao se encerrou. Isso, na maioria dos casos, pode ser um problema. Por que? Essa 
		 * 		execucao que iniciou no segundo 0 e terminou no 7, vai se juntar a execucao que iniciou no segundo 5. E, se essa execucao no
		 * 		segundo 5 tambem tiver, naquele momento, demandando muito processamento, nao vai terminar ate o segundo 10. Entao, ela
		 * 		vai terminar la pelo segundo 14. E as coisas comecam a sobrepor-se e podemos ter problemas de performance na Aplicacao.
		 * 		Qual a solucao disso? Usar o Agendamento com Delay Fixo.
		 * 
		 *  	Supondo que a execucao deste metodo iniciou-se no segundo 0 de um minuto qualquer. O que vai acontecer? Esses 5 segundos
		 *  	definidos em fixedDelay so vao comecar a contar novamente quando a chamada para este metodo avisarSobreLancamentosVencidos()
		 *  	se encerrar. Entao, se demorou 3 segundos, vai comecar a contar 5 segundos a partir do segundo 3. Ai ele vai ser executado 
		 *  	de novo somente no segundo 8. Se demorou 7 segundos, entao vai comecar a contar a partir do segundo 7 e vamos ter uma proxima
		 *  	execucao somente no segundo 12. Por isso que isso eh bacana porque nao deixa as execucoes se sobreporem.
		 *  
		 *     Vamos definir um exemplo agora. Vamos utilizar o proprio 'sout' (abreviacao de System.out.println()), nao precisamos mais do 
		 *     que isso, porque eh so um exemplo. Vamos definir "Metodo sendo executado..." so para que vejamos a execucao disso.
		 *     
		 *     Vamos definir menos tempo tambem em fixedDelay: 2 segundos apenas para que possamos ver bastante sendo o metodo sendo
		 *     executado.
		 *     
		 *     Agora, podemos iniciar a nossa Aplicacao.
		 *     
		 *     Ainda precisamos fazer mais uma coisa: a anotacao @Scheculed(fixedDelay = 1000 * 2) eh a unica coisa que precisamos definir
		 *     para agendar. So que nos precisamos habilitar esse agendamento. Para tanto, devemos criar uma Classe de Configuracao.
		 *     Inclusive poderiamos utilizar as Classes de Configuracao AuthrorizationServerConfig, BasicSecurityConfig, ResourceServerConfig,
		 *     mas nao faz sentido definir o Scheduler nessas Classes. Entao, vamos criar outra Classe especifica para o Scheduler.
		 *     
		 *     Vamos chama-la de WebConfig e vamos cria-la dentro do pacote config.
		 *     Ver Classe WebConfig.
		 *     
		 * 
		System.out.println("Metodo sendo executado...");
	}
	**/
	
	/**
	 *
	 *  Aula 22.15. Criando um Agendamento de Tarefa (Scheduler)
	 *  
	 *  Agora vamos estudar o segundo padrao, que eh o Cron.
	 *  
	 *   Para que que serve esse Padrao Cron? Muitas vezes a gente quer que o Servico seja executado em determinado horario fixo.
	 *   Inclusive, esse vai ser o nosso exemplo, quando nos referimos ao agendamento de envio de e-mail, esse vai ser o nosso caso.
	 *   Nos temos, no padrao Cron, uma forma de especificar horarios para que o nosso metodo seja executado. Conseguimos, por exemplo,
	 *   determinar que um metodo seja executado todo o dia primeiro de cada mes ou conseguimos fazer um agendamento para executar
	 *   o metodo toda o dia a meia-noite. Conseguimos chegar ate ao nivel de segundo no detalhe. Por exemplo, se quisermos que todo o dia
	 *   5 de cada mes, a meia-noite, quinze minutos e quinze segundos, se tivermos esse horario, se esse horario for importante, nos conseguimos
	 *   configurar no Cron.
	 *   Para mostrarmos o agendamento de tarefa em um horario especifico, nos vamos agendar para as 20 horas e 29 minutos. Como fazemos para
	 *   executar as 20 horas e 29 minutos? 
	 *   Definimos, primeiramente, o segundo 0, o minuto 29, a hora 20 e mais 3 asteriscos. Se quisessemos que houvessem 60 execucoes dentro
	 *   do minuto 29, nos nao especificariamos o segundo, definiriamos * no lugar do segundo, conforme:
	 *   cron = "* 29 20 * * *"
	 *   
	 *   No nosso caso, sera definido que o agendamento sera para todos os dias as 6 horas da manha, conforme:
	 *   cron = "0 0 6 * * *"
	 *   Nesse horario, o metodo vai verficar na base se existem lancamentos vencidos e ira enviar um e-mail caso tenha algum lancamento
	 *   vencido.
	 *   O que sao os 3 asteriscos? O quarto asterisco (da direita para esquerda) representa o dia do mes. O quinto representa o mes 	e o 
	 *   sexto representa o dia da semana.
	 *   Como eh representar todo dia 5 de cada mes as 6 horas da manha?
	 *   0 0 6 5 * *
	 *   Como eh representar todo o mes de janeiro no dia 5 as 6 horas da manha?
	 *   0 0 6 5 1 *
	 *    
	 *    No nosso caso, o agendamento vai ficar definido como para ser executado todos os dias as 6 horas da manha. 
	 */
	/** 
	 * Aula 22.21. Agendando o envio de e-mail.
	 * 
	 * Nesta aula, nos vamos implementar de fato o nosso metodo avisarSobreLancamentosVencidos que esta anotado com @Scheduled, que esta na classe 
	 * LancamentoService, que foi criado quando estavamos aprendendo sobre a funcionalidade de agendamento de tarefas. Agora, iremos implementa-lo de fato, 
	 * nao apenas para testar a funcionalidade de agendamento.
	 * **/
	 // @Scheduled(cron = "0 0 6 * * *")
	/** 
	 * Aula 22.21. Agendando o envio de e-mail.
	 * So para testar, vamos definir Delay Fixo, porque, com Delay Fixo, logo que a Aplicacao sobe, este metodo eh chamado. Entoa, iremos testar dessa forma:
	 * com Delay Fixo e comentando o Agendamento com Cron. No valor de Delay Fixo, vamos definir um valor grande para que o metodo nao seja chamado vezes 
	 * seguidas. 1000 milissegundos corresponde a 1 segundo X 60 equivale a 1 minuto X 30 equivale a 30 minutos. 
	 * Para testar, basta levantar a Aplicacao. 
	 * Agora que ja testamos, vamos comentar o Delay Fixo e descomentar o Agendamento com Cron. Sendo assim, este metodo vai ser 
	 * disparado todos os dias as 6 horas da manha. **/
	@Scheduled(fixedDelay = 1000 * 60 * 30)
	public void avisarSobreLancamentosVencidos() {
		/** Aula 22.22. Incluindo Logs no Agendamento do Envio de Email. 
		 * 1. O primeiro log que iremos definir eh um log de debug. Geralmente, em logs de debug, como dificilmente esta habilitado,
		 * constumamos fazer uma validacao para saber se o log de debug esta habilitado. Se estiver habilitado, a gente vai logar. 
		 * 8. Antes de testar, ja que utilizamos o log de debug, nos vamos aprender como que faz para Habilitar Logs de Debug que 
		 * partam somente do pacote da nossa Aplicacao. Por exemplo, se a nossa Aplicacao, as nossas classes dentro dos nossos pacotes,
		 * como eh o caso de LancamentoService, logarem com Debug, entao essa informacao vai aparecer no Log. Se uma outra Classe
		 * de um outro Pacote, por exemplo, os pacotes do Spring, que tem muito log de debug la, se eles logarem em nivel de debug,
		 * nao vai aparecer nos logs, nos vamos habilitar somente para os nossos pacotes. Como que a gente faz isso? A primeira coisa eh
		 * copiarmos o pacote da nossa Aplicacao:  com.example.algamoney.api. Vamos abrir application.properties e vamos utilizar uma
		 * propriedade: logging.level. Logo apos as duas palavras logging.level, colar o nome do pacote da nossa Aplicacao:
		 * logging.level.com.example.algamoney.api. Para o valor, definir o nivel de logging desejado, que sera o nivel de Debug.
		 * Ver application.properties.
		 * A partir de agora, os logs definidos neste metodo irao aparecer assim que levantarmos a Aplicacao.
		 * Para testar, utilizar a configuracao de Fixed Delay.
		 *  **/
		 if (LOGGER.isDebugEnabled()) {
			 LOGGER.debug("Preparando envio de "
			 		+ "e-mails de aviso de lançamentos vencidos.");
		}
		 /** System.out.println("Metodo sendo executado..."); **/
		/**
		 * Aula 22.21. Agendando o envio de e-mail.
		 * A primeira coisa que faremos eh buscar a Lista de Lancamentos vencidos. 
		 * **/
		List<Lancamento> vencidos = this.lancamentoRepository
				.findByDataVencimentoLessThanEqualAndDataPagamentoIsNull(LocalDate.now());
		
		/** Aula 22.22. Incluindo Logs no Agendamento do Envio de Email.
		 * 2. Aqui abaixo da pesquisa de lancamentos vencidos, vamos fazer uma verificacao: se a lista de lancamentos vencidos for
		 * vazia, logar uma mensagem de informacao: Sem lancamentos vencidos para aviso. Caso caia nessa condicao, retornar, nao queremos
		 * que continue o processamento. **/
		if (vencidos.isEmpty()) {
			LOGGER.info("Sem lancamentos vencidos para aviso.");
			return;
		}
		
		/** Aula 22.22. Incluindo Logs no Agendamento do Envio de Email. 
		 * 3. Caso tenha passado da condicao acima, vamos logar uma mensagem de informacao: Existem {} lancamentos vencidos. O 
		 * segundo parametro define a quantidade de parametros vencidos a ser substituido em {} **/
		LOGGER.info("Existem {} lancamentos vencidos.", vencidos.size());
		
		
		/** Agora, nos vamos precisar da Lista de Usuarios por Permissao. 
		 * Para passarmos o parametro da permissao, vamos definir uma constante LANCAMENTO_PESQUISA **/
		List<Usuario> destinatarios = this.usuarioRepository
				.findByPermissoesDescricao(DESTINATARIOS);
		
		/** Aula 22.22. Incluindo Logs no Agendamento do Envio de Email. 
		 * 4. Vamos fazer uma verificacao para ver se tem algum destinatario.
		 * Se nao tiver nenhum destinatario, ninguem para receber esse aviso, nos vamos logar com um aviso: Existem lancamentos vencidos, mas
		 * o sistema nao encontrou destinatarios. Caso caia nessa condicao, retornar. **/
		if (destinatarios.isEmpty()) {
			LOGGER.warn("Existem lancamentos vencidos, mas o "
					+ "sistema nao encontrou destinatarios.");
		}
		
		/** Aula 22.22. Incluindo Logs no Agendamento do Envio de Email. 
		 * 5. Se passou da condicao acima, vamos enviar o e-mail.**/
		
		/** Agora, temos definido os lancamentos vencidos e os destinatarios.
		 * Vamos, agora, injetar outra variavel de instancia da classe Mailer para conseguirmos enviar o Email de fato.
		 * Nao queremos utilizar os metodos Mailer.enviarEmail() diretamente. Entao, vamos construir, em Mailer, um metodo
		 * especifico para essa nossa funcionalidade. O nome do metodo sera avisarSobreLancamentosVencidos().
		 * Ver Mailer. **/
		mailer.avisarSobreLancamentosVencidos(vencidos, destinatarios);
		/** Neste momento, ja podemos testar. **/
		
		/** Aula 22.22. Incluindo Logs no Agendamento do Envio de Email. 
		 * 6. Apos enviar o email, vamos definir outro log de informacao: Envio de e-mail de aviso concluido. **/
		LOGGER.info("Envio de e-mail de aviso concluido.");
		/** 7. Agora, ja podemos fazer um teste.  **/
	}
	
	/**
	 * Aula 25.02. Novas Assinaturas do Spring Data JPA

	 * Continuando, entao, nesta aula aqui, o que nos iremos fazer eh a atualizacao dos metodos do Spring Data JPA. Nos veremos que tiveram algumas mudancas e 
	 * a gente vai fazer essas simples correcoes nesta aula.

	 * 1. Vamos comecar pelo pacote .service. A gente teve problemas nas classes LancamentoService e PessoaService. Na classe LancamentoService, no metodo 
	 * validarPessoa(), o metodo findOne(), agora, devolve um Optional. Na verdade, ele recebe um Example e a gente nao esta passando isso, a gente esta 
	 * passando o codigo. A gente teria que substituir isso por findById().
	 * 
	 * 2. O metodo findById(), sim, retorna um Optional. So que, neste caso aqui, a gente pode utilizar o getOne(). Neste caso especifico, a gente pode utilizar o
	 * metodo getOne(), que ele continua recebendo o codigo e continua devolvendo o objeto do tipo Pessoa, nao um Optional de Pessoa. Neste caso aqui especifico,
	 * a gente pode fazer isso:
	 * pessoa = this.pessoaRepository.getOne(lancamento.getPessoa().getCodigo());
	 * 
	 * 3. No caso do metodo buscarLancamentoExistente(), o que a gente vai fazer eh um findById(), devolvendo um Optional:
	 *  Optional<Lancamento> lancamentoSalvo = this.lancamentoRepository.findOne(codigo);
	 *  Ao inves de definir lancamentoSalvo == null, a gente vai definir lancamentoSalvo.isPresent(), so que a gente vai ter que negar:
	 *  if (!lancamentoSalvo.isPresent()) { ... }
	 * 
	 * 4. E retornamos fazendo a chamada para o metodo get() de Optional, 
	 * return lancamentoSalvo.get()
	 * 
	 * 5. Okay, esta classe esta corrigida. Vamos, agora, para PessoaService.java.
	 * Ver PessoaService.java.
	 * 
	 * @param lancamento 
	 */
	private void validarPessoa(Lancamento lancamento) {
		Pessoa pessoa = null;
		if (lancamento.getPessoa().getCodigo() != null) {
			/** pessoa = this.pessoaRepository.findOne(lancamento.getPessoa().getCodigo()); **/
			pessoa = this.pessoaRepository.getOne(lancamento.getPessoa().getCodigo());
		}
		if (pessoa == null || pessoa.isInativo()) {
			throw new PessoaInexistenteOuInativaException();
		}
	}

	private Lancamento buscarLancamentoExistente(Long codigo) {
		Optional<Lancamento> lancamentoSalvo = this.lancamentoRepository.findById(codigo);
		/** if (lancamentoSalvo == null ) { **/
		if (!lancamentoSalvo.isPresent()) {
			throw new IllegalArgumentException();
		}
		return lancamentoSalvo.get();
	}
}
