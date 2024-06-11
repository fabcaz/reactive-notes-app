package com.reactivenotesapp.repositories;

import com.reactivenotesapp.domain.Timestamp;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CustomTimestampRepository {
  Mono<Integer> count();

  /**
   * Counts the number of timestamps of a given note
   * @param note_id
   * @return number of timestamps of the given note.
   */
  Mono<Integer> countByNoteId(Long note_id);

  Mono<Timestamp> save(Timestamp timestamp);

  Flux<Timestamp> saveAll(Iterable<Timestamp> timestamps);

  Mono<Integer> update(Timestamp timestamp);

  Mono<Integer> deleteById(Long note_id, String timeStr);

  Flux<Timestamp> findAll();

  Mono<Timestamp> findById(Long note_id, String timeStr);

  // same as findById; could define this as explicit alias;
  //Flux<Timestamp> findByNoteIdAndTime(Long note_id, String timeStr);

  Flux<Timestamp> findByNoteId(Long note_id);

  Flux<Timestamp> findByContentLike(String content);
}
