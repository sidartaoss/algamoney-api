/**
 * 
 */
package com.example.algamoney.api.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.BucketLifecycleConfiguration;
import com.amazonaws.services.s3.model.CreateBucketRequest;
import com.amazonaws.services.s3.model.Tag;
import com.amazonaws.services.s3.model.lifecycle.LifecycleFilter;
import com.amazonaws.services.s3.model.lifecycle.LifecycleTagPredicate;
import com.example.algamoney.api.config.property.AlgamoneyApiProperty;

/**
 * @author sosilva
 *
	* Aula 22.29. Criando Conta na Amazon AWS

	* Nesta aula, nos vamos dar o primeiro passo para poder enviar os nossos arquivos de anexo de 
	* Lancamento la para o Servico da Amazon S3. S3 que significa Simple Storage Service, eh 
	* um Servico da Amazon muito usado para armazenamento de arquivos. Como o proprio site 
	* diz, eh para armazenamento de objetos, para armazenar e recuperar qualquer quantidade de 
	* dados de qualquer local. O site eh https://aws.amazon.com/pt/s3
	
	* Ele eh um servico pago, mas a gente pode criar uma conta gratuita. Clicar no bota Crie uma 
	* conta gratis para criar uma conta gratuita. Ele vai pedir algumas informacoes. Nao vamos 
	* fazer o cadastro aqui, porque eh um cadastro muito simples. Eh um cadastro de 3 passos, 
	* onde, nesse primeiro passo aqui voce vai informar os seus dados de acesso: email, senha. 
	* No segundo passo, voce vai informar o seu endereco. O terceiro passo eh voce informar um 
	* cartao de credito, porque, apesar de ser uma conta gratuita, eh necessario um cartao de 
	* credito para poder validar a sua conta.
	
	* Sobre o nivel gratuito que eh oferecido do S3, pode-se, durante 1 ano, armazenar ate 5GB de 
	* arquivos para poder fazer testes ou ate mesmo para construir uma aplicacao comercial que, 
	* durante esse 1 ano, se estiver dentro da faixa gratuita, nao vai ter problema nenhum de 
	* utilizar. No caso do instrutor, ele ja saiu desse nivel gratuito e para desenvolvimento e 
	* testes, faz mais ou menos 2 meses que esta enviando arquivos de teste somente da Aplicacao 
	* que roda na maquina local. Ele gastou cerca de 8 centavos de dolares durante esse tempo, 
	* durante esses dois meses, fazendo testes na Amazon S3. Obviamente, nao foi enviado nenhum 
	* arquivo grande, pois eh cobrado por quanto que esta sendo ocupado de espaco, depois que se 
	* saiu da faixa gratuita. Mas, no caso do instrutor que esta fora da faixa gratuita, porque a 
	* conta ja tem mais de um ano, ele pagou cerca de 8 centavos. Entao, o instrutor acredita que, 
	* se separar-se 1 real ou dois, nos vamos conseguir testar a Aplicacao, mesmo que nao se esteja 
	* dentro do nivel gratuito dentro do primeiro ano que a Amazon oferece de servico gratuito.
	
	* No caso do instrutor, ele vai entrar na conta dele, porque, alem de criar a conta, eh 
	* necessario fazer uma outra coisa.
	
	* Vamos entrar no AWS Management Console, fazer o login da conta, Clicar no Nome do 
	* Usuario -> Clicar em My Security Credentials, vai aparecer uma janela informando 
	* que as credenciais criadas nessa pagina dao acesso a usar os Servicos da Amazon em nome do 
	* Usuario. A Amazon recomenda, nessa janela, tambem, se o Usuario quiser atribuir permissoes 
	* especificas para cada Servico, como, por exemplo, uma Permissao so para o S3, uma Permissao 
	* especifica so para o S2, que eh um outro Servico da Amazon ou qualquer outro Servico, 
	* clicar no link AWS best practice, que ele vai redirecionar para criar o seu 
	* Identity and Access Management (IAM), que sao permissoes mais especificas, mais limitadas.
	
	* Para o nosso caso da nossa Aplicacao, vamos clicar no botao 
	* Continue to Security Credentials. Vamos, em seguida, clicar em Access keys 
	* (acces key ID and secret access key) e nos vamos criar uma nova Access Key. Vamos 
	* clicar no botao Create New Access Key. Eh aberta uma Janela chamada Create Access Key. 
	* Muito atencao, pois essa eh a unica oportunidade que teremos de fazer o Download 
	* das informacoes de acesso, da sua chave de acesso. Entao, na hora em que for criado, deve-se 
	* fazer o Download do arquivo e deve ser guardado em local seguro para que nao seja perdido. 
	* Se, por acaso, for perdido ou nao for feito o Download do arquivo, entao sera necessario 
	* criar outra chave e usar outra chave.
	
	* Criar outra chave eh facil.
	
	* Para cada Access Key criada, eh possivel torna-la inativa ou deleta-la. Depois que fizermos 
	* os nossos testes, desenvolvermos e aprendermos a funcionalidade, eh possivel deletar a 
	* Access Key, caso ela nao seja mais necessaria.
	
	* Sendo assim, eh necessario criar e fazer o Download da Access Key para usarmos em aulas 
	* futuras. O arquivo baixado contem a Access Key e a Secret Key.
	
	* Fim da Aula 22.29. Criando Conta na Amazon AWS.
	
	
	
	
	* Aula 22.30. Configurando o Servico S3.
	
	* Nesta aula, nos vamos fazer a configuracao do Servico S3, que eh o Simple Storage Service 
	* da Amazon, porque a gente vai precisar de uma pre-configuracao para poder enviar os 
	* nossos arquivos la para o S3. Entao, para a gente poder fazer essa configuracao, 
	* nos vamos ir no pacote .config e vamos criar classe S3Config.java.

 	* 1. A primeira coisa que nos faremos eh anotar a classe com @Configuration
 	* 
 	* 2. Antes de comecar a configurar a instancia da Classe AmazonS3, que eh a instancia que a 
 	* gente vai precisar, nos vamos adicionar o PATH da API da Amazon no pom.xml.
 	* Ver pom.xml.
 	* 
 	* 
 */
