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

	public Seguranca getSeguranca() {
		return seguranca;
	}

	public String getOrigemPermitida() {
		return origemPermitida;
	}

	public void setOrigemPermitida(String origemPermitida) {
		this.origemPermitida = origemPermitida;
	}
}
