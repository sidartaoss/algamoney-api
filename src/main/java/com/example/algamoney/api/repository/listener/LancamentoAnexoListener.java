/**
 * 
 */
package com.example.algamoney.api.repository.listener;

import javax.persistence.PostLoad;

import org.springframework.util.StringUtils;

import com.example.algamoney.api.AlgamoneyApiApplication;
import com.example.algamoney.api.model.Lancamento;
import com.example.algamoney.api.storage.S3;

/**
 * @author sosilva
 * Aula 22.36. Configurando URL do Anexo
 * No Postman, nos vamos fazer uma Requisicao para o Lancamento de Codigo 16. Vamos usar 
 * GET, http://localhost:8080/lancamentos/16. Por que esse registro? Porque esse 
 * Lancamento tem um anexo. Retornou:
 * 		{
 * 		    "codigo": 16,
 * 		    "descricao": "Viagem Julho",
 * 		    "dataVencimento": "2018-07-27",
 * 		    "dataPagamento": null,
 * 		    "valor": 6500,
 * 		    "observacao": "Nordeste",
 * 		    "tipo": "DESPESA",
 * 		    "categoria": {
 * 		        "codigo": 1,
 * 		        "nome": "Lazer"
 * 		    },
 * 		    "pessoa": {
 * 		        "codigo": 1,
 * 		        "nome": "Joao Silva",
 * 		        "endereco": {
 * 		            "logradouro": "Rua do Abacaxi",
 * 		            "numero": "10",
 * 		            "complemento": null,
 * 		            "bairro": "Brasil",
 * 		            "cep": "38.400-12",
 * 		            "cidade": "Uberlandia",
 * 		            "estado": "MG"
 * 		        },
 * 		        "ativo": true
 * 		    },
 * 		    "anexo": "8e3819b8-6e61-4e07-a27f-4c12299f9194_CodeConventions.pdf",
 * 		    "urlAnexo": null
 * 		}
 * O que acontece? Nos temos um anexo, mas ainda nao temos retornado a URL para esse anexo. 
 * E eh isso que a gente vai trabalhar nesta aula. Como que a gente vai fazer isso? Nos vamos 
 * criar um Listener do JPA para que, toda a vez que o objeto Lancamento for carregado da 
 * base de dados, nos queremos que o JPA preencha o atributo urlAnexo, de forma que, quando 
 * ele for devolvido na Resposta, ele ja vai ter a URL preenchida.
 * 
 * 1. Vamos abrir o STS e a primeira coisa que nos iremos fazer eh criar esse Listener. 
 * Ele vai ser criado no pacote .repository, mais especificamente no pacote .listener. 
 * O nome da classe sera LancamentoAnexoListener.  
 */
public class LancamentoAnexoListener {

