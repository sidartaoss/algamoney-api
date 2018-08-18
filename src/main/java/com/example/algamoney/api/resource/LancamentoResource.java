package com.example.algamoney.api.resource;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.algamoney.api.dto.Anexo;
import com.example.algamoney.api.dto.LancamentoEstatisticaCategoria;
import com.example.algamoney.api.dto.LancamentoEstatisticaDia;
import com.example.algamoney.api.event.RecursoCriadoEvent;
import com.example.algamoney.api.exceptionhandler.AlgamoneyExceptionHandler.Erro;
import com.example.algamoney.api.model.Lancamento;
import com.example.algamoney.api.repository.LancamentoRepository;
import com.example.algamoney.api.repository.filter.LancamentoFilter;
import com.example.algamoney.api.repository.projection.ResumoLancamento;
import com.example.algamoney.api.service.LancamentoService;
import com.example.algamoney.api.service.exception.PessoaInexistenteOuInativaException;
import com.example.algamoney.api.storage.S3;

import net.sf.jasperreports.engine.JRException;

@RestController
@RequestMapping("/lancamentos")
public class LancamentoResource {

	@Autowired
	private LancamentoRepository lancamentoRepository;

	@Autowired
	private LancamentoService lancamentoService;

	@Autowired
	private ApplicationEventPublisher publisher;

	@Autowired
	private MessageSource messageSource;
	
	/**
	 * Aula 22.33. Enviando arquivos para o S3
	 * Nesta aula, o que nos vamos fazer eh abrir a classe LancamentoResource e nos vamos fazer um teste no metodo que a gente criou na aula passada, 
	 * que eh o metodo que envia o arquivo para o S3.
	 * 
	 * 1. O que nos vamos fazer? Primeiramente, nos vamos injetar uma instancia de S3.
	 * 
	 * 2. Nos vamos ajustar o metodo uploadAnexo(). Para isso, vamos comentar a versao do metodo uploadAnexo da Aula 22.28, criando uma nova versao
	 * do metodo para a aula 22.33.
	 **/
	@Autowired
	private S3 s3;
	
	/**
	 * Aula 22.28. Upload de Arquivos para a API.

	 * Nesta aula, a gente vai comecar a funcionalidade de anexos de Lancamento.

	 * Como que vai funcionar?

	 * Os Usuarios dos clientes da nossa API vao poder fazer o cadastro de um Lancamento e 
	 * tambem enviar um arquivo junto com esse cadastro. Esse arquivo seria, por exemplo, a 
	 * imagem de uma nota fiscal ou uma nota fiscal em PDF, coisas do tipo, que tenham a ver 
	 * com o Lancamento. Entao, a gente vai comecar a construir essa funcionalidade nesta aula, 
	 * onde a gente vai criar a nossa acao la dentro da nossa classe LancamentoResource para 
	 * receber o arquivo que vem da Requisicao. O nosso objetivo, nesta aula, eh simplesmente 
	 * receber o arquivo da requisicao. As tratativas com esse arquivo depois, como que vai 
	 * funcionar, a gente vai construindo ao longo desta aula. 

	 * 1. Entao, nos vamos abrir o STS, vamos abrir LancamentoResource e nos vamos criar um 
	 * novo metodo chamado uploadAnexo(), que vai retornar uma String. Ele vai receber um
	 * parametro, que sera anotado com @RequestParam, do tipo MultipartFile que 
	 * chamaremos de anexo. A classe MultipartFile eh uma classe bacana no Spring MVC
	 * que ja nos entrega, quando nos queremos fazer o upload, os metodos que a gente precisa
	 * para pegar os bytes desse arquivo e salvar onde quer que seja: localmente, no Amazon
	 * S3, como faremos depois, enfim, ja eh uma boa ajuda para nos.
	 * @throws IOException 
	 */
	/** Aula 22.28. Upload de Arquivos para a API. **/
	/** 5. Agora, podemos mapear o metodo com POST, /anexo
	 * 		  A seguranca sera a mesma que para o Cadastro.
	 * 	  
	 * 	   6. Agora, nos podemos abrir o Postman para fazermos um teste:
	 * 		   POST, http://localhost:8080/lancamentos/anexo
	 * 		   Body: form-data. Key: anexo. Value: File: Escolher um arquivo pdf.
	 * 		   N.T.: Key, em Body: form-data, deve ter o mesmo valor definido que o parametro
	 * 				   da anotacao @RequestParam: @RequestParam("anexo").  Nos podemos
	 * 			       omitir da anotacao porque o parametro ja se chama anexo.
	 * 		
	 * 		7. Okay, o nosso Upload esta funcionando.
	 * 			Fim da Aula 22.28. Upload de Arquivos para a API.
	 *  **
	@PostMapping("/anexo")
	@PreAuthorize("hasAuthority('ROLE_CADASTRAR_LANCAMENTO') and #oauth2.hasScope('write')")
	public String uploadAnexo(@RequestParam MultipartFile anexo) throws IOException {
		/** Aula 22.28. Upload de Arquivos para a API. **
		/** 2. Como dissemos, o objetivo desta aula eh somente recebermos o arquivo, entao
		 * para garantirmos e termos certeza de que recebemos o arquivo, nos vamos simplesmente
		 * declarar um OutputStream. No argumento do construtor de FileOutputStream, 
		 * vamos definir o caminho para um arquivo.
		 * MultipartFile.getOriginalFilename() vai nos retornar o nome que o Usuario
		 * Final que esta querendo fazer o Upload definiu para o arquivo dele. 
		 * Vamos relancar a excecao de FileOutputStream. **
		OutputStream out = new FileOutputStream(
				"C:\\Users\\sosilva\\Desktop\\anexo--" + anexo.getOriginalFilename());
		
		/** Aula 22.28. Upload de Arquivos para a API. **/
		/** 3. Vamos escrever os bytes do anexo. MultipartFile ja tem um metodo getBytes()
		 * que ja nos ajuda muito.
		 * Vamos relancar a excecao de MultipartFile **
		out.write(anexo.getBytes());
		out.close();
		
		/** Aula 22.28. Upload de Arquivos para a API. **
		/**4. Vamos retornar simplesmente OK, simplesmente para nos sabermos que tudo
		 * ocorreu bem. **
		return "ok";
	}
	**/
	
