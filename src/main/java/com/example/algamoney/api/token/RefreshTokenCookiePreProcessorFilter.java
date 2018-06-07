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
 * Classe Pré-Processadora e Filtro.
 * 
 * Criar um Filtro na Aplicação que pega o Cookie, tirar o Refresh Token e adicionar na Requisição.
 * 
 * @author sosilva
 *
 */
/** Anotar com @Component para tornar-se um Compoenente do Spring e Auto-detecção pelo Framework. **/
@Component
/** Definir o filtro com prioridade muito alta, porque precisa analisar a Requisição antes de tudo e,
 * se for Requisição que tenha, no Corpo, grant_type = refresh_token, seja /oauth/token e tenha o Cookie,
 * precisa adicionar na Requisição para poder funcionar a Aplicação.
 *  **/
@Order(Ordered.HIGHEST_PRECEDENCE)
public class RefreshTokenCookiePreProcessorFilter implements Filter {

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		/** Primeiro precisa pegar o HttpServletRequest. **/
		HttpServletRequest req = (HttpServletRequest) request;
		
		/** Verificar se: a requisição é para /oauth/token; 
		 * 					se tem o parâmetro grant_type com o valor refresh_token no Corpo da Requisição; **/
		/** 					se tem algum Cookie. **/
		/**
		 * Se sim Então há o Refresh Token no Corpo da Requisição.
		 * **/
		if ("/oauth/token".equalsIgnoreCase(req.getRequestURI())
				&& "refresh_token".equals(req.getParameter("grant_type"))
				&& req.getCookies() != null) {
			/**
			 * Encontrar o Cookie e adicioná-lo dentro do Corpo da Requisição, no Mapa de Parâmetros da Requisição.
			 * No Mapa de Parâmetros da Requisição, não consegue mexer mais. A solução é criar uma classe para substituir
			 * a Requisição.
			 * **/
			for (Cookie cookie : req.getCookies()) {
				if (cookie.getName().equals("refreshToken")) {
					String refreshToken = cookie.getValue();
					req = new MyServletRequestWrapper(req, refreshToken);
				}
			}
		}
		
		/** Não esquecer de continuar a Cadeia do Filtro. **/
		/** Está substituindo Requisição com  o Wrapper da Requisição que foi criado. Nesse Wrapper
		 * da Requisição existe o Refresh Token nos parâmetros da Requisição. **/
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
		
		/** Também vai receber no Construtor o Refresh Token para quando a Aplicação precisar do
		 * parameterMap, ou seja, ao invocar getParameterMap(). **/
		public MyServletRequestWrapper(HttpServletRequest request, String refreshToken) {
			super(request);
			this.refreshToken = refreshToken;
		}
		
		@Override
		public Map<String, String[]> getParameterMap() {
			/** Os valores que já estão dentro do Mapa da Requisição continuam. **/
			ParameterMap<String, String[]> map = new ParameterMap<>(getRequest().getParameterMap());
			/** Vai apenas adicionar Refresh Token. **/
			map.put("refresh_token", new String[] { this.refreshToken });
			/** Comportamento Padrão é deixar travado o Mapa da Requisição.  **/
			map.setLocked(true);
			return map;
		}
		
	}

}
