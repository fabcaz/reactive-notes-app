package com.reactivenotesapp.services;


import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;


//TODO delete this; simply not necessary and causes problems in BaseNote descendents
//TODO or rename it TagService
public interface BaseService <T>{

  Flux<T> findAll(); //should collect flux to list
  Mono<T> findById(UUID id);

  Flux<T> findByName(String name);

  Flux<T> findByNameLike(String name);
  Mono<T> create(T entity);
  Mono<T> update(T entity);
  Mono<Void> deleteById(UUID id);

  Mono<Long> count();



}
