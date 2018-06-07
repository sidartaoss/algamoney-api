package com.example.algamoney.api.repository.projection;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.example.algamoney.api.model.TipoLancamento;

/**
 * Problema: Existem momentos em que a gente nao precisa retornar todas as informacoes.
*  Solucao: Ai entra a ideia de Projecao. Projecao e retornar aquilo que a gente precisa: criar um resumo do Recurso retornado. 
*  
*  Exemplo: Criar um Resumo de Lancamento, chamando essa Projecao de Resumo. 
*  
*  Podem haver varias Projecoes.
 * @author SEMPR
 *
 */
public class ResumoLancamento {

	/** O que quero retornar neste Resumo **/
	private Long codigo;
	private String descricao;
	private LocalDate dataVencimento;
	private LocalDate dataPagamento;
	private BigDecimal valor;
	private TipoLancamento tipo;
	/** Categoria e Pessoa como String. N�o ir� ser retornado todos os dados de Categoria/Pessoa. 
	 * Retornar somente o atributo nome. **/
	private String categoria;
	private String pessoa;

	/**
	 * Construtor utilizado na hora de implementar a Consulta pelo Criteria/JPA para criar o objeto ResumoLancamento.
	 * Criar o m�todo que faz a Resumo na interface LancamentoRepositoryQuery.
	 * @param codigo
	 * @param descricao
	 * @param dataVencimento
	 * @param dataPagamento
	 * @param valor
	 * @param tipo
	 * @param categoria
	 * @param pessoa
	 */
	public ResumoLancamento(Long codigo, String descricao, LocalDate dataVencimento, LocalDate dataPagamento,
			BigDecimal valor, TipoLancamento tipo, String categoria, String pessoa) {
		this.codigo = codigo;
		this.descricao = descricao;
		this.dataVencimento = dataVencimento;
		this.dataPagamento = dataPagamento;
		this.valor = valor;
		this.tipo = tipo;
		this.categoria = categoria;
		this.pessoa = pessoa;
	}

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

	public TipoLancamento getTipo() {
		return tipo;
	}

	public void setTipo(TipoLancamento tipo) {
		this.tipo = tipo;
	}

	public String getCategoria() {
		return categoria;
	}

	public void setCategoria(String categoria) {
		this.categoria = categoria;
	}

	public String getPessoa() {
		return pessoa;
	}

	public void setPessoa(String pessoa) {
		this.pessoa = pessoa;
	}

	
}
