/**
 * 
 */
package com.example.algamoney.api.storage;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.AccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.GroupGrantee;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.ObjectTagging;
import com.amazonaws.services.s3.model.Permission;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.SetObjectTaggingRequest;
import com.amazonaws.services.s3.model.Tag;
import com.example.algamoney.api.config.property.AlgamoneyApiProperty;

/**
 * @author SEMPR
 *
 *	Aula 22.32. Implementando o Envio do Arquivo para o S3
 * Chegou a hora de a gente criar o codigo que vai efetivamente pegar o nosso arquivo, os bytes do nosso arquivo e enviar ate o servico S3 da Amazon. 
 * A gente nao vai efetivamente fazer um teste nesta aula, mas nos vamos codificar o metodo que vai fazer isso. E esse metodo vai ficar em uma classe que 
 * nos vamos chamar de S3. Inclusive nos vamos criar um pacote novo para essa classe, que nos vamos chamar de .storage.
 * 
 * Esta classe nos vamos anotar com @Component, i.e., indica que a classe anotada eh um componente. 
 * Tais classes sao consideradas candidatas para auto-deteccao ao utilizar-se configuracao baseada em anotacoes. 
 */
@Component
public class S3 {
	
	/** Aula 22.32. Implementando o Envio do Arquivo para o S3
	 * 10.2. Declarar uma instancia de Simple Logging Facade for Java para sabermos se metodo salvarTemporariamente() encerrou e 
	 * encerrou com sucesso.
	 */
	private static final Logger LOG = LoggerFactory.getLogger(S3.class);

	/** Aula 22.32. Implementando o Envio do Arquivo para o S3
	 * 8.1. Injetar a classe de propriedades AlgamoneyApiProperty para obtermos o nome do bucket.
	 */
	@Autowired
	private AlgamoneyApiProperty property;
	
	/** Aula 22.32. Implementando o Envio do Arquivo para o S3 
	 * 1. A primeira coisa que faremos eh injetar uma instancia de AmazonS3. 
	 * N.T.: Eh necessario anotar com @Bean o metodo amazonS3() na classe S3Config.java para que o Spring possa injetar a instancia
	 * a qual nos precisamos de AmazonS3. **/
	@Autowired
	private AmazonS3 amazonS3;
	
