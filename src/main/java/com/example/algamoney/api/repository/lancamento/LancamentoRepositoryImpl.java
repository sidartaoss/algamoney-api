package com.example.algamoney.api.repository.lancamento;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.util.StringUtils;

import com.example.algamoney.api.model.Categoria_;
import com.example.algamoney.api.model.Lancamento;
import com.example.algamoney.api.model.Lancamento_;
import com.example.algamoney.api.model.Pessoa_;
import com.example.algamoney.api.repository.filter.LancamentoFilter;
import com.example.algamoney.api.repository.projection.ResumoLancamento;

/**
 * Classe de implementacao para  metodos customizados de consulta, utilizando os filtros de Lancamento. 
 * @author sosilva
 *
 */
public class LancamentoRepositoryImpl implements LancamentoRepositoryQuery {

	/** Injetar o EntityManager para poder trabalhar com a(s) consulta(s). **/
	@PersistenceContext
	private EntityManager manager;
	
	@Override
	public Page<Lancamento> filtrar(LancamentoFilter lancamentoFilter, Pageable pageable) {
		/** Criteria do JPA. **/
		/** Como a Criteria do Hibernate depreciou, usaremos a Criteria do JPA, que nao eh tao simples. **/
		
		/** Primeira coisa  criar um CriteriaBuilder. Utilizado para construir as Criterias. **/
		CriteriaBuilder builder = manager.getCriteriaBuilder();
		
		/** Criar uma CriteriaQuery para Lancamento. **/
		CriteriaQuery<Lancamento> criteria = builder.createQuery(Lancamento.class)  ;
		/** Antes de adicionar as restricoes, criar o Root de Lancamento (referencia FROM) **/
		/** Utiliza os atributos de Lancamento para fazer o(s) filtro(s). **/
		Root<Lancamento> root = criteria.from(Lancamento.class);
		
		/**
		 * Adicionar os filtros...
		 * Criar as restricoes para o filtro (WHERE). where recebe, como parametro, um array de Predicados.
		 * **/
		Predicate[] predicates = this.criarRestricoes(lancamentoFilter, builder, root);
		criteria.where(predicates);
		
		/** Criar a Query para Lancamento. **/
		TypedQuery<Lancamento> query = manager.createQuery(criteria);
		
		/** Retornar o resultado da Query. **/
		/** return query.getResultList(); **/
		
		/** Adicionando Paginacao: **/
		/** Adicionar na query a quantidade total de resultados que deve trazer e a pagina onde vai comecar a paginar. **/
		this.adicionarRestricoesDePaginacao(query, pageable);
		
		return new PageImpl<>(query.getResultList(), pageable, total(lancamentoFilter));
	}

	@Override
	public Page<ResumoLancamento> resumir(LancamentoFilter lancamentoFilter, Pageable pageable) {
		/** Usar o Construtor do ResumoLancamento. Vai fazer a consulta e quer retornar um Resumo de Lancamento. **/
		/** Para comecar, sempre vai precisar do CriteriaBuilder. **/
		CriteriaBuilder builder = this.manager.getCriteriaBuilder();
		/** Em seguida, criar a CriteriaQuery. **/
		CriteriaQuery<ResumoLancamento> criteria = builder.createQuery(ResumoLancamento.class);
		/** Tem que buscar esse ResumoLancamento de uma Entidade. Qual a Entidade? Da Entidade Root. **/
		Root<Lancamento> root = criteria.from(Lancamento.class);
		/** Em seguida, comeca a fazer a Selecao (SELECT). **/
		/** O que quer construir? Um Resumo de Lancamento. Passar os argumentos para o Construtor na ordem correta. **/
		criteria.select(builder.construct(ResumoLancamento.class
				,	root.get(Lancamento_.codigo)	, root.get(Lancamento_.descricao)
				,	root.get(Lancamento_.dataVencimento)	, root.get(Lancamento_.dataPagamento)
				,	root.get(Lancamento_.valor)	, root.get(Lancamento_.tipo)
				,	root.get(Lancamento_.categoria).get(Categoria_.nome)
				,	root.get(Lancamento_.pessoa).get(Pessoa_.nome)));
		
		/** A partir daqui eh igual ao metodo filtrar() **/
		Predicate[] predicates = this.criarRestricoes(lancamentoFilter, builder, root);
		criteria.where(predicates);
		
		TypedQuery<ResumoLancamento> query = manager.createQuery(criteria);
		this.adicionarRestricoesDePaginacao(query, pageable);
		
		return new PageImpl<>(query.getResultList(), pageable, total(lancamentoFilter));		
	}

