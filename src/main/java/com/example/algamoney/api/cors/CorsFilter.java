package com.example.algamoney.api.cors;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.example.algamoney.api.config.property.AlgamoneyApiProperty;

/**
 * O CORS que o Spring ja suporta nao vai funcionar para o Spring Security OAuth2.
 *
 * Entao, esta classe sera um filtro para o CORS.
 * 
 * O CORS eh simplesmente definir Headers HTTP. Os Headers que come�am com Access-Control. 
 * 
 * Eh possivel fazer qualquer Requisicao via Javascript porque habilitou o CORS na Aplicacao a partir deste Filtro. 
 *
 * @author SEMPR
 *
 */
/** Eh um componente do Spring para auto-detecta-lo pelo Framework. **/
@Component
/** Definir o filtro com uma ordem de prioridade bem alta para ser executado logo no inicio. **/
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CorsFilter implements Filter {

	/** private String origemPermitida = "http://localhost:8000"; // TODO: Configurar para diferentes ambientes (Producao, Teste, etc.). **/
	/** Configurações externas do nosso Projeto. 
	 * Dá para fazer tudo por perfis: isso se chama Profiles. No Spring, é possível trocar um Profile dependendo do ambiente desejado. **/
	@Autowired
	private AlgamoneyApiProperty algamoneyApiProperty;
	
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		/** Primeiramente, converter o ServletRequest para HttpServletRequest e ServletResponse para HttpServletResponse. **/
		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse res = (HttpServletResponse) response;
		
		/** Definicao de Headers importantes do Response. Enviar esses Headers sempre. **/
		/** Definir qual eh a origem permitida. **/
		res.setHeader("Access-Control-Allow-Origin", algamoneyApiProperty.getOrigemPermitida());
		/** Definir Allow-Credentials = true para permitir que o Cookie seja enviado. **/
		res.setHeader("Access-Control-Allow-Credentials", "true");
		
		/** Se a Requisicao for um OPTIONS Entao 
		* Permitir este Preflighted Request
		* Se o metodo HTTP for OPTIONS e seguir o filtro, o Spring Security vai bloquear. Entao, tratar quando o metodo for OPTIONS. **/
		if ("OPTIONS".equals(req.getMethod()) && algamoneyApiProperty.getOrigemPermitida().equals(req.getHeader("Origin"))) {
			/** Setar os Headers do Response. **/ 
			/** Define todos os metodos que serao suportados/permitidos. **/
			res.setHeader("Access-Control-Allow-Methods", "POST, GET, DELETE, PUT, OPTIONS");
			/** Quais os headers que serao permitidos. **/
			res.setHeader("Access-Control-Allow-Headers", "Authorization, Content-Type, Accept");
			/** Qual o tempo que o Browser vai fazer a proxima Requisicao? Colocar em cache de 1 hora. So depois de 1 hora que o Browser
			* vai fazer a proxima Requisicao. **/
			res.setHeader("Access-Control-Max-Age", "3600"); 
			
			/** Dar uma Resposta de OK. HTTP 200 **/
			res.setStatus(HttpServletResponse.SC_OK);
		} else { 
			/** Senao Continua com o filtro normal. **/
			chain.doFilter(request, response);
		}
	}
	
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
	}


	@Override
	public void destroy() {
	}

}
