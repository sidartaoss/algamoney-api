package com.example.algamoney.api.config.token;

import java.util.HashMap;
import java.util.Map;

import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;

import com.example.algamoney.api.security.UsuarioSistema;

/**
 * Classe para incrementar o Token com customizacao.
 * @author SEMPR
 *
 */
public class CustomTokenEnhancer implements TokenEnhancer {

	@Override
	public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
		/** Já recebe a autenticacao:  OAuth2Authentication authentication. 
		 *   getPrincipal() para pegar o usuario logado. 
		 *   Em AppUserDetailsService, ao inves de retornar um User, retornar Classe UsuarioSistema. 
		 *   
		 *   UsuarioSistema eh o Usuario Logado. **/		
		UsuarioSistema usuarioSistema = (UsuarioSistema) authentication.getPrincipal();
		
		Map<String, Object> additionalInformation = new HashMap<>();
		additionalInformation.put("nome", usuarioSistema.getUsuario().getNome());
		((DefaultOAuth2AccessToken) accessToken).setAdditionalInformation(additionalInformation);
		
		return accessToken;
	}

}
