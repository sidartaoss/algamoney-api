package com.example.algamoney.api.token;

import java.io.IOException;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import org.apache.catalina.util.ParameterMap;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * Classe Pr�-Processadora e Filtro.
 * 
 * Criar um Filtro na Aplica��o que pega o Cookie, tirar o Refresh Token e adicionar na Requisi��o.
 * 
 * @author sosilva
 *
 */
/** Anotar com @Component para tornar-se um Compoenente do Spring e Auto-detec��o pelo Framework. **/
@Component
/** Definir o filtro com prioridade muito alta, porque precisa analisar a Requisi��o antes de tudo e,
 * se for Requisi��o que tenha, no Corpo, grant_type = refresh_token, seja /oauth/token e tenha o Cookie,
 * precisa adicionar na Requisi��o para poder funcionar a Aplica��o.
 *  **/
@Order(Ordered.HIGHEST_PRECEDENCE)
public class RefreshTokenCookiePreProcessorFilter implements Filter {

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		/** Primeiro precisa pegar o HttpServletRequest. **/
		HttpServletRequest req = (HttpServletRequest) request;
		
		/** Verificar se: a requisi��o � para /oauth/token; 
		 * 					se tem o par�metro grant_type com o valor refresh_token no Corpo da Requisi��o; **/
		/** 					se tem algum Cookie. **/
		/**
		 * Se sim Ent�o h� o Refresh Token no Corpo da Requisi��o.
		 * **/
		if ("/oauth/token".equalsIgnoreCase(req.getRequestURI())
				&& "refresh_token".equals(req.getParameter("grant_type"))
				&& req.getCookies() != null) {
			/**
			 * Encontrar o Cookie e adicion�-lo dentro do Corpo da Requisi��o, no Mapa de Par�metros da Requisi��o.
			 * No Mapa de Par�metros da Requisi��o, n�o consegue mexer mais. A solu��o � criar uma classe para substituir
			 * a Requisi��o.
			 * **/
			for (Cookie cookie : req.getCookies()) {
				if (cookie.getName().equals("refreshToken")) {
					String refreshToken = cookie.getValue();
					req = new MyServletRequestWrapper(req, refreshToken);
				}
			}
		}
		
		/** N�o esquecer de continuar a Cadeia do Filtro. **/
		/** Est� substituindo Requisi��o com  o Wrapper da Requisi��o que foi criado. Nesse Wrapper
		 * da Requisi��o existe o Refresh Token nos par�metros da Requisi��o. **/
		chain.doFilter(req, response);
		
	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		
	}
	
	/**
	 * Classe Wrapper do ServletRequest.
	 * Adaptar o request para um Servlet.
	 * @author sosilva
	 *
	 */
	static class MyServletRequestWrapper extends HttpServletRequestWrapper {

		private String refreshToken;
		
		/** Tamb�m vai receber no Construtor o Refresh Token para quando a Aplica��o precisar do
		 * parameterMap, ou seja, ao invocar getParameterMap(). **/
		public MyServletRequestWrapper(HttpServletRequest request, String refreshToken) {
			super(request);
			this.refreshToken = refreshToken;
		}
		
		@Override
		public Map<String, String[]> getParameterMap() {
			/** Os valores que j� est�o dentro do Mapa da Requisi��o continuam. **/
			ParameterMap<String, String[]> map = new ParameterMap<>(getRequest().getParameterMap());
			/** Vai apenas adicionar Refresh Token. **/
			map.put("refresh_token", new String[] { this.refreshToken });
			/** Comportamento Padr�o � deixar travado o Mapa da Requisi��o.  **/
			map.setLocked(true);
			return map;
		}
		
	}

}
