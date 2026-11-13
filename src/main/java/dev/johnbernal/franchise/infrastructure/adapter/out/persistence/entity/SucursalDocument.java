package dev.johnbernal.franchise.infrastructure.adapter.out.persistence.entity;

import java.util.List;

public record SucursalDocument(String id, String nombre, List<ProductoDocument> productos) {

	public SucursalDocument {
		if (productos == null) {
			productos = List.of();
		} else {
			productos = List.copyOf(productos);
		}
	}

}
