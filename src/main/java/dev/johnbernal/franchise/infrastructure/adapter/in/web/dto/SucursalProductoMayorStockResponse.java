package dev.johnbernal.franchise.infrastructure.adapter.in.web.dto;

import dev.johnbernal.franchise.domain.model.Producto;
import dev.johnbernal.franchise.domain.model.SucursalProductoMayorStock;

public record SucursalProductoMayorStockResponse(String sucursalId, String sucursalNombre, ProductoResponse producto) {

	public static SucursalProductoMayorStockResponse fromDomain(SucursalProductoMayorStock resultado) {
		if (resultado == null) {
			return null;
		}
		Producto producto = resultado.producto();
		ProductoResponse productoDto = null;
		if (producto != null) {
			productoDto = new ProductoResponse(producto.id(), producto.nombre(), producto.stock());
		}
		return new SucursalProductoMayorStockResponse(resultado.sucursalId(), resultado.sucursalNombre(), productoDto);
	}
}
