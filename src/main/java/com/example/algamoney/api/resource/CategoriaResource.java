package com.example.algamoney.api.resource;

import java.util.List;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.algamoney.api.event.RecursoCriadoEvent;
import com.example.algamoney.api.model.Categoria;
import com.example.algamoney.api.repository.CategoriaRepository;

@RestController
@RequestMapping("/categorias")
public class CategoriaResource {

	@Autowired
	private CategoriaRepository categoriaRepository;
	
	@Autowired
	private ApplicationEventPublisher publisher;
	
	/**
	 * CORS - Cross-origin resource sharing
	 * 
	 * N�o ser� utilizado neste Projeto porque integra��o com Spring Security OAuth2 n�o est�
	 * legal nesta vers�o do Spring Security.
	 * 
	 * Com o CORS, como a Requisi��o de Preflight � o Browser quem faz sem passar as Credenciais de Seguran�a,
	 * o Spring Security n�o permite fazer a Requisi��o.
	 * 
	 * 
<html>
<body>

<button onclick="buscar()">Buscar Categorias</button>

<script>
	function printResponse() {
		console.log(this.responseText);
	}
	
	function buscar() {
	/** Criar um objeto XMLHttpRequest. 
	* Esse objeto que o Browser disponibiliza � para fazermos
	* requisi��es HTTP pelo Browser. As requisi��es AJAX s�o
	* feitas a partir do objeto XMLHttpRequest.
	**
		var req = new XMLHttpRequest();
		/** Adiciona um evento Listener ao objeto, 
		* definido como a fun��o printResponse, a qual
		* vai imprimir o texto da Resposta quando
		* carregar (load). Ou seja, vou fazer uma Requisi��o
		* HTTP, assim que terminar essa Requisi��o, quero que
		* imprima a Resposta.
		**
		req.addEventListener("load", printResponse);
		/** Aqui ainda n�o est� mandando a Requisi��o. Est� apenas definindo que vai fazer uma Requisi��o GET
		* para determinado Endere�o.
		**
		req.open("GET", "http://localhost:8080/categorias");
		/** Adicionando um determinado Header para for�ar
		* a Requisi��o funcionar.
		**
		req.setRequestHeader('HEADER1', 'VALOR');
		req.send();
	}
</script>

</body>
</html>

	* Neste caso, um Servidor Phyton est� servindo na porta
	* 8000, � um dom�nio separado.
	* Funciona no Postman, mas n�o funciona no Javascript.
	* Porque existe uma prote��o, o Browser implementa uma
	* pol�tica de seguran�a em que ele s� permite o Javascript
	* fazer requisi��es para a mesma Origem (Headers / Origin):
	* Headers
	* Cache-Control: no-cache
	* Connection: keep-alive
	* Host: localhost:8080
	* Origin: http://localhost:8000
	* Pragma: no-cache
	* Referer: http://localhost:8000/teste-get-categorias.html
	* User-Agent: Mozilla/5.0
	* 
	* Se tivesse carregado o Javascript a partir da Aplica��o
	* Spring, http://localhost:8080, iria funcionar. Mas est�
	* levantando a Aplica��o em outro Servidor.
	*
	* Dom�nio � composto pelo:
	* 		Protocolo (HTTP/HTTPS),
	*		Endere�o (localhost:8080) localhost + a porta
	*		
	* Se o Protocolo ou o Endere�o mudar, j� n�o � mais
	* a mesma origem.
	* 
	* Essa prote��o � conhecida como Same-origin policy.
	* O Javascript s� pode fazer requisi��es para a mesma origem.
	*
	* As exce��es s�o habilitadas atrav�s do CORS.
	*
	* Se habilitar o CORS, vai permitir fazer requisi��es
	* em origens diferentes.
	*
	* Adicionar uma exce��o para Origin.
	*
	*
	* Headers
	* Request URL: http://localhost:8080/categorias
	* Request Method: OPTIONS
	* Status Code: 403
	* Remote Address: [::1]: 8080
	* Referrer Policy: no-referrer-when-downgrade
	*
	* Est� fazendo um GET, mas apareceu um m�todo OPTIONS, ao
	* inv�s de GET. 
	* 
	* OPTIONS � chamado de Preflighted request.
	* 
	* Antes do Browser fazer a Requisi��o, ele pergunta o que
	* ele pode fazer. O Servidor responde: nessa URL, voc� pode fazer GET/POST/etc. Isso � definido com a anota��o
	* @CrossOrigin
	* 
	* � uma requisi��o simples que o Browser envia antes para saber se o Browser poder� fazer uma determinada requisi��o depois. O Browser manda esse OPTIONS para a URL que quer fazer a Requisi��o.
	* 
	*  maxAge: Depois que a Requisi��o de Preflight � autorizada,  depois que deu certo,
	*  vai fazer uma pr�ximia Requisi��o depois de 10 segundos.
	*  Essa Requisi��o de Preflight, com o OPTIONS, � mantida em cache por um determinado tempo: maxAge.
	* 
	* Olhando Aba Network no Browser:
	* Fez primeiro uma Requisi��o com OPTIONS, retornando 200:
	* Headers
	* Request URL: http://localhost:8080/categorias
	* Request Method: OPTIONS
	* Status Code: 200
	* 
	* A Requisi��o em seguida � o GET para /categorias:
	* Headers
	* Request URL: http://localhost:8080/categorias
	* Request Method: GET
	* Status Code: 200
	* 
	* Headers de Resposta com Sucesso (200) na Primeira Requisi��o com OPTIONS
	* Response Headers
	* 		Access-Control-Allow-Credentials: true [Significa que poderia mandar credenciais de seguran�a tamb�m, como um Cookie]
	* 		Access-Control-Allow-Headers: header1 [Aqui s�o definidos os Headers que s�o permitidos]
	* 		Access-Control-Allow-Methods: GET 	[Aqui s�o definidos os m�todos permitidos]
	* 		Access-Control-Allow-Origin: http://localhost:8000	[Aqui � definida a Origem que � permitida]
	* 		Access-Control-Max-Age: 10
	* 
	**/	
	/** Permitir com que todas as origens sejam autorizadas a chamar o 
	 * m�todo listar(), ou seja, fazer GET em /categorias **/
	/** maxAge: quanto tempo para o Browser fazer essa Requisi��o inicial novamente. **/
	/** O localhost 8000 � permitido **/	
	/** @CrossOrigin(maxAge = 10, origins = { "http://localhost:8000" }) **/
	/** Anota��o para adicionar permiss�es: @PreAuthorize **/
	/** Passar uma String para fazer a valida��o: perguntar se tem autoridade para a ROLE para o Usu�rio que estiver usando a Aplica��o. **/
	/** hasAuthority('ROLE_PESQUISAR_CATEGORIA') � a defini��o de escopo do Usu�rio logado. 
	 * #oauth2.hasScope('read') � a defini��o de escopo da Aplica��o Cliente definido em AuthorizationServerConfig, onde � 
	 * definido qual o Cliente e qual(is) o(s) escopo(s) do Cliente. **/
	/**
	 * Exemplo: Usu�rio Admin, na Aplica��o Angular, pode Ler e Escrever.
	 * 				   Usu�rio Admin, na Aplica��o Mobile, poder apenas Ler.
	 * **/
	@PreAuthorize("hasAuthority('ROLE_PESQUISAR_CATEGORIA') and #oauth2.hasScope('read')")
	@GetMapping
	public List<Categoria> listar() {
		return categoriaRepository.findAll();
	}
	
	@PreAuthorize("hasAuthority('ROLE_CADASTRAR_CATEGORIA') and #oauth2.hasScope('write')")
	@PostMapping
	public ResponseEntity<Categoria> criar(@Valid @RequestBody Categoria categoria, HttpServletResponse response) {
		Categoria categoriaSalva = categoriaRepository.save(categoria);
		publisher.publishEvent(new RecursoCriadoEvent(this, response, categoriaSalva.getCodigo()));
		return ResponseEntity.status(HttpStatus.CREATED).body(categoriaSalva);
	}
	
	@PreAuthorize("hasAuthority('ROLE_PESQUISAR_CATEGORIA') and #oauth2.hasScope('read')")
	@GetMapping("/{codigo}")
	public ResponseEntity<Categoria> buscarPeloCodigo(@PathVariable Long codigo) {
		 Categoria categoria = categoriaRepository.findOne(codigo);
		 return categoria != null ? ResponseEntity.ok(categoria) : ResponseEntity.notFound().build();
	}
	
}
