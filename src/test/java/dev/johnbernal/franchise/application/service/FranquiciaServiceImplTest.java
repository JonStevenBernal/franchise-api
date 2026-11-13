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
import dev.johnbernal.franchise.domain.exception.StockInvalidoException;
import dev.johnbernal.franchise.domain.model.Franquicia;
import dev.johnbernal.franchise.domain.model.Producto;
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
	private String sucursalId;
	private String nombreProducto;
	private int stockInicial;
	private String productoId;
	private Sucursal sucursalExistente;
	private Producto productoExistente;
	private Franquicia franquiciaExistente;
	private Franquicia franquiciaConSucursal;
	private Franquicia franquiciaConSucursalYProducto;

	@BeforeEach
	void setUp() {
		franquiciaService = new FranquiciaServiceImpl(franquiciaRepositoryPort);
		nombreFranquicia = "Franquicia El Dorado";
		franquiciaId = "franquicia-id-123";
		nombreSucursal = "Sucursal Norte";
		sucursalId = "sucursal-1";
		nombreProducto = "Cafe Latte";
		stockInicial = 25;
		productoId = "producto-1";
		sucursalExistente = new Sucursal(sucursalId, nombreSucursal, List.of());
		productoExistente = new Producto(productoId, nombreProducto, stockInicial);
		Sucursal sucursalConProducto = new Sucursal(sucursalId, nombreSucursal, List.of(productoExistente));
		franquiciaExistente = new Franquicia(franquiciaId, nombreFranquicia, List.of());
		franquiciaConSucursal = new Franquicia(franquiciaId, nombreFranquicia, List.of(sucursalExistente));
		franquiciaConSucursalYProducto = new Franquicia(franquiciaId, nombreFranquicia, List.of(sucursalConProducto));
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

	@Test
	@DisplayName("Debe agregar un nuevo producto a la sucursal cuando los datos son validos y no esta duplicado")
	void agregarProductoConDatosValidosDebePersistirYRetornarFranquicia() {
		when(franquiciaRepositoryPort.buscarPorId(franquiciaId)).thenReturn(Mono.just(franquiciaConSucursal));
		when(franquiciaRepositoryPort.guardar(any(Franquicia.class)))
				.thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));

		StepVerifier.create(franquiciaService.agregarProducto(franquiciaId, sucursalId, nombreProducto, stockInicial))
				.assertNext(franquiciaActualizada -> {
					assertThat(franquiciaActualizada.id()).isEqualTo(franquiciaId);
					assertThat(franquiciaActualizada.sucursales()).hasSize(1);
					Sucursal sucursalActualizada = franquiciaActualizada.sucursales().getFirst();
					assertThat(sucursalActualizada.productos()).hasSize(1);
					Producto productoCreado = sucursalActualizada.productos().getFirst();
					assertThat(productoCreado.id()).isNotNull();
					assertThat(productoCreado.nombre()).isEqualTo(nombreProducto);
					assertThat(productoCreado.stock()).isEqualTo(stockInicial);
				}).verifyComplete();

		verify(franquiciaRepositoryPort).buscarPorId(franquiciaId);
		verify(franquiciaRepositoryPort).guardar(any(Franquicia.class));
	}

	@Test
	@DisplayName("Debe retornar error StockInvalidoException cuando el stock inicial es negativo")
	void agregarProductoConStockNegativoDebeLanzarExcepcion() {
		int stockNegativo = -5;

		StepVerifier.create(franquiciaService.agregarProducto(franquiciaId, sucursalId, nombreProducto, stockNegativo))
				.expectErrorMatches(throwable -> throwable instanceof StockInvalidoException).verify();

		verify(franquiciaRepositoryPort, never()).buscarPorId(any());
		verify(franquiciaRepositoryPort, never()).guardar(any(Franquicia.class));
	}

	@Test
	@DisplayName("Debe retornar error EntidadNoEncontradaException cuando la franquicia no existe al agregar producto")
	void agregarProductoCuandoFranquiciaNoExisteDebeLanzarExcepcion() {
		when(franquiciaRepositoryPort.buscarPorId(franquiciaId)).thenReturn(Mono.empty());

		StepVerifier.create(franquiciaService.agregarProducto(franquiciaId, sucursalId, nombreProducto, stockInicial))
				.expectErrorMatches(throwable -> {
					boolean esTipoCorrecto = throwable instanceof EntidadNoEncontradaException;
					boolean contieneMensaje = throwable.getMessage().contains(franquiciaId);
					return esTipoCorrecto && contieneMensaje;
				}).verify();

		verify(franquiciaRepositoryPort).buscarPorId(franquiciaId);
		verify(franquiciaRepositoryPort, never()).guardar(any(Franquicia.class));
	}

	@Test
	@DisplayName("Debe retornar error EntidadNoEncontradaException cuando la sucursal no existe en la franquicia")
	void agregarProductoCuandoSucursalNoExisteDebeLanzarExcepcion() {
		String sucursalInexistenteId = "sucursal-inexistente";
		when(franquiciaRepositoryPort.buscarPorId(franquiciaId)).thenReturn(Mono.just(franquiciaConSucursal));

		StepVerifier.create(
				franquiciaService.agregarProducto(franquiciaId, sucursalInexistenteId, nombreProducto, stockInicial))
				.expectErrorMatches(throwable -> {
					boolean esTipoCorrecto = throwable instanceof EntidadNoEncontradaException;
					boolean contieneMensaje = throwable.getMessage().contains(sucursalInexistenteId);
					return esTipoCorrecto && contieneMensaje;
				}).verify();

		verify(franquiciaRepositoryPort).buscarPorId(franquiciaId);
		verify(franquiciaRepositoryPort, never()).guardar(any(Franquicia.class));
	}

	@Test
	@DisplayName("Debe retornar error EntidadDuplicadaException cuando ya existe un producto con ese nombre en la sucursal")
	void agregarProductoConNombreDuplicadoDebeLanzarExcepcion() {
		when(franquiciaRepositoryPort.buscarPorId(franquiciaId)).thenReturn(Mono.just(franquiciaConSucursalYProducto));

		StepVerifier.create(franquiciaService.agregarProducto(franquiciaId, sucursalId, nombreProducto, stockInicial))
				.expectErrorMatches(throwable -> {
					boolean esTipoCorrecto = throwable instanceof EntidadDuplicadaException;
					boolean contieneMensaje = throwable.getMessage().contains(nombreProducto);
					return esTipoCorrecto && contieneMensaje;
				}).verify();

		verify(franquiciaRepositoryPort).buscarPorId(franquiciaId);
		verify(franquiciaRepositoryPort, never()).guardar(any(Franquicia.class));
	}
}
