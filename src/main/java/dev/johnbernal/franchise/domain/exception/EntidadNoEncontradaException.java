package dev.johnbernal.franchise.domain.exception;

public class EntidadNoEncontradaException extends ReglaNegocioException {

	private static final long serialVersionUID = -7970212761291323329L;

	public EntidadNoEncontradaException(String entidad, String id) {
		super(String.format("%s con identificador '%s' no fue encontrada", entidad, id));
	}
}
