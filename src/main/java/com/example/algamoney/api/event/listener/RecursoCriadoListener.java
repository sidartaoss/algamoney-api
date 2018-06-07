package com.example.algamoney.api.event.listener;

import java.net.URI;

import javax.servlet.http.HttpServletResponse;

import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.example.algamoney.api.event.RecursoCriadoEvent;

/**
 * Classe que ouve o evento de RecursoCriadoEvent. Quando for lançado o evento RecursoCriadoEvent,
 * este é o listener que vai ouvir e aqui que vai executar o código.
 * @author SEMPR
 *
 */
/**
 * Precisa anotar a classe como @Component para ela ser um componente do Spring.
 * 
 * @author SEMPR
 *
 */
@Component
public class RecursoCriadoListener implements ApplicationListener<RecursoCriadoEvent> {

	/**
	 * Quando acontecer o evento, nós vamos receber e adicionar o Header Location.
	 */
	@Override
	public void onApplicationEvent(RecursoCriadoEvent recursoCriadoEvent) {
		HttpServletResponse response = recursoCriadoEvent.getResponse();
		Long codigo = recursoCriadoEvent.getCodigo();
		this.adicionarHeaderLocation(response, codigo);
	}

	/**
	 * Adicionar o Header Location.
	 * 
	 * @param response
	 * @param codigo
	 */
	private void adicionarHeaderLocation(HttpServletResponse response, Long codigo) {
		URI uri = ServletUriComponentsBuilder.fromCurrentRequestUri().path("/{codigo}")
				.buildAndExpand(codigo).toUri();
		response.setHeader("Location", uri.toASCIIString());
	}

}
