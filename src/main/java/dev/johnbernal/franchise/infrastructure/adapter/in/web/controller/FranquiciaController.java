package dev.johnbernal.franchise.infrastructure.adapter.in.web.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import dev.johnbernal.franchise.domain.port.in.FranquiciaUseCasePort;
import dev.johnbernal.franchise.infrastructure.adapter.in.web.dto.AgregarProductoRequest;
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

	@PostMapping("/{franquiciaId}/sucursales/{sucursalId}/productos")
	@ResponseStatus(HttpStatus.CREATED)
	public Mono<FranquiciaResponse> agregarProducto(@PathVariable String franquiciaId, @PathVariable String sucursalId,
			@Valid @RequestBody AgregarProductoRequest request) {
		return franquiciaUseCasePort.agregarProducto(franquiciaId, sucursalId, request.nombre(), request.stock())
				.map(FranquiciaResponse::fromDomain);
	}

	@DeleteMapping("/{franquiciaId}/sucursales/{sucursalId}/productos/{productoId}")
	@ResponseStatus(HttpStatus.OK)
	public Mono<FranquiciaResponse> eliminarProducto(@PathVariable String franquiciaId, @PathVariable String sucursalId,
			@PathVariable String productoId) {
		return franquiciaUseCasePort.eliminarProducto(franquiciaId, sucursalId, productoId)
				.map(FranquiciaResponse::fromDomain);
	}
}
