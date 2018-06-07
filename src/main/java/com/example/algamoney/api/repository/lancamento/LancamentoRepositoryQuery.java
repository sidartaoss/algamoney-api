package com.example.algamoney.api.repository.lancamento;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

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
