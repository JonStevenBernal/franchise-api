package dev.johnbernal.franchise.infrastructure.adapter.out.persistence.mapper;

import java.util.ArrayList;
import java.util.List;

import org.springframework.util.CollectionUtils;

import dev.johnbernal.franchise.domain.model.Franquicia;
import dev.johnbernal.franchise.domain.model.Producto;
import dev.johnbernal.franchise.domain.model.Sucursal;
import dev.johnbernal.franchise.infrastructure.adapter.out.persistence.entity.FranquiciaDocument;
import dev.johnbernal.franchise.infrastructure.adapter.out.persistence.entity.ProductoDocument;
import dev.johnbernal.franchise.infrastructure.adapter.out.persistence.entity.SucursalDocument;

public final class FranquiciaPersistenceMapper {

	private FranquiciaPersistenceMapper() {
	}

	public static FranquiciaDocument toDocument(Franquicia franquicia) {
		if (franquicia == null) {
			return null;
		}
		List<SucursalDocument> sucursalesDoc = new ArrayList<>();
		if (!CollectionUtils.isEmpty(franquicia.sucursales())) {
			for (Sucursal sucursal : franquicia.sucursales()) {
				sucursalesDoc.add(toSucursalDocument(sucursal));
			}
		}
		return new FranquiciaDocument(franquicia.id(), franquicia.nombre(), sucursalesDoc);
	}

	public static Franquicia toDomain(FranquiciaDocument documento) {
		if (documento == null) {
			return null;
		}
		List<Sucursal> sucursalesDomain = new ArrayList<>();
		if (!CollectionUtils.isEmpty(documento.sucursales())) {
			for (SucursalDocument sucursalDoc : documento.sucursales()) {
				sucursalesDomain.add(toSucursalDomain(sucursalDoc));
			}
		}
		return new Franquicia(documento.id(), documento.nombre(), sucursalesDomain);
	}

	private static SucursalDocument toSucursalDocument(Sucursal sucursal) {
		List<ProductoDocument> productosDoc = new ArrayList<>();
		if (!CollectionUtils.isEmpty(sucursal.productos())) {
			for (Producto producto : sucursal.productos()) {
				productosDoc.add(new ProductoDocument(producto.id(), producto.nombre(), producto.stock()));
			}
		}
		return new SucursalDocument(sucursal.id(), sucursal.nombre(), productosDoc);
	}

	private static Sucursal toSucursalDomain(SucursalDocument documento) {
		List<Producto> productosDomain = new ArrayList<>();
		if (!CollectionUtils.isEmpty(documento.productos())) {
			for (ProductoDocument productoDoc : documento.productos()) {
				productosDomain.add(new Producto(productoDoc.id(), productoDoc.nombre(), productoDoc.stock()));
			}
		}
		return new Sucursal(documento.id(), documento.nombre(), productosDomain);
	}
}
