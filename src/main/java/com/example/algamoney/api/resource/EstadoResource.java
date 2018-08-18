/**
 * 
 */
package com.example.algamoney.api.resource;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.algamoney.api.model.Estado;
import com.example.algamoney.api.repository.EstadoRepository;

/**
 * @author sosilva
 *
 * Aula 24.02. Criando Pesquisa de Estados e Cidades
 * 
 * 6. Primeiramente, anotar a classe com @RestController, 
 * @RequestMapping("/estados")
 * 
 * 7. Agora, vamos construir um metodo que vai devolver uma Lista de Estados chamado de listar(). Devolver os estados atraves do metodo
 * findAll().
 * 
 * 8. Injetar uma instancia de EstadoRepository.
 * 
 * 9. Agora, vamos criar o Recurso de Cidade. Ver CidadeResource.java.
 */
@RestController
@RequestMapping("/estados")
public class EstadoResource {

	@Autowired
	private EstadoRepository estadoRepository;
	
	@GetMapping
	/** @PreAuthorize("isAuthenticated()") **/
	@PreAuthorize("hasAuthority('ROLE_PESQUISAR_PESSOA') and #oauth2.hasScope('read')")
	public List<Estado> listar() {
		return this.estadoRepository.findAll();
	}
}
