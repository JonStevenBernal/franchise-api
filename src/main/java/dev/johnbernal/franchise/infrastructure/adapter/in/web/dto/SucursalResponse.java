package dev.johnbernal.franchise.infrastructure.adapter.in.web.dto;

import java.util.List;

public record SucursalResponse(String id, String nombre, List<ProductoResponse> productos) {
}
