/**
 * 
 */
package com.example.algamoney.api.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.example.algamoney.api.model.TipoLancamento;

/**
 *  Agora, vamos criar uma nova classe no pacote dto. Essa nova classe vai carregar os Lancamentos de Estatistica por Dia, onde vamos
 * obter o total de Lancamentos por Dia. 
 * 
 * Para esta Classe, nos vamos precisar das propriedades: tipo (enum TipoLancamento), dia (LocalDate), total (BigDecimal), referente ao
 * total gasto no Dia.   
 * 
 */

/**
 * @author SEMPR
 *
 */
public class LancamentoEstatisticaDia {

	private TipoLancamento tipo;
	private LocalDate dia;
	private BigDecimal total;
	
	/**
	 * @param tipo
	 * @param dia
	 * @param total
	 */
	public LancamentoEstatisticaDia(TipoLancamento tipo, LocalDate dia, BigDecimal total) {
		/** Remover super() **/
		/** super(); **/
		this.tipo = tipo;
		this.dia = dia;
		this.total = total;
	}

	/**
	 * @return the tipo
	 */
	public TipoLancamento getTipo() {
		return tipo;
	}

	/**
	 * @param tipo the tipo to set
	 */
	public void setTipo(TipoLancamento tipo) {
		this.tipo = tipo;
	}

	/**
	 * @return the dia
	 */
	public LocalDate getDia() {
		return dia;
	}

	/**
	 * @param dia the dia to set
	 */
	public void setDia(LocalDate dia) {
		this.dia = dia;
	}

	/**
	 * @return the total
	 */
	public BigDecimal getTotal() {
		return total;
	}

	/**
	 * @param total the total to set
	 */
	public void setTotal(BigDecimal total) {
		this.total = total;
	}
}
