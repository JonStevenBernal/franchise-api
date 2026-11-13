package dev.johnbernal.franchise.infrastructure.adapter.out.persistence.adapter;

import org.springframework.stereotype.Component;

import dev.johnbernal.franchise.domain.model.Franquicia;
import dev.johnbernal.franchise.domain.port.out.FranquiciaRepositoryPort;
import dev.johnbernal.franchise.infrastructure.adapter.out.persistence.entity.FranquiciaDocument;
import dev.johnbernal.franchise.infrastructure.adapter.out.persistence.mapper.FranquiciaPersistenceMapper;
import dev.johnbernal.franchise.infrastructure.adapter.out.persistence.repository.SpringDataFranquiciaRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class FranquiciaMongoRepositoryAdapter implements FranquiciaRepositoryPort {

	private final SpringDataFranquiciaRepository springDataFranquiciaRepository;

	public FranquiciaMongoRepositoryAdapter(SpringDataFranquiciaRepository springDataFranquiciaRepository) {
		this.springDataFranquiciaRepository = springDataFranquiciaRepository;
	}

	@Override
	public Mono<Franquicia> guardar(Franquicia franquicia) {
		FranquiciaDocument documento = FranquiciaPersistenceMapper.toDocument(franquicia);
		return springDataFranquiciaRepository.save(documento).map(FranquiciaPersistenceMapper::toDomain);
	}

	@Override
	public Mono<Franquicia> buscarPorId(String id) {
		return springDataFranquiciaRepository.findById(id).map(FranquiciaPersistenceMapper::toDomain);
	}

	@Override
	public Flux<Franquicia> listarTodas() {
		return springDataFranquiciaRepository.findAll().map(FranquiciaPersistenceMapper::toDomain);
	}

	@Override
	public Mono<Boolean> existePorNombre(String nombre) {
		return springDataFranquiciaRepository.existsByNombreIgnoreCase(nombre);
	}
}
