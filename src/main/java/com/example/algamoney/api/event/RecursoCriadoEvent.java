package com.example.algamoney.api.event;

import javax.servlet.http.HttpServletResponse;

import org.springframework.context.ApplicationEvent;

/**
 * Evento da aplica��o para um Recurso Criado.
 * Quando for necess�rio adicionar o Header Location, ser� necess�rio criar este evento, 
 * passando o ServletResponse e o c�digo.
 * (� necess�rio criar o Listener para este evento.)
 * @author SEMPR
 *
 */
public class RecursoCriadoEvent extends ApplicationEvent {

	private static final long serialVersionUID = 1L;

	private HttpServletResponse response;
	private Long codigo;
	
	public RecursoCriadoEvent(Object source, HttpServletResponse response, Long codigo) {
		super(source);
		this.response = response;
		this.codigo = codigo;
	}

	public HttpServletResponse getResponse() {
		return response;
	}

	public Long getCodigo() {
		return codigo;
	}

}