	/**
	 * Aula 22.36. Configurando URL do Anexo  
	 * 2. Para nos criarmos um Listener do JPA, eh muito simples. A gente simplesmente cria 
	 * um metodo, cujo nome pode ser qualquer um e o que a gente precisa mesmo eh da anotacao 
	 * @PostLoad, do pacote javax.persistence. Esse metodo precisa receber, como parametro,
	 * Lancamento.
	 * **/
	@PostLoad
	public void postLoad(Lancamento lancamento) {
		/**
		 * Aula 22.36. Configurando URL do Anexo
		 * 3. Agora, nos vamos fazer uma verificacao para ver se tem anexo no Lancamento
		 * passado como parametro. Isso porque nos so vamos preencher a URL se tiver o 
		 * anexo em Lancamento. Senao, nao adianta chamarmos aquele metodo configurarUrl,
		 * que esta dentro da nossa classe S3.
		 * */
		if (StringUtils.hasText(lancamento.getAnexo())) {
			/**
			 * Aula 22.36. Configurando URL do Anexo
			 * 4. Se tiver texto, nos vamos mandar montar a URL a partir do metodo
			 * configurarUrl da classe S3. Qual eh o problema que nos temos aqui?
			 * A instancia desta classe (LancamentoAnexoListener), quem vai construir eh o 
			 * Hibernate, no final das contas, eh o framework Hibernate e nao eh o Spring.
			 * E qual eh o problema de nao ser o Spring? Como nao eh o Spring, nos nao podemos
			 * injetar a classe S3, ou seja: 
			 * @Autowired
			 * private S3 s3;
			 * Nao podemos fazer isso, porque quem cria essa instancia nao eh o Spring, eh o 
			 * Hibernate. Entao, nao podemos utilizar a injecao de dependencias do Spring.
			 * Entao, o que temos que fazer? Nos vamos ate a classe AlgamoneyApiApplication
			 * e vamos criar uma instancia estatica. Ver AlgamoneyApiApplication.java.
			 * 
			 * 8. Definir uma variavel local chamada s3 da classe S3.
			 * E vamos utilizar o metodo getBean() da classe AlgamoneyApliApplication,
			 * passando, como parametro, S3.class, que eh o tipo da instancia que nos queremos
			 * receber de volta.
			 * 
			 * */
			S3 s3 = AlgamoneyApiApplication.getBean(S3.class);
			/**
			 * Aula 22.36. Configurando URL do Anexo
			 * 9. Agora, o que vamos fazer? Nos vamos chamar o 
			 * metodo setUrlAnexo() de lancamento, passando, como parametro, 
			 * s3.configurarUrl(), passando lancamento.getAnexo para o nome do objeto.
			 * Ainda nao esta terminado. Nao adianta somente criar esta classe e este
			 * metodo e anota-lo com @PostLoad.
			 * Eh necessario ir na entida Lancamento e anotar essa entidade com 
			 * @EntityListener.
			 * Ver classe Lancamento.java.
			 * */
			lancamento.setUrlAnexo(s3.configurarUrl(lancamento.getAnexo()));
			/**
			 * Aula 22.36. Configurando URL do Anexo
			 * 11. Agora, podemos subir o servidor e testar no Postman.
			 * Vamos testar novamente o Lancamento com idenficador 16. Agora, nos deveremos
			 * ver a URL do anexo, porque esse Lancamento possui anexo:
			 * GET, http://localhost:8080/lancamentos/16
			 * Resposta:
			 * {
				    "codigo": 16,
				    "descricao": "Viagem Julho",
				    "dataVencimento": "2018-07-27",
				    "dataPagamento": null,
				    "valor": 6500,
				    "observacao": "Nordeste",
				    "tipo": "DESPESA",
				    "categoria": {
				        "codigo": 1,
				        "nome": "Lazer"
				    },
				    "pessoa": {
				        "codigo": 1,
				        "nome": "Joao Silva",
				        "endereco": {
				            "logradouro": "Rua do Abacaxi",
				            "numero": "10",
				            "complemento": null,
				            "bairro": "Brasil",
				            "cep": "38.400-12",
				            "cidade": "Uberlandia",
				            "estado": "MG"
				        },
				        "ativo": true
				    },
				    "anexo": "8e3819b8-6e61-4e07-a27f-4c12299f9194_CodeConventions.pdf",
				    "urlAnexo": "\\\\sidarta.silva-algamoney-arquivos.s3.amazonaws.com/8e3819b8-6e61-4e07-a27f-4c12299f9194_CodeConventions.pdf"
				}
				* Agora, foi retornado a URL. Nos vamos copiar a URL: sidarta.silva-algamoney-arquivos.s3.amazonaws.com/8e3819b8-6e61-4e07-a27f-4c12299f9194_CodeConventions.pdf
				* sem os contra-barras, porque a gente nao precisa disso. Essas barras serao uteis 
				* somente quando a gente estiver utilizando isso no nosso Cliente, porque, na hora
				* que for fazer o parser desse JSON para um objeto, por exemplo, Javascript,
				* ou qualquer outro Cliente que faca um parser para qualquer outra linguagem
				* ou qualquer outra coisa, essas 4 contra-barra vao se tornar somente 2 barras que
				* vai possibilitar se utilizar quando for se montar um link e colocar essa URL.
				* Vamos abrir o Browser, agora, vamos abrir uma nova aba e vamos colar a
				* URL e vamos fazer a Requisicao.
				* Verificamos que se trata, justamente, do arquivo que nos enviamos para upload.
				* Entao, agora, nos ja temos a funcionalidade de, quando formos buscar um 
				* Lancamento, a gente ja tem preenchido a URL do anexo, para que os possiveis 
				* clientes do nosso backend possam tirar proveito dessa URL.
				* 
			 * */
		}
	}
}
