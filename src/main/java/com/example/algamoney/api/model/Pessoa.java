package com.example.algamoney.api.model;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "pessoa")
public class Pessoa {

	@Id
	/** @GeneratedValue(strategy = GenerationType.IDENTITY) **/
	private Long codigo;

	@NotNull
	private String nome;

	@Embedded
	private Endereco endereco;

	@NotNull
	private Boolean ativo;
	
	/** Aula 22.23. Criando a Entidade Contato para Suportar Mestre-Detalhe 
	 * 11. Vamos criar a associacao Lista de Contatos.
	 * 		Vamos anotar com @OneToMany. Vai fazer uma referencia para o que ja foi mapeado dentro de Contato, que eh
	 * a propriedade pessoa. Vamos anota tambem com a anotacao @Valid, porque a Lista de Contatos tem que ser uma lista valida.
	 * Ja que iremos inserir os Contatos pela entidade Pessoa, entao nos precisamos que essa Lista de Contatos seja uma lista 
	 * valida. Vamos definir tambem, na anotacao OneToMany, o atributo cascade como CascadeType.ALL, ja que a gente vai fazer
	 * as insercoes de Contatos atraves da entidade Pessoa.
	 * 
	 *  Agora, a gente vai criar a migracao: arquivo SQL.
	 *  Ver src/main/resources/db/migration/V05__criar_tabela_contato.sql.
	 *  
	 * 12. Como nos criamos uma entidade nova: Contato, nos nao temos o Meta-Model dela. Para gerar o Meta-Model:
	 * a. Deletar as classes de Meta-Model (com Nome + underline) do pacote .model;
	 * b. Clicar com o botao direito na raiz do Projeto: algamoney-api para gerarmos novas classes de Meta-Model;
	 * c. Ir em Properties -> Java Compiler -> Annotation Processing. Em Generated source directory, definir o Caminho: src/main/java
	 * d. Em Java Compiler -> Annotation Processing -> Factory Path, desmarcar a opcao do JAR hibernate-jpamodelgen-5.2.16.Final.jar e clicar 
	 * 		no botao Apply. Confirmar a mensagem para fazer o build do projeto. Marcar novamente a opcao do 
	 *     JAR hibernate-jpamodelgen-5.2.16.Final.jar e clicar no botao Apply. 
	 *     Confirmar a mensagem para fazer o build do projeto.
	 *     Clicar em Apply and Close.
	 * 
	 *  13. Nesta aula, entao, nos criamos a entidade Contato, a associacao contatos dentro da classe Pessoa
	 *  e re-geramos o Meta-Model das nossas classes para incluir tambem a entidade Contato.
	 *  N.T.: Eh necessario remover/comentar a dependencia do pom.xml para evitar erro de compilacao
	 *  			por classes duplicadas. Ao tentar Run As -> Maven install, o Maven considera as classes 
	 *  			Meta-Model como duplicadas.
	 *  <dependency>
		    <groupId>org.hibernate</groupId>
		    <artifactId>hibernate-jpamodelgen</artifactId>
		    <version>5.2.16.Final</version>
		    <scope>provided</scope>
		</dependency>
	 *  Por esta aula eh isto.
	 *  Fim da Aula 22.23. Criando a Entidade Contato para Suportar Mestre-Detalhe.
	 *   
	 *  **/
	/**
	 * Aula 22.24. Resolvendo o StackOverflowError com @JsonIgnoreProperties

	 	* Nesta aula, nos vamos corrigir um problema que vai acontecer quando nos formos 
	 	* buscar a entidade Pessoa, quando fizermos uma Requisicao pelo recurso Pessoa.

	 	* Vamos abrir o Postman para mostrar o problema. Vamos fazer uma requisicao 
	 	* em http://localhost:8080/pessoas/1, para o identificador de Pessoa 1, porque nos 
	 	* sabemos que essa Pessoa ja possui um Contato. Na aula passada, quando fomos criar 
	 	* a migracao, nos fizemos uma insercao tambem de um Contato para uma Pessoa de 
	 	* identificador 1.

	 	* Ao mandar executar a Requisicao, eh retornado, no Corpo da Resposta, 'Bad String'.

	 	* Conferindo no Console do STS, verificamos o Warning: 
	 	* .m.m.a.ExceptionHandlerExceptionResolver : Resolved exception caused by Handler 
	 	* execution: org.springframework.http.converter.HttpMessageNotWritableException:
	 	* Could not write JSON: Infinite recursion (StackOverflowError); nested exception 
	 	* is com.fasterxml.jackson.databind.JsonMappingException: Infinite 
	 	* recursion (StackOverflowError) 
	 	* (through reference chain: com.example.algamoney.api.model.Pessoa["contatos"]->org.
	 	* hibernate.collection.internal.PersistentBag[0]->com.example.algamoney.api.model.
	 	* Contato["pessoa"]

	 	* Por que isso aconteceu? Se formos na classe Pessoa, quando for gerar o JSON dessa 
	 	* entidade, o Java vai ler as propriedades codigo, nome, endereco, ativo, normalmente como 
	 	* ja estava sendo feito. Depois o Java vai ler a lista de Contatos. Qual eh o problema? Na 
	 	* hora que o Java for ler essa lista, ele tem que vir para dentro da Contato classe Contato. 
	 	* Ai ele vair ler as propriedades da classe Contato: codigo, nome, email, telefone e vai ter 
	 	* que ler pessoa. Ai o que ele vai fazer? Ele vai entrar dentro da classe Pessoa novamente e 
	 	* vai voltar as ler as propriedades codigo, nome, endereco, ativo e vai ler contatos de novo. 
	 	* Ai eh que nos estamos entrando na recursividade infinita. Ai ele vai entrar na classe Contato 
	 	* de novo, vai ler as propriedades codigo, nome, email, telefone, vai ler pessoa de novo, 
	 	* entra em Pessoa, ai vai chegar na lista de contatos e vai ficando nessa recursividade ate 
	 	* dar o StackOverflowError. 

	 	* Como que nos corrigimos isso? Nos vamos definir uma anotacao para que, na hora 
	 	* de gerar JSON, quando o Java estiver lendo as propriedades de Contato, nos queremos 
	 	* que ele ignore a propriedade pessoa. So que nos nao vamos definir a anotacao 
	 	* @JsonIgnoreProperties na propriedade pessoa da classe Contato. Nos vamos definir 
	 	* essa anotacao na propriedade contatos da classe Pessoa e, como argumento na 
	 	* anotacao, definimos a propriedade pessoa. Por que iremos definir assim?
	 	* A gente nao vai criar agora uma Classe de Recurso para Contato,  mas se a gente 
	 	* quisesse criar e definisse @JsonIgnoreProperties na propriedade pessoa da classe
	 	* Contato, nos iriamos ter um problema, porque, se quisessemos uma Classe de Recurso
	 	* exclusiva para Contato, ai nao iriamos poder trabalhar com a propriedade pessoa. Entao,
	 	* por isso iremos preferir colocar para ignorar a propriedade a partir da classe Pessoa, 
	 	* porque foi na Requisicao da classe Pessoa que tivemos esse problema. Se nos fizessemos
	 	* uma Requisicao diretamente para Contato e definissemos @JsonIgnoreProperties na 
	 	* propriedade pessoa, ai a pessoa nao seria transformada em JSON e nao poderiamos ter
	 	* essa propriedade na nossa Resposta para a Requisicao. Por isso, iremos preferir definir
	 	* a anotacao @JsonIgnoreProperties, ignorando a propriedade pessoa da classe Contato
	 	* a partir da classe Pessoa. Com essa anotacao, nos conseguimos ignorar a propriedade pessoa
	 	* que esta dentro de Contato. Se, mais para a frente, quisermos criar um Recurso de Contato,
	 	* exclusivo para Contato, nos nao teremos o problema para a propriedade pessoa de Contato e
	 	* a propriedade pessoa vai ser renderizada na Resposta do JSON normalmente.
	 	* Agora, entao, vamos voltar no Postman para fazermos novamente o teste.
	 	* Mandando, novamente, a requisicao em http://localhost:8080/pessoas/1, nos obtemos
	 	* a resposta sem erros.
	 	* Por esta aula, eh isto.
	 	* Fim da Aula 22.24. Resolvendo o StackOverflowError com @JsonIgnoreProperties.
	 */
	@JsonIgnoreProperties("pessoa")
	@Valid
	/** Aula 22.26. Usando a propriedade orphanRemoval
	 * 1. Nesta aula, iremos apresentar um outro problema na nossa Aplicacao. Vamos abrir o 
	 * Postman e Buscar Pessoa pelo Codigo, GET http://localhst:8080/pessoas/11. Como 
	 * o registro com codigo 11 possui Contato, iremos utilizar a Consulta para esse registro. 

	 * Agora, nos vamos na Atualizacao de Pessoa com Codigo 11. Nessa atualizacao, nos vamos remover o contato:
    "contatos": [
        {
            "codigo": 2,
            "nome": "Fernando Henrique",
            "email": "fernando@algamoney.com",
            "telefone": "00 0000-0000"
        }
    ]
    
	 * para:
	"contatos": []

	 * Eh como se um cliente do BackEnd, que pode ser o Angular, por exemplo, falasse: 
	 * Salve essa Pessoa sem Contato, porque esse Contato esta errado ou porque nao eh mais o 
	 * Contato dessa Pessoa. Entao, o cliente vai remover. Entao, o cliente vai enviar a 
	 * Requisicao sem Contato.

	 * A Resposta parece ter sido Okay. So que, se a gente voltar na Pesquisa de Pessoa pelo 
	 * Codigo e pesquisarmos novamente, o Contato continua la. Ou seja, foi 
	 * feita uma Requisicao para se atualizar o Contato com uma Lista vazia, a lista de Contato 
	 * estava vazia na Resposta, so que nao foi removido do banco de dados.

	 * Nos, basicamente, temos que fazer duas coisas.
	 * Nos vamos abrir o STS e ver. 

	 * A primeira coisa que a gente vai fazer eh em Pessoa, onde a gente vai utilizar a opcao 
	 * orphanRemoval na anotacao OneToMany. O que vai acontecer com essa definicao
	 * orphanRemoval = true? Na hora que uma Pessoa for para ser salva, o JPA 
	 * vai fazer uma verificacao e tudo que tiver na base de dados que nao estiver na nossa
	 * lista, vai ser removido. Isso por causa do atributo orphanRemoval definido como true.
	 * Nos vamos fazer um teste, agora, mas vamos adiantando que ainda temos outro problema
	 * para resolver. So vamos fazer o teste para que vejamos o problema. 
	 * Entao, vamos abrir a Requisicao para atualizar uma Pessoa pelo Codigo informado.
	 * A lista de Contatos deve estar vazia.
	 * {
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
	Vamos mandar atualizar.
	Ocorreu um erro: A collection with cascade=\"all-delete-orphan\" was no longer 
		referenced by the owning entity instance: com.example.algamoney.api.model.Pessoa.contatos; 
		nested exception is org.hibernate.HibernateException: A collection with 
		cascade=\"all-delete-orphan\" was no longer referenced by the owning entity instance: 
		com.example.algamoney.api.model.Pessoa.contatos
	O que que aconteceu? O erro esta dizendo que a colecao com cascade = delete-orphan (opcao 
	que nos definimos) nao esta sendo mais referenciada pela entidade pai que, no caso, eh Pessoa.
	Ou seja, a lista de Contatos, na hora em que o JPA foi tentar inseri-la, ele viu que nao era mais
	uma lista persistente, eh como se ela nao fosse mais uma lista persistente. Entao, foi lancado
	o erro.
	O que nos vamos fazer? Nos vamos consertar isso da seguinte forma: nos vamos em 
	PessoaService e vamos alterar o o metodo atualizar().
	Ver metodo atualizar na Classe PessoaService. 
	
	 **/
	@OneToMany(mappedBy = "pessoa",  cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Contato> contatos;

	public Long getCodigo() {
		return codigo;
	}

	public void setCodigo(Long codigo) {
		this.codigo = codigo;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public Endereco getEndereco() {
		return endereco;
	}

	public void setEndereco(Endereco endereco) {
		this.endereco = endereco;
	}

	public Boolean getAtivo() {
		return ativo;
	}

	public void setAtivo(Boolean ativo) {
		this.ativo = ativo;
	}
	
	@JsonIgnore
	@Transient
	public boolean isInativo() {
		return !this.ativo;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((codigo == null) ? 0 : codigo.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Pessoa other = (Pessoa) obj;
		if (codigo == null) {
			if (other.codigo != null)
				return false;
		} else if (!codigo.equals(other.codigo))
			return false;
		return true;
	}

	public List<Contato> getContatos() {
		return contatos;
	}

	public void setContatos(List<Contato> contatos) {
		this.contatos = contatos;
	}

}
