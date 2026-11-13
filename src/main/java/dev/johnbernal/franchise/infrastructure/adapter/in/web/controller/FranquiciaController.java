package dev.johnbernal.franchise.infrastructure.adapter.in.web.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import dev.johnbernal.franchise.domain.port.in.FranquiciaUseCasePort;
import dev.johnbernal.franchise.infrastructure.adapter.in.web.dto.ActualizarNombreRequest;
import dev.johnbernal.franchise.infrastructure.adapter.in.web.dto.ActualizarStockRequest;
import dev.johnbernal.franchise.infrastructure.adapter.in.web.dto.AgregarProductoRequest;
import dev.johnbernal.franchise.infrastructure.adapter.in.web.dto.AgregarSucursalRequest;
import dev.johnbernal.franchise.infrastructure.adapter.in.web.dto.CrearFranquiciaRequest;
import dev.johnbernal.franchise.infrastructure.adapter.in.web.dto.FranquiciaResponse;
import dev.johnbernal.franchise.infrastructure.adapter.in.web.dto.SucursalProductoMayorStockResponse;
import jakarta.validation.Valid;
import reactor.core.publisher.Flux;
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

	@PatchMapping("/{franquiciaId}/nombre")
	@ResponseStatus(HttpStatus.OK)
	public Mono<FranquiciaResponse> actualizarNombreFranquicia(@PathVariable String franquiciaId,
			@Valid @RequestBody ActualizarNombreRequest request) {
		return franquiciaUseCasePort.actualizarNombreFranquicia(franquiciaId, request.nombre())
				.map(FranquiciaResponse::fromDomain);
	}

	@PostMapping("/{franquiciaId}/sucursales")
	@ResponseStatus(HttpStatus.CREATED)
	public Mono<FranquiciaResponse> agregarSucursal(@PathVariable String franquiciaId,
			@Valid @RequestBody AgregarSucursalRequest request) {
		return franquiciaUseCasePort.agregarSucursal(franquiciaId, request.nombre())
				.map(FranquiciaResponse::fromDomain);
	}

	@PatchMapping("/{franquiciaId}/sucursales/{sucursalId}/nombre")
	@ResponseStatus(HttpStatus.OK)
	public Mono<FranquiciaResponse> actualizarNombreSucursal(@PathVariable String franquiciaId,
			@PathVariable String sucursalId, @Valid @RequestBody ActualizarNombreRequest request) {
		return franquiciaUseCasePort.actualizarNombreSucursal(franquiciaId, sucursalId, request.nombre())
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

	@PatchMapping("/{franquiciaId}/sucursales/{sucursalId}/productos/{productoId}/stock")
	@ResponseStatus(HttpStatus.OK)
	public Mono<FranquiciaResponse> actualizarStockProducto(@PathVariable String franquiciaId,
			@PathVariable String sucursalId, @PathVariable String productoId,
			@Valid @RequestBody ActualizarStockRequest request) {
		return franquiciaUseCasePort.actualizarStockProducto(franquiciaId, sucursalId, productoId, request.stock())
				.map(FranquiciaResponse::fromDomain);
	}

	@GetMapping("/{franquiciaId}/productos/mayor-stock")
	@ResponseStatus(HttpStatus.OK)
	public Flux<SucursalProductoMayorStockResponse> obtenerProductosConMayorStockPorSucursal(
			@PathVariable String franquiciaId) {
		return franquiciaUseCasePort.obtenerProductosConMayorStockPorSucursal(franquiciaId)
				.map(SucursalProductoMayorStockResponse::fromDomain);
	}
}
