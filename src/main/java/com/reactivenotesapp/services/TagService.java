package com.reactivenotesapp.services;

import com.reactivenotesapp.domain.Tag;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface TagService {


  Mono<Integer> count();

  /**
   * Counts the number of tags of a given note
   * @param note_id
   * @return number of tags of the given note.
   */
  Mono<Integer> countByNoteId(Long note_id);

  /**
   * Counts the number of times a given tag has been used on a note.
   * @param tag_id
   * @return number of notes using the given tag.
   */
  Mono<Integer> countOccurrences(String tag_id);

  Mono<Tag> save(Tag tag);

  Flux<Tag> saveAll(Iterable<Tag> tags);

  Mono<Integer> deleteById(String id);
  /*
  findByNote is done by findNotesWithTag(s?) in the audiovidual repository
   */
  Flux<Tag> findAll();

  Flux<Tag> findById(String id);

  //same as findById in impl as the name is natural id
  Flux<Tag> findByName(String name);

  Flux<Tag> findByNameLike(String name);

  Flux<Tag> findByNoteId(Long note_id);
}
