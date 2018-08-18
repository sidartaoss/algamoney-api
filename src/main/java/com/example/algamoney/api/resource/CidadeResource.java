/**
 * 
 */
package com.example.algamoney.api.resource;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.algamoney.api.model.Cidade;
import com.example.algamoney.api.repository.CidadeRepository;

/**
 * @author sosilva
 * Aula 24.02. Criando Pesquisa de Estados e Cidades
 * 
 * 10. Anotar, primeiramente, com @RestController, @RestMapping("/cidades")
 * 
 * 11. Agora, nos vamos criar o metodo para devolver a lista de Cidades, o qual chamaremos de pesquisar(), porque ele vai receber, como 
 * parmetro, o codigo do Estado.
 * 
 * 12. Agora, vamos definir o Repositorio de Cidade, @Aurowired private CidadeRepository cidadeRepository;
 * 
 * 13, Agora, nos vamos criar o metodo no Repositorio de Cidade que vai trazer todas as Cidades a partir de um Estado. Esse metodo vai
 * se chamar findByEstadoCodigo(), passando o codigo do Estado como parametro.
 * Ver CidadeRepository.java.  
 * 
 * 16. Agora, nos, simplesmente, vamos retornar this.cidadeRepository.findByEstadoCodigo(), passando o parametro codigoEstado. Dessa forma,
 * a gente vai ter a lista de Cidades de um Estado.
 * 
 * 17. Vamos mapear o metodo pesquisar() com @GetMapping.
 * 
 * 18. Anotar, tambem, a Action que esta definida em EstadoResource, para o metodo listar().
 * 
 * 19. Agora, vamos abrir o Postman para fazer um teste: GET, http://localhost:8080/estados.
 * 
 * 20. Antes de testar, vamos definir a seguranca dos metodos em CidadeResource e EstadoResource. Nos precisamos definir
 * a premissa apenas de que o Usuario deve estar autenticado: @PreAuthorize("isAuthenticated()").
 * 
 * 21. Agora, podemos testar: GET, http://localhost:8080/estados, com Autenticacao do tipo Basic:
 * username: admin@algamoney.com
 * password: admin
 * 
 * 22. Nao funcionou, retornou erro: Status 401 Unauthorized
 * {
	    "error": "unauthorized",
	    "error_description": "Full authentication is required to access this resource"
	}
 * 
 *  23. Vamos, entao, definir Autenticacao do tipo Oauth2, com a permissao de leitura para a Entidade Pessoa, 
 *  pois as Entidades Cidade e Estado sao utilizadas na tela de Cadastro de Pessoa:
 * @PreAuthorize("hasAuthority('ROLE_PESQUISAR_PESSOA') and #oauth2.hasScope('read')")
 * 
 * 24. Re-testamos e, agora, funcionou.
 * 
 * 25. Vamos, agora, criar uma Requisicao para Cidades, GET, http://localhost:8080/cidades, com o parametro
 * estado com o valor 1, http://localhost:8080/cidades?estado=11, cidades para o estado de Minas Gerais:
 * Retornou: Status 200 OK
 * [
	    {
	        "codigo": 1,
	        "nome": "Belo Horizonte",
	        "estado": {
	            "codigo": 11,
	            "nome": "Minas Gerais"
	        }
	    },
	    {
	        "codigo": 2,
	        "nome": "Uberlândia",
	        "estado": {
	            "codigo": 11,
	            "nome": "Minas Gerais"
	        }
	    },
	    {
	        "codigo": 3,
	        "nome": "Uberaba",
	        "estado": {
	            "codigo": 11,
	            "nome": "Minas Gerais"
	        }
	    }
	]
 * 
 * 
 */
@RestController
@RequestMapping("/cidades")
public class CidadeResource {

	@Autowired
	private CidadeRepository cidadeRepository;
	
	@GetMapping
	/** @PreAuthorize("isAuthenticated()") **/
	@PreAuthorize("hasAuthority('ROLE_PESQUISAR_PESSOA') and #oauth2.hasScope('read')")
	List<Cidade> pesquisar(@RequestParam("estado") Long codigoEstado) {
		return this.cidadeRepository.findByEstadoCodigo(codigoEstado);
	}
}
