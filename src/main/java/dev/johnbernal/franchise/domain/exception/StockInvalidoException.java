package dev.johnbernal.franchise.domain.exception;

public class StockInvalidoException extends ReglaNegocioException {

	private static final long serialVersionUID = 5976514579290653653L;

	public StockInvalidoException(int stock) {
		super(String.format("El stock no puede ser negativo, recibido: %d", stock));
	}
}