	/**
	 * Aula 22.33. Enviando arquivos para o S3
	 * 
	 * @param anexo
	 * @return
	 * @throws IOException
	 */
	@PostMapping("/anexo")
	@PreAuthorize("hasAuthority('ROLE_CADASTRAR_LANCAMENTO') and #oauth2.hasScope('write')")
	/** public String uploadAnexo(@RequestParam MultipartFile anexo) throws IOException {**/
		/** Aula 22.33. Enviando arquivos para o S3
		 * 6.1. Iremos retornar a classe Anexo, definindo no pacote .dto, com duas propriedades: nome e url.
		 * Ver classe Anexo. 
		 */
	public Anexo uploadAnexo(@RequestParam MultipartFile anexo) throws IOException {		
		/** Aula 22.33. Enviando arquivos para o S3
		 * 3. Aqui, ao inves de salvar no Desktop, como na Aula 22.28, nos vamos definir uma variavel local nome, chamando o metodo
		 * salvarTemporariamente, passando o parametro, que eh o mesmo que este metodo esta recebendo.
		 * Por ora, para o primeiro teste, vamos retornar a variavel nome. 
		 * Antes de iniciarmos a Aplicacao, vamos abrir o Browser e logar na Conta da Amazon AWS. Clicar em Services, Selecionar Storage - S3. 
		 * O bucket da nossa Aplicacao nao devera ter sido criado ainda. Ele vai ser criado assim que nos levantarmos a Aplicacao, porque
		 * nos temos a Classe S3Config e, logo na configuracao da nossa instancia de AmazonS3, nos temos um if que, se cair dentro dele,
		 * o nosso bucket sera criado. Obviamente, como ele ainda nao esta criado, ele precisa ser criado nesta vez que iremos levantar
		 * a Aplicacao. Vamos examinar o log para ver se vai levantar com sucesso e, caso levante com sucesso, nos ja vamos olhar a criacao 
		 * desse bucket.
		 *
		 * Erro:
		 * Nao pode criar o Bean como nome lancamentoResource. Nao teve a dependencia satisfeita atraves da propriedade s3.
		 * 
		 * Sera que esquecemos de anotar alguma classe? Vamos ver a classe S3.java. Esta anotada com @Component, eh um componente. AmazonS3 eh um Bean
		 * que nos estamos criando la na classe S3Config.java, entao AmazonS3 tem que ser injetado tambem em S3Config. Esta anotado em S3Config como um
		 * Bean.
		 * 
		 * Examinando melhor o stacktrace de erro:
		 * Erro ao criar o Bean com nome 'amazonS3' definido na Classe S3Config.
		 * 
		 * Na sequencia do stacktrace:
		 * com.amazonws.SdkClientException: Unable to find a region via the region provider chain.
		 * 
		 * Nao foi especificado a regiao.
		 * 
		 * Por que nao foi especificado a regiao? Acessando o Servico S3 no Amazon AWS Console, ao clicar na Regiao, esta dizendo que nao requer selecao de 
		 * regiao. Entao, nos imaginamos que, na configuracao, tambem nao fosse necessario definir a regiao.
		 * 
		 * Ja que eh necessario definir, vamos configurar a regiao em S3Config.amazonS3(), na parte onde nos configuramos as credenciais. 
		 * Ver S3Config.amazonS3().
		 * 
		 * 5. Agora, vamos levantar o Servidor novamente. Foi criado o bucket no Servico S3 da Amazon.
		 * Agora, nos vamos abrir o Postman e nos vamos fazer a mesma requisicao:
		 * http://localhost:8080/lancamentos/anexo
		 * Retornou 200 OK e, no Corpo da Resposta, retornou o nome unico do arquivo criado: eb30f51a-0f2e-45c2-9bd5-2c83ab4c57e9_download.pdf 
		 * Agora, nos vamos voltar no Browser, no S3 no console da Amazon e vamos olhar dentro do bucket criado:
		 * sidarta.silva-algamoney-arquivos. Foi feito o upload do mesmo arquivo eb30f51a-0f2e-45c2-9bd5-2c83ab4c57e9_download.pdf para dentro
		 * do bucket.
		 * Clicando no link do arquivo, abrem algumas informacoes do arquivo, como Expiration rule, i.e., a regra de expiracao a qual o arquivo ainda
		 * esta sujeito, porque ele ainda eh um arquivo temporario.
		 * Se Clicarmos no bucket sidarta.silva-algamoney-arquivos e selecionarmos a Aba Management, i.e., Gerenciamento, repare-se que nos temos
		 * definido em Lifecycle rule, a regra que nos configuramos em S3Config.
		 * Se Clicarmos na Aba Properties do arquivo, repare-se que esta configurado uma Tag. Se clicarmos no quadradinho Tags, vemos a tag
		 * expirar definida com o valor true, igual a tag que nos definimos no metodo salvarTemporariamente().  
		 * Dessa forma, o nosso arquivo ja esta sendo enviado para o S3.
		 * 
		 * 6. Mas, agora, nos queremos mudar esse retorno. Nos nao queremos devolver simplesmente o nome do arquivo neste 
		 * metodo. Nos iremos devolver uma classe que nos chamaremos de Anexo, que ira conter o nome do arquivo
		 * e a URL necessaria para acessar esse arquivo. Entao, vamos criar a classe Anexo. no pacote .dto.
		 *  **/
		String nome = this.s3.salvarTemporariamente(anexo);
		/**
		 * Aula 22.33. Enviando arquivos para o S3
		 * 7. Vamos retornar uma instancia de Anexo, passando os parametros nome e url.
		 * O segundo parametro nos vamos definir a partir de s3.configurarUrl, passando, como parametro, o nome do objeto.
		 * 
		 * 8. Agora, podemos testar no Postman em:
		 * localhost:8080/lancamentos/anexo
		 * Esperamos um JSON no Corpo da Resposta.
		 * 
		 * Codigo da Resposta foi 200 OK. No Corpo da Resposta, retornou o JSON:
		 * {
    			"nome": "9b94b400-15b0-4ad9-bdce-256294c4ba9c_download.pdf",
    			"url": "\\sidarta.silva-algamoney-arquivos.s3.amazonaws.com/9b94b400-15b0-4ad9-bdce-256294c4ba9c_download.pdf"
			}
		 * 
		 * com o nome do arquivo e outra propriedade com o nome da URL.
		 * No Browser, no Console S3 da AWS, ao entrar no bucket sidarta.silva-algamoney-arquivos novamente e clicar no link da figura de 
		 * atualizar ao lado direito da tela, aparecem os arquivos criados nas Requisicoes do Postman. 
		 */
		return new Anexo(nome, s3.configurarUrl(nome));
	}	