	/** Aula 22.32. Implementando o Envio do Arquivo para o S3
	 * 2. A segunda coisa que faremos eh criar o metodo salvarTemporariamente(). Esse metodo vai receber aquela instancia do MultipartFile 
	 * que nos recebemos la no metodo onde nos construimos o upload do arquivo (uploadAnexo), que esta la na nossa classe 
	 * LancamentoResource. 
	 */
	/** Aula 22.32. Implementando o Envio do Arquivo para o S3
	 * 12. Este metodo:
	 * 1. Define uma instancia de AccessControlList para definir que esse objeto pode ser lido.
	 * 2. Define Meta-dados Content-Type e Content-Length (o tamanho do arquivo).
	 * 3. Define um nome unico para o arquivo.
	 * 4. Define a Requisicao para incluir o objeto de requisicao.
	 * 5. Define nessa Requisicao que o objeto de requisicao vai ter a tag expirar.
	 * 6. De fato envia a Requisicao para o S3. 
	 * 7. Define um Debug para informar o envio com sucesso do arquivo.
	 * 8. Devolve o nome unico do arquivo.
	 * Fim da Aula 22.32. Implementando o Envio do Arquivo para o S3.  
	 * 
	 * @param arquivo
	 * @return
	 */
	public String salvarTemporariamente(MultipartFile arquivo) {
		/** Aula 22.32. Implementando o Envio do Arquivo para o S3
		 * 3. Nos precisamos de 3 coisas antes de criarmos a requisicao para enviar esse objeto. Nos precisamos: A. De qual que eh a 
		 * Permissao que nos vamos definir para esse objeto, a Grantee; B. Vamos precisar definir alguns Meta-dados; C. Vamos precisar
		 * criar um nome unico para esse arquivo que esta chegando para nos (o objeto sendo passado como parametro). Depois de definir
		 * essas tres coisas, nos vamos poder criar o objeto, PutObjectRequest, para, efetivamente, enviarmos esse arquivo para o S3.
		 * 
		 * 4. A primeira coisa eh instanciarmos um primeiro objeto chamado AccessControlList (ACL). 
		 */
		AccessControlList acl = new AccessControlList();
		/** Aula 22.32. Implementando o Envio do Arquivo para o S3
		 * 5. Em seguida, vamos chamar o metodo grantPermission() e, nesse metodo, vamos definir a permissao. Como primeiro parametro
		 * para Grantee, vamos definir GroupGrantee.AllUsers. E qual eh a permissao que todos os Usuarios vao poder ter? Como segundo
		 * parametro, definimos Permission.Read.
		 */
		acl.grantPermission(GroupGrantee.AllUsers, Permission.Read);
		
		/** Aula 22.32. Implementando o Envio do Arquivo para o S3
		 * 6. O primeiro passo foi definido. O segundo passo eh definirmos os Meta-dados. Entao, nos vamos definir com a classe
		 * ObjectMetadata, que eh tambem uma classe da API da AWS.
		 * O primeiro Metadado importante para o envio do nosso arquivo que iremos definir eh o Content-Type. Vamos definir o valor
		 * a partir do metodo arquivo.getContentType().
		 * O segundo Metadado  a ser definido eh o Content-Length. Obtemos o valor a partir de arquivo.getSize().
		 */
		ObjectMetadata objectMetadata = new ObjectMetadata();
		objectMetadata.setContentType(arquivo.getContentType());
		objectMetadata.setContentLength(arquivo.getSize());
		
		/** Aula 22.32. Implementando o Envio do Arquivo para o S3
		 * 7. A segunda coisa foi definida. A terceira coisa que nos precisamos eh criar um nome 	unico para esse arquivo. Entao,
		 * nos vamos definir esse nome unico a partir do metodo gerarNomeUnico(), passando, como parametor, o nome original do
		 * arquivo, que eh arquivo.getOriginalFilename(). 
		 */
		String nomeUnico = this.gerarNomeUnico(arquivo.getOriginalFilename());
		
		/** Aula 22.32. Implementando o Envio do Arquivo para o S3
		 * 8. Agora, nos vamos criar a instancia para o objeto de request PutObjectRequest, para podermos enviar esse arquivo
		 * de fato para o S3. Aqui, nos vamos utilizar o Construtor que tem 4 parametros. O primeiro eh o que vai receber o nome do
		 * bucket. Inclusive, para isso, nos vamos injetar a nossa classe AlgamoneyApiProperty. O segundo parametro vai ser o nome do 
		 * arquivo, i.e., o nome do objeto, porque o S3 trata muito com o termo objeto, ele nao trata com o termo arquivo especificamente.
		 * Entao, quando estivermos procurando algo sobre o S3 e encontrar o termo objeto, equivale ao nome do nosso arquivo.
		 *  No terceiro parametro, nos vamos passar os bytes do arquivo ou o InputStream. No nosso caso, nos vamos passar o InputStream.
		 *  No ultimo parametro, nos vamos passar os metadados do objeto, i.e., ObjectMetadata.
		 *  
		 *  8.2. Precisamos, tambem, chamar o metodo withAccessControlList().
		 *  
		 *  8.3. Vamos tratar a excecao da chamada do metodo arquivo.getInputStream com try/catch e nos vamos re-lancar a excecao,
		 *  passando a excecao para a frente.
		 */
		try {
			PutObjectRequest putObjectRequest = new PutObjectRequest(
					property.getS3().getBucket(),
						nomeUnico,
						arquivo.getInputStream(),
						objectMetadata)
					.withAccessControlList(acl);
			
			/** Aula 22.32. Implementando o Envio do Arquivo para o S3
			 * 11.3. Vamos definir a tag na Requisiacao, chamando o metodo setTagging(). Dentro do Construtor, instanciamos 
			 * ObjectTagging. O parametro do Construtor eh uma lista que precisamos passar, entao nos vamos utilizar a classe
			 * java.util.Arrays, chamando o metodo asList() e, como parametro, vamos passar a instancia de Tag, que eh a mesma
			 * instancia que passamos quando fomos configurar o filtro na aula passada (Classe S3Config,
			 * metodo amazonS3(), new LifecycleFilter()).
			 * O nome tem que ser igual ao definido na configuracao (Classe S3Config.java).
			 * Agora sim o nosso arquivo eh um arquivo temporario e o nome do nosso metodo fez sentido e o nosso metodo esta 
			 * pronto.
			 * Ver descricao do metodo para encerramento da aula.
			 */
			putObjectRequest.setTagging(new ObjectTagging(Arrays.asList(new Tag("expirar", "true"))));
			
			/** Aula 22.32. Implementando o Envio do Arquivo para o S3
			 * 9. Feito isso, basta, entao, a gente, realmente, fazer a requisicao. 
			 */
			amazonS3.putObject(putObjectRequest);
			
			/** Aula 22.32. Implementando o Envio do Arquivo para o S3
			 * 10.1. Para finalizarmos, nos vamos definir um log para a gente saber se o metodo se encerrou. Primeiramente, vamos
			 * declarar uma instancia de Simple Logging Facade for Java: sl4j.Logger.
			 * 10.3. Nos vamos definir um log de Debug. Para a verificacao isDebugEnabled(), no caso dos pacotes da nossa Aplicacao, estara
			 * habilitado, conforme definido em application.properties:
			 * logging.level.com.example.algamoney.api=DEBUG
			 * Para o log que estamos definindo agora, vai aparecer quando um arquivo for para o S3.
			 * 
			 */
			if (LOG.isDebugEnabled()) {
				LOG.debug("Arquivo {} enviado com sucesso para o S3.", 
						arquivo.getOriginalFilename());
			}
			/** Aula 22.32. Implementando o Envio do Arquivo para o S3
			 * 11.1. Nos temos que retornar o nome unico que geramos dentro deste metodo para o arquivo.
			 * 
			 * 
			 * 11.2. Verificamos que falta uma coisa muito importante no nosso metodo, que eh a tag, que eh o que vai dar sentido ao nome
			 * do nosso metodo. Nos estamos salvando um arquivo temporario, so que, para ele realmente ser temporario, nos precisamos
			 * definir aquela tag, definindo que vai expirar daqui 1 dia.
			 */
			return nomeUnico;
		} catch (IOException e) {
			throw new RuntimeException("Problemas ao tentar enviar o arquivo para o S3.", e);
		}
	}
	
