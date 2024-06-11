package com.reactivenotesapp.repositories;

import com.reactivenotesapp.domain.Audiovisual;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface AudiovisualRepository extends ReactiveCrudRepository<Audiovisual, UUID>, CustomAudiovisualRepository {

  Mono<Audiovisual> findByName(String name);
  Flux<Audiovisual> findByNameLike(String name);


}
