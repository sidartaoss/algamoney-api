package com.example.algamoney.api.exceptionhandler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.orm.jpa.JpaObjectRetrievalFailureException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * Classe respons�vel por capturar exce��es de resposta de entidades.
 * Para esta classe conseguir capturar as exce��es da aplica��o, � necess�rio ser um controlador. 
 * Definir como @ControllerAdvice, porque observa toda a aplica��o. Tornar-se um componente do Spring e observar toda a aplica��o.
 * @author SEMPR
 *
 */
@ControllerAdvice
public class AlgamoneyExceptionHandler extends ResponseEntityExceptionHandler {

	@Autowired
	private MessageSource messageSource;
	
	/**
	 * Status code:
	 * 2xx -> Sucesso
	 * 4xx -> Erro do cliente. Requisi��o ruim. A culpa � de quem chamou a API (o cliente, ex.: o Postman)
	 * 5xx -> Erro no servi�o/servidor. Bug no sistema, banco de dados fora do ar, etc.
	 * 
	**/
	
	/**
	 * Capturar as mensagens quando n�o conseguir ler.
	 */
	@Override
	protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex,
			HttpHeaders headers, HttpStatus status, WebRequest request) {
		/** handleExceptionInternal **/
		/** Eh necessario definir o body. **/
		/** Eh possivel trocar o status ou passar outros headers. **/
		String mensagemUsuario = messageSource.getMessage("mensagem.invalida", null, LocaleContextHolder.getLocale());
		String mensagemDesenvolvedor = ex.getCause() == null ? ex.toString() : ex.getCause().toString();
		// Erro body = new Erro(mensagemUsuario, mensagemDesenvolvedor);
		List<Erro> body = Arrays.asList(new Erro(mensagemUsuario, mensagemDesenvolvedor));
		return handleExceptionInternal(ex, body, headers, HttpStatus.BAD_REQUEST, request);
	}
	
	/**
	 * M�todo que trata quando o argumento de um m�todo n�o est� v�lido.
	 */
	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
			HttpHeaders headers, HttpStatus status, WebRequest request) {
		List<Erro> body = this.criarListaDeErros(ex.getBindingResult());
		return handleExceptionInternal(ex, body, headers, HttpStatus.BAD_REQUEST, request);
	}
	
	/** 1. **/
	/**
	 * Quero que este novo m�todo que vou criar trata de uma determinada exce��o.
	 * **/
	/** Annotation para definir que este m�todo vai tratar quando a exce��o EmptyResultDataAccessException for lan�ada. **/
	/** @ExceptionHandler({ EmptyResultDataAccessException.class })
	/** Tentando acessar um recurso que n�o existe. **
	@ResponseStatus(code = HttpStatus.NOT_FOUND)
	public void handleEmptyResultDataAccessException() {
		
	}
	**/

	/** 2. **/
	/** Quer retornar uma mensagem ao usu�rio / desenvolvedor. **/
	@ExceptionHandler({ EmptyResultDataAccessException.class })
	@ResponseStatus(code = HttpStatus.NOT_FOUND)
	public ResponseEntity<Object> handleEmptyResultDataAccessException(EmptyResultDataAccessException ex,
			WebRequest request) {
		String mensagemUsuario = messageSource.getMessage("recurso.nao-encontrado", null, LocaleContextHolder.getLocale());
		/** EmptyResultDataAccessException n�o tem uma Causa (Cause). EmptyResultDataAccessException j� � a exce��o pronta. **/
		String mensagemDesenvolvedor = ex.getCause() == null ? ex.toString() : ex.getCause().toString();
		List<Erro> body = Arrays.asList(new Erro(mensagemUsuario, mensagemDesenvolvedor));		
		return handleExceptionInternal(ex, body, new HttpHeaders(), HttpStatus.NOT_FOUND, request);
	}	
	
	public static class Erro {
		
		private String mensagemUsuario;
		private String mensagemDesenvolvedor;
		
		public Erro(String mensagemUsuario, String mensagemDesenvolvedor) {
			this.mensagemUsuario = mensagemUsuario;
			this.mensagemDesenvolvedor = mensagemDesenvolvedor;
		}
		
		public String getMensagemUsuario() {
			return mensagemUsuario;
		}
		
		public void setMensagemUsuario(String mensagemUsuario) {
			this.mensagemUsuario = mensagemUsuario;
		}
		
		public String getMensagemDesenvolvedor() {
			return mensagemDesenvolvedor;
		}
		
		public void setMensagemDesenvolvedor(String mensagemDesenvolvedor) {
			this.mensagemDesenvolvedor = mensagemDesenvolvedor;
		}
	}
	
	@ExceptionHandler({ JpaObjectRetrievalFailureException.class })
	public ResponseEntity<Object> handleJpaObjectRetrievalFailureException(JpaObjectRetrievalFailureException ex, WebRequest request ) {
		String mensagemUsuario = messageSource.getMessage("recurso.operacao-nao-permitida", null,
				LocaleContextHolder.getLocale());
		/** getRootCauseMessage Obter mensagem da causa ra�z da exce��o. **/
		String mensagemDesenvolvedor = ExceptionUtils.getRootCauseMessage(ex);
		List<Erro> body = Arrays.asList(new Erro(mensagemUsuario, mensagemDesenvolvedor));
		return handleExceptionInternal(ex, body, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
	}
	
	@ExceptionHandler({ DataIntegrityViolationException.class })
	public ResponseEntity<Object> handleDataIntegrityViolationException(DataIntegrityViolationException ex, WebRequest request ) {
		String mensagemUsuario = messageSource.getMessage("recurso.operacao-nao-permitida", null,
				LocaleContextHolder.getLocale());
		/** getRootCauseMessage Obter mensagem da causa ra�z da exce��o. **/
		String mensagemDesenvolvedor = ExceptionUtils.getRootCauseMessage(ex);
		List<Erro> body = Arrays.asList(new Erro(mensagemUsuario, mensagemDesenvolvedor));
		return handleExceptionInternal(ex, body, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
	}	

	/**
	 * 
	 * @param bindingResult BindingResult cont�m a lista de todos os erros.
	 * @return
	 */
	private List<Erro> criarListaDeErros(BindingResult bindingResult) {
		List<Erro> erros = new ArrayList<>();
		
		/** FieldError: erro de valida��o do atributo da entidade. **/
		for (FieldError fieldError : bindingResult.getFieldErrors()) {
			String mensagemUsuario = messageSource.getMessage(fieldError, LocaleContextHolder.getLocale());
			String mensagemDesenvolvedor = fieldError.toString();
			erros.add(new Erro(mensagemUsuario, mensagemDesenvolvedor));			
		}

		
		return erros;
	}
}