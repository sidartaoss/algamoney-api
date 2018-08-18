package com.example.algamoney.api.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.algamoney.api.model.Lancamento;
import com.example.algamoney.api.repository.lancamento.LancamentoRepositoryQuery;

public interface LancamentoRepository extends JpaRepository<Lancamento, Long>, LancamentoRepositoryQuery {
	
	/**
	 * Aula 22.20. Buscando Lancamentos Vencidos com Spring Data JPA

	 * Esta quase chegando o momento de integrarmos o agendamento com o envio de Email para podermos criar essa Funcionalidade de enviar um aviso para os 
	 * Usuarios do Sistema sobre os Lancamentos vencidos.

	 * Para fazermos isso, precisamos de uma Lista de Usuarios destinatarios que vao receber esse Email e, tambem, da Lista de Lancamentos que estao vencidos. 
	 * Sao duas consultas que a gente precisa e a gente vai criar agora.

	 * Uma coisa bacana eh que a gente vai poder criar essas Consultas com os recursos do Spring Data JPA, ou seja, a gente vai poder cria-las a partir, simplesmente, 
	 * da assinatura de metodos nas nossas interfaces de repositorio.

	 * A primeira pesquisa que a gente vai fazer eh a que vai buscar os lancamentos vencidos na nossa base. Vamos abrir LancamentoRepository e, nessa Classe, 
	 * vamos comecar a criar o nosso metodo. Ele vai devolver uma lista de Lancamento e o nome dele vai ser findBy, findBy eh como se fosse um prefixo
	 * para quando estamos criando esse tipo de Consulta, baseando na assinatura dos metodos. Depois de findBy, definimos a propriedade pela qual
	 * queremos buscar, que eh a dataVencimento: findByDataVencimento. Agora, qual que eh a condicao? A condicao eh que a dataVencimento seja maior ou igual
	 * ao que passarmos no parametro desse metodo. Como representamos isso? lessThanEqual: findByDataVencimentoLessThanEqual(LocalDate data).
	 * 
	 * Este metodo vai buscar onde dataVencimento for menor ou igual a data que esta sendo passado como parametro. So que nao adianta somente dataVencimento
	 * ser menor ou igual ao parametro. Nos precisamos definir a condicao tambem de que dataPagamento eh nula: AndDataPagamentoIsNull:
	 * findByDataVencimentoLessThanEqualAndDataPagamentoIsNull.
	 * Dessa forma, nos conseguimos pesquisar os Lancamentos vencidos.
	 * 
	 * A assinatura do metodo funciona como se fosse uma Query.
	 * Se for definido, por exemplo:
	 * findByDataVencimentoLessThanEqualDataPagamentoIsNull
	 * , vai dar erro: Invalid derived query! No property lessThanEqualDataPagamento found for type LocalDate! Traversed path: Lancamento.dataVencimento.
	 * Foi definido a condicao, so que, na hora de definir a outra parte: dataPagamentoIsNull, eh necessario definir o And, porque, senao, nao funciona mesmo,
	 * eh como se fosse em uma Query na clausula Where do SQL, em que seria necessario definir AND ou OR.
	 * 
	 * Esta pesquisa esta encerrada. Agora, vamos definir outra pesquisa, que sao os usuarios que tenham determinada Permissao.
	 * Escolhemos essa forma de buscar os usuarios destinatarios de email. Por exemplo, na hora de o agendamento disparar o nosso metodo, ele vai pesquisar os
	 * lancamentos vencidos e, na hora de obter os destinatarios, serao pegos os destinatarios pela Permissao que ele tem na nossa base de dados.
	 * Nesse caso, vamos utilizar a permissao de PESQUISA_LANCAMENTO. Quando formos configurar o metodo de agendamento, iremos utilizar essa Permissao.
	 * Por ora, o que precisamos eh um metodo que busca Usuarios da base que tenham determinada Permissao. Iremos construir isso na Classe UsuarioRepository.
	 * Ver classe UsuarioRepository.
	 */
	List<Lancamento> findByDataVencimentoLessThanEqualAndDataPagamentoIsNull(LocalDate data);
	
}