	/**
	 * Aula 22.33. Enviando arquivos para o S3
	 * 6.5. Metodo que vai receber um objeto que ira retornar a URL completa para acessar esse objeto do S3.
	 * **/		
	public String configurarUrl(String objeto) {
		/**
		 * Aula 22.33. Enviando arquivos para o S3
		 * 6.6. Concatenar http com o nome do bucket + uma string que eh o padrao do S3: .s3.amazonaws.com/ concatenando o nome do objeto,
		 * que eh o que esta vindo no parametro.
		 */
		/** return "http://" + property.getS3().getBucket() + ".s3.amazonsw.com/" + objeto	; **/
		
		/** 6.7. Vamos fazer melhor: ao inves de http://, vamos definir \\, porque ai nao vai importar se o protocolo eh http ou https: 
		 * qualquer um dos dois protocolos a URL vai se ajustar.
		 * Como estamos em uma String, o contra-barra, que eh um caracter de escape, nos vamos ter que definir \\\\, porque cada par de \\
		 * vai resultar em uma contra-barra.
		 * 
		 * 6.8. Voltar ao metodo uploadAnexo da classe LancamentoResource.
		 * **/
		/**
		 * Aula 23.20. Fazendo Download do Anexo
		 * 
		 * 19. Apareceu, ao clicar no link do anexo de Download, em outra Aba, 'Página não encontrada'. 
		 * Para corrigir, deve-se alterar o metodo configurarUrl() da classe S3.java para:
		 * return "https:" + property.getS3().getBucket() + ".s3.amazonaws.com/" + objeto; ao inves de:
		 * return "\\\\" + property.getS3().getBucket() + ".s3.amazonaws.com/" + objeto;
		 * Voltar para lancamento-cadastro.component.html, no Visual Studio Code.
		 */
		/** return "\\\\" + property.getS3().getBucket() + ".s3.amazonaws.com/" + objeto; **/
		 return "https:" + property.getS3().getBucket() + ".s3.amazonaws.com/" + objeto;
	}
	