	@PreAuthorize("hasAuthority('ROLE_PESQUISAR_LANCAMENTO') and #oauth2.hasScope('read')")
	@GetMapping
	/**
	 * Receber parametros page, size para paginacao atraves da interface Pageable
	 **/
	/** page: numero da pagina atual **/
	/** size: numero de registros a serem exibidos por pagina **/
	/**
	 * Retorna uma pagina de Lan�amentos.
	 * 
	 * @param lancamentoFilter
	 * @param pageable
	 * @return @{link org.springframework.data.domain.Page<Lancamento>} Interface
	 *         Page retorna varios atributos novos de Paginacao na resposta.
	 */
	public Page<Lancamento> pesquisar(LancamentoFilter lancamentoFilter, Pageable pageable) {
		/**
		 * Para implementar uma consulta customizada, criar uma interface
		 * LancamentoRepositoryQuery dentro do pacote repository e, para funcionar,
		 * definir o nome como LancamentoRepositoryQuery para o SpringDataJPA entender.
		 */
		return this.lancamentoRepository.filtrar(lancamentoFilter, pageable);
	}

	/**
	 * Adicionar ao Recurso uma forma de buscar esse Resumo de Lancamento. Vai
	 * continuar sendo um GET em Lancamento. Ao inves do nome pesquisar() sera
	 * resumir().
	 * 
	 * Como vai saber qual metodo chamar se ha o mesmo GetMapping para os metodos
	 * pesquisar() e resumir()? Ai que entra a questao da Projecao: vamos adicionar
	 * um parametro chamado resumo. Se tiver um parametro chamado resumo na
	 * Requisicao Entao vai chamar o metodo resumir().
	 * 
	 * @param lancamentoFilter
	 * @param pageable
	 * @return
	 */
	@GetMapping(params = "resumo")
	@PreAuthorize("hasAuthority('ROLE_PESQUISAR_LANCAMENTO') and #oauth2.hasScope('read')")
	public Page<ResumoLancamento> resumir(LancamentoFilter lancamentoFilter, Pageable pageable) {
		return this.lancamentoRepository.resumir(lancamentoFilter, pageable);
	}
	
