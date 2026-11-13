package dev.johnbernal.franchise.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import dev.johnbernal.franchise.domain.exception.EntidadDuplicadaException;
import dev.johnbernal.franchise.domain.exception.EntidadNoEncontradaException;
import dev.johnbernal.franchise.domain.model.Franquicia;
import dev.johnbernal.franchise.domain.model.Sucursal;
import dev.johnbernal.franchise.domain.port.out.FranquiciaRepositoryPort;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class FranquiciaServiceImplTest {

	@Mock
	private FranquiciaRepositoryPort franquiciaRepositoryPort;

	private FranquiciaServiceImpl franquiciaService;

	private String nombreFranquicia;
	private String franquiciaId;
	private String nombreSucursal;

	@BeforeEach
	void setUp() {
		franquiciaService = new FranquiciaServiceImpl(franquiciaRepositoryPort);
		nombreFranquicia = "Franquicia El Dorado";
		franquiciaId = "franquicia-id-123";
		nombreSucursal = "Sucursal Norte";
	}

	@Test
	@DisplayName("Debe crear y retornar una franquicia cuando el nombre es valido y no existe")
	void crearFranquiciaConNombreValidoDebePersistirYRetornarFranquicia() {
		when(franquiciaRepositoryPort.existePorNombre(nombreFranquicia)).thenReturn(Mono.just(false));

		when(franquiciaRepositoryPort.guardar(any(Franquicia.class)))
				.thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));

		StepVerifier.create(franquiciaService.crearFranquicia("  " + nombreFranquicia + "  "))
				.assertNext(franquicia -> {
					assertThat(franquicia.id()).isNotNull();
					assertThat(franquicia.nombre()).isEqualTo(nombreFranquicia);
					assertThat(franquicia.sucursales()).isEmpty();
				}).verifyComplete();

		verify(franquiciaRepositoryPort).existePorNombre(nombreFranquicia);
		verify(franquiciaRepositoryPort).guardar(any(Franquicia.class));
	}

	@Test
	@DisplayName("Debe retornar error EntidadDuplicadaException cuando ya existe una franquicia con ese nombre")
	void crearFranquiciaConNombreDuplicadoDebeLanzarExcepcion() {
		when(franquiciaRepositoryPort.existePorNombre(nombreFranquicia)).thenReturn(Mono.just(true));

		StepVerifier.create(franquiciaService.crearFranquicia(nombreFranquicia)).expectErrorMatches(throwable -> {
			boolean esTipoCorrecto = throwable instanceof EntidadDuplicadaException;
			boolean contieneMensaje = throwable.getMessage().contains(nombreFranquicia);
			return esTipoCorrecto && contieneMensaje;
		}).verify();

		verify(franquiciaRepositoryPort).existePorNombre(nombreFranquicia);
		verify(franquiciaRepositoryPort, never()).guardar(any(Franquicia.class));
	}

	@Test
	@DisplayName("Debe agregar una sucursal exitosamente a una franquicia existente")
	void agregarSucursalConDatosValidosDebeAgregarSucursalYRetornarFranquicia() {
		Franquicia franquiciaExistente = new Franquicia(franquiciaId, nombreFranquicia, List.of());

		when(franquiciaRepositoryPort.buscarPorId(franquiciaId)).thenReturn(Mono.just(franquiciaExistente));

		when(franquiciaRepositoryPort.guardar(any(Franquicia.class)))
				.thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));

		StepVerifier.create(franquiciaService.agregarSucursal(franquiciaId, "  " + nombreSucursal + "  "))
				.assertNext(franquiciaActualizada -> {
					assertThat(franquiciaActualizada.sucursales()).hasSize(1);
					Sucursal sucursalCreada = franquiciaActualizada.sucursales().getFirst();
					assertThat(sucursalCreada.id()).isNotNull();
					assertThat(sucursalCreada.nombre()).isEqualTo(nombreSucursal);
					assertThat(sucursalCreada.productos()).isEmpty();
				}).verifyComplete();

		verify(franquiciaRepositoryPort).buscarPorId(franquiciaId);
		verify(franquiciaRepositoryPort).guardar(any(Franquicia.class));
	}

	@Test
	@DisplayName("Debe retornar error EntidadDuplicadaException cuando ya existe una sucursal con ese nombre en la franquicia")
	void agregarSucursalConNombreDuplicadoDebeLanzarExcepcion() {
		Sucursal sucursalExistente = new Sucursal("sucursal-1", nombreSucursal, List.of());
		Franquicia franquiciaConSucursal = new Franquicia(franquiciaId, nombreFranquicia, List.of(sucursalExistente));

		when(franquiciaRepositoryPort.buscarPorId(franquiciaId)).thenReturn(Mono.just(franquiciaConSucursal));

		StepVerifier.create(franquiciaService.agregarSucursal(franquiciaId, nombreSucursal))
				.expectErrorMatches(throwable -> {
					boolean esTipoCorrecto = throwable instanceof EntidadDuplicadaException;
					boolean contieneMensaje = throwable.getMessage().contains(nombreSucursal);
					return esTipoCorrecto && contieneMensaje;
				}).verify();

		verify(franquiciaRepositoryPort).buscarPorId(franquiciaId);
		verify(franquiciaRepositoryPort, never()).guardar(any(Franquicia.class));
	}

	@Test
	@DisplayName("Debe retornar error EntidadNoEncontradaException cuando la franquicia no existe")
	void agregarSucursalCuandoFranquiciaNoExisteDebeLanzarExcepcion() {
		when(franquiciaRepositoryPort.buscarPorId(franquiciaId)).thenReturn(Mono.empty());

		StepVerifier.create(franquiciaService.agregarSucursal(franquiciaId, nombreSucursal))
				.expectErrorMatches(throwable -> {
					boolean esTipoCorrecto = throwable instanceof EntidadNoEncontradaException;
					boolean contieneMensaje = throwable.getMessage().contains(franquiciaId);
					return esTipoCorrecto && contieneMensaje;
				}).verify();

		verify(franquiciaRepositoryPort).buscarPorId(franquiciaId);
		verify(franquiciaRepositoryPort, never()).guardar(any(Franquicia.class));
	}
}
