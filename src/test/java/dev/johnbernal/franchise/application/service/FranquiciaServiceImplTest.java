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
import dev.johnbernal.franchise.domain.model.SucursalProductoMayorStock;
import dev.johnbernal.franchise.domain.port.out.FranquiciaRepositoryPort;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class FranquiciaServiceImplTest {

	@Mock
	private FranquiciaRepositoryPort franquiciaRepositoryPort;

	private FranquiciaServiceImpl franquiciaService;

	private String nombreFranquicia;
	private String nuevoNombreFranquicia;
	private String nombreDuplicado;
	private String franquiciaId;
	private String nombreSucursal;
	private String sucursalId;
	private String sucursalInexistenteId;
	private String nombreProducto;
	private int stockInicial;
	private int nuevoStock;
	private int stockNegativo;
	private String productoId;
	private String productoInexistenteId;
	private Sucursal sucursalExistente;
	private Producto productoExistente;
	private Producto otroProducto;
	private Producto productoMayorSur;
	private Franquicia franquiciaExistente;
	private Franquicia franquiciaConSucursal;
	private Franquicia franquiciaConSucursalYProducto;
	private Franquicia franquiciaConDosProductos;
	private Franquicia franquiciaMultiSucursal;

	@BeforeEach
	void setUp() {
		franquiciaService = new FranquiciaServiceImpl(franquiciaRepositoryPort);
		nombreFranquicia = "Franquicia El Dorado";
		nuevoNombreFranquicia = "Franquicia El Dorado Renombrada";
		nombreDuplicado = "Otra Franquicia";
		franquiciaId = "franquicia-id-123";
		nombreSucursal = "Sucursal Norte";
		sucursalId = "sucursal-1";
		sucursalInexistenteId = "sucursal-inexistente";
		nombreProducto = "Cafe Latte";
		stockInicial = 25;
		nuevoStock = 80;
		stockNegativo = -5;
		productoId = "producto-1";
		productoInexistenteId = "producto-inexistente";
		sucursalExistente = new Sucursal(sucursalId, nombreSucursal, List.of());
		productoExistente = new Producto(productoId, nombreProducto, stockInicial);
		otroProducto = new Producto("producto-2", "Croissant", 10);
		productoMayorSur = new Producto("producto-3", "Capuccino", 60);
		Producto productoMenorSur = new Producto("producto-4", "Te Verde", 12);
		Sucursal sucursalConProducto = new Sucursal(sucursalId, nombreSucursal, List.of(productoExistente));
		Sucursal sucursalConDosProductos = new Sucursal(sucursalId, nombreSucursal,
				List.of(productoExistente, otroProducto));
		Sucursal sucursalSinProductos = new Sucursal("sucursal-2", "Sucursal Centro", List.of());
		Sucursal sucursalSur = new Sucursal("sucursal-3", "Sucursal Sur", List.of(productoMenorSur, productoMayorSur));
		franquiciaExistente = new Franquicia(franquiciaId, nombreFranquicia, List.of());
		franquiciaConSucursal = new Franquicia(franquiciaId, nombreFranquicia, List.of(sucursalExistente));
		franquiciaConSucursalYProducto = new Franquicia(franquiciaId, nombreFranquicia, List.of(sucursalConProducto));
		franquiciaConDosProductos = new Franquicia(franquiciaId, nombreFranquicia, List.of(sucursalConDosProductos));
		franquiciaMultiSucursal = new Franquicia(franquiciaId, nombreFranquicia,
				List.of(sucursalConDosProductos, sucursalSinProductos, sucursalSur));
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

	@Test
	@DisplayName("Debe eliminar el producto indicado de la sucursal y conservar los demas productos")
	void eliminarProductoExistenteDebeEliminarYRetornarFranquicia() {
		when(franquiciaRepositoryPort.buscarPorId(franquiciaId)).thenReturn(Mono.just(franquiciaConDosProductos));
		when(franquiciaRepositoryPort.guardar(any(Franquicia.class)))
				.thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));

		StepVerifier.create(franquiciaService.eliminarProducto(franquiciaId, sucursalId, productoId))
				.assertNext(franquiciaActualizada -> {
					assertThat(franquiciaActualizada.id()).isEqualTo(franquiciaId);
					assertThat(franquiciaActualizada.sucursales()).hasSize(1);
					Sucursal sucursalActualizada = franquiciaActualizada.sucursales().getFirst();
					assertThat(sucursalActualizada.productos()).containsExactly(otroProducto);
				}).verifyComplete();

		verify(franquiciaRepositoryPort).buscarPorId(franquiciaId);
		verify(franquiciaRepositoryPort).guardar(any(Franquicia.class));
	}

	@Test
	@DisplayName("Debe retornar error EntidadNoEncontradaException cuando la franquicia no existe al eliminar producto")
	void eliminarProductoCuandoFranquiciaNoExisteDebeLanzarExcepcion() {
		when(franquiciaRepositoryPort.buscarPorId(franquiciaId)).thenReturn(Mono.empty());

		StepVerifier.create(franquiciaService.eliminarProducto(franquiciaId, sucursalId, productoId))
				.expectErrorMatches(throwable -> {
					boolean esTipoCorrecto = throwable instanceof EntidadNoEncontradaException;
					boolean contieneMensaje = throwable.getMessage().contains(franquiciaId);
					return esTipoCorrecto && contieneMensaje;
				}).verify();

		verify(franquiciaRepositoryPort).buscarPorId(franquiciaId);
		verify(franquiciaRepositoryPort, never()).guardar(any(Franquicia.class));
	}

	@Test
	@DisplayName("Debe retornar error EntidadNoEncontradaException cuando la sucursal no existe al eliminar producto")
	void eliminarProductoCuandoSucursalNoExisteDebeLanzarExcepcion() {
		when(franquiciaRepositoryPort.buscarPorId(franquiciaId)).thenReturn(Mono.just(franquiciaConSucursalYProducto));

		StepVerifier.create(franquiciaService.eliminarProducto(franquiciaId, sucursalInexistenteId, productoId))
				.expectErrorMatches(throwable -> {
					boolean esTipoCorrecto = throwable instanceof EntidadNoEncontradaException;
					boolean contieneMensaje = throwable.getMessage().contains(sucursalInexistenteId);
					return esTipoCorrecto && contieneMensaje;
				}).verify();

		verify(franquiciaRepositoryPort).buscarPorId(franquiciaId);
		verify(franquiciaRepositoryPort, never()).guardar(any(Franquicia.class));
	}

	@Test
	@DisplayName("Debe retornar error EntidadNoEncontradaException cuando el producto no existe en la sucursal")
	void eliminarProductoCuandoProductoNoExisteDebeLanzarExcepcion() {
		when(franquiciaRepositoryPort.buscarPorId(franquiciaId)).thenReturn(Mono.just(franquiciaConSucursalYProducto));

		StepVerifier.create(franquiciaService.eliminarProducto(franquiciaId, sucursalId, productoInexistenteId))
				.expectErrorMatches(throwable -> {
					boolean esTipoCorrecto = throwable instanceof EntidadNoEncontradaException;
					boolean contieneMensaje = throwable.getMessage().contains(productoInexistenteId);
					return esTipoCorrecto && contieneMensaje;
				}).verify();

		verify(franquiciaRepositoryPort).buscarPorId(franquiciaId);
		verify(franquiciaRepositoryPort, never()).guardar(any(Franquicia.class));
	}

	@Test
	@DisplayName("Debe actualizar el stock del producto indicado y conservar los demas productos intactos")
	void actualizarStockProductoConDatosValidosDebeActualizarYRetornarFranquicia() {
		when(franquiciaRepositoryPort.buscarPorId(franquiciaId)).thenReturn(Mono.just(franquiciaConDosProductos));
		when(franquiciaRepositoryPort.guardar(any(Franquicia.class)))
				.thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));

		StepVerifier.create(franquiciaService.actualizarStockProducto(franquiciaId, sucursalId, productoId, nuevoStock))
				.assertNext(franquiciaActualizada -> {
					assertThat(franquiciaActualizada.id()).isEqualTo(franquiciaId);
					Sucursal sucursalActualizada = franquiciaActualizada.sucursales().getFirst();
					assertThat(sucursalActualizada.productos()).hasSize(2);
					Producto productoActualizado = sucursalActualizada.productos().getFirst();
					assertThat(productoActualizado.id()).isEqualTo(productoId);
					assertThat(productoActualizado.nombre()).isEqualTo(nombreProducto);
					assertThat(productoActualizado.stock()).isEqualTo(nuevoStock);
					assertThat(sucursalActualizada.productos().get(1)).isEqualTo(otroProducto);
				}).verifyComplete();

		verify(franquiciaRepositoryPort).buscarPorId(franquiciaId);
		verify(franquiciaRepositoryPort).guardar(any(Franquicia.class));
	}

	@Test
	@DisplayName("Debe retornar error StockInvalidoException cuando el nuevo stock es negativo")
	void actualizarStockProductoConStockNegativoDebeLanzarExcepcion() {
		StepVerifier
				.create(franquiciaService.actualizarStockProducto(franquiciaId, sucursalId, productoId, stockNegativo))
				.expectErrorMatches(throwable -> throwable instanceof StockInvalidoException).verify();

		verify(franquiciaRepositoryPort, never()).buscarPorId(any());
		verify(franquiciaRepositoryPort, never()).guardar(any(Franquicia.class));
	}

	@Test
	@DisplayName("Debe retornar error EntidadNoEncontradaException cuando la franquicia no existe al actualizar stock")
	void actualizarStockProductoCuandoFranquiciaNoExisteDebeLanzarExcepcion() {
		when(franquiciaRepositoryPort.buscarPorId(franquiciaId)).thenReturn(Mono.empty());

		StepVerifier.create(franquiciaService.actualizarStockProducto(franquiciaId, sucursalId, productoId, nuevoStock))
				.expectErrorMatches(throwable -> {
					boolean esTipoCorrecto = throwable instanceof EntidadNoEncontradaException;
					boolean contieneMensaje = throwable.getMessage().contains(franquiciaId);
					return esTipoCorrecto && contieneMensaje;
				}).verify();

		verify(franquiciaRepositoryPort).buscarPorId(franquiciaId);
		verify(franquiciaRepositoryPort, never()).guardar(any(Franquicia.class));
	}

	@Test
	@DisplayName("Debe retornar error EntidadNoEncontradaException cuando la sucursal no existe al actualizar stock")
	void actualizarStockProductoCuandoSucursalNoExisteDebeLanzarExcepcion() {
		when(franquiciaRepositoryPort.buscarPorId(franquiciaId)).thenReturn(Mono.just(franquiciaConSucursalYProducto));

		StepVerifier.create(
				franquiciaService.actualizarStockProducto(franquiciaId, sucursalInexistenteId, productoId, nuevoStock))
				.expectErrorMatches(throwable -> {
					boolean esTipoCorrecto = throwable instanceof EntidadNoEncontradaException;
					boolean contieneMensaje = throwable.getMessage().contains(sucursalInexistenteId);
					return esTipoCorrecto && contieneMensaje;
				}).verify();

		verify(franquiciaRepositoryPort).buscarPorId(franquiciaId);
		verify(franquiciaRepositoryPort, never()).guardar(any(Franquicia.class));
	}

	@Test
	@DisplayName("Debe retornar error EntidadNoEncontradaException cuando el producto no existe al actualizar stock")
	void actualizarStockProductoCuandoProductoNoExisteDebeLanzarExcepcion() {
		when(franquiciaRepositoryPort.buscarPorId(franquiciaId)).thenReturn(Mono.just(franquiciaConSucursalYProducto));

		StepVerifier.create(
				franquiciaService.actualizarStockProducto(franquiciaId, sucursalId, productoInexistenteId, nuevoStock))
				.expectErrorMatches(throwable -> {
					boolean esTipoCorrecto = throwable instanceof EntidadNoEncontradaException;
					boolean contieneMensaje = throwable.getMessage().contains(productoInexistenteId);
					return esTipoCorrecto && contieneMensaje;
				}).verify();

		verify(franquiciaRepositoryPort).buscarPorId(franquiciaId);
		verify(franquiciaRepositoryPort, never()).guardar(any(Franquicia.class));
	}

	@Test
	@DisplayName("Debe retornar el producto con mayor stock por cada sucursal con productos e ignorar sucursales vacias")
	void obtenerProductosConMayorStockPorSucursalCuandoTieneSucursalesDebeRetornarMayorStock() {
		when(franquiciaRepositoryPort.buscarPorId(franquiciaId)).thenReturn(Mono.just(franquiciaMultiSucursal));

		StepVerifier.create(franquiciaService.obtenerProductosConMayorStockPorSucursal(franquiciaId))
				.assertNext(resultadoNorte -> assertThat(resultadoNorte)
						.isEqualTo(new SucursalProductoMayorStock(sucursalId, nombreSucursal, productoExistente)))
				.assertNext(resultadoSur -> assertThat(resultadoSur)
						.isEqualTo(new SucursalProductoMayorStock("sucursal-3", "Sucursal Sur", productoMayorSur)))
				.verifyComplete();

		verify(franquiciaRepositoryPort).buscarPorId(franquiciaId);
	}

	@Test
	@DisplayName("Debe retornar flujo vacio cuando la franquicia existe pero no tiene sucursales")
	void obtenerProductosConMayorStockPorSucursalCuandoNoTieneSucursalesDebeRetornarVacio() {
		when(franquiciaRepositoryPort.buscarPorId(franquiciaId)).thenReturn(Mono.just(franquiciaExistente));

		StepVerifier.create(franquiciaService.obtenerProductosConMayorStockPorSucursal(franquiciaId)).verifyComplete();

		verify(franquiciaRepositoryPort).buscarPorId(franquiciaId);
	}

	@Test
	@DisplayName("Debe retornar error EntidadNoEncontradaException cuando la franquicia no existe al consultar mayor stock")
	void obtenerProductosConMayorStockPorSucursalCuandoFranquiciaNoExisteDebeLanzarExcepcion() {
		when(franquiciaRepositoryPort.buscarPorId(franquiciaId)).thenReturn(Mono.empty());

		StepVerifier.create(franquiciaService.obtenerProductosConMayorStockPorSucursal(franquiciaId))
				.expectErrorMatches(throwable -> {
					boolean esTipoCorrecto = throwable instanceof EntidadNoEncontradaException;
					boolean contieneMensaje = throwable.getMessage().contains(franquiciaId);
					return esTipoCorrecto && contieneMensaje;
				}).verify();

		verify(franquiciaRepositoryPort).buscarPorId(franquiciaId);
	}

	@Test
	@DisplayName("Debe actualizar el nombre de la franquicia cuando existe y el nuevo nombre esta disponible")
	void actualizarNombreFranquiciaConDatosValidosDebeActualizarYRetornarFranquicia() {
		when(franquiciaRepositoryPort.buscarPorId(franquiciaId)).thenReturn(Mono.just(franquiciaConSucursal));
		when(franquiciaRepositoryPort.existePorNombre(nuevoNombreFranquicia)).thenReturn(Mono.just(false));
		when(franquiciaRepositoryPort.guardar(any(Franquicia.class)))
				.thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));

		StepVerifier.create(franquiciaService.actualizarNombreFranquicia(franquiciaId, "  " + nuevoNombreFranquicia + "  "))
				.assertNext(franquiciaActualizada -> {
					assertThat(franquiciaActualizada.id()).isEqualTo(franquiciaId);
					assertThat(franquiciaActualizada.nombre()).isEqualTo(nuevoNombreFranquicia);
					assertThat(franquiciaActualizada.sucursales()).containsExactly(sucursalExistente);
				}).verifyComplete();

		verify(franquiciaRepositoryPort).buscarPorId(franquiciaId);
		verify(franquiciaRepositoryPort).existePorNombre(nuevoNombreFranquicia);
		verify(franquiciaRepositoryPort).guardar(any(Franquicia.class));
	}

	@Test
	@DisplayName("Debe retornar error EntidadDuplicadaException cuando otra franquicia ya usa el nuevo nombre")
	void actualizarNombreFranquiciaConNombreDuplicadoDebeLanzarExcepcion() {
		when(franquiciaRepositoryPort.buscarPorId(franquiciaId)).thenReturn(Mono.just(franquiciaExistente));
		when(franquiciaRepositoryPort.existePorNombre(nombreDuplicado)).thenReturn(Mono.just(true));

		StepVerifier.create(franquiciaService.actualizarNombreFranquicia(franquiciaId, nombreDuplicado))
				.expectErrorMatches(throwable -> {
					boolean esTipoCorrecto = throwable instanceof EntidadDuplicadaException;
					boolean contieneMensaje = throwable.getMessage().contains(nombreDuplicado);
					return esTipoCorrecto && contieneMensaje;
				}).verify();

		verify(franquiciaRepositoryPort).buscarPorId(franquiciaId);
		verify(franquiciaRepositoryPort).existePorNombre(nombreDuplicado);
		verify(franquiciaRepositoryPort, never()).guardar(any(Franquicia.class));
	}

	@Test
	@DisplayName("Debe retornar error EntidadNoEncontradaException cuando la franquicia no existe al actualizar nombre")
	void actualizarNombreFranquiciaCuandoNoExisteDebeLanzarExcepcion() {
		when(franquiciaRepositoryPort.buscarPorId(franquiciaId)).thenReturn(Mono.empty());

		StepVerifier.create(franquiciaService.actualizarNombreFranquicia(franquiciaId, nuevoNombreFranquicia))
				.expectErrorMatches(throwable -> {
					boolean esTipoCorrecto = throwable instanceof EntidadNoEncontradaException;
					boolean contieneMensaje = throwable.getMessage().contains(franquiciaId);
					return esTipoCorrecto && contieneMensaje;
				}).verify();

		verify(franquiciaRepositoryPort).buscarPorId(franquiciaId);
		verify(franquiciaRepositoryPort, never()).guardar(any(Franquicia.class));
	}
}
