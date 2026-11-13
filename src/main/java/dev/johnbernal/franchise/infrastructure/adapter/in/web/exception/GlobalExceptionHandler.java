package dev.johnbernal.franchise.infrastructure.adapter.in.web.exception;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;

import dev.johnbernal.franchise.domain.exception.EntidadDuplicadaException;
import dev.johnbernal.franchise.domain.exception.EntidadNoEncontradaException;
import dev.johnbernal.franchise.domain.exception.StockInvalidoException;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(EntidadDuplicadaException.class)
	public ResponseEntity<Map<String, Object>> manejarEntidadDuplicada(EntidadDuplicadaException excepcion) {
		return construirRespuesta(HttpStatus.CONFLICT, excepcion.getMessage());
	}

	@ExceptionHandler(EntidadNoEncontradaException.class)
	public ResponseEntity<Map<String, Object>> manejarEntidadNoEncontrada(EntidadNoEncontradaException excepcion) {
		return construirRespuesta(HttpStatus.NOT_FOUND, excepcion.getMessage());
	}

	@ExceptionHandler(StockInvalidoException.class)
	public ResponseEntity<Map<String, Object>> manejarStockInvalido(StockInvalidoException excepcion) {
		return construirRespuesta(HttpStatus.BAD_REQUEST, excepcion.getMessage());
	}

	@ExceptionHandler(WebExchangeBindException.class)
	public ResponseEntity<Map<String, Object>> manejarValidaciones(WebExchangeBindException excepcion) {
		Map<String, Object> cuerpo = new HashMap<>();
		cuerpo.put("timestamp", LocalDateTime.now().toString());
		cuerpo.put("status", HttpStatus.BAD_REQUEST.value());
		cuerpo.put("error", HttpStatus.BAD_REQUEST.getReasonPhrase());

		Map<String, String> erroresCampos = new HashMap<>();
		excepcion.getFieldErrors().forEach(error -> erroresCampos.put(error.getField(), error.getDefaultMessage()));
		cuerpo.put("mensajes", erroresCampos);

		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(cuerpo);
	}

	private ResponseEntity<Map<String, Object>> construirRespuesta(HttpStatus status, String mensaje) {
		Map<String, Object> cuerpo = new HashMap<>();
		cuerpo.put("timestamp", LocalDateTime.now().toString());
		cuerpo.put("status", status.value());
		cuerpo.put("error", status.getReasonPhrase());
		cuerpo.put("mensaje", mensaje);
		return ResponseEntity.status(status).body(cuerpo);
	}
}
