package dev.johnbernal.franchise.domain.model;

import java.util.List;

public record Sucursal(String id, String nombre, List<Producto> productos) {
	
	public Sucursal {
		if (productos == null) {
			productos = List.of();
		} else {
			productos = List.copyOf(productos);
		}
	}
}
