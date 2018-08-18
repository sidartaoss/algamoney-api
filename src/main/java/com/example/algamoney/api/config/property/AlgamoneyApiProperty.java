package com.example.algamoney.api.config.property;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuracao de propriedades do Spring Boot para alternar o valor de uma propriedade em ambientes de Desenvolvimento e Producao.
 * 
 * Propriedades em Producao que tem que ser diferentes em Desenvolvimento.
 * Implementar uma forma de configurar isso.
 * 
 * Tem que adicionar também na classe Main do Projeto - AlgamoneyApiApplication a anotação @EnableConfigurationProperties.
 * 
 * @author SEMPR
 *
 */
/** Tem que adicionar a anotacao do Spring para Propriedades de Configuracao. Entre parenteses define o nome para essa configuracao. 
 * Ao salvar, fica mostrando em amarelo de que eh necessario adicionar no pom.xml uma dependencia extra: spring-boot-configuration-processor. **/
@ConfigurationProperties("algamoney")
public class AlgamoneyApiProperty {

	/** private String origemPermitida = "http://localhost:8000"; **/
	private String origemPermitida;
	
	/** Dentro de AlgamoneyApiProperty pode ter um atributo do tipo Seguranca. Gera apenas o Getter dele, pois eh um atributo final. 
	 * Por que assim? Porque ai vai conseguir configurar coisas como: AlgamoneyApiProperty.seguranca.enableHttps. Entao, consegue
	 * separar o que e de seguranca, infra-estrutura, etc. **/
	private final Seguranca seguranca = new Seguranca();
	
	/** 	 
	 * 		Aula 22.16. Configurando o Envio de E-mail. 
	 * 		II. Como foi feito para Seguranca, nos vamos definir uma propriedade mail. **/
	private final Mail mail = new Mail();
	
	/** Aula 22.30. Configurando o Servico S3. 
	 * 7. Criar uma instancia da propriedade S3. **/
	private final S3 s3 = new S3();
	
	/** Aula 22.30. Configurando o Servico S3. 
	 * 8. Criar somente o getter da propriedade s3.
	 * Agora, vamos voltar para a Classe S3Config.java. **/
	public S3 getS3() {
		return s3;
	}
	
	public String getOrigemPermitida() {
		return origemPermitida;
	}
	
	public void setOrigemPermitida(String origemPermitida) {
		this.origemPermitida = origemPermitida;
	}	
	
	public Seguranca getSeguranca() {
		return seguranca;
	}
	
	/**
	 * Aula 22.16. Configurando o Envio de E-mail. 
	 * III. Agora, nos vamos criar o getter dessa nova propriedade: getMail().
	 * Agora podemos ir em application.properties que, quando formos definir as propriedades,
	 * ira aparecer as propriedades de mail tambem. Ver a definicao em application.properties. **/
	public Mail getMail() {
		return mail;
	}
	
	/** Vou adicionar o que quero como configuracao. Por exemplo: **/
	/** private boolean enableHttps; 
	 * 
	 * Pode-se melhorar isso e colocar agrupado por temas. Por exemplo, o enableHttps faz parte da Seguranca da Aplicacao. Entao, 
	 * cria-se uma classe interna para cada tema, definindo os atributos nas classes internas.
	 * **/
	
	public static class Seguranca {
		private boolean enableHttps;

		public boolean isEnableHttps() {
			return enableHttps;
		}

		public void setEnableHttps(boolean enableHttps) {
			this.enableHttps = enableHttps;
		}
	}
	
	/**  
	 *	Aula 22.16. Configurando o Envio de E-mail. 
	 * 
	 * 	I. Vamos criar uma Classe chamada Mail e, nesse Classe, nos vamos definir quatro propriedades.	
	 * **/
	public static class Mail {
		
		private String host;
		private Integer port;
		private String username;
		private String password;

		/**
		 * @return the host
		 */
		public String getHost() {
			return host;
		}

