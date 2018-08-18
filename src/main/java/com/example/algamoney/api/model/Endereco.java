package com.example.algamoney.api.model;

import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Embeddable
public class Endereco {

	private String logradouro;
	private String numero;
	private String complemento;
	private String bairro;
	private String cep;
	/**
	 * Aula 24.01. Criando Entidades Cidade e Estado
	 * 9. Remover os atributos cidade, estado para a criacao de combos dependentes.
	 * 
	 * 10. Remover getters e setters dos atributos cidade, estado para a criacao de combos dependentes.
	 * 
	 * 11. Criar atributo de associacao do tipo Cidade.
	 * Nao vamos definir associacao do tipo Estado, porque Cidade ja contem o Estado.
	 * 
	 * 12. Anotar atributo de associacao cidade com @ManyToOne e @JoinColumn.
	 * 
	 * 13. Nossas alteracoes estao prontas. O que a gente precisa fazer, agora, eh a migracao. Vamos ir, agora, ate as migracoes:
	 * src/main/resources/db/migration e vamos criar uma nova migracao: V07__criar_e_registrar_cidade_estado.sql.
	 * 
	 * 14. A migracao Cria a Tabela ESTADO, adicionar todos os estados na base, cria a tabela de CIDADE e adiciona alguns registros
	 * de cidades de exemplo para a gente poder testar.
	 * 
	 * 15. Alteramos a tabela de PESSOA, porque, apesar de a gente ter removido cidade, estado da classe de Endereco, na tabela,
	 * esses campos, colunas ficam na tabela de PESSOA, quando se fala em banco de dados. Entao, esses campos foram removidos de PESSOA e foi adicionado
	 * o campo CODIGO_CIDADE na tabela PESSOA e adicionou a Foreign Key para a tabela CIDADE e atualizamos os registros de pessoas da base de dados para 
	 * cidade com codigo 2.
	 * 
	 * 16. Atualizar o Projeto - F5 na raiz do Projeto no STS e reiniciar o Spring Boot. Vamos ver se vai ficar tudo okay e vamos fazer um teste
	 * com o Postman.
	 * 
	 * 17. Vamos abrir o Postman.  Primeiro o GET de Pessoa, Buscar Pessoa pelo Codigo: http://localhost:8080/pessoas/1. Vamos verificar se vem
	 * a Cidade dessa Pessoa:
	 * {
		    "codigo": 1,
		    "nome": "João Silva",
		    "endereco": {
		        "logradouro": "Rua do Abacaxi",
		        "numero": "10",
		        "complemento": null,
		        "bairro": "Brasil",
		        "cep": "38.400-121"
		    },
		    "ativo": true,
		    "contatos": [
		        {
		            "codigo": 1,
		            "nome": "Marcos Henrique",
		            "email": "marcos@algamoney.com",
		            "telefone": "00 0000-0000"
		        }
		    ]
		}
		* 
		* 18. Nao veio o atributo cidade no JSON. Verificamos que nao definimos o getter e setter do atributo de associacao cidade na classe Endereco.
		* Vamos definir, entao, o getter e setter de Cidade.
		* 
		* 19. Re-testando, agora, no Postman, veio o atributo de associacao cidade no JSON:
		* {
			    "codigo": 1,
			    "nome": "João Silva",
			    "endereco": {
			        "logradouro": "Rua do Abacaxi",
			        "numero": "10",
			        "complemento": null,
			        "bairro": "Brasil",
			        "cep": "38.400-121",
			        "cidade": {
			            "codigo": 2,
			            "nome": "Uberlândia",
			            "estado": {
			                "codigo": 11,
			                "nome": "Minas Gerais"
			            }
			        }
			    },
			    "ativo": true,
			    "contatos": [
			        {
			            "codigo": 1,
			            "nome": "Marcos Henrique",
			            "email": "marcos@algamoney.com",
			            "telefone": "00 0000-0000"
			        }
			    ]
			}  
		*  
		*  20. Vamos, agora, alterar uma Pessoa, PUT Pessoa, http://localhost:8080/pessoas/1, vamos alterar Pessoa de Codigo 1.
		*  Vamos mudar o Codigo da Cidade, ao inves de codigo 2, vamos utilizar o codigo 1:
		*  {
			    "codigo": 1,
			    "nome": "João Silva",
			    "endereco": {
			        "logradouro": "Rua do Abacaxi",
			        "numero": "10",
			        "complemento": null,
			        "bairro": "Brasil",
			        "cep": "38.400-121",
			        "cidade": {
			            "codigo": 1
			        }
			    },
			    "ativo": true,
			    "contatos": [
			        {
			            "codigo": 1,
			            "nome": "Marcos Henrique",
			            "email": "marcos@algamoney.com",
			            "telefone": "00 0000-0000"
			        }
			    ]
			}
		*  
		*  21. Okay, retornou Status 200 OK
		*  
		*  22. Vamos, agora, verificar se atulizou conforme esperado com GET Pessoa, http://localhost:8080/pessoa/1, Buscar Pessoa
		*  pelo Codigo 1.
		*  
		*  23. Okay, verificamos que foi alterado a cidade conforme o esperado para a cidade de codigo 1.
		*  {
			    "codigo": 1,
			    "nome": "João Silva",
			    "endereco": {
			        "logradouro": "Rua do Abacaxi",
			        "numero": "10",
			        "complemento": null,
			        "bairro": "Brasil",
			        "cep": "38.400-121",
			        "cidade": {
			            "codigo": 1,
			            "nome": "Belo Horizonte",
			            "estado": {
			                "codigo": 11,
			                "nome": "Minas Gerais"
			            }
			        }
			    },
			    "ativo": true,
			    "contatos": [
			        {
			            "codigo": 1,
			            "nome": "Marcos Henrique",
			            "email": "marcos@algamoney.com",
			            "telefone": "00 0000-0000"
			        }
			    ]
			}
	 * 
	 * 24. Fim da Aula 24.01. Criando Entidades Cidade e Estado.
	 * 
    private String cidade;
	private String estado;
	**/
	@ManyToOne
	@JoinColumn(name = "codigo_cidade")
	private Cidade cidade;
	
	public String getLogradouro() {
		return logradouro;
	}

	public void setLogradouro(String logradouro) {
		this.logradouro = logradouro;
	}

	public String getNumero() {
		return numero;
	}

	public void setNumero(String numero) {
		this.numero = numero;
	}

	public String getComplemento() {
		return complemento;
	}

	public void setComplemento(String complemento) {
		this.complemento = complemento;
	}

	public String getBairro() {
		return bairro;
	}

	public void setBairro(String bairro) {
		this.bairro = bairro;
	}

	public String getCep() {
		return cep;
	}

	public void setCep(String cep) {
		this.cep = cep;
	}

	public Cidade getCidade() {
		return cidade;
	}

	public void setCidade(Cidade cidade) {
		this.cidade = cidade;
	}

	/**
	public String getCidade() {
		return cidade;
	}

	public void setCidade(String cidade) {
		this.cidade = cidade;
	}

	public String getEstado() {
		return estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}
	**/
}
