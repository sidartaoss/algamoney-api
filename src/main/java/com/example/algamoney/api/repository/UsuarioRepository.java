package com.example.algamoney.api.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.algamoney.api.model.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

	/** Optional � porque se n�o encontrar n�o precisa ficar verificando se � diferente de null. Pode ser usada uma abordagem 
	 * mais orientada a objetos. com Optional **/
	Optional<Usuario> findByEmail(String email);
}
