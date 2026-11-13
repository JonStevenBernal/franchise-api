package dev.johnbernal.franchise.infrastructure.adapter.in.web.dto;

import jakarta.validation.constraints.NotBlank;

public record ActualizarNombreRequest(
		@NotBlank(message = "El nombre es obligatorio y no puede estar vacio") String nombre) {
}

