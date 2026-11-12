package dev.johnbernal.franchise.domain.port.out;

import dev.johnbernal.franchise.domain.model.Franquicia;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface FranquiciaRepositoryPort {
	
	Mono<Franquicia> guardar(Franquicia franquicia);

	Mono<Franquicia> buscarPorId(String id);

	Flux<Franquicia> listarTodas();

	Mono<Boolean> existePorNombre(String nombre);
}
