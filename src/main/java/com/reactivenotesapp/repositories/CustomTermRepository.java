package com.reactivenotesapp.repositories;

import com.reactivenotesapp.domain.Term;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CustomTermRepository {
  Mono<Integer> count();

  /**
   * Counts the number of terms of a given note
   * @param note_id
   * @return number of terms of the given note.
   */
  Mono<Integer> countByNoteId(Long note_id);

  /**
   * Counts the number of times a given term appears across notes.
   * @param term
   * @return number of notes containing a definition for the given term.
   */
  Mono<Integer> countOccurrences(String term);

  Mono<Term> save(Term term);

  Flux<Term> saveAll(Iterable<Term> terms);

  Mono<Integer> update(Term term);

  Mono<Integer> deleteById(Long note_id, String term);

  Flux<Term> findAll();

  Mono<Term> findById(Long note_id, String term);

  Flux<Term> findByTerm(String term);

  Flux<Term> findByTermLike(String term);

  Flux<Term> findByNoteId(Long note_id);
}