@Configuration
public class S3Config {

	/** Aula 22.30. Configurando o Servico S3. 
	 * 4. Injetar AlgamoneyApiProperty para obter informacoes de 
	 * accessKey e secretKey. Para utilizarmos essa classe, eh necessario
	 * definir as propriedades accessKey e secretKey na classe AlgamoneyApiProperty.
	 * Ver AlgamoneyApiProperty.java. **/
	@Autowired
	private AlgamoneyApiProperty property;
	
	/** 
	 * Aula 22.30. Configurando o Servico S3. 
	 * 		4. Vamos, aqui, definir o metodo que vai devolver a instancia de AmazonS3, anotando
	 * 			com @Bean (indica que o metodo produz um Bean para ser gerenciado
	 * 			pelo container do Spring.
	 * **/
	/** Aula 22.31. Criando o Bucket no S3 Automaticamente
	 * Este metodo:
	 * 1. Pega as credenciais,
	 * 2. instancia a classe mais importante, que eh a AmazonS3. Ate esse momento,
	 * a gente ja poderia devolver essa instancia e ja utilizar. So que, se nao tivessemos
	 * a parte que esta dentro da verificacao (if) que faz a criacao do bucket, o nosso
	 * bucket ja teria que estar criado la no S3. O que nos fizemos para melhorar isso?
	 * 3. Nos criamos o bucket com o metodo createBucket(). Como nos temos uma especificidade
	 * na nossa Aplicacao, que eh poder ter arquivo temporario dentro do nosso bucket, entao
	 * nos criamos a Regra, onde nos vamos filtrar todos os arquivos que estiverem com a tag
	 * expirar e
	 * 4. eles vao ser excluidos depois de um dia. 
	 * 5. Por fim, estamos definindo que esta Regra esta habilitada.
	 * 6. Em seguida, criamos o objeto de configuracao e, depois,
	 * 7. estamos associando a Regra criada, chamada de regraExpiracao com o bucket criado. 
	 * 8. Feito isso, nos ja temos a nossa configuracao que vai ser necessaria na nossa Aplicacao 
	 * concluida.
	 * Fim da Aula 22.31. Criando o Bucket no S3 Automaticamente.
	 *  **/
	@Bean
	public AmazonS3 amazonS3() {
		/** Aula 22.30. Configurando o Servico S3. 
		 * 		5. A primeira coisa que faremos eh criar uma instancia de AWSCredentials.
		 * 	Agora nos vamos precisar definir accessKey e secretKey, que  sao informacoes 
		 * referentes ao arquivo baixado na aula passada,
		 * 	o qual o instrutor solicitou que fosse criado: as credenciais.
		 * 	O arquivo rootkey.csv foi criado e baixado e era utilizado para obter as
		 * 	informacoes de accessKey, secretKey. 
		 * 	A forma como vamos trazer as informacoes de accessKey e secretKey para 
		 * 	dentro desta classe vai ser atraves da classe AlgamoneyApiProperty. Sendo assim,
		 * vamos injetar AlgamoneyApiProperty.
		 * 9. Vamos utilizar as propriedades property.s3.accessKeyId e property.s3.secretKeyId
		 * **/
		AWSCredentials credenciais =  new BasicAWSCredentials(
				property.getS3().getAccessKeyId(), 
				property.getS3().getSecretKey());
		
		/** Aula 22.30. Configurando o Servico S3. 
		 * 10. Agora, vamos criar a instancia de AmazonS3 que eh a instancia a ser retornada.
		 * Caso utilizemos defaultClient() da Classe AmazonS3ClientBuilder, o que vai acontecer
		 * eh que ele ja vai construir o objeto AmazonS3 sem passar nenhum parametro, sem fazer
		 * praticamente nehuma configuracao, vai assumir tudo o que eh padrao.
		 * Aqui, no caso, como nos queremos configurar as credenciais, entao nos vamos utilizar o
		 * metodo standard().
		 * Depois, nos vamos chamar withCredentials(), passando: 
		 * new AWSStaticCredentialsProvider(). No construtor dessa classe, nos vamos passar
		 * as credenciais que acabamos de criar logo acima, no Passo 9.
		 * Depois, nos podemos, entao, chamar o metodo build().
		 * **/
		AmazonS3 amazonS3 = AmazonS3ClientBuilder.standard()
				.withCredentials(new AWSStaticCredentialsProvider(credenciais))
				/** Aula 22.33. Enviando arquivos para o S3
				 * 4. Vamos definir a regiao, porque eh uma configuracao mandatoria na criacao do bucket.
				 * Vamos chamar o metodo withRegion, passando, como parametro, o enum Regions.US_EAST_1
				 * Qualquer valor definido como regiao, no caso do S3, vai funcionar. Da onde nos vamos pegar a regiao US_EAST_1?
				 * Voltando no Browser, no Console da Amazon S3 (Console Home), esta definido, no canto superior direito, ao lado do login, 
				 * a regiao do Usuario. No nosso caso, esta definido US East (N. Virginia).
				 * Voltar para a definicao do metodo LancamentoResource.uploadAnexo().  
				 */
				.withRegion(Regions.US_EAST_1)
				.build();
		
		/** Aula 22.31. Criando o Bucket no S3 Automaticamente 
		 * 3. Vamos fazer a seguinte verificacao: Se nao existir o bucket 
		 *  que nos configuramos, vai ser criado. 
		 * **/
		if (!amazonS3.doesBucketExistV2(property.getS3().getBucket())) {
			/** Aula 22.31. Criando o Bucket no S3 Automaticamente 
			 * 3. Nos vamos criar o bucket, chamando o metodo createBucket() e passando, como
			 * parametro, uma instancia da classe CreateBucketRequest, passando, como parametro
			 * para o Construtor, o nome do bucket que nos configuramos.
			 *  Dessa forma, eh feita a configuracao
			 *  da criacao do bucket de forma automatica: o Usuario nao vai precisar ir la no S3
			 *  da Amazon para criar esse bucket para, so depois, poder enviar os arquivos. **/
			amazonS3.createBucket(
						new CreateBucketRequest(property.getS3().getBucket()));
			
			/** Aula 22.31. Criando o Bucket no S3 Automaticamente 
			 * 4. Outra coisa que podemos fazer com bucket que nao podemos fazer com os 
			 * diretorios que ficam abaixo dele, outra diferenca entre um bucket e um simples
			 * diretorio eh que nos podemos criar regras para um bucket.
			 * Por exemplo:
			 * Aqui na nossa Aplicacao, quando alguem enviar um arquivo para ser anexado a um 
			 * Lancamento, primeiro a gente vai deixar esse arquivo como temporario. E, depois
			 * que a Pessoa confirmar o registro do Lancamento, a gente vai tirar esse arquivo
			 * de temporario e fazer com que ele se torne um arquivo permanente. E nos conseguimos
			 * aplicar essa regra atraves da classe Rule que a gente tem para poder criar Regras
			 * para um bucket. Entao, o que nos iremos fazer? Toda a vez que a gente inserir um
			 * arquivo (anexo), ele vai ser enviado la para o S3 com uma tag chamada expirar.
			 * Enquanto ele estiver com essa tag, se passar um dia e ele ainda estiver com essa tag, 
			 * as regras do bucket que nos iremos configurar agora vao excluir esse arquivo. Entao,
			 * a gente nao precisa ficar preocupando em deletar arquivos temporarios. Ai, 
			 * caso a Pessoa confirme o registro do Lancamento ao qual esse anexo vai se juntar,
			 * ai a gente tira essa tag de expirado do arquivo e o bucket nao vai limpar esse arquivo
			 * que a gente incluiu.
			 * E quando que a gente faz isso?
			 * 
			 * 5. Primeiro, nos vamos criar a instancia da classe Rule, que fica
			 * dentro de BucketLifcycleConfiguration.
			 * 6. Agora, nos vamos configurar a Rule da seguinte forma: ela tem alguns
			 * metodos para a gente configurar, o primeiro deles para a gente 
			 * dar um identificador para essa regra: withId(). Eh um id misturado com uma
			 * descricao para ficar bem obvio ate quando procurarmos isso la dentro do S3.
			 * 7. Agora, nos vamos definir um filtro nessa Rule, porque a gente nao vai querer
			 * expirar todos os arquivos, a gente vai expirar somente aqueles arquivos que
			 * estiverem com a tag expirar. Como que a gente faz isso? Entao, nos vamos 
			 * chamar o metodo .withFilter(), passando uma instancia de LifecycleFilter, 
			 * passando como parametro no Construtor, uma instancia de LifecycleTagPredicate,
			 * passando, como parametro no Construtor, uma instancia de Tag,
			 * passando, como parametro no Construtor, o nome da tag: expirar e o valor: true.
			 * 8. Vamos definir depois de um dia: chamar o metodo withExpirationInDays(), 
			 * passando o numero de dias como parametro. Estamos definindo qual que eh o tempo 
			 * de vigencia para a Regra.
			 * 9. Agora, vamos definir mais uma configuracao de Status da Regra: se esta ativa
			 * ou inativa.
			 * Com isso, ja temos definido a nossa Regra. 
			 * **/
			BucketLifecycleConfiguration.Rule regraExpiracao = 
						new BucketLifecycleConfiguration.Rule()
						.withId("Regra de expiracao de arquivos temporarios")
						.withFilter(new LifecycleFilter(
								new LifecycleTagPredicate(new Tag("expirar", "true"))))
						.withExpirationInDays(1)
						.withStatus(BucketLifecycleConfiguration.ENABLED);
			
			/** Aula 22.31. Criando o Bucket no S3 Automaticamente
			 * 10. Agora, nos vamos criar uma instancia da classe BucketLifecycleConfiguration para
			 * incluir a Regra definida acima.
			 *  **/
			BucketLifecycleConfiguration configuration = new BucketLifecycleConfiguration()
						.withRules(regraExpiracao);
			/** Aula 22.31. Criando o Bucket no S3 Automaticamente
			 * 11. Feito isso, agora que criamos a Regra e o objeto de Configuracao, nos vamos
			 * setar essa regra para o bucket que acabamos de criar. Como parametro, nos vamos
			 * passar o Nome e a Classe de Configuracao.
			 *  **/
			amazonS3.setBucketLifecycleConfiguration(property.getS3().getBucket(), 
						configuration);
			/** Aula 22.31. Criando o Bucket no S3 Automaticamente 
			 * 12. Agora sim nos temos a nosssa Configuracao do S3 concluida. **/
		}
		
		/** Aula 22.30. Configurando o Servico S3. 
		 * 11. Pronto, agora nos podemos retornar a instancia de AmazonS3. **/
		return amazonS3;
		/** Aula 22.30. Configurando o Servico S3.
		 * 12. Antes de finalizar, nos vamos abrir o arquivo application.properties e vamos colar
		 * essas duas prorpriedades que nos acabamos de utilizar: 
		 * property.getS3().getAccessKeyId(),
		 * property.getS3().getSecretKey().
		 * Ver application.properties.
		 * 
		 * 14. Com isso, nos ja podemos encerrar esta aula. Com este metodo, nos ja conseguimos
		 * enviar arquivos la par a Amazon, para o Servico S3 e conseguimos, inclusive, resgatar
		 * arquivos tambem do S3, do Servico da Amazon.
		 * Mas, como a nossa Aplicacao tem uma especificidade, na proxima aula, nos vamos
		 * definir nesta Classe mais uma configuracao referente ao bucket que a gente vai 
		 * utilizar. Inclusive, esse conceito, tambem, o instrutor ira explicar na proxima aula.
		 * So iremos destacar que, com isso que foi definido agora, nos ja conseguiriamos enviar
		 * e retornar arquivos la do S3 e, caso o nosso cenario fosse um cenario bem simples, 
		 * nos ja conseguiriamos enviar/retornar arquivos com o que foi definido nesta aula.
		 * Fim da Aula 22.30. Configurando o Servico S3. 
		 * */
	}
}
