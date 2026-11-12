package dev.johnbernal.franchise.domain.exception;

public class EntidadDuplicadaException extends ReglaNegocioException {
	
	private static final long serialVersionUID = 3755107323194763354L;

	public EntidadDuplicadaException(String entidad, String nombre) {
		super(String.format("%s con nombre '%s' ya existe", entidad, nombre));
	}
}
