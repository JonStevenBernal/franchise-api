package dev.johnbernal.franchise.domain.port.in;

import dev.johnbernal.franchise.domain.model.Franquicia;
import dev.johnbernal.franchise.domain.model.SucursalProductoMayorStock;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface FranquiciaUseCasePort {
	
	Mono<Franquicia> crearFranquicia(String nombre);

	Mono<Franquicia> actualizarNombreFranquicia(String franquiciaId, String nuevoNombre);

	Mono<Franquicia> agregarSucursal(String franquiciaId, String nombreSucursal);

	Mono<Franquicia> actualizarNombreSucursal(String franquiciaId, String sucursalId, String nuevoNombre);

	Mono<Franquicia> agregarProducto(String franquiciaId, String sucursalId, String nombreProducto, int stockInicial);

	Mono<Franquicia> eliminarProducto(String franquiciaId, String sucursalId, String productoId);

	Mono<Franquicia> actualizarStockProducto(String franquiciaId, String sucursalId, String productoId, int nuevoStock);

	Mono<Franquicia> actualizarNombreProducto(String franquiciaId, String sucursalId, String productoId,
			String nuevoNombre);

	Flux<SucursalProductoMayorStock> obtenerProductosConMayorStockPorSucursal(String franquiciaId);

	Mono<Franquicia> obtenerFranquiciaPorId(String franquiciaId);

	Flux<Franquicia> listarFranquicias();
}
