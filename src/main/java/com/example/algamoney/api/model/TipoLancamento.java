package com.example.algamoney.api.model;

public enum TipoLancamento {

	/**
	 * Aula 22.8. Criando o DTO do Relatorio.
	 * 
	 *  Definir uma String para cada Enum porque, como vamos exibir isso la no Relatorio, vamos quere exibir de uma maneira
	 *  que nao fique tudo em Maiusculo (RECEITA, DESPESA).
	 *  Assim sendo, sera definido um valor que vai receber a descricao do Enum. Sera definido os mesmos nomes de cada 
	 *  Enum.
	 *  
	 *   Em seguida, definir uma propriedade do tipo String chamada de descricao.
	 *   
	 *   Depois, definir um Construtor, recebendo, como argumento, a propriedade descricao.
	 *   
	 *   Entao, definir o Getter getDescricao().
	 *   
	 *   Pronto, o Enum esta ajustado para podermos utilizar a propriedade descricao na hora de exibir la no nosso Relatorio. 
	 */
	
	RECEITA("Receita"), 
	DESPESA("Despesa")
	
	;
	
	private final String descricao;
	
	private TipoLancamento(String descricao) {
		this.descricao = descricao;
	}
	
	public String getDescricao() {
		return descricao;
	}
}