	/**
	 * Aula 25.02. Novas Assinaturas do Spring Data JPA
	 * 
	 * 12. Com a migracao para o Spring Boot 2, corrigir o metodo buscarPeloCodigo, substituindo o metodo findOne() por findById().
	 * Vamos corrigir o retorno tambem, return lancamento.isPresent() ? ResponseEntity.ok(lancamento.get()) : ResponseEntity.notFound().build(); 
	 * Se estiver presente entao chamar o metodo get() do Optional. Se nao esta presente, continua retornando Not Found.
	 * 
	 * 13. Mais abaixo, no metodo remover(), substituir o metodo delete() por deleteById(). O restante, continua a mesma coisa.
	 * 
	 * 14. Classe LancamentoResource esta corrigida. Vamos, agora, corrigir a classe PessoaResource.
	 * Ver classe PessoaResource.java.
	 * 
	 * @param codigo
	 * @return
	 */

	@PreAuthorize("hasAuthority('ROLE_PESQUISAR_LANCAMENTO') and #oauth2.hasScope('read')")
	@GetMapping("/{codigo}")
	public ResponseEntity<Lancamento> buscarPeloCodigo(@PathVariable Long codigo) {
		/** Lancamento lancamento = this.lancamentoRepository.findOne(codigo); **/
		Optional<Lancamento> lancamento = this.lancamentoRepository.findById(codigo);
		/** return lancamento != null ? ResponseEntity.ok(lancamento) : ResponseEntity.notFound().build(); **/
		return lancamento.isPresent() ? ResponseEntity.ok(lancamento.get()) : ResponseEntity.notFound().build();
	}

