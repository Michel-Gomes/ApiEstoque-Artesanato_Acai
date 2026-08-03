package br.com.artesanatoestoque.handler;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import br.com.artesanatoestoque.exception.RecursoNaoEncontradoException;

@RestControllerAdvice
public class GlobalExceptionHandler {

	private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

	/**
	 * Recurso não encontrado -> 404
	 */
	@ExceptionHandler(RecursoNaoEncontradoException.class)
	public ResponseEntity<Object> handleRecursoNaoEncontrado(RecursoNaoEncontradoException ex) {
		return construirResposta(HttpStatus.NOT_FOUND, ex.getMessage());
	}

	/**
	 * Regra de negócio violada / argumento inválido -> 400
	 * (é isso que a maioria dos services lança hoje: produto não encontrado,
	 * estoque insuficiente, código duplicado, etc.)
	 */
	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<Object> handleIllegalArgument(IllegalArgumentException ex) {
		return construirResposta(HttpStatus.BAD_REQUEST, ex.getMessage());
	}

	/**
	 * Erros de validação do Bean Validation (@Valid nos DTOs) -> 400
	 */
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<Object> handleValidacao(MethodArgumentNotValidException ex) {
		Map<String, String> erros = new LinkedHashMap<>();
		ex.getBindingResult().getFieldErrors().forEach(erro ->
			erros.put(erro.getField(), erro.getDefaultMessage())
		);

		Map<String, Object> body = new LinkedHashMap<>();
		body.put("timestamp", LocalDateTime.now());
		body.put("status", HttpStatus.BAD_REQUEST.value());
		body.put("error", "Erro de validação");
		body.put("campos", erros);

		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
	}

	/**
	 * Conflito de concorrência (duas requisições alterando o mesmo registro) -> 409
	 */
	@ExceptionHandler(ObjectOptimisticLockingFailureException.class)
	public ResponseEntity<Object> handleConcorrencia(ObjectOptimisticLockingFailureException ex) {
		log.warn("Conflito de concorrência detectado: {}", ex.getMessage());
		return construirResposta(HttpStatus.CONFLICT,
			"O registro foi alterado por outra operação. Tente novamente.");
	}

	/**
	 * Qualquer outro erro não tratado -> 500, sem vazar detalhes internos
	 */
	@ExceptionHandler(Exception.class)
	public ResponseEntity<Object> handleGenerico(Exception ex) {
		log.error("Erro não tratado", ex);
		return construirResposta(HttpStatus.INTERNAL_SERVER_ERROR,
			"Ocorreu um erro interno. Tente novamente mais tarde.");
	}

	private ResponseEntity<Object> construirResposta(HttpStatus status, String mensagem) {
		Map<String, Object> body = new LinkedHashMap<>();
		body.put("timestamp", LocalDateTime.now());
		body.put("status", status.value());
		body.put("error", status.getReasonPhrase());
		body.put("message", mensagem);
		return ResponseEntity.status(status).body(body);
	}
}