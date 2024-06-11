package com.reactivenotesapp.services;

import com.reactivenotesapp.domain.Tag;
import com.reactivenotesapp.repositories.TagRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class TagServiceImpl implements TagService {

  private final TagRepository tagRepository;

  public TagServiceImpl(TagRepository tagRepository) {
    this.tagRepository = tagRepository;
  }

  @Override
  public Mono<Integer> count() {
    return tagRepository.count();
  }

  @Override
  public Mono<Integer> countByNoteId(Long note_id) {
    return tagRepository.countByNoteId(note_id);
  }

  @Override
  public Mono<Integer> countOccurrences(String tag_id) {
    return tagRepository.countOccurrences(tag_id);
  }

  @Override
  public Mono<Tag> save(Tag tag) {
    return tagRepository.save(tag);
  }

  @Override
  public Flux<Tag> saveAll(Iterable<Tag> tags) {
    return tagRepository.saveAll(tags);
  }

  @Override
  public Mono<Integer> deleteById(String id) {
    return tagRepository.deleteById(id);
  }

  @Override
  public Flux<Tag> findAll() {
    return tagRepository.findAll();
  }

  @Override
  public Flux<Tag> findById(String id) {
    return tagRepository.findById(id);
  }

  @Override
  public Flux<Tag> findByName(String name) {
    return tagRepository.findByName(name);
  }

  @Override
  public Flux<Tag> findByNameLike(String name) {
    return tagRepository.findByNameLike(name);
  }

  @Override
  public Flux<Tag> findByNoteId(Long note_id) {
    return tagRepository.findByNoteId(note_id);
  }
}
