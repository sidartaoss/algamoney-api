package com.example.algamoney.api.service;

import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import com.example.algamoney.api.model.Pessoa;
import com.example.algamoney.api.repository.PessoaRepository;

/**
 * 
 * @author sosilva
 *
 */

/**
 * Anotacao @Service serve para definir que esta classe sera um componente do
 * Spring. O Spring podera injeta-la onde precisar nos componentes do Spring.
 * Define-se a regra de negocio em uma classe mais especifica para negocio,
 * deixando o codigo mais organizado.
 **/
@Service
public class PessoaService {

	@Autowired
	private PessoaRepository pessoaRepository;

	public Pessoa atualizar(Long codigo, Pessoa pessoa) {
		Pessoa pessoaSalva = this.buscarPessoaPeloCodigo(codigo);
		
		/** Aula 22.26. Usando a propriedade orphanRemoval **/
		/** 2. Para continuarmos trabalhando com a lista persistente, ou seja, a lista de Contatos 
		 * na variavel pessoaSalva, iremos definir da seguinte forma:
		 * pessoaSalva.getContatos().clear()
		 * Nos vamos so limpar a lista, nos nao vamos definir uma nova instancia, iremos so limpa-la,
		 * nao vamos definir uma nova instancia. 
		 * **/
		pessoaSalva.getContatos().clear();
		/** Aula 22.26. Usando a propriedade orphanRemoval **/
		/** 3. Agora, nos vamos adicionar, na variavel pessoaSalva, todos os contatos que
		 * estao na Pessoa que esta vindo por parametro.  **/
		pessoaSalva.getContatos().addAll(pessoa.getContatos());
		
		/** Aula 22.26. Usando a propriedade orphanRemoval **/
		/** 4. Feito isso,  a gente vai alterar 
		 * de pessoa.getContatos().forEach(c -> c.setPessoa(pessoa)); para 
		 * pessoaSalva.getContatos().forEach(c -> c.setPessoa(pessoaSalva)); **/
		pessoaSalva.getContatos().forEach(c -> c.setPessoa(pessoaSalva));
		
		/** Aula 22.25. Inserindo uma Pessoa com Contato **/
		/** 3. Vamos setar a pessoa, objeto pessoa que esta vindo como
		 * 		parametro para cada Contato. Agora, podemos testar essa 
		 * 		atualizacao tambem: PUT http://localhost:8080/pessoas/11
		 * {
			    "codigo": 11,
			    "nome": "Carlos da Silva",
			    "endereco": {
			        "logradouro": "Rua Laranja",
			        "numero": "10",
			        "complemento": null,
			        "bairro": "Brasil",
			        "cep": "38.400-12",
			        "cidade": "Uberlandia",
			        "estado": "MG"
			    },
			    "ativo": true,
			    "contatos": [
			        {
			            "codigo": 2,
			            "nome": "Fernando Henrique",
			            "email": "fernando@algamoney.com",
			            "telefone": "00 0000-0000"
			        }
			    ]
			}
			N.T.: Deve ser informado o atributo codigo de Pessoa no Corpo do JSON, porque
			esse atributo eh utilizado ao setar Pessoa para cada Contato. Caso 
			nao informar, ocorre o erro do Hibernate:
			"org.hibernate.TransientPropertyValueException: object references an unsaved 
			transient instance - save the transient instance before flushing : com.example.
			algamoney.api.model.Contato.pessoa -> com.example.algamoney.api.model.Pessoa; 
			nested exception is java.lang.IllegalStateException: org.hibernate.
			TransientPropertyValueException: object references an unsaved transient instance - 
			save the transient instance before flushing : com.example.algamoney.api.model.
			Contato.pessoa -> com.example.algamoney.api.model.Pessoa"
			Por esta aula eh isto, nos corrigimos esse problema de inserir e atualizar uma Pessoa
			com o seu Contato.
			Fim da Aula 22.25. Inserindo uma Pessoa com Contato.
		 * **/
		/** pessoa.getContatos().forEach(c -> c.setPessoa(pessoa)); **/
		
		/** Aula 22.26. Usando a propriedade orphanRemoval **/
		/** 5. Agora que a gente ja esta atualizando a propriedade contatos de
		 * pessoaSalva, entao a gente vai ignorar essa propriedade contatos tambem na copia
		 * a seguir:
		 *   **/
		BeanUtils.copyProperties(pessoa, pessoaSalva, "codigo", "contatos");
		/**
		 *  Aula 22.26. Usando a propriedade orphanRemoval
		 * 	6. Agora, devera funcionar, ou seja, deve atualizar a lista de Contatos para uma
		 * lista vazia. Vamos voltar para o Postman para testarmos:
		 *   PUT, localhost:8080/pessoas/11,
		 *   {
			    "codigo": 11,
			    "nome": "Carlos da Silva e Silva",
			    "endereco": {
			        "logradouro": "Rua Laranja",
			        "numero": "10",
			        "complemento": null,
			        "bairro": "Brasil",
			        "cep": "38.400-12",
			        "cidade": "Uberlandia",
			        "estado": "MG"
			    },
			    "ativo": true,
			    "contatos": []
			}
			Verificamos, realmente, que a lista de Contatos foi removida e resolvemos o problema
			de atualizarmos um registro de Pessoa com uma Lista de Contatos vazia.
			Fim da Aula 22.26. Usando a propriedade orphanRemoval.
			**/
		return this.pessoaRepository.save(pessoaSalva);
	}