	@GetMapping(params = "codigo")
	@PreAuthorize("hasAuthority('ROLE_PESQUISAR_LANCAMENTO') and #oauth2.hasScope('read')")
	public ResponseEntity<Long> buscarUltimoRegistro() {
		Long codigo = this.lancamentoRepository.buscarUltimoRegistro();
		return codigo != null ? ResponseEntity.ok(codigo) : ResponseEntity.notFound().build();
	}

	@PreAuthorize("hasAuthority('ROLE_CADASTRAR_LANCAMENTO') and #oauth2.hasScope('write')")
	@PostMapping
	public ResponseEntity<Lancamento> criar(@Valid @RequestBody Lancamento lancamento, HttpServletResponse response) {
		/** Substituir Repository pelo novo Servico criado, contendo validacoes. **/
		/** Lancamento lancamentoSalvo = this.lancamentoRepository.save(lancamento); **/
		Lancamento lancamentoSalvo = this.lancamentoService.salvar(lancamento);
		this.publisher.publishEvent(new RecursoCriadoEvent(this, response, lancamentoSalvo.getCodigo()));
		return ResponseEntity.status(HttpStatus.CREATED).body(lancamentoSalvo);
	}

	@ExceptionHandler({ PessoaInexistenteOuInativaException.class })
	public ResponseEntity<Object> handlePessoaInexistenteOuInativaException(PessoaInexistenteOuInativaException ex) {
		String mensagemUsuario = messageSource.getMessage("pessoa.inexistente-ou-inativa", null,
				LocaleContextHolder.getLocale());
		String mensagemDesenvolvedor = ex.toString();
		List<Erro> body = Arrays.asList(new Erro(mensagemUsuario, mensagemDesenvolvedor));
		return ResponseEntity.badRequest().body(body);
	}

	@PreAuthorize("hasAuthority('ROLE_REMOVER_LANCAMENTO') and #oauth2.hasScope('write')")
	@DeleteMapping("/{codigo}")
	@ResponseStatus(code = HttpStatus.NO_CONTENT)
	public void remover(@PathVariable Long codigo) {
		/** this.lancamentoRepository.delete(codigo); **/
		this.lancamentoRepository.deleteById(codigo);
	}

	@PreAuthorize("hasAuthority('ROLE_CADASTRAR_LANCAMENTO') and #oauth2.hasScope('write')")
	@PutMapping("/{codigo}")
	public ResponseEntity<Lancamento> atualizar(@PathVariable Long codigo, @Valid @RequestBody Lancamento lancamento) {
		try {
			return ResponseEntity.ok(this.lancamentoService.atualizar(codigo, lancamento));
		} catch (IllegalArgumentException e) {
			return ResponseEntity.notFound().build();
		}
	}
	
	/**
	 * Aula 22.3. Retornando os Dados Estatisticos de Lancamento por Categoria

	 * Nesta aula, vamos abrir a Classe LancamentoResource, porque eh nela que vamos criar o metodo que vai devolver para os nossos 
	 * Clientes os dados estatiscos de Lancamento Por Categoria.

	 * Vamos chamar esse metodo tambem porCategoria().
	 * 
	 * Quando esse metodo for chamado, esse Recurso for invocado, ele vai devolver os dados referentes aquele mes.
	 * 
	 * Definimos o GetMapping para fazermos o mapeamento do Recurso:
	 *  
	 * @return
	 */
	/** 	Por que nao definir /graficos/, ao inves de /estatisticas/, no mapeamento? Ja que esse metodo sera utilizado posteriormente
	 * 		para a criacao de graficos no Front-End?
	 *  	Porque o nossa objetivo eh que o Back-End nao precise conhecer o Front-End, ou seja, essas informacoes estatisticas
	 *  	poderao ser usadas tanto para gerar graficos quanto para gerar relatorios. Dessa forma, nao recomenda-se definir
	 *  	algo que de, fortemente, a entender uma possivel dependencia com a parte Front-End. O que vai acontecer? O Front-End
	 *  	vai reconhecer o nosso Recurso e vai utiliza-lo como desejar: se quiser gerar um Grafico, vai usa-lo para tanto, se quiser
	 *  	gerar um Relatorio, gerar uma Grid para o Usuario, ele podera usa-lo para tanto. 
	 *  **/
	/**  
	 * Quanto a Permissao, vamos definir a mesma permissao para Pesquisar Lancamentos.
	 * 
	 * Agora podemos testar no Postman: 
	 * http://localhost:8080/lancamentos/estatisticas/por-categoria
	 * 
	 * **/	
	@GetMapping("/estatisticas/por-categoria")
	@PreAuthorize("hasAuthority('ROLE_PESQUISAR_LANCAMENTO') and #oauth2.hasScope('read')")
	public List<LancamentoEstatisticaCategoria> porCategoria() {
		/** Passamos a data atual como parametro para podermos mostrar um grafico do mes corrente.  **/
		return this.lancamentoRepository.porCategoria(LocalDate.now());
	}
	
