package com.example.algamoney.api.model;

import java.math.BigDecimal;
import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.example.algamoney.api.repository.listener.LancamentoAnexoListener;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Aula 22.36. Configurando URL do Anexo
 * 10. Anotar a entidade Lancamento com @EntityListener para definir os ouvintes
 * desta entidade.
 * Passar, como parametro, o array dos listeners (ouvintes).
 * Dessa forma, toda vez que um Lancamento for carregado do banco de dados, o 
 * ouvinte (listener) LancamentoAnexoListener sera disparado e, se ele tiver um anexo,
 * a URL vai ser configurada.
 * Voltar para a classe LancamentoAnexoListener.java.
 * */
@EntityListeners(LancamentoAnexoListener.class)
@Entity
@Table(name = "lancamento")
public class Lancamento {

	@Id
	/** @GeneratedValue(strategy = GenerationType.IDENTITY) **/
	private Long codigo;

	@NotNull
	@Size(min = 5, max = 50)
	private String descricao;

	@NotNull
	@Column(name = "data_vencimento")
	private LocalDate dataVencimento;

	@Column(name = "data_pagamento")
	private LocalDate dataPagamento;

	@NotNull
	private BigDecimal valor;

	@Size(max = 100)
	private String observacao;

	@NotNull
	@Enumerated(EnumType.STRING)
	private TipoLancamento tipo;

	@NotNull
	@ManyToOne
	@JoinColumn(name = "codigo_categoria")
	private Categoria categoria;

	/**
	 * Aula 22.27. Ignorando Contatos da Pessoa na Pesquisa de Lancamento

	 * 1. Nesta aula, iremos ajustar uma funcionalidade. Nao eh um problema da Aplicacao, mas 
	 * iremos mostrar e definir uma abordagem diferente.

	 * O que esta acontecendo, agora, na nossa Aplicacao? Quando a gente faz uma pesquisa 
	 * por Lancamentos, GET em http://localhost:8080/lancamentos, o retorno de Lancamento 
	 * ja esta bem grande e ele traz o atributo pessoa e, quando uma Pessoa ainda tiver contatos, 
	 * ainda esta trazendo os contatos dessa Pessoa e, muito dificilmente, a gente vai precisar 
	 * de contatos a partir de uma pesquisa que a gente fizer em Lancamentos.

	 * Sendo assim, para dimiuirmos um pouco o tamanho do JSON, a medida que a Aplicacao 
	 * for sendo utilizada, mais Contatos iriam sendo inseridos e a gente iria ter um retorno 
	 * muito grande com informacoes desnecessarias para a gente. Entao, o que iremos fazer? 
	 * Nos vamos no STS, na Classe Lancamento. Vamos ate a propriedade pessoa e iremos 
	 * utilizar a notacao parecida com o que utilizamos na Lista de contatos da entidade Pessoa, 
	 * que eh a anotacao @JsonIgnoreProperties("pessoa"). O que essa anotacao esta 
	 * definindo? Ela esta definindo para que seja ignorado a propriedade pessoa que esta 
	 * dentro da entidade Contato. A gente vai fazer de maneira parecida na Classe 
	 * Lancamento: @JsonIgnoreProperties("contatos"). Quando fizer uma pesquisa pelo
	 * Recurso de Lancamento, nos queremos ignorar a propriedade contatos da Pessoa.
	 * Dessa forma, nos diminuirmos o tamanho do JSON da Resposta, ate porque a gente
	 * nao precisa acessar o Contato de uma Pessoa a partir de Lancamento.
	 * Vamos fazer a pesquisa novamente no Postman:
	 * GET, http://localhost:8080/lancamentos.
	 * Okay, a propriedade contatos foi ignorada no JSON.
	 * Fim da Aula 22.27. Ignorando Contatos da Pessoa na Pesquisa de Lancamento.
	 * **/
	@JsonIgnoreProperties("contatos")
	@NotNull
	@ManyToOne
	@JoinColumn(name = "codigo_pessoa")
	private Pessoa pessoa;
	
