package com.example.algamoney.api.repository.lancamento;

import java.time.LocalDate;
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

import com.example.algamoney.api.dto.LancamentoEstatisticaCategoria;
import com.example.algamoney.api.dto.LancamentoEstatisticaDia;
import com.example.algamoney.api.dto.LancamentoEstatisticaPessoa;
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

	/**
	 *  Aula 22.12. Criando a Consulta do Relatorio
	 *  
	 * A primeira coisa que iremos fazer eh copiar a implementacao do metodo porDia() 
	 * para usa-lo como base.
	 * 
	 */
	@Override
	public List<LancamentoEstatisticaPessoa> porPessoa(LocalDate inicio, LocalDate fim) {
		/** Essa primeira linha de criteriaBuilder vai ficar igual.  **/
		CriteriaBuilder criteriaBuilder = manager.getCriteriaBuilder();
		
		/** Nessa segunda linha, iremos mudar o Generics Type do CriteriaQuery para
		 * LancamentoEstatisticaPessoa, assim como o parametro do metodo 
		 * createQuery para LancamentoEstatisticaPessoa. **/
		CriteriaQuery<LancamentoEstatisticaPessoa> criteriaQuery = criteriaBuilder.
				createQuery(LancamentoEstatisticaPessoa.class);
		
		/** Root fica a mesma coisa, porque, como ja vimos em outras aulas, nos estamos buscando
		 * os dados da Entidade Lancamento.  **/
		Root<Lancamento> root = criteriaQuery.from(Lancamento.class);
		
		/** Aqui no metodo criteriaQuery.select(), onde construimos os nossos objetos a partir 
		 * do que esta retornando na Query, alteramos a Classe no metodo 
		 * criteriaBuilder.construct() para LancamentoEstatisticaPessoa. Segunda coisa eh
		 * verificarmos o que esta sendo recebido como parametro no Construtor da Classe
		 * LancamentoEstatisticaPessoa: tipo, pessoa, total. Entao, eh isso que devemos passar
		 * como parametro. O total eh um SUM do valor.
		 *  **/
		criteriaQuery.select(criteriaBuilder.construct(LancamentoEstatisticaPessoa.class, 
				root.get(Lancamento_.tipo),
				root.get(Lancamento_.pessoa),
				criteriaBuilder.sum(root.get(Lancamento_.valor))));
		
		/** Na clausula WHERE, ou seja, no metodo criteriaQuery.where(), ao inves de termos
		 * as variaveis primeiroDia e o ultimoDia, vamos remove-las e vamos passar os parametros
		 * que estamos recebendo no metodo: inicio e fim. **/
		criteriaQuery.where(
				criteriaBuilder.greaterThanOrEqualTo(root.get(Lancamento_.dataVencimento), 
						inicio),
				criteriaBuilder.lessThanOrEqualTo(root.get(Lancamento_.dataVencimento), 
						fim));
		
		/** O GROUP BY nos vamos agrupar de outra maneira: por Tipo e por Pessoa, ja que o
		 * nosso Relatorio eh um Relatorio por Pessoa.  **/
		criteriaQuery.groupBy(root.get(Lancamento_.tipo), 
				root.get(Lancamento_.pessoa));
		
		/** Para finalizarmos, substituimos o Generics Type LancamentoEstatisticaDia por
		 * LancamentoEstatisticaPessoa.
		 * Dessa forma, o nosso metodo esta pronto para ser utilizado, invocando-o e passando
		 * as informacoes de Periodo para o Relatorio. **/
		TypedQuery<LancamentoEstatisticaPessoa> typedQuery = manager
				.createQuery(criteriaQuery);
		
		return typedQuery.getResultList();
	}

	/**
	 * Aula 22.2. Criando Consulta para Dados por Categoria
	 * 
	 * Declaramos o CriteriaBuilder.
	 * 
	 * Depois, declaramos o CriteriaQuery.
	 * 
	 * Em seguida, declaramos o Root, do tipo Lancamento. Lembrando que, no Generics do CriteriaQuery, eh o que a gente quer devolver
	 * e o Generics do Root eh onde a gente vai buscar os dados, que eh na Entidade Lancamento.
	 * 
	 * Entao, nos construimos o nosso objeto (em criteriaQuery.select()...), lembrando que passamos, como parametro na invocacao
	 * do metodo construct(), o tipo da classe (LancamentoEstatisticaCategoria.class) e, em seguida, os parametros que serao recebidos
	 * pelo Construtor.
	 * 
	 * Apos isso, definimos o primeiro e o ultimo dias do mes de referencia.
	 * 
	 * Na clausula where, nos definimos duas condicoes: uma definindo que dataVencimento tem que ser maior ou igual ao primeiro dia e
	 * essa mesma dataVencimento tem que ser menor ou igual ao ultimo dia.
	 * 
	 * Depois, nos agrupamos por Categoria.
	 * 
	 * Em seguida, simplesmente, nos criamos a nossa Query para podermos retornar o resultado final.
	 * 
	 * Na proxima aula, nos vamos criar o metodo no Controlador para devolver essas informacoes aqui e vamos testar.
	 * 
	 */
	@Override
	public List<LancamentoEstatisticaCategoria> porCategoria(LocalDate mesReferencia) {
		/** A primeira coisa que precisamos definir eh o CriteriaBuilder. **/
		CriteriaBuilder criteriaBuilder = this.manager.getCriteriaBuilder();
		/** O proximo passo eh definirmos CriteriaQuery, do tipo LancamentoEstatisticaCategoria. 
		 * 		Em createQuery, vamos passar LancamentoEstatisticaCategoria.class. **/
		CriteriaQuery<LancamentoEstatisticaCategoria> criteriaQuery = criteriaBuilder
				.createQuery(LancamentoEstatisticaCategoria.class);
		/** O proximo passo eh definirmos o Root da nossa Consulta. No Generics de Root, nos vamos definir
		 * 		Lancamento ao inves de LancamentoEstatisticaCategoria. Por que? A nossa Consulta, a nossa criteriaQuery
		 * 		vai retornar uma Lista de LancamentoEstatisticaCategoria. So que, na hora de buscarmos os dados, buscarmos
		 * 		essas informacoes, a gente vai buscar da Entidade Lancamento, da tabela de Lancamento. Entao, eh por isso que,
		 * 		no Root, definimos Lancamento. 
		 * 		
		 * 		Vamos invocar o metodo from de CriteriaQuery para podermos buscar o valor da Entidade Lancamento. **/
		Root<Lancamento> root = criteriaQuery.from(Lancamento.class) ;
		
		/** Agora, nos vamos mostrar, para a Criteria do JPA, como que o nosso objeto LancamentoEstatisticaCategoria 
		 *   precisa ser construido.
		 *   Ao invocar o metodo select() de CriteriaBuilder, passamos CriteriaBuilder como parametro, invocando o 
		 *   metodo construct, que recebe dois parametros: o primeiro eh a classe e o segundo sao os dados que ficarao no
		 *   Construtor, que o Construtor dessa Classe recebe.
		 *   
		 *   O primeiro parametro do Construtor eh Categoria e o segundo eh total. Vamos precisar fazer um SUM para podermos
		 *   somar por Categoria.
		 *   O primeiro parametro, entao, nos argumentos para o Construtor (selections), definimos como
		 *   root.get(Lancamento_.categoria).
		 *   Para o segundo parametro, nos utilizamos o CriteriaBuilder, invocando o metodo sum(), passando, como parametro,
		 *  a propriedade que queremos que seja feita a soma:
		 *  criteriaBuilder.sum(root.get(Lancamento_.valor)).
		 *   **/ 
		criteriaQuery.select(criteriaBuilder.construct(LancamentoEstatisticaCategoria.class, 
				root.get(Lancamento_.categoria), 
				criteriaBuilder.sum(root.get(Lancamento_.valor))));
		
		/** Agora, nos ja podemos trabalhar a clausula WHERE. So que, antes de trabalharmos nela, nos precisamos definir os parametros
		 * que nos vamos utilizar. Baseando-se no mes de referencia, nos vamos obter o primeiro dia do mes o ultimo dia do mes de
		 * referencia.
		 * Primeiramente, nos vamos definir duas propriedades: primeiroDia, que eh o primeiro dia do mes:
		 * LocalDate primeiroDia = mesReferencia.withDayOfMonth(1); 
		 * Passamos 1 como parametro porque nos queremos o primeiro dia.
		 * A outra variavel que vamos definir tambem do tipo LocalDate:
		 * LocalDate ultimoDia = mesReferencia.withDayOfMonth(mesReferencia.lengthOfMonth);
		 * No parametro, ao inves de passarmos um numero, nos vamos passar mesReferencia.lengthOfMonth().
		 * Dessa forma, nos temos o primeiro e o ultimo dia para podermos trabalhar, agora, na clausula WHERE.
		 * **/ 
		LocalDate primeiroDia = mesReferencia.withDayOfMonth(1);
		LocalDate ultimoDia = mesReferencia.withDayOfMonth(mesReferencia.lengthOfMonth());
		
		/** Para podermos trabalhar na clausula WHERE, utilizamos o CriteriaQuery, invocando o metodo where().
		 * 		Aqui, nos vamos trabalhar duas condicoes. A primeira condicao eh que a Data de Vencimento tem que ser
		 * 		maior ou igual ao Dia Primeiro (primeiroDia).
		 * 		A segunda condicao eh que ainda a Data de Vencimento tem que ser menor ou igual ao Ultimo Dia.  
		 * 
		 * 		Passamos, como parametro do metod where, criteriaBuilder, invocando, para a primeira condicao, 
		 * 		o metodo greaterThanOrEqualTo(). O primeiro parametro desse metodo eh a expressao, a partir da variavel root, 
		 * 		onde definimos Lancamento_dataVencimento. O segundo parametro eh a variavel primeiroDia
		 * 		
		 * 		A segunda condicao eh parecida, so que, agora, ela invoca o metodo lessThaOrEqualTo(), onde vamos passar
		 * 		a propriedade Lancamento_dataVencimento e o segundo parametro eh a variavel ultimoDia.
		 *   
		 * **/
		criteriaQuery.where(
				
				criteriaBuilder.greaterThanOrEqualTo(root.get(Lancamento_.dataVencimento), 
						primeiroDia),
				criteriaBuilder.lessThanOrEqualTo(root.get(Lancamento_.dataVencimento), 
						ultimoDia)				
				
				);
		
		/** Agora, nos so precisamos fazer o agrupamento e retornar a Consulta. 
		 * 		
		 * 		Para fazermos o agrupamento, nos utilizamos o CriteriaQuery, invocando o metodo groupBy, passando, como parametro,
		 * 		a propriedade que desejamos que seja agrupada. Nos vamos agrupar por Categoria, porque a nossa somatoria eh por
		 * 		Categoria.
		 * **/
		criteriaQuery.groupBy(root.get(Lancamento_.categoria));
		
		/** Agora, so falta nos retornarmos o resultado. 
		 * 		Nos vamos definir um TypedQuery, do tipo LancamentoEstatisticaCategoria, que eh a nossa Classe de Retorno.
		 * 		Vamos invocar o metodo createQuery() do EntityManager, passando, como parametro, a nossa consulta, criteriaQuery.
		 * **/
		TypedQuery<LancamentoEstatisticaCategoria> typedQuery = this.manager
					.createQuery(criteriaQuery);
		
		/** O ultimo passo, agora, eh retornarmos TypedQuery, invocando o metodo getResultList(). **/
		return typedQuery.getResultList();
	}
	
	/**
	 * 	Aula 22.4. Criando Consulta para Dados por Dia
	 * 
	 * Copiamos o metodo porCategoria(), alterando o retorno do nosso metodo. 
	 * Alteramos o Generics do CriteriaQuery.
	 * Alteramos, em seguida, na chamada do metodo select(), como construir o objeto LancamentoEstatisticaDia, 
	 * porque ele tem um Construtor diferente em relacao a LancamentoEstatisticaCategoria, que
	 * recebe Tipo, DataVencimento e Total.
	 * Alteramos o agrupamento, que eh por Tipo e DataVencimento.
	 * Em seguida, alteramos o Generics de TypedQuery e devolvemos a Consulta da mesma forma 
	 * em relacao ao metodo porCategoria().
	 * 
	 * Na proxima aula, vamos desenvolver o metodo no Controlador para podermos testar.
	 */
	@Override
	public List<LancamentoEstatisticaDia> porDia(LocalDate mesReferencia) {
		CriteriaBuilder criteriaBuilder = this.manager.getCriteriaBuilder();
		
		CriteriaQuery<LancamentoEstatisticaDia> criteriaQuery = criteriaBuilder
				.createQuery(LancamentoEstatisticaDia.class);
		
		Root<Lancamento> root = criteriaQuery.from(Lancamento.class) ;
		
		/** Aqui fica um pouco diferente, porque temos um Construtor diferente em
		 * relacao a LancamentoEstatisticaCategoria, que vai receber:
		 * TipoLancamento tipo, LocalDate dia, BigDecimal total  **/
		criteriaQuery.select(criteriaBuilder.construct(LancamentoEstatisticaDia.class, 
				root.get(Lancamento_.tipo),
				root.get(Lancamento_.dataVencimento),
				criteriaBuilder.sum(root.get(Lancamento_.valor))));

		LocalDate primeiroDia = mesReferencia.withDayOfMonth(1);
		LocalDate ultimoDia = mesReferencia.withDayOfMonth(mesReferencia.lengthOfMonth());
		
		criteriaQuery.where(
				
				criteriaBuilder.greaterThanOrEqualTo(root.get(Lancamento_.dataVencimento), 
						primeiroDia),
				criteriaBuilder.lessThanOrEqualTo(root.get(Lancamento_.dataVencimento), 
						ultimoDia)				
				
				);
		
		/** Vamos alterar para agrupar por Tipo e por Data Vencimento.
		 * Queremos separar por Tipo: se eh Receita ou Despesa e por Dia, entao
		 * queremos ver a somatoria de gastos, por exemplo, Despesas, no dia Primeiro de 
		 * algum mes. **/
		criteriaQuery.groupBy(root.get(Lancamento_.tipo), 
					root.get(Lancamento_.dataVencimento));
		
		TypedQuery<LancamentoEstatisticaDia> typedQuery = this.manager
					.createQuery(criteriaQuery);
		
		return typedQuery.getResultList();
	}	
	
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
