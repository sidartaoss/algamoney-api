package com.example.algamoney.api.repository.filter;

/**
 * Classe para fazer o filtro de pesquisa em Pessoa.
 * 
 * @author SEMPR
 *
 */
public class PessoaFilter {

	private String nome;
	private Boolean ativo;

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public Boolean getAtivo() {
		return ativo;
	}

	public void setAtivo(Boolean ativo) {
		this.ativo = ativo;
	}
	
}
