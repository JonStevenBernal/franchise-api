package dev.johnbernal.franchise.infrastructure.adapter.in.web.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AgregarProductoRequest(
		@NotBlank(message = "El nombre del producto es obligatorio y no puede estar vacio") String nombre,
		@NotNull(message = "El stock inicial es obligatorio") @Min(value = 0, message = "El stock inicial no puede ser negativo") Integer stock) {
}
