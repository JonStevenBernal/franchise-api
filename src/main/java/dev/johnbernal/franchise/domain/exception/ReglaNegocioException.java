package dev.johnbernal.franchise.domain.exception;

public abstract class ReglaNegocioException extends RuntimeException {

	private static final long serialVersionUID = 281266935723451668L;

	protected ReglaNegocioException(String mensaje) {
		super(mensaje);
	}
}
