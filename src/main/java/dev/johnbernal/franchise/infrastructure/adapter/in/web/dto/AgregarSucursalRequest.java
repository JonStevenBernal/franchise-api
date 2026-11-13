package dev.johnbernal.franchise.infrastructure.adapter.in.web.dto;

import jakarta.validation.constraints.NotBlank;

public record AgregarSucursalRequest(
		@NotBlank(message = "El nombre de la sucursal es obligatorio y no puede estar vacio") String nombre) {
}
