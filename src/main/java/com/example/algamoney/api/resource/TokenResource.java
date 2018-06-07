package com.example.algamoney.api.resource;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.algamoney.api.config.property.AlgamoneyApiProperty;

/**
 * Implementar Logout.
 * Problema: Estamos usando JSON Web Token. Com ele, nao temos armazenamento do token. O que fazer para fazer o Logout, ja que nao tem como verificar o tempo de expiracao do token?
 *
 * Quando Aplicacao Cliente clicar em Logout, a aplicacao vai apagar o Access Token da memoria que o Browser estiver usando na Aplicacao Cliente.
 *  E vai chamar no Servidor para remover o Refresh Token para remover o Refresh Token do Cookie.
 *
 * Nao tem nada pronto assim no Spring Security Oauth.
 * Entao, eh necessario criar uma Classe Resource de Tokens.
 *
 * Logout eh tirar o valor do Refresh Token.
 *
 * @author sosilva
 *
 */
@RestController
@RequestMapping("/tokens")
public class TokenResource {

	@Autowired
	private AlgamoneyApiProperty algamoneyApiProperty;
	
	/** Chamar um metodo para revogar, pegar de volta, invalidar um Token. 
	 * Vai fazer isso quando chamar na URL /revoke 
	 * O verbo DELETE na URL revoke **/
	@DeleteMapping("/revoke")
	public void revoke(HttpServletRequest request, HttpServletResponse response) {
		/** Aqui vai fazer o metodo apagar o Cookie. Para tanto, deve receber um HttpServletRequest e um HttpServletResponse. 
		 * Remover o Cookie com a API de Servlet normal.
		 * Definir o Cookie com conteudo vazio (nulo). **/
		Cookie refreshTokenCookie = new Cookie("refreshToken", null);
		refreshTokenCookie.setHttpOnly(true);
		refreshTokenCookie.setSecure(algamoneyApiProperty.getSeguranca().isEnableHttps());	
		/** Tirando o Cookie nessa URL: **/
		refreshTokenCookie.setPath(request.getContextPath() + "/oauth/token");
		/** Quando quer que o Cookie expire? Agora. **/
		refreshTokenCookie.setMaxAge(0);
		
		response.addCookie(refreshTokenCookie);
		response.setStatus(HttpStatus.NO_CONTENT.value());
		
	}
}
