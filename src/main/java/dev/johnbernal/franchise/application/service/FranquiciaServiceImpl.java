package dev.johnbernal.franchise.application.service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import dev.johnbernal.franchise.domain.exception.EntidadDuplicadaException;
import dev.johnbernal.franchise.domain.exception.EntidadNoEncontradaException;
import dev.johnbernal.franchise.domain.exception.StockInvalidoException;
import dev.johnbernal.franchise.domain.model.Franquicia;
import dev.johnbernal.franchise.domain.model.Producto;
import dev.johnbernal.franchise.domain.model.Sucursal;
import dev.johnbernal.franchise.domain.port.in.FranquiciaUseCasePort;
import dev.johnbernal.franchise.domain.port.out.FranquiciaRepositoryPort;
import reactor.core.publisher.Mono;

/**
 * Implementación del servicio de aplicación para la gestión de Franquicias y
 * Sucursales.
 */
@Service
public class FranquiciaServiceImpl implements FranquiciaUseCasePort {

	private final FranquiciaRepositoryPort franquiciaRepositoryPort;

	public FranquiciaServiceImpl(FranquiciaRepositoryPort franquiciaRepositoryPort) {
		this.franquiciaRepositoryPort = franquiciaRepositoryPort;
	}

	@Override
	public Mono<Franquicia> crearFranquicia(String nombre) {
		String nombreLimpio = nombre.trim();
		return franquiciaRepositoryPort.existePorNombre(nombreLimpio).filter(Boolean.FALSE::equals)
				.switchIfEmpty(Mono.error(new EntidadDuplicadaException("Franquicia", nombreLimpio)))
				.map(noExiste -> new Franquicia(UUID.randomUUID().toString(), nombreLimpio, List.of()))
				.flatMap(franquiciaRepositoryPort::guardar);
	}

	@Override
	public Mono<Franquicia> agregarSucursal(String franquiciaId, String nombreSucursal) {
		String nombreLimpio = nombreSucursal.trim();
		return buscarFranquiciaPorId(franquiciaId)
				.doOnNext(franquicia -> validarSucursalNoDuplicada(franquicia, nombreLimpio))
				.map(franquicia -> asociarNuevaSucursal(franquicia, nombreLimpio))
				.flatMap(franquiciaRepositoryPort::guardar);
	}

	@Override
	public Mono<Franquicia> agregarProducto(String franquiciaId, String sucursalId, String nombreProducto,
			int stockInicial) {
		if (stockInicial < 0) {
			return Mono.error(new StockInvalidoException(stockInicial));
		}
		String nombreLimpio = nombreProducto.trim();
		return buscarFranquiciaPorId(franquiciaId)
				.map(franquicia -> agregarProductoASucursal(franquicia, sucursalId, nombreLimpio, stockInicial))
				.flatMap(franquiciaRepositoryPort::guardar);
	}

	@Override
	public Mono<Franquicia> eliminarProducto(String franquiciaId, String sucursalId, String productoId) {
		return buscarFranquiciaPorId(franquiciaId)
				.map(franquicia -> eliminarProductoDeSucursal(franquicia, sucursalId, productoId))
				.flatMap(franquiciaRepositoryPort::guardar);
	}

	@Override
	public Mono<Franquicia> actualizarStockProducto(String franquiciaId, String sucursalId, String productoId,
			int nuevoStock) {
		if (nuevoStock < 0) {
			return Mono.error(new StockInvalidoException(nuevoStock));
		}
		return buscarFranquiciaPorId(franquiciaId)
				.map(franquicia -> actualizarStockEnSucursal(franquicia, sucursalId, productoId, nuevoStock))
				.flatMap(franquiciaRepositoryPort::guardar);
	}

	private Mono<Franquicia> buscarFranquiciaPorId(String franquiciaId) {
		return franquiciaRepositoryPort.buscarPorId(franquiciaId)
				.switchIfEmpty(Mono.error(new EntidadNoEncontradaException("Franquicia", franquiciaId)));
	}

	private static Sucursal buscarSucursalEnFranquicia(Franquicia franquicia, String sucursalId) {
		if (CollectionUtils.isEmpty(franquicia.sucursales())) {
			throw new EntidadNoEncontradaException("Sucursal", sucursalId);
		}
		return franquicia.sucursales().stream().filter(sucursal -> sucursal.id().equals(sucursalId)).findFirst()
				.orElseThrow(() -> new EntidadNoEncontradaException("Sucursal", sucursalId));
	}

	private static Producto buscarProductoEnSucursal(Sucursal sucursal, String productoId) {
		if (CollectionUtils.isEmpty(sucursal.productos())) {
			throw new EntidadNoEncontradaException("Producto", productoId);
		}
		return sucursal.productos().stream().filter(producto -> producto.id().equals(productoId)).findFirst()
				.orElseThrow(() -> new EntidadNoEncontradaException("Producto", productoId));
	}

	private static void validarSucursalNoDuplicada(Franquicia franquicia, String nombreSucursal) {
		if (!CollectionUtils.isEmpty(franquicia.sucursales())) {
			boolean existe = franquicia.sucursales().stream()
					.anyMatch(sucursal -> sucursal.nombre().equalsIgnoreCase(nombreSucursal));
			if (existe) {
				throw new EntidadDuplicadaException("Sucursal", nombreSucursal);
			}
		}
	}

