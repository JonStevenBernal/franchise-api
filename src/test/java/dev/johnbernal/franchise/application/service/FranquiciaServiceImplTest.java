package dev.johnbernal.franchise.application.service;

import dev.johnbernal.franchise.domain.exception.EntidadDuplicadaException;
import dev.johnbernal.franchise.domain.model.Franquicia;
import dev.johnbernal.franchise.domain.port.out.FranquiciaRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FranquiciaServiceImplTest {

    @Mock
    private FranquiciaRepositoryPort franquiciaRepositoryPort;

    private FranquiciaServiceImpl franquiciaService;

    private String nombreFranquicia;

    @BeforeEach
    void setUp() {
        franquiciaService = new FranquiciaServiceImpl(franquiciaRepositoryPort);
        nombreFranquicia = "Franquicia El Dorado";
    }

    @Test
    @DisplayName("Debe crear y retornar una franquicia cuando el nombre es valido y no existe")
    void crearFranquiciaConNombreValidoDebePersistirYRetornarFranquicia() {
        when(franquiciaRepositoryPort.existePorNombre(nombreFranquicia))
                .thenReturn(Mono.just(false));

        when(franquiciaRepositoryPort.guardar(any(Franquicia.class)))
                .thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));

        StepVerifier.create(franquiciaService.crearFranquicia("  " + nombreFranquicia + "  "))
                .assertNext(franquicia -> {
                    assertThat(franquicia.id()).isNotNull();
                    assertThat(franquicia.nombre()).isEqualTo(nombreFranquicia);
                    assertThat(franquicia.sucursales()).isEmpty();
                })
                .verifyComplete();

        verify(franquiciaRepositoryPort).existePorNombre(nombreFranquicia);
        verify(franquiciaRepositoryPort).guardar(any(Franquicia.class));
    }

    @Test
    @DisplayName("Debe retornar error EntidadDuplicadaException cuando ya existe una franquicia con ese nombre")
    void crearFranquiciaConNombreDuplicadoDebeLanzarExcepcion() {
        when(franquiciaRepositoryPort.existePorNombre(nombreFranquicia))
                .thenReturn(Mono.just(true));

        StepVerifier.create(franquiciaService.crearFranquicia(nombreFranquicia))
                .expectErrorMatches(throwable -> {
                    boolean esTipoCorrecto = throwable instanceof EntidadDuplicadaException;
                    boolean contieneMensaje = throwable.getMessage().contains(nombreFranquicia);
                    return esTipoCorrecto && contieneMensaje;
                })
                .verify();

        verify(franquiciaRepositoryPort).existePorNombre(nombreFranquicia);
        verify(franquiciaRepositoryPort, never()).guardar(any(Franquicia.class));
    }
}
