/**
 * 
 */
package com.example.algamoney.api.model;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * Aula 24.01. Criando Entidades Cidade e Estado
 * 6. Primeiramente, definir @Entity, @Table, name = 'cidade'
 * 
 * 7. A classe Cidade tambem vai ter atributos codigo, nome. E vai ter, tambem, o atributo estado.
 * Anotar, em estado, como uma associacao ManyToOne. Em seguida, definir: @JoinColumn, a coluna do banco vai ser 
 * 'codigo_estado'
 * 
 * 8. Agora, a gente vai na entidade Endereco e a gente vai remover os dois atributos cidade, estado.
 * Ver Endereco.java.
 * 
 * @author SEMPR
 *	
 */
@Entity
@Table(name = "cidade")
public class Cidade {

	@Id
	private Long codigo;
	private String nome;
	
	@ManyToOne
	@JoinColumn(name = "codigo_estado")
	private Estado estado;

	/**
	 * @return the codigo
	 */
	public Long getCodigo() {
		return codigo;
	}

	/**
	 * @param codigo the codigo to set
	 */
	public void setCodigo(Long codigo) {
		this.codigo = codigo;
	}

	/**
	 * @return the nome
	 */
	public String getNome() {
		return nome;
	}

	/**
	 * @param nome the nome to set
	 */
	public void setNome(String nome) {
		this.nome = nome;
	}

	public Estado getEstado() {
		return estado;
	}

	public void setEstado(Estado estado) {
		this.estado = estado;
	}
}
