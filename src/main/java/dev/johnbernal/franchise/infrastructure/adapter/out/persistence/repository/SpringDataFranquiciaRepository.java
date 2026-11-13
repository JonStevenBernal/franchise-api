package dev.johnbernal.franchise.infrastructure.adapter.out.persistence.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

import dev.johnbernal.franchise.infrastructure.adapter.out.persistence.entity.FranquiciaDocument;
import reactor.core.publisher.Mono;

@Repository
public interface SpringDataFranquiciaRepository extends ReactiveMongoRepository<FranquiciaDocument, String> {
	
	Mono<Boolean> existsByNombreIgnoreCase(String nombre);
	
}
