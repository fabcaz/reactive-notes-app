package com.reactivenotesapp.repositories;

import com.reactivenotesapp.domain.Audiovisual;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CustomAudiovisualRepository {

  //note: queries for multiple notes should return partial notes (exept for findAllFull). If attributes from other tables
  //      are desired then service layer should flatmap results with queries that fetch those attributes.


  Mono<Integer> countAudiovisual();
  //TODO create command object for PATCH req to implement addTag (needs refactoring), deleteTag
  /*
  saveAll List<entity> append tuple to values sql statement
  exists // just find by id and if flux empty then return false
  delete tag for note
  deleteAll (truncate)
   */

  Mono<Audiovisual> save(Audiovisual au);


  Flux<Audiovisual> saveAll(Iterable<Audiovisual> notes);

  Mono<Audiovisual> update(Audiovisual au);

  Mono<Integer> deleteById(Long id);

  Flux<Audiovisual> findAllPartial();

  Flux<Audiovisual> findAllFull();

  Mono<Audiovisual> findByIdFull(Long id);
  Mono<Audiovisual> findByIdPartial(Long id);

  Flux<Audiovisual> findByNameFull(String name); //names may not be unique
  Flux<Audiovisual> findByNamePartial(String name);

  Flux<Audiovisual> findByNameLike(String name);

  //partial notes
  Flux<Audiovisual> findNotesWithTag(String tagName);

  //partial notes
  Flux<Audiovisual> findNotesWithTags(String[] tagArray);

  //partial notes
  Flux<Audiovisual> findNotesWithDescriptionContainingSubstring(String name);

  //partial notes
  Flux<Audiovisual> findNotesWithContentContainingSubstring(String name);

  //partial notes
  Flux<Audiovisual> findNotesWithTerm(String termName);
}
