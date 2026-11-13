package dev.johnbernal.franchise.infrastructure.adapter.out.persistence.entity;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "franquicias")
public record FranquiciaDocument(@Id String id, @Indexed(unique = true) String nombre,
		List<SucursalDocument> sucursales) {

	public FranquiciaDocument {
		if (sucursales == null) {
			sucursales = List.of();
		} else {
			sucursales = List.copyOf(sucursales);
		}
	}

}