	/**
	 * Aula 22.5. Retornando os Dados Estatisticos de Lancamento por Dia

	 * Nesta aula, vamos abrir a nossa Classe de Recurso LancamentoResource para podermos criar o retorno das Estatisticas por Dia. 
	 * Nos vamos copiar o metodo porCategoria() e altera-lo.
	 * 
	 * Primeiro, vamos mudar o mapeamento: ao inves de ser por categoria, sera por dia.
	 * 
	 * A autorizacao sera a mesma que porCategoria().
	 * 
	 * O retorno do metodo sera alterado para LancamentoEstatisticaDia.
	 * 
	 * O metodo a ser invocado eh o metodo lancamentoRepository.porDia().
	 * 
	 * Agora, podemos abrir o Postman e testar o metodo:
	 * http://localhost:8080/lancamentos/estatisticas/por-dia
	 */
	@GetMapping("/estatisticas/por-dia")
	@PreAuthorize("hasAuthority('ROLE_PESQUISAR_LANCAMENTO') and #oauth2.hasScope('read')")
	public List<LancamentoEstatisticaDia> porDia() {
		/** Passamos a data atual como parametro para podermos mostrar um grafico do mes corrente.  **/
		// return this.lancamentoRepository.porDia(LocalDate.now());
		/** Apenas para teste: mes anterior. **/
		return this.lancamentoRepository.porDia(LocalDate.now().withMonth(5));
	}
	
	/**
	 * Aula 22.14. Retornando os Bytes do Relatorio na Requisicao.

	 * Estamos quase concluindo essa parte do Relatorio. O que iremos fazer nesta aula eh criar o Recurso no nosso Controlador para poder 
	 * ja devolver os bytes do Relatorio na Requisicao. Vamos abrir a nossa Classe de Recurso LancamentoResource. Nessa Classe, vamos 
	 * criar o nosso metodo que vai devolver o nosso Relatorio.
	 * 
	 * Vamos nomear o metodo como relatorioPorPessoa(), retornando ResponseEntity<byte[]>.
	 * O metodo vai receber dois parametros LocalDate inicio, fim.
	 * Anotar o primeiro parametro com @RequestParam, 
	 * @throws JRException 
	 * @DateTimeFormat para a Aplicacao da API entender o que for passado na Requisicao, porque a data sera passada em um formato
	 * especifico: yyyy-MM-dd 
	 */
	@GetMapping("/relatorios/por-pessoa")
	@PreAuthorize("hasAuthority('ROLE_PESQUISAR_LANCAMENTO') and #oauth2.hasScope('read')")
	public ResponseEntity<byte[]> relatorioPorPessoa(
				@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate inicio, 
				@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate fim) throws JRException {
		/** A primeira coisa eh buscar os bytes do Relatorio, chamando o metodo relatorioPorPessoa() de lancamentoService. 
		 *  Vamos re-lancar a Excecao na assinatura do metodo. **/
		byte[] relatorio = this.lancamentoService.relatorioPorPessoa(inicio, fim);
		/** Vamos devolver os bytes utilizando ResponseEntity<byte[]>.
		 * body(relatorio): passar os bytes no corpo da Resposta. 
		 * 
		 * Agora, vamos abrir o Postman e fazer uma Requisicao e tentar baixar o PDF:
		 * http://localhost:8080/lancamentos/relatorios/por-pessoa?inicio=2018-01-01&fim=2018-04-01 **/
		return ResponseEntity.ok()
					.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_PDF_VALUE)
					.body(relatorio);
	}

}
