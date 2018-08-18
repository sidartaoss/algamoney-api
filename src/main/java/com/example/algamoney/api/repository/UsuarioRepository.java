package com.example.algamoney.api.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.algamoney.api.model.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

	/** Optional eh porque se nao encontrar nao precisa ficar verificando se eh diferente de null. Pode ser usada uma abordagem 
	 * mais orientada a objetos. com Optional **/
	Optional<Usuario> findByEmail(String email);
	
	/**
	 * Aula 22.20. Buscando Lancamentos Vencidos com Spring Data JPA
	 * 
	 * Agora, vamos definir outra pesquisa, que sao os usuarios que tenham determinada Permissao.
	 * Escolhemos essa forma de buscar os usuarios destinatarios de email. Por exemplo, na hora de o agendamento disparar o nosso metodo, ele vai pesquisar os
	 * lancamentos vencidos e, na hora de obter os destinatarios, serao pegos os destinatarios pela Permissao que ele tem na nossa base de dados.
	 * Nesse caso, vamos utilizar a permissao de PESQUISA_LANCAMENTO. Quando formos configurar o metodo de agendamento, iremos utilizar essa Permissao.
	 * Por ora, o que precisamos eh um metodo que busca Usuarios da base que tenham determinada Permissao. Iremos construir isso na Classe UsuarioRepository.
	 * 
	 * Vamos definir o retorno do metodo como uma Lista de Usuarios. 
	 * Comecamos a assinatura do metodo com o prefixo findBy.
	 * Em seguida, definimos o nome da propriedade: permissoes: findByPermissoes.
	 * Nao sera passado o codigo da Permissao ou das Permissoes. Sera passado a descricao de uma Permissao que queremos pesquisar. Entao, o que devemos fazer?
	 * Ja foi definido a propriedade permissoes, entao, em seguida, deve ser definido a propriedade descricao, que eh uma propriedade de dentro das permissoes,
	 * ou seja, a propriedade descricao da permissao tem que ser igual ao que estamos passando no parametro: findByPermissoesDescricao.
	 * 
	 * Dessa forma, definimos os dois metodos: LancamentoRepository.findByDataVencimentoLessThanEqualAndDataPagamentoIsNull
	 * e UsuarioRepository.findByPermissoesDescricao.
	 */
	List<Usuario> findByPermissoesDescricao(String permissaoDescricao);
}
