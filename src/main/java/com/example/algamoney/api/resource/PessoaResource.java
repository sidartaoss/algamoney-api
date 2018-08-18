package com.example.algamoney.api.resource;

import java.util.Optional;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.example.algamoney.api.event.RecursoCriadoEvent;
import com.example.algamoney.api.model.Pessoa;
import com.example.algamoney.api.repository.PessoaRepository;
import com.example.algamoney.api.repository.filter.LancamentoFilter;
import com.example.algamoney.api.repository.filter.PessoaFilter;
import com.example.algamoney.api.service.PessoaService;

@RestController
@RequestMapping("/pessoas")
public class PessoaResource {

	@Autowired
	private PessoaRepository pessoaRepository;
	
	@Autowired
	private ApplicationEventPublisher publisher;

	@Autowired
	private PessoaService pessoaService;
	
	/** 1.
	@GetMapping
	public List<Pessoa> listar() {
		return this.pessoaRepository.findAll();
	}
	**/
	
	/** 2. 
	 * Adicionando pesquisa por filtros. **/
	@PreAuthorize("hasAuthority('ROLE_PESQUISAR_PESSOA') and #oauth2.hasScope('read')")	
	@GetMapping
	public Page<Pessoa> pesquisar(PessoaFilter pessoaFilter, Pageable pageable) {
		return this.pessoaRepository.filtrar(pessoaFilter, pageable);
	}	
	
	/** 1. **/
	/** @PostMapping **/
	/** public ResponseEntity<Pessoa> criar(@Valid @RequestBody Pessoa pessoa, HttpServletResponse response) {
		Pessoa pessoaSalva = this.pessoaRepository.save(pessoa);
		URI uri = ServletUriComponentsBuilder.fromCurrentRequestUri().path("/{codigo}")
				.buildAndExpand(pessoaSalva.getCodigo()).toUri();
		response.setHeader("Location", uri.toASCIIString());
		return ResponseEntity.created(uri).body(pessoaSalva);
	}
	**/

	/** 2. **/
	@PreAuthorize("hasAuthority('ROLE_CADASTRAR_PESSOA') and #oauth2.hasScope('write')")	
	@PostMapping
	public ResponseEntity<Pessoa> criar(@Valid @RequestBody Pessoa pessoa, HttpServletResponse response) {
		Pessoa pessoaSalva = this.pessoaRepository.save(pessoa);
		/** Disparar um evento no Spring. 
		 * O objetivo, ao disparar um evento do Spring, eh evitar-se codigo replicado nas classes de recurso. 
		 * Quando for necessario adicionar um outro comportamento quando um recurso for criado, pode-se 
		 * implementar outro listener. **/
		/** Pode-se implementar varios listeners, cada um tendo uma acao especifica para cada recurso criado. **/
		/** Nao eh necessario alterar nada na classe de recurso em relacao ao comportamento do recurso criado. **/
		this.publisher.publishEvent(new RecursoCriadoEvent(this, response, pessoaSalva.getCodigo()));
		return ResponseEntity.status(HttpStatus.CREATED).body(pessoaSalva);
	}
	
	/**
	 * Aula 25.02. Novas Assinaturas do Spring Data JPA
	 * 
	 * 15. Com a migracao para o Spring Boot 2, corrigir o metodo buscarPeloCodigo(), substituindo o metodo findOne() por findById().
	 * Vamos corrigir o retorno tambem, return pessoa.isPresent() ? ResponseEntity.ok(pessoa.get()) : ResponseEntity.notFound().build();
	 * 
	 * 16. Mais abaixo, no metodo remover(), substituir o metodo delete() por deleteById(). O restante, continua a mesma coisa.
	 * 
	 * 17. Okay, esta corrigido a atualizacao com relacao ao Spring Data JPA 2, que eh a versao que esta vindo, agora, com o Spring Boot.
	 * Fim da Aula 25.02. Novas Assinaturas do Spring Data JPA.
	 * 
	 * @param codigo
	 * @return
	 */

	@PreAuthorize("hasAuthority('ROLE_PESQUISAR_PESSOA') and #oauth2.hasScope('read')")
	@GetMapping("/{codigo}")
	public ResponseEntity<Pessoa> buscarPeloCodigo(@PathVariable Long codigo) {
		/** Pessoa pessoa = pessoaRepository.findOne(codigo); **/
		Optional<Pessoa> pessoa = pessoaRepository.findById(codigo);
		/** return pessoa != null ? ResponseEntity.ok(pessoa) : ResponseEntity.notFound().build(); **/
		return pessoa.isPresent() ? ResponseEntity.ok(pessoa.get()) : ResponseEntity.notFound().build();
	}
	
	@GetMapping(params = "codigo")
	@PreAuthorize("hasAuthority('ROLE_PESQUISAR_PESSOA') and #oauth2.hasScope('read')")
	public ResponseEntity<Long> buscarUltimoRegistro() {
		Long codigo = this.pessoaRepository.buscarUltimoRegistro(); 
		return codigo != null ? ResponseEntity.ok(codigo) : ResponseEntity.notFound().build();
	}

	@PreAuthorize("hasAuthority('ROLE_REMOVER_PESSOA') and #oauth2.hasScope('write')")	
	@DeleteMapping("/{codigo}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void remover(@PathVariable Long codigo) {
		/** pessoaRepository.delete(codigo); **/
		pessoaRepository.deleteById(codigo);
	}
	
	/** 1. **/
	/** Metodo com a regra de negocio. **/
	/**
	@PutMapping("/{codigo}")
	public ResponseEntity<Pessoa> atualizar(@PathVariable Long codigo, @Valid @RequestBody Pessoa pessoa) {
		Pessoa pessoaSalva = this.pessoaRepository.findOne(codigo);
		if (pessoaSalva == null) {
			throw new EmptyResultDataAccessException(1);
		}
		BeanUtils.copyProperties(pessoa, pessoaSalva, "codigo");
		return ResponseEntity.ok(this.pessoaRepository.save(pessoaSalva));
	}
	**/

	@PreAuthorize("hasAuthority('ROLE_CADASTRAR_PESSOA') and #oauth2.hasScope('write')")	
	/** 2. **/
	/** Metodo com a regra de negocio extraido para uma classe de servico. **/
	@PutMapping("/{codigo}")
	public ResponseEntity<Pessoa> atualizar(@PathVariable Long codigo, @Valid @RequestBody Pessoa pessoa) {
		return ResponseEntity.ok(this.pessoaService.atualizar(codigo, pessoa));
	}
	
	/**
	 * 
	 * @param codigo
	 * @param pessoa
	 * @return
	 */
	/** Para atualizacao parcial de uma propriedade, expoe essa propriedade.
	 * Retornar NO_CONTENT. Nao eh necessario retornar nada quando atualizar essa propriedade. **/
	@PreAuthorize("hasAuthority('ROLE_CADASTRAR_PESSOA') and #oauth2.hasScope('write')")	
	@PutMapping("/{codigo}/ativo")
	@ResponseStatus(code = HttpStatus.NO_CONTENT)
	public void atualizarPropriedadeAtivo(@PathVariable Long codigo, @RequestBody Boolean ativo) {
		this.pessoaService.atualizarPropriedadeAtivo(codigo, ativo);
	}

}