		/**
		 * @param host the host to set
		 */
		public void setHost(String host) {
			this.host = host;
		}

		/**
		 * @return the port
		 */
		public Integer getPort() {
			return port;
		}

		/**
		 * @param port the port to set
		 */
		public void setPort(Integer port) {
			this.port = port;
		}

		/**
		 * @return the username
		 */
		public String getUsername() {
			return username;
		}

		/**
		 * @param username the username to set
		 */
		public void setUsername(String username) {
			this.username = username;
		}

		/**
		 * @return the password
		 */
		public String getPassword() {
			return password;
		}

		/**
		 * @param password the password to set
		 */
		public void setPassword(String password) {
			this.password = password;
		}
	}
	
	/** Aula 22.30. Configurando o Servico S3. 
	 * 5. Esta aula vai definir a Classe S3 e duas propriedades: accessKeyId e secretKeyId.
	 * Essas sao as mesmas propriedades que estao no arquivo rootkey.csv, que foi baixado
	 * de: https://console.aws.amazon.com/iam/home?region=us-east-1#/security_credential
	 * (Your Security Credencials).  **/
	public static class S3 {
		private String accessKeyId;
		private String secretKey;
		/**
		 * Aula 22.31. Criando o Bucket no S3 Automaticamente

		 * La no Servico S3 na Amazon, quando a gente envia um arquivo para la, a gente guarda 
		 * esses arquivos dentro de buckets (baldes).

		 * O que seria um bucket no S3? Eh como se fosse um diretorio raiz, mas ele tem algumas 
		 * caracteristicas em especial. A primeira eh que nao se pode ter um nome de bucket 
		 * repetido dentre todos os que existem no S3, tanto todos os outros do Usuario como 
		 * os buckets de outras pessoas e/ou outras empresas. Entao, o nome do nosso bucket 
		 * precisa ser unico. Inclusive, recomenda-se que seja definido um prefixo do Usuario na 
		 * hora de o Usuario criar os buckets la no S3. 
		 * 
		 * 1. Por exemplo, nos vamos criar na nossa Classe AlgamoneyApiProperty a 
		 * propriedade que vai guardar o nome do bucket. A propriedade ja sera inicializada com 
		 * o valor: "aw-", (aw eh o prefixo da algaworks), 
		 * recomenda-se que o Usuario defina um prefixo proprio, ja que os nomes dos buckets nao 
		 * podem se repetir, "aw-algamoney-arquivos": esse eh o nome do bucket definido
		 * na aula. Nos nao podemos utilizar o mesmo nome, porque, como ja vamos criar um bucket
		 * com esse exato nome, entao nos nao poderemos criar um bucket igual, com o mesmo nome.
		 * Entao, iremos definir o nome "sidarta.silva-algamoney-arquivos".
		 *  **/
		private String bucket = "sidarta.silva-algamoney-arquivos";
		
		/** Aula 22.31. Criando o Bucket no S3 Automaticamente 
		 * 2. Iremos criar o getter e setter para a propriedade bucket.
		 * Nos nao iremos definir o valor da propriedade bucket dentro do arquivo
		 * application.properties porque a propriedade ja foi inicializada aqui com o 
		 * valor sidarta.silva-algamoney-arquivos.
		 * Agora, iremos voltar a nossa classe de configuracao S3Config.java.
		 * **/
		public String getBucket() {
			return bucket;
		}
		
		public void setBucket(String bucket) {
			this.bucket = bucket;
		}
		
		/** Aula 22.30. Configurando o Servico S3. 
		 * 6. Criar os getters e setters **/
		public String getAccessKeyId() {
			return accessKeyId;
		}

		public void setAccessKeyId(String accessKeyId) {
			this.accessKeyId = accessKeyId;
		}

		public String getSecretKey() {
			return secretKey;
		}

		public void setSecretKey(String secretKey) {
			this.secretKey = secretKey;
		}

	}

}
