package dev.johnbernal.franchise.application.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import dev.johnbernal.franchise.domain.exception.EntidadDuplicadaException;
import dev.johnbernal.franchise.domain.model.Franquicia;
import dev.johnbernal.franchise.domain.port.in.FranquiciaUseCasePort;
import dev.johnbernal.franchise.domain.port.out.FranquiciaRepositoryPort;
import reactor.core.publisher.Mono;

/**
 * Implementación del servicio de aplicación para la gestión de Franquicias.
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
		return franquiciaRepositoryPort.existePorNombre(nombreLimpio).flatMap(existe -> {
			if (Boolean.TRUE.equals(existe)) {
				return Mono.error(new EntidadDuplicadaException("Franquicia", nombreLimpio));
			}
			Franquicia nuevaFranquicia = new Franquicia(UUID.randomUUID().toString(), nombreLimpio, List.of());
			return franquiciaRepositoryPort.guardar(nuevaFranquicia);
		});
	}
}
