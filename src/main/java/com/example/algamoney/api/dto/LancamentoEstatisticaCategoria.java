/**
 * 
 */
package com.example.algamoney.api.dto;

import java.math.BigDecimal;

import com.example.algamoney.api.model.Categoria;

/**
 * Aula 22.1. Preparação do retorno dos dados para os gráficos

 * Neste Capitulo, vamos iniciar uma serie de melhorias no nosso Back-End, a comecar por esta aula, onde iremos implementar a 
 * funcionalidade que vai retornar dados resumidos para que eles sejam utilizados, por exemplo, em graficos. Nesta aula, especificamente, 
 * o que a gente vai fazer eh a construcao das classes que vao carregar as informacoes, sao os DTOs. Nao vamos chamar de entidades, 
 * porque nao sao, vamos chamar de DTOs, que sao as classes que carregarao os valores relacionados a esses resumos, a esses dados 
 * estatisticos para que eles possam ser utilizados por algum recurso do front-end, como, por exemplo, graficos. Nesta aula, nos vamos 
 * implemetar isso. Em outras aulas, a gente vai fazer as consultas e, tambem construir o metodo no nosso Controlador, na nossa Classe 
 * de Recurso e, depois, claro, fazer um teste com Postman. 

 * Antes de inicar a implementacao, nos vamos abrir o STS e vamos comentar sobre tres pequenas alteracoes. A primeira foi no pom.xml. 
 * O que faremos eh, simplesmente, definir a versao do spring-boot-starter-parent para 1.5.10.

 * Passando para esta aula, que trata da questao de retornar os dados para os graficos, para uma funcionalidade como, por exemplo, 
 * os graficos no front-end. Para isso, nos vamos criar um pacote, chamado com.example.algamoney.api.dto.

 *  DTO: Data Transfer Object. A primeira classe que vamos criar vai se chamar LancamentoEstatisticaCategoria. Essa classe vai retornar 
 *  alguns dados de Categoria, uma somatoria de gastos por Categoria, esse eh o tipo de informacao que essa classe vai carregar. 
 * 
 * Nos vamos precisar definir duas propriedades, que sao: a associacao Categoria e o total, do tipo BigDecimal.
 * 
 * Agora, nos vamos definir um Construtor que recebe essas propriedades como parametros.
 * 
 * Agora, vamos criar uma nova classe no pacote dto. Essa nova classe vai carregar os Lancamentos de Estatistica por Dia, onde vamos
 * obter o total de Lancamentos por Dia. Ver Classe LancamentoEstatisticaDia.
 */

/**
 * @author SEMPR
 *
 */
public class LancamentoEstatisticaCategoria {

	private Categoria categoria;	
	private BigDecimal total;

	/**
	 * @param categoria
	 * @param total
	 */
	public LancamentoEstatisticaCategoria(Categoria categoria, BigDecimal total) {
		/** Remover o super() **/
		/** super(); **/
		this.categoria = categoria;
		this.total = total;
	}

	/**
	 * @return the categoria
	 */
	public Categoria getCategoria() {
		return categoria;
	}

	/**
	 * @param categoria the categoria to set
	 */
	public void setCategoria(Categoria categoria) {
		this.categoria = categoria;
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