	/**
	 * Aula 22.34. Anexando Arquivo no Lancamento
	 * 8. Neste metodo, nos vamos remover aquela tag do arquivo que ja esta no S3, que eh o arquivo que 
	 * a gente esta recebendo no parametro e esta sendo chamado de objeto.
	 * */
	public void salvar(String objeto) {
	/**  
	 * Aula 22.34. Anexando Arquivo no Lancamento
	 * 9. Vamos instanciar a classe SetObjectTaggingRequest
	 * Para o Construtor, o primeiro parametro (bucketName),
	 * nos vamos obter da classe de propriedades.
	 * O segundo parametro (key) equivale ao objeto, i.e., ao nosso arquivo.
	 * O terceiro parametro (tagging) eh um objeto que nos ja utilizamos no momento de inserir
	 * o arquivo temporariamente: ObjectTagging, so que, agora, ao inves de passarmos uma lista
	 * com uma tag, nos vamos passar uma lista vazia. Vamos usar a Collections.emptyList().
	 * Ou seja, como nos estamos passando uma lista vazia, o que vai acontecer eh que essa lista
	 * de tags que esta sendo passada agora vai sobrescrever a lista que ja esta no S3 e, como
	 * estamos passando uma lista vazia, simplesmente, a tag do arquivo sera removida.
	 * **/
		SetObjectTaggingRequest setObjectTaggingRequest = new SetObjectTaggingRequest(
				property.getS3().getBucket(), 
				objeto,
				new ObjectTagging(Collections.emptyList()));
		/**  
		 * Aula 22.34. Anexando Arquivo no Lancamento
		 * 10. Agora, basta efetivamente fazermos a requisicao, onde iremos passar a requisicao
		 * que acabamos de criar. Dessa forma, nos, quando formos salvar um Lancamento, agora,
		 * ele vai pegar o arquivo temporario e ira salva-lo, deixa-lo como permanente.
		 * Agora, entao, vamos testar esse processo. 
		 * Vamos abrir o Postman. Primeiramente, vamos enviar um anexo temporario em:
		 * POST, http://localhost:8080/lancamentos/anexo
		 * Em Body, form-data, key = anexo, Value = Escolher um arquivo. Vamos selecionar o arquivo
		 * download.pdf.
		 *  Levantar o Servidor e Enviar a Requisicao. Retornou 200 OK, sucesso.
		 * {
    			"nome": "3f8d7df2-465f-46be-ae91-2079662d057c_download.pdf",
    			"url": "\\sidarta.silva-algamoney-arquivos.s3.amazonaws.com/3f8d7df2-465f-46be-ae91-2079662d057c_download.pdf"
			}
		Agora, nos vamos copiar o valor da propriedade nome: 3f8d7df2-465f-46be-ae91-2079662d057c_download.pdf, ou seja,
		o nome do arquivo temporario e nos vamos, agora, fazer uma requisicao para buscar o Lancamento de 
		de identificador 1. Por que buscar esse Lancamento? Porque nos queremos pegar o JSON dele para tomarmos
		como base para criarmos um novo Lancamento:
		http://localhost:8080/lancamentos, POST,
		Em Body: raw, Text - JSON:
		{
		    "descricao": "Viagem Julho",
		    "dataVencimento": "2018-05-27",
		    "dataPagamento": null,
		    "valor": 6500,
		    "observacao": "Nordeste",
		    "tipo": "DESPESA",
		    "categoria": {
		        "codigo": 1
		    },
		    "pessoa": {
		        "codigo": 1
		    },
		    "anexo": "3f8d7df2-465f-46be-ae91-2079662d057c_download.pdf"
		}
		* Antes de tornar o arquivo permanente, vamos no Browser, no Servico S3 do Console AWS
		* e vamos verificar o arquivo temporario que acabamos de enviar para o S3:
		* 3f8d7df2-465f-46be-ae91-2079662d057c_download.pdf
		* Ao clicar no Link do arquivo, na Aba Overview, note-se que tem uma Data de Expiracao:
		* Expiration date: Jul 17, 2018 9:00:00 PM GMT-0300.
		* O que nos iremos fazer eh remover essa tag, essa data de expiracao dele.
		* Vamos, agora, enviar a Requisicao no Postman.
		* O resultado foi 201 Created, sucesso. Retornou, no Corpo da Resposta:
		* {
		    "codigo": 16,
		    "descricao": "Viagem Julho",
		    "dataVencimento": "2018-05-27",
		    "dataPagamento": null,
		    "valor": 6500,
		    "observacao": "Nordeste",
		    "tipo": "DESPESA",
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
		    "anexo": "3f8d7df2-465f-46be-ae91-2079662d057c_download.pdf",
		    "urlAnexo": null
		}   
		* Agora, vamos voltar no Browser, no Servico S3, vamos clicar na imagem de Atualizar no Canto Direito
		* da Tela, Clicar no Arquivo e, agora, ele nao tem mais a Data de Expiracao.
		* Na proxima aula, iremos ajustar o metodo atualizar() da classe LancamentoService.
		* Nesta aula, conseguimos vincular um anexo com a criacao do Lancamento e, la no Browser, no Servico S3,
		* nos vimos a mudanca, a Tag sendo removida e, para a proxima aula, nos vamos ajustar atualizar() para
		* tambem prever a questao de adicionar um anexo em um Lancamento que ja existia e foi simplesmente
		* atualizado ou trocar um anexo que ja esta em um Lancamento por outro que o Usuario esteja querendo.
		* Fim da Aula 22.34. Anexando Arquivo no Lancamento.
		 * */
		this.amazonS3.setObjectTagging(setObjectTaggingRequest);
	}

