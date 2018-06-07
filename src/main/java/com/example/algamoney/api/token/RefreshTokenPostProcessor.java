package com.example.algamoney.api.token;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import com.example.algamoney.api.config.property.AlgamoneyApiProperty;

/**
 * Classe Processador depois de o Refresh Token ter sido criado.
 * @author SEMPR
 *
 */
/** Controlador para interceptar Requisicoes. **/
/**
 * OAuth2AccessToken eh o tipo do dado que quero que seja interceptado quando estiver voltando as Respostas. **/
/**
 * Se quiser interceptar os Controladores que retornam Categoria, a entidade de ResponseBodyAdvice<> seria Categoria. Toda vez que estivesse 
 * sendo criado um Corpo de Resposta do tipo Categoria, cairia aqui nos m�todos desta classe.  
 * **/
@ControllerAdvice
public class RefreshTokenPostProcessor implements ResponseBodyAdvice<OAuth2AccessToken> {

	@Autowired
	private AlgamoneyApiProperty algamoneyApiProperty;
	
	/**
	 * S� vai executar m�todo beforeBodyWrite() quando o m�todo supports() retornar true.
	 * E somente na condi��o: Quando o nome do m�todo for igual a postAccessToken. 
	 */
	@Override
	public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
		return returnType.getMethod().getName().equals("postAccessToken");
	}

	/**
	 * Aqui que vai ser definido a regra desejada.
	 * Adicionar Refresh Token no Cookie e remover Refresh Token do Corpo da Requisicao.
	 * 
	 *  Para obter um Novo Access Token usando Refresh Token, nao vai mandar o Refresh Token no Corpo da Requisicao. Quando tem o Cookie,
	 *  ele eh enviado automaticamente; nao precisa acessa-lo no Cookie para envia-lo novamente no Corpo da Requisicao.
	 *  A nossa Aplicacao nao sabe pegar o Refresh Token do Cookie. Eh necessario ensinar a Aplicacao a pegar o Refresh Token do Cookie e coloca-lo
	 *  de volta na Requisicao. 
	 *  
	 */
	@Override
	public OAuth2AccessToken beforeBodyWrite(OAuth2AccessToken body, MethodParameter returnType,
			MediaType selectedContentType, Class<? extends HttpMessageConverter<?>> selectedConverterType,
			ServerHttpRequest request, ServerHttpResponse response) {

		/** Recuperar os atributos do Corpo da Requisi��o **/
		String refreshToken = body.getRefreshToken().getValue();
		/** Adicionar Refresh Token em um Cookie Http. **/
		/** Para adicionar o Refresh Token no Cookie, precisa do HttpServletRequest e do HttpServletResponse.
		 * Converter ServerHttpRequest e ServerHttpResponse.  **/
		HttpServletRequest req = ((ServletServerHttpRequest) request).getServletRequest();
		HttpServletResponse res = ((ServletServerHttpResponse) response).getServletResponse();
		
		this.adicionarRefreshTokenNoCookie(refreshToken, req, res);
		
		/** Fazer um cast de  OAuth2AccessToken para DefaultOAuth2AccessToken para obter metodo que 
		 * remove Refresh Token. **/
		DefaultOAuth2AccessToken token = (DefaultOAuth2AccessToken) body;
		this.removerRefreshTokenDoBody(token);
		return body;
	}

	/**
	 * Cookie eh HTTP apenas. Entao, o Javascript nao vai conseguir recuperar esse Cookie.
	 * Depois que o definirmos, em Producao, para ser Seguro (HTTPS), so vai funcionar em HTTPS, nao sera enviado em uma Requisicao HTTP normal,
	 * so em uma Requisicao HTTPS.
	 * @param refreshToken
	 * @param req
	 * @param res
	 */
	private void adicionarRefreshTokenNoCookie(String refreshToken, HttpServletRequest req, HttpServletResponse res) {
		Cookie refreshTokenCookie = new Cookie("refreshToken", refreshToken);
		/** So seja acessivel em Http. **/
		refreshTokenCookie.setHttpOnly(true);
		/** Eh um Cookie que deve ser seguro? Deve funcionar apenas em Https? false por enquanto **/
		/** Em Produ��o, vamos fazer o Deploy em Https. Tem que ser true em Producao. Em Desenvolvimento tem que ser false. **/
		refreshTokenCookie.setSecure(algamoneyApiProperty.getSeguranca().isEnableHttps());
		/** Para qual Caminho esse Cookie deve ser enviado pelo Browser automaticamente? **/
		refreshTokenCookie.setPath(req.getContextPath() + "/oauth/token");
		/** Em quanto tempo esse Cookie vai expirar em dias? 30 dias. **/
		refreshTokenCookie.setMaxAge(2592000);
		/** Adicioar Cookie na Resposta. **/
		res.addCookie(refreshTokenCookie);
	}
	
	private void removerRefreshTokenDoBody(DefaultOAuth2AccessToken token) {
		token.setRefreshToken(null);
	}

}