	private Predicate[] criarRestricoes(LancamentoFilter lancamentoFilter, CriteriaBuilder builder,
			Root<Lancamento> root) {
		List<Predicate> predicates = new ArrayList<>();
		if (!StringUtils.isEmpty(lancamentoFilter.getDescricao())) {
			/** Para fazer a consulta em descricao, vai usar um LIKE, usando o Builder e...  **/
			predicates.add(builder.like(
					/**  Passando para minusculo **/
					builder.lower(
							/** Definir qual eh o campo que quer filtrar: descricao.
							 * Como em: where descricao like '%blablabla%'  **/
							/**
							 * Uma forma de nao precisar digitar a string 'descricao' eh utilizar o MetaModel. 
							 * Escrevendo diretamente a string pode-se digitar errado. O erro so eh percebido quando executar.
							 * O MetaModel analise a entidade e vai criar uma classe que tem um atributo 
							 * que vai ser um atributo estatico dentro da classe que vai representar o atributo
							 * de uma forma que, se for alterado o atributo, o MetaModel atualiza isso, regerando a(s) classe(s).
							 * O objetivo do MetaModel eh evitar escrever String e nao errar.
							 * **/
							/** root.get("descricao")), **/
							root.get(Lancamento_.descricao)),
					/** Segundo parametro do LIKE vai ser: **/
					"%" + lancamentoFilter.getDescricao() + "%"));
		}
		if (lancamentoFilter.getDataVencimentoDe() != null) {
			/** Se Data Vencimento De Entao tem que ser Maior ou Igual >= **/
			predicates.add(
					builder.greaterThanOrEqualTo(root.get(Lancamento_.dataVencimento), lancamentoFilter.getDataVencimentoDe()));
		}
		if (lancamentoFilter.getDataVencimentoAte() != null) {
			/** Se Data Vencimento Ate Entao tem que ser Menor ou Igual <= **/			
			predicates.add(
					builder.lessThanOrEqualTo(root.get(Lancamento_.dataVencimento), lancamentoFilter.getDataVencimentoAte()));
		}
		return predicates.toArray(new Predicate[predicates.size()]);
	}

	/**
	 * Qual eh o total de registros por pagina e em qual pagina comecar.
	 * @param query
	 * @param pageable
	 */
	/** 1.
	private void adicionarRestricoesDePaginacao(TypedQuery<Lancamento> query, Pageable pageable) {
		int paginaAtual = pageable.getPageNumber();
		int totalRegistrosPorPagina = pageable.getPageSize();
		int primeiroRegistroDaPagina = paginaAtual * totalRegistrosPorPagina;
		query.setFirstResult(primeiroRegistroDaPagina);
		query.setMaxResults(totalRegistrosPorPagina);
	}
	**/

	/**
	 * Qual eh o total de registros por pagina e em qual pagina comecar.
	 * @param query
	 * @param pageable
	 */
	/** 2. 
	 * Para deixar o metodo mais generico, alterar Lancamento para ? **/
	private void adicionarRestricoesDePaginacao(TypedQuery<?> query, Pageable pageable) {
		int paginaAtual = pageable.getPageNumber();
		int totalRegistrosPorPagina = pageable.getPageSize();
		int primeiroRegistroDaPagina = paginaAtual * totalRegistrosPorPagina;
		query.setFirstResult(primeiroRegistroDaPagina);
		query.setMaxResults(totalRegistrosPorPagina);
	}	
	
	/**
	 * Calcular a quantidade de elementos totais para este filtro. Se est� filtrando, nao eh para trazer todo mundo.
	 * Criar um COUNT(*) para este filtro.
	 * @param lancamentoFilter
	 * @return
	 */
	private Long total(LancamentoFilter lancamentoFilter) {
		CriteriaBuilder builder = manager.getCriteriaBuilder();
		CriteriaQuery<Long> criteria = builder.createQuery(Long.class);
		/** Em quem esta fazendo a Consulta? Lancamento. **/
		Root<Lancamento> root = criteria.from(Lancamento.class);
		
		/** Adicionar o filtro **/
		Predicate[] predicates = this.criarRestricoes(lancamentoFilter, builder, root);
		criteria.where(predicates);
		
		/** Fazer um Select na Criteria. 
		 * Contar quantos registros tem. COUNT(*) **/
		criteria.select(builder.count(root));
		return manager.createQuery(criteria).getSingleResult();
	}

	@Override
	public Long buscarUltimoRegistro() {
		CriteriaBuilder builder = this.manager.getCriteriaBuilder();
		CriteriaQuery<Long> criteria = builder.createQuery(Long.class);
		Root<Lancamento> root = criteria.from(Lancamento.class);
		criteria.select(builder.max(root.get(Lancamento_.codigo)));
		return manager.createQuery(criteria).getSingleResult();
	}

	
}
