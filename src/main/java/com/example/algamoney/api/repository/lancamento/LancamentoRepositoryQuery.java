package com.example.algamoney.api.repository.lancamento;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.example.algamoney.api.dto.LancamentoEstatisticaCategoria;
import com.example.algamoney.api.dto.LancamentoEstatisticaDia;
import com.example.algamoney.api.dto.LancamentoEstatisticaPessoa;
import com.example.algamoney.api.model.Lancamento;
import com.example.algamoney.api.repository.filter.LancamentoFilter;
import com.example.algamoney.api.repository.projection.ResumoLancamento;

/**
 * Interface que define metodos customizados para o repositorio (camada Model)
 * de Lancamento.
 * 
 * @author sosilva
 *
 */
public interface LancamentoRepositoryQuery {

	/**
	 * Aula 22.12. Criando a Consulta do Relatorio
	 * 
	 * De volta para o STS, o que iremos fazer agora eh criar a Consulta que vai alimentar o nosso 
	 * Relatorio. O bacana eh que eh uma consulta bem parecida com essas que a gente ja fez.
	 * 
	 * Vamos abrir LancamentoRepositoryImpl e LancamentoRepositoryQuery. Em LancamentoRepositoryQuery, 
	 * nos vamos definir a assinatura do metodo a ser implementado em LancamentoRepositoryImpl. 
	 * Vai ser bem parecido com as Consultas porCategoria() e porDia(), principalmente porDia(), 
	 * vamos nos basear na Consulta porDia() para criar a nova Consulta, que chamaremos de porPessoa().
	 * A diferenca eh que, agora, nos vamos receber dois parametros, que serao os parametros referentes
	 * ao Periodo.
	 * Vamos, agora, implementar o metodo porPessoa() na Classe LancamentoRepositoryImpl.
	 * Ver LancamentoRepositoryImpl.
	 */
		List<LancamentoEstatisticaPessoa> porPessoa(LocalDate inicio, LocalDate fim);

	/**
	 * Aula 22.2. Criando Consulta para Dados por Categoria

	 * Agora que ja temos a nossa Classe de Estatistica de Categoria criada, nos vamos comecar a criar as consultas. Para isso, nos vamos 
	 * utilizar o LancamentoRepositoryQuery e, tambem, LancamentoRepositoryImpl. Nos vamos utilizar essas classes para podermos criar 
	 * a nossa Consulta.

	 * Nos vamos iniciar pela interface LancamentoRepositoryQuery. Vamos declarar o nosso metodo, que vai retornar uma Lista da
	 * Classe LancamentoEstatisticaCategoria. Vamos definir o nome do metodo como porCategoria, recebendo, como parametro, 
	 * o mes referencia, do tipo LocalDate, porque nao vamos fazer consultas abertas por periodo, onde o Usuario vai informar um
	 * Periodo tal.
	 * 
	 * Nos vamos ver que sera simples, caso queiramos implementar essa funcionalidade. Mas, no nosso caso, nos vamos definir um 
	 * LocalDate como referencia para trazermos dados somente daquele mes informado. Entao, por isso que definimos o nome do
	 * parametro como mesReferencia.
	 * 
	 * Agora, vamos definir o metodo na Classe de Implementacao. Ver LancamentoRepositoryImpl.
	 * 
	 */
	public List<LancamentoEstatisticaCategoria> porCategoria(LocalDate mesReferencia);  
	
	/**
	 * Aula 22.4. Criando Consulta para Dados por Dia
	 *
	 * Nesta aula, nos vamos implementar a Consulta de Estatisticas Por Dia, que vai 
	 * retornar uma somatoria de Lancamentos por Categoria e Por Dia.
	 *
	 * @param mesReferencia
	 * @return
	 */
	public List<LancamentoEstatisticaDia> porDia(LocalDate mesReferencia);
	
	/**
	 * 
	 * @param lancamentoFilter
	 * @param pageable
	 * @return
	 */
	Page<Lancamento> filtrar(LancamentoFilter lancamentoFilter, Pageable pageable);

	/**
	 * Passando os parametros LancamentoFilter e Pageable, vai continuar conseguindo filtrar e paginar.
	 * @param lancamentoFilter
	 * @param pageable
	 * @return
	 */
	Page<ResumoLancamento> resumir(LancamentoFilter lancamentoFilter, Pageable pageable);
	
	Long buscarUltimoRegistro();
}
