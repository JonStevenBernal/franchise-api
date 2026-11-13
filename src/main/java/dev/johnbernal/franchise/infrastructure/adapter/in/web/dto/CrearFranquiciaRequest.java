package dev.johnbernal.franchise.infrastructure.adapter.in.web.dto;

import jakarta.validation.constraints.NotBlank;

public record CrearFranquiciaRequest(
		@NotBlank(message = "El nombre de la franquicia es obligatorio y no puede estar vacio") String nombre) {
}