	/** Aula 22.35. Atualizando e Removendo Anexo
	 * @param anexo
	 */
	public void remover(String objeto) {
		/** Aula 22.35. Atualizando e Removendo Anexo
		 * 3. Para implementar o metodo remover, nos vamos precisar do objeto 
		 * DeleteObjectRequest do pacote com.amazonaws..services.s3.model
		 * Vamos instanciar o objeto deleteObjectRequest, passando dois parametros
		 * no Construtor: o bucket e o nome do objeto, que eh o anexo.
		 * Vamos alterar o nome do parametro anexo para objeto porque, dentro da classe S3,
		 *  a gente chama os arquivos de objetos.
		 */
		DeleteObjectRequest deleteObjectRequest = new DeleteObjectRequest(
				property.getS3().getBucket(), 
				objeto);
		/** 
		 * Aula 22.35. Atualizando e Removendo Anexo
		 * 4. Agora, nos usamos a instancia de amazonS3 para enviar essa requisicao de delecao.
		 * Pronto, o metodo de delecao ja foi concluido. Voltar para o metodo
		 * LancamentoService.atualizar().
		 */
		amazonS3.deleteObject(deleteObjectRequest);
	}
	
	/** Aula 22.35. Atualizando e Removendo Anexo
	 * @param anexo
	 * @param anexo2
	 */
	public void substituir(String objetoAntigo, String objetoNovo) {
		/** Aula 22.35. Atualizando e Removendo Anexo
		 * 7. Nesse metodo, a gente vai usar o que a gente ja tem. 
		 * Vamos fazer o seguinte: Se tem (StringUtils.hasText), anexo antigo, entao nos vamos
		 * remover. 
		 */
		if (StringUtils.hasText(objetoAntigo)) {
			this.remover(objetoAntigo);
		}
		/** Aula 22.35. Atualizando e Removendo Anexo
		 * 8. Independente se tem ou se nao tem, nos mandamos salvar o novo anexo.
		 * O metodo salvar() eh simplesmente remover a tag expirar do arquivo.
		 * Com isso, ja eh possivel fazermos um teste.
		 * Voltar para o metodo atualizar na classe LancamentoService.
		 */
		this.salvar(objetoNovo);
	}

	/** Aula 22.32. Implementando o Envio do Arquivo para o S3
	 * 7.1. Esse metodo tera uma implementacao muito simples, que vai ser a partir da classe java.util.UUID, metodo randomUUID().toString()
	 * + underline + o parametro originalFilename.
	 * Dessa forma, nos temos o nome unico para o nosso arquivo.
	 * @param originalFilename
	 * @return
	 */
	private String gerarNomeUnico(String originalFilename) {
		return UUID.randomUUID().toString() + "_" + originalFilename;
	}



	
}
