package dev.johnbernal.franchise.domain.port.in;

import dev.johnbernal.franchise.domain.exception.EntidadDuplicadaException;
import dev.johnbernal.franchise.domain.exception.EntidadNoEncontradaException;
import dev.johnbernal.franchise.domain.exception.StockInvalidoException;
import dev.johnbernal.franchise.domain.model.Franquicia;
import dev.johnbernal.franchise.domain.model.SucursalProductoMayorStock;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Casos de uso y operaciones de negocio permitidas para la gestión de
 * Franquicias, Sucursales y Productos.
 */
public interface FranquiciaUseCasePort {

	/**
	 * Registra una nueva franquicia en el sistema tras aplicar validaciones de
	 * duplicidad.
	 *
	 * @param nombre nombre comercial de la franquicia
	 * @return franquicia persistida con identificador asignado
	 * @throws EntidadDuplicadaException si ya existe una franquicia registrada con
	 *                                   el mismo nombre
	 */
	Mono<Franquicia> crearFranquicia(String nombre);

	/**
	 * Actualiza el nombre de una franquicia existente.
	 *
	 * @param franquiciaId identificador único de la franquicia
	 * @param nuevoNombre  nuevo nombre a asignar
	 * @return franquicia actualizada
	 * @throws EntidadNoEncontradaException si la franquicia no existe
	 * @throws EntidadDuplicadaException    si ya existe otra franquicia con el
	 *                                      nuevo nombre
	 */
	default Mono<Franquicia> actualizarNombreFranquicia(String franquiciaId, String nuevoNombre) {
		return Mono.error(new UnsupportedOperationException("Operación aún no implementada"));
	}

	/**
	 * Agrega una nueva sucursal a una franquicia existente.
	 *
	 * @param franquiciaId   identificador de la franquicia
	 * @param nombreSucursal nombre de la nueva sucursal
	 * @return franquicia con la nueva sucursal agregada
	 * @throws EntidadNoEncontradaException si la franquicia no existe
	 * @throws EntidadDuplicadaException    si ya existe una sucursal con ese nombre
	 *                                      en la franquicia
	 */
	default Mono<Franquicia> agregarSucursal(String franquiciaId, String nombreSucursal) {
		return Mono.error(new UnsupportedOperationException("Operación aún no implementada"));
	}

	/**
	 * Actualiza el nombre de una sucursal perteneciente a una franquicia.
	 *
	 * @param franquiciaId identificador de la franquicia
	 * @param sucursalId   identificador de la sucursal
	 * @param nuevoNombre  nuevo nombre para la sucursal
	 * @return franquicia con el nombre de la sucursal actualizado
	 * @throws EntidadNoEncontradaException si la franquicia o la sucursal no
	 *                                      existen
	 * @throws EntidadDuplicadaException    si ya existe otra sucursal con ese
	 *                                      nombre en la franquicia
	 */
	default Mono<Franquicia> actualizarNombreSucursal(String franquiciaId, String sucursalId, String nuevoNombre) {
		return Mono.error(new UnsupportedOperationException("Operación aún no implementada"));
	}

	/**
	 * Agrega un nuevo producto con su stock inicial a una sucursal determinada.
	 *
	 * @param franquiciaId   identificador de la franquicia
	 * @param sucursalId     identificador de la sucursal
	 * @param nombreProducto nombre del producto a ofertar
	 * @param stockInicial   cantidad de inventario inicial (debe ser mayor o igual
	 *                       a 0)
	 * @return franquicia con el nuevo producto registrado en la sucursal
	 * @throws EntidadNoEncontradaException si la franquicia o la sucursal no
	 *                                      existen
	 * @throws EntidadDuplicadaException    si el producto ya existe en la sucursal
	 * @throws StockInvalidoException       si el stock inicial es negativo
	 */
	default Mono<Franquicia> agregarProducto(String franquiciaId, String sucursalId, String nombreProducto,
			int stockInicial) {
		return Mono.error(new UnsupportedOperationException("Operación aún no implementada"));
	}

	/**
	 * Elimina un producto de una sucursal específica.
	 *
	 * @param franquiciaId identificador de la franquicia
	 * @param sucursalId   identificador de la sucursal
	 * @param productoId   identificador del producto a retirar
	 * @return franquicia con el producto eliminado de la sucursal
	 * @throws EntidadNoEncontradaException si la franquicia, la sucursal o el
	 *                                      producto no existen
	 */
	default Mono<Franquicia> eliminarProducto(String franquiciaId, String sucursalId, String productoId) {
		return Mono.error(new UnsupportedOperationException("Operación aún no implementada"));
	}

	/**
	 * Modifica el stock disponible de un producto en una sucursal.
	 *
	 * @param franquiciaId identificador de la franquicia
	 * @param sucursalId   identificador de la sucursal
	 * @param productoId   identificador del producto
	 * @param nuevoStock   nueva cantidad de inventario (debe ser mayor o igual a 0)
	 * @return franquicia con el stock del producto actualizado
	 * @throws EntidadNoEncontradaException si la franquicia, la sucursal o el
	 *                                      producto no existen
	 * @throws StockInvalidoException       si el nuevo stock es negativo
	 */
	default Mono<Franquicia> actualizarStockProducto(String franquiciaId, String sucursalId, String productoId,
			int nuevoStock) {
		return Mono.error(new UnsupportedOperationException("Operación aún no implementada"));
	}

	/**
	 * Actualiza el nombre de un producto dentro de una sucursal.
	 *
	 * @param franquiciaId identificador de la franquicia
	 * @param sucursalId   identificador de la sucursal
	 * @param productoId   identificador del producto
	 * @param nuevoNombre  nuevo nombre a asignar al producto
	 * @return franquicia con el nombre del producto actualizado
	 * @throws EntidadNoEncontradaException si la franquicia, la sucursal o el
	 *                                      producto no existen
	 * @throws EntidadDuplicadaException    si ya existe otro producto con ese
	 *                                      nombre en la sucursal
	 */
	default Mono<Franquicia> actualizarNombreProducto(String franquiciaId, String sucursalId, String productoId,
			String nuevoNombre) {
		return Mono.error(new UnsupportedOperationException("Operación aún no implementada"));
	}

	/**
	 * Obtiene el listado de productos con mayor stock por cada sucursal de una
	 * franquicia dada.
	 *
	 * @param franquiciaId identificador de la franquicia consultada
	 * @return flujo reactivo con el producto de mayor stock por cada sucursal que
	 *         cuente con productos
	 * @throws EntidadNoEncontradaException si la franquicia no existe
	 */
	default Flux<SucursalProductoMayorStock> obtenerProductosConMayorStockPorSucursal(String franquiciaId) {
		return Flux.error(new UnsupportedOperationException("Operación aún no implementada"));
	}

	/**
	 * Consulta una franquicia por su identificador único.
	 *
	 * @param franquiciaId identificador de la franquicia
	 * @return franquicia encontrada
	 * @throws EntidadNoEncontradaException si la franquicia no existe
	 */
	default Mono<Franquicia> obtenerFranquiciaPorId(String franquiciaId) {
		return Mono.error(new UnsupportedOperationException("Operación aún no implementada"));
	}

	/**
	 * Lista todas las franquicias registradas en el sistema.
	 *
	 * @return flujo reactivo con todas las franquicias
	 */
	default Flux<Franquicia> listarFranquicias() {
		return Flux.error(new UnsupportedOperationException("Operación aún no implementada"));
	}
}
