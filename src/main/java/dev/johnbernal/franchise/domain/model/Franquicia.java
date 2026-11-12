package dev.johnbernal.franchise.domain.model;

import java.util.List;

public record Franquicia(String id, String nombre, List<Sucursal> sucursales) {
	
	public Franquicia {
		if (sucursales == null) {
			sucursales = List.of();
		} else {
			sucursales = List.copyOf(sucursales);
		}
	}
}
