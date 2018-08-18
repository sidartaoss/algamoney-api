/**
 * 
 */
package com.example.algamoney.api.model;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;

/**
 * @author sosilva
 *
 *	Aula 22.23. Criando a Entidade Contato para Suportar Mestre-Detalhe

 * Nesta aula, a gente vai adicionar uma lista de Contatos dentro da nossa Entidade Pessoa. 
 * A Entidade Pessoa, que pode ser Fisica ou Juridica, vai ter uma lista de Contatos.

 * Nos, ainda, nao temos essa Entidade Contato. Antes de criarmos a propriedade contatos, nos 
 * vamos ter que criar a Entidade Contato. E a gente nao vai criar uma Classe de Recurso, um 
 * Controlador para essa nossa Entidade Contato. E um jeito bacana de um Front-End poder 
 * inserir Contatos em uma Pessoa seria criando uma tela Mestre-Detalhe, ja que a gente nao 
 * vai ter um Recurso para insercao direta de um Contato, o Contato vai ter que ser inserido 
 * a partir da Pessoa.

 * Entao, para a gente comecar esse mapeamento, para podermos dar esse suporte para a 
 * construcao de uma tela Mestre-Detalhe, vamos criar, primeiramente, a nossa Entidade 
 * Contato.

 * Vamos abrir o Projeto, dentro do nosso pacote .model, nos vamos criar a entidade Contato. 

 * A primeira coisa que faremos eh mapear a Classe com @Entity e @Table.
 */
@Entity
@Table(name = "contato")
public class Contato {

	/**
	 * Aula 22.23. Criando a Entidade Contato para Suportar Mestre-Detalhe 
	 * 1. A Classe Contato vai ter os seguintes atributos. **/
	/** 2. Agora, vamos fazer o mapeamento tanto do objeto relacional quanto tambem das 
	 * validacoes **/
	/** 3. O primeiro atributo a ser mapeado eh codigo. A primeira coisa eh a gente mapear
	 * com @Id, porque codigo vai ser o identificador. Em seguida, anotar com @GeneratedValue
	 * para definir a estrategia de geracao de chave, a qual sera a mesma que estamos usando
	 * ao longo do curso: GenerationType.IDENTITY.
	 * **/
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long codigo;
	
	/** 4. O nome vai ser anotado com @NotEmpty para validacao (anotacao do Hibernate). **/
	@NotEmpty
	private String nome;
	
	/** 5. O email vai ser anotado com @Email para validacao (anotacao do Hibernate).
	 * Sera anotado tambem com @NotNull (anotacao do JPA).**/
	@Email
	@NotNull
	private String email;
	
	/** 6. O telefone vai ser anotado com @NotEmpty (anotacao do Hibernate).  **/
	private String telefone;
	
	/** 7. Referencia para a Classe Pessoa. A pessoa vai ser anotada com @NotNull (anotacao do JPA). 
	 * Pessoa tambem deve ser mapeado com @ManyToOne (anotacao do JPA) e 
	 * @JoinColumn (anotacao do JPA).**/
	
	/** Aula Aula 22.25. Inserindo uma Pessoa com Contato
	 * 1. 	O que que a gente vai fazer, entao?
	* Nos nao vamos remover a anotacao @JsonIgnoreProperties na propriedade contatos da 
	* Classe Pessoa. Na Classe Contato, o que nos vamos fazer eh remover a anotacao 
	* @NotNull do atributo pessoa, nao vai mais haver essa validacao e, na Classe 
	* PessoaResource, no metodo criar(), ao inves de chamar o Repositorio de Pessoa 
	* diretamente, nos vamos chamar o Servico.
	* Ver Classe PessoaService, no metodo criar(). **/
	/** @NotNull **/
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "codigo_pessoa")
	private Pessoa pessoa;
	
	/** 8. Agora, vamos criar os Getters e Setters. **/
	
	/** 9. Agora, vamos criar o hashCode() e o equals(). **/
	
	/** 10. Nossa entidade esta concluida. **/
	
	/** 11. Agora, vamos abrir a entidade Pessoa.
	 * Ver a classe Pessoa. **/
	

	public Long getCodigo() {
		return codigo;
	}

	public void setCodigo(Long codigo) {
		this.codigo = codigo;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getTelefone() {
		return telefone;
	}

	public void setTelefone(String telefone) {
		this.telefone = telefone;
	}

	public Pessoa getPessoa() {
		return pessoa;
	}

	public void setPessoa(Pessoa pessoa) {
		this.pessoa = pessoa;
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
		if (!(obj instanceof Contato)) {
			return false;
		}
		Contato other = (Contato) obj;
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
