package com.reactivenotesapp.services;

import com.reactivenotesapp.domain.Audiovisual;
import com.reactivenotesapp.domain.BaseNoteEntity;
import com.reactivenotesapp.domain.Tag;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface BaseNoteService<T extends BaseNoteEntity> {//extends BaseService<T> {


  Mono<T> save(T note);

  Flux<T> saveAll(Iterable<T> notes);
  Flux<T> findAllPartial();
  Flux<T> findAllFull();
  Mono<T> findByIdFull(Long id);
  Mono<T> findByIdPartial(Long id);
  Flux<T> findByNameFull(String name);
  Flux<T> findByNamePartial(String name);
  Flux<T> findByNameLike(String name);
  Flux<T> findNotesWithTag(String tagName);
  Flux<T> findNotesWithTags(String[] tagArray);
  Flux<T> findNotesWithDescriptionContainingSubstring(String subString);
  Flux<T> findNotesWithContentContainingSubstring(String subString);
  Flux<T> findNotesWithTerm(String termName);
}