	private static void validarProductoNoDuplicado(Sucursal sucursal, String nombreProducto) {
		if (!CollectionUtils.isEmpty(sucursal.productos())) {
			boolean existe = sucursal.productos().stream()
					.anyMatch(producto -> producto.nombre().equalsIgnoreCase(nombreProducto));
			if (existe) {
				throw new EntidadDuplicadaException("Producto", nombreProducto);
			}
		}
	}

	private static Franquicia asociarNuevaSucursal(Franquicia franquicia, String nombreSucursal) {
		Sucursal nuevaSucursal = new Sucursal(UUID.randomUUID().toString(), nombreSucursal, List.of());
		List<Sucursal> nuevasSucursales = new ArrayList<>();
		if (!CollectionUtils.isEmpty(franquicia.sucursales())) {
			nuevasSucursales.addAll(franquicia.sucursales());
		}
		nuevasSucursales.add(nuevaSucursal);

		return new Franquicia(franquicia.id(), franquicia.nombre(), nuevasSucursales);
	}

	private static Franquicia agregarProductoASucursal(Franquicia franquicia, String sucursalId, String nombreProducto,
			int stockInicial) {
		Sucursal sucursalObjetivo = buscarSucursalEnFranquicia(franquicia, sucursalId);
		validarProductoNoDuplicado(sucursalObjetivo, nombreProducto);
		Sucursal sucursalActualizada = crearSucursalConNuevoProducto(sucursalObjetivo, nombreProducto, stockInicial);
		return reemplazarSucursalEnFranquicia(franquicia, sucursalActualizada);
	}

	private static Sucursal crearSucursalConNuevoProducto(Sucursal sucursal, String nombreProducto, int stockInicial) {
		Producto nuevoProducto = new Producto(UUID.randomUUID().toString(), nombreProducto, stockInicial);
		List<Producto> nuevosProductos = new ArrayList<>();
		if (!CollectionUtils.isEmpty(sucursal.productos())) {
			nuevosProductos.addAll(sucursal.productos());
		}
		nuevosProductos.add(nuevoProducto);
		return new Sucursal(sucursal.id(), sucursal.nombre(), nuevosProductos);
	}

	private static Franquicia eliminarProductoDeSucursal(Franquicia franquicia, String sucursalId, String productoId) {
		Sucursal sucursalObjetivo = buscarSucursalEnFranquicia(franquicia, sucursalId);
		buscarProductoEnSucursal(sucursalObjetivo, productoId);
		Sucursal sucursalActualizada = crearSucursalSinProducto(sucursalObjetivo, productoId);
		return reemplazarSucursalEnFranquicia(franquicia, sucursalActualizada);
	}

	private static Sucursal crearSucursalSinProducto(Sucursal sucursal, String productoId) {
		List<Producto> productosRestantes = sucursal.productos().stream()
				.filter(producto -> !producto.id().equals(productoId)).toList();
		return new Sucursal(sucursal.id(), sucursal.nombre(), productosRestantes);
	}

	private static Franquicia actualizarStockEnSucursal(Franquicia franquicia, String sucursalId, String productoId,
			int nuevoStock) {
		Sucursal sucursalObjetivo = buscarSucursalEnFranquicia(franquicia, sucursalId);
		Producto productoObjetivo = buscarProductoEnSucursal(sucursalObjetivo, productoId);
		Producto productoActualizado = new Producto(productoObjetivo.id(), productoObjetivo.nombre(), nuevoStock);
		Sucursal sucursalActualizada = reemplazarProductoEnSucursal(sucursalObjetivo, productoActualizado);
		return reemplazarSucursalEnFranquicia(franquicia, sucursalActualizada);
	}

	private static Sucursal reemplazarProductoEnSucursal(Sucursal sucursal, Producto productoActualizado) {
		List<Producto> productosActualizados = sucursal.productos().stream()
				.map(producto -> reemplazarProductoSiCoincideId(producto, productoActualizado)).toList();
		return new Sucursal(sucursal.id(), sucursal.nombre(), productosActualizados);
	}

	private static Producto reemplazarProductoSiCoincideId(Producto productoActual, Producto productoActualizado) {
		if (productoActual.id().equals(productoActualizado.id())) {
			return productoActualizado;
		}
		return productoActual;
	}

	private static Franquicia reemplazarSucursalEnFranquicia(Franquicia franquicia, Sucursal sucursalActualizada) {
		List<Sucursal> sucursalesActualizadas = franquicia.sucursales().stream()
				.map(sucursal -> reemplazarSiCoincideId(sucursal, sucursalActualizada)).toList();
		return new Franquicia(franquicia.id(), franquicia.nombre(), sucursalesActualizadas);
	}

	private static Sucursal reemplazarSiCoincideId(Sucursal sucursalActual, Sucursal sucursalActualizada) {
		if (sucursalActual.id().equals(sucursalActualizada.id())) {
			return sucursalActualizada;
		}
		return sucursalActual;
	}
}
