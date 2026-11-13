package dev.johnbernal.franchise.application.service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import dev.johnbernal.franchise.domain.exception.EntidadDuplicadaException;
import dev.johnbernal.franchise.domain.exception.EntidadNoEncontradaException;
import dev.johnbernal.franchise.domain.model.Franquicia;
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

	private Mono<Franquicia> buscarFranquiciaPorId(String franquiciaId) {
		return franquiciaRepositoryPort.buscarPorId(franquiciaId)
				.switchIfEmpty(Mono.error(new EntidadNoEncontradaException("Franquicia", franquiciaId)));
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

	private static Franquicia asociarNuevaSucursal(Franquicia franquicia, String nombreSucursal) {
		Sucursal nuevaSucursal = new Sucursal(UUID.randomUUID().toString(), nombreSucursal, List.of());
		List<Sucursal> nuevasSucursales = new ArrayList<>();
		if (!CollectionUtils.isEmpty(franquicia.sucursales())) {
			nuevasSucursales.addAll(franquicia.sucursales());
		}
		nuevasSucursales.add(nuevaSucursal);

		return new Franquicia(franquicia.id(), franquicia.nombre(), nuevasSucursales);
	}
}
