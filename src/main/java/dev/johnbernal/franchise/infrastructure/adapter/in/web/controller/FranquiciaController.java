package dev.johnbernal.franchise.infrastructure.adapter.in.web.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import dev.johnbernal.franchise.domain.port.in.FranquiciaUseCasePort;
import dev.johnbernal.franchise.infrastructure.adapter.in.web.dto.AgregarSucursalRequest;
import dev.johnbernal.franchise.infrastructure.adapter.in.web.dto.CrearFranquiciaRequest;
import dev.johnbernal.franchise.infrastructure.adapter.in.web.dto.FranquiciaResponse;
import jakarta.validation.Valid;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/franquicias")
public class FranquiciaController {

	private final FranquiciaUseCasePort franquiciaUseCasePort;

	public FranquiciaController(FranquiciaUseCasePort franquiciaUseCasePort) {
		this.franquiciaUseCasePort = franquiciaUseCasePort;
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public Mono<FranquiciaResponse> crearFranquicia(@Valid @RequestBody CrearFranquiciaRequest request) {
		return franquiciaUseCasePort.crearFranquicia(request.nombre()).map(FranquiciaResponse::fromDomain);
	}

	@PostMapping("/{franquiciaId}/sucursales")
	@ResponseStatus(HttpStatus.CREATED)
	public Mono<FranquiciaResponse> agregarSucursal(@PathVariable String franquiciaId,
			@Valid @RequestBody AgregarSucursalRequest request) {
		return franquiciaUseCasePort.agregarSucursal(franquiciaId, request.nombre())
				.map(FranquiciaResponse::fromDomain);
	}
}