	public void atualizarPropriedadeAtivo(Long codigo, Boolean ativo) {
		Pessoa pessoaSalva = this.buscarPessoaPeloCodigo(codigo);
		pessoaSalva.setAtivo(ativo);
		this.pessoaRepository.save(pessoaSalva);
	}
	
	/**
	 * Aula 25.02. Novas Assinaturas do Spring Data JPA
	 * 
	 * 6. Com a migracao para o Spring Boot 2, corrigir o metodo buscarPessoaPeloCodigo(). Substituir o metodo findOne() por findById().
	 * 
	 *  7. Ao inves de verificar pessoaSalva == null, definir !pessoaSalva.isPresent().
	 *  
	 *  8. Se estiver presente, devolver Pessoa, chamando o metodo get de Optional.
	 *  
	 *  9. Okay, as classes do pacote .service estao corrigidas. Vamos aqui, agora, para o pacote .resource, classe CategoriaResource.java.
	 *  Ver CategoriaResource.java.
	 * 
	 * @param codigo
	 * @return
	 */

	public Pessoa buscarPessoaPeloCodigo(Long codigo) {
		/** Pessoa pessoaSalva = this.pessoaRepository.findOne(codigo); **/
		Optional<Pessoa> pessoaSalva = this.pessoaRepository.findById(codigo);
		/** if (pessoaSalva == null) { **/
		if (!pessoaSalva.isPresent()) {
			throw new EmptyResultDataAccessException(1);
		}
		return pessoaSalva.get();
	}

	/**
	 * Aula 22.25. Inserindo uma Pessoa com Contato
	 * 2. Neste metodo, utilizando Lambda, 
	 * nos vamos obter os contatos que estao dentro do parametro Pessoa
	 * e vamos setar o parametro pessoa.
	 * @param pessoa
	 * @return
	 */
	public Pessoa salvar(Pessoa pessoa) {
		/** Aula 22.25. Inserindo uma Pessoa com Contato
		 * **/
		/** Vamos utilizar forEach() (expressao Lambda) sobre contatos. 
		 * Definimos c como o parametro da Lambda (item da lista de Contatos). 
		 * O corpo da expressao Lambda sera c.setPessoa(pessoa); **/
		pessoa.getContatos().forEach(c -> c.setPessoa(pessoa));
		/** Agora, so precisamos utilizar o Repositorio para salvar, como estavamos fazendo
		 *  na Classe PessoaResource. 
		 *  Dessa forma, quando formos testar, ja tem que funcionar a insercao de Pessoa. 
		 *  Podemos voltar para o Postman para testarmos:
		 *  POST em http://localhost:8080/pessoas 
		 *  Lembrando que o que a gente fez foi remover a obrigatoriedade
		 *  do atributo pessoa na Classe Contato, mas, em PessoaService, nos
		 *  setamos, isto eh, forcamos a configuracao da Pessoa dentro do Contato.
		 *  Entao, por isso qu nao teremos perigo de um Contato ser salvo sem
		 *  ter uma Pessoa configurada (associada). Agora, nos vamos copiar a linha:
		 *  pessoa.getContatos().forEach(c -> c.setPessoa(pessoa)); e vamos fazer a mesma
		 *  coisa para atualizarmos.
		 *  Ver metodo atualizar. **/
		return pessoaRepository.save(pessoa);
	}
}