	/**
	 * Aula 22.34. Anexando Arquivo no Lancamento
	 * 
	 * Talvez, agora, voce esteja se perguntando: Como que eu pego aquele arquivo que a gente enviou para o S3 e coloco-o como um anexo de 
	 * Lancamento?
	 *
	 * Eh nesta aula que nos iremos fazer isso. 
	 * 
	 * 1. Para fazer isso, a primeira coisa que nos iremos precisar eh alterar a entidade Lancamento 
	 * para incluir a propriedade que a gente vai chamar aqui de anexo (tipo String).
	 * Nao precisamos anotar a propriedade anexo, porque nao vai ser uma propriedade obrigatoria. 
	 * O nome da coluna vai ser anexo.
	 * **/
	private String anexo;
	
	/**
	 * Aula 22.34. Anexando Arquivo no Lancamento
	 * 2. E tambem vamos definir outra propriedade chamada urlAnexo (tipo String);
	 * Essa propriedade vai ser transiente, nos nao vamos persistir no banco de dados.
	 * Essa propriedade que vai retornar a URL completa.
	 * */
	@Transient
	private String urlAnexo;
	
	/**
	 * Aula 22.34. Anexando Arquivo no Lancamento
	 * 3. Agora, vamos criar getter e setter para anexo e urlAnexo.
	 * */
	public String getAnexo() {
		return anexo;
	}
	
	public void setAnexo(String anexo) {
		this.anexo = anexo;
	}
	
	public String getUrlAnexo() {
		return urlAnexo;
	}
	
	public void setUrlAnexo(String urlAnexo) {
		this.urlAnexo = urlAnexo;
	}
	
	/**
	 * Aula 22.34. Anexando Arquivo no Lancamento
	 * 4. Agora, vamos criar a migracao, ja que criamos uma propriedade nova que vai ser uma coluna nova.
	 * Em src/main/resources/db/migration, nos vamos criar a migracao 6: 
	 * V06__coluna_anexo_tabela_lancamento.sql
	 * */	
	
	public Long getCodigo() {
		return codigo;
	}

	public void setCodigo(Long codigo) {
		this.codigo = codigo;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public LocalDate getDataVencimento() {
		return dataVencimento;
	}

	public void setDataVencimento(LocalDate dataVencimento) {
		this.dataVencimento = dataVencimento;
	}

	public LocalDate getDataPagamento() {
		return dataPagamento;
	}

	public void setDataPagamento(LocalDate dataPagamento) {
		this.dataPagamento = dataPagamento;
	}

	public BigDecimal getValor() {
		return valor;
	}

	public void setValor(BigDecimal valor) {
		this.valor = valor;
	}

	public String getObservacao() {
		return observacao;
	}

	public void setObservacao(String observacao) {
		this.observacao = observacao;
	}

	public TipoLancamento getTipo() {
		return tipo;
	}

	public void setTipo(TipoLancamento tipo) {
		this.tipo = tipo;
	}

	public Categoria getCategoria() {
		return categoria;
	}

	public void setCategoria(Categoria categoria) {
		this.categoria = categoria;
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
		if (!(obj instanceof Lancamento)) {
			return false;
		}
		Lancamento other = (Lancamento) obj;
		if (codigo == null) {
			if (other.codigo != null) {
				return false;
			}
		} else if (!codigo.equals(other.codigo)) {
			return false;
		}
		return true;
	}
	
	/** Aula 22.19. Processando o Template e Enviando o Email.
	 *  Metodo utilizado no template HTML aviso-lancamentos-vencidos.html para formatar o estilo das linhas de Receita e Despesa. 
	 *  Anotar com @JsonIgnore porque nao deve ser definido com uma propriedade a retornada no JSON. **/
	@JsonIgnore
	public boolean isReceita() {
		return TipoLancamento.RECEITA == this.tipo;
	}

}
