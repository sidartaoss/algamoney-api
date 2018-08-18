package com.example.algamoney.api.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Aula 24.01. Criando Entidades Cidade e Estado

* Nesta aula aqui, a gente vai dar suporte para que o nosso Front-End consiga construir, de maneira organizada e logica, combos dependentes.

* Para fazer isso, especificamente, o que a gente vai construir aqui, o que a gente vai alterar no Projeto eh: a entidade de Enderco a gente vai altera-la, 
* porque a gente vai construir duas outras entidades novas, que sao as entidades de Estado e Cidade. E a gente vai tirar os atributos estado e cidade de 
* Endereco e passar a usar diretamente a Entidade.

* Dessa forma, a gente vai conseguir fazer um combo dependente do tipo: escolheu Cidade, escolheu Estado e, depois, lista-se todas as cidades daquele 
* Estado, em um segundo momento.

* Entao, eh esse o suporte que a gente vai dar. Vamos comecar a construir isso agora. 

* 1. Vamos abrir o STS, no nosso pacote .model. Nos vamos comecar pela entidade Estado. Vamos criar uma classe nova de nome Estado. 

* 2. A primeira coisa que iremos faze eh anotar com @Entity, para nao esquecermos. Depois, com @Table, com o atributo name como "estado".

* 3. A gente vai ter somente dois atributos, que eh o codigo e o nome do estado. No codigo, a gente vai mapear com @Id. A gente nao precisa de uma estrategia
* de geracao de chave primaria, porque a gente nao vai ter a classe de recurso, a gente nao vai fazer as insercoes.
* 
* 4. Agora, nos vamos definir os getters e os setters das duas propriedades. Vamos gerar tambem os metodos equals() e hashcode() somente com codigo.
* 
* 5. A nossa entidade Estado esta pronta. Agora, vamos criar a Entidade Cidade. Ver Cidade.java.
* 
 * @author SEMPR
 *
 */

@Entity
@Table(name = "estado")
public class Estado {

	@Id
	private Long codigo;
	
	private String nome;

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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((codigo == null) ? 0 : codigo.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof Estado)) {
			return false;
		}
		Estado other = (Estado) obj;
		if (codigo == null) {
			if (other.codigo != null) {
				return false;
			}
		} else if (!codigo.equals(other.codigo)) {
			return false;
		}
		return true;
	}
	
	
}
