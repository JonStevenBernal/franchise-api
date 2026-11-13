package dev.johnbernal.franchise.infrastructure.adapter.in.web.dto;

import java.util.ArrayList;
import java.util.List;

import org.springframework.util.CollectionUtils;

import dev.johnbernal.franchise.domain.model.Franquicia;
import dev.johnbernal.franchise.domain.model.Producto;
import dev.johnbernal.franchise.domain.model.Sucursal;

public record FranquiciaResponse(String id, String nombre, List<SucursalResponse> sucursales) {
	
	public static FranquiciaResponse fromDomain(Franquicia franquicia) {
		if (franquicia == null) {
			return null;
		}
		List<SucursalResponse> sucursalesDto = new ArrayList<>();
		if (!CollectionUtils.isEmpty(franquicia.sucursales())) {
			for (Sucursal sucursal : franquicia.sucursales()) {
				List<ProductoResponse> productosDto = new ArrayList<>();
				if (!CollectionUtils.isEmpty(sucursal.productos())) {
					for (Producto producto : sucursal.productos()) {
						productosDto.add(new ProductoResponse(producto.id(), producto.nombre(), producto.stock()));
					}
				}
				sucursalesDto.add(new SucursalResponse(sucursal.id(), sucursal.nombre(), productosDto));
			}
		}
		return new FranquiciaResponse(franquicia.id(), franquicia.nombre(), sucursalesDto);
	}
	
}
