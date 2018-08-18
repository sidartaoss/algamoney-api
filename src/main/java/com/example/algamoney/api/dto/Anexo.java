/**
 * 
 */
package com.example.algamoney.api.dto;

/**
 * @author SEMPR
 *
 * Aula 22.33. Enviando arquivos para o S3
 * 6.2. Definir duas propriedades: nome e url.
 */
public class Anexo {
	
	private String nome;
	private String url;

	/**
	 * Aula 22.33. Enviando arquivos para o S3
	 * 6.3. Definir o Construtor usando os campos.
	 * **/
	public Anexo(String nome, String url) {
		this.nome = nome;
		this.url = url;
	}
	
	/**
	 * Aula 22.33. Enviando arquivos para o S3
	 * 6.4. Definir dois getters: getNome e getUrl
	 * **/	

	public String getNome() {
		return nome;
	}
	
	public String getUrl() {
		return url;
	}
	
	/**
	 * Aula 22.33. Enviando arquivos para o S3
	 * 6.4. Agora, iremos criar um metodo dentro da Classe S3 que vai devolver essa URL: configurarUrl().
	 * Ver metodo configurarUrl em S3.
	 * **/	
}
