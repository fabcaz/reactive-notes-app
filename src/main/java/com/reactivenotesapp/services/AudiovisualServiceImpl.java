package com.reactivenotesapp.services;

import com.reactivenotesapp.domain.Audiovisual;
import com.reactivenotesapp.repositories.AudiovisualRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

//TODO log things onNext or onTemination


/*
TODO:
  1) should have a partial note
  2) should have a full note; includes tags, ts, termdefs
    - this requires multiple queries since joining all tables involved creates a cartesian product
    -- findAllNotes and collect to HashMap<UUID, Note>
    -- use HashMap keySet to create the list for the IN statement for queries that retrieve the List fields contents
    -- For Lsit field queries, instead of converter, use accumulators that store the results in HashMaps<UUID, List<XXX>>


    Map<LocalTime, String> timeStamps
    Map<String, String> termDefs
    List<Tag> tags

    in repo: queryXXtable = sql("query").somehowConvert()

    cannot use doOnNext since it consumes; cannot make a call to repo from inside
    maybe do:
    queryNoteTable.then(
      note.getId
    )

    =====USE FLATMAP


TODO :

//or use concatMap
note_service.findAllFull()
  .flatMapSequential( au -> {
    Log id = au.getId.get(); // could throw here to make it clear where the issue is.
    List<Tag> tags = tag_service.findByNoteId(id).toList()                     // should be inner join in custom impl
    Map<String, String> terms = term_service.findByNoteId(id).toHashMap()      // table has composite PK; unsure if simple enough for ReactiveCRUD extension
    Map<String, String> timestamps = timestamp_service.findByNoteId(id).toHashMap() // table has composite PK; unsure if simple enough for ReactiveCRUD extension
    au.setTags(tags);
    au.setTerms(terms);
    au.setTimeStamps(timestamps);

    return Mono.from(au);
    }
  ) 



 */



@Service
public class AudiovisualServiceImpl implements AudiovisualService {

  private final AudiovisualRepository audiovisualRepository;
  private final TagService tagService;
  private final TermService termService;
  private final TimestampService timestampService;

  public AudiovisualServiceImpl(AudiovisualRepository audiovisualRepository, TagService tagService, TermService termService, TimestampService timestampService) {
    this.audiovisualRepository = audiovisualRepository;
    this.tagService = tagService;
    this.termService = termService;
    this.timestampService = timestampService;
  }

//===========================================================================

  @Override
  public Mono<Audiovisual> save(Audiovisual note) {
    return audiovisualRepository.save(note).log();
  }

  @Override
  public Flux<Audiovisual> saveAll(Iterable<Audiovisual> notes) {
    return audiovisualRepository.saveAll(notes).log();
  }

  //@Override
  public Mono<Audiovisual> update(Audiovisual entity) {
    return audiovisualRepository.save(entity).log();
  }

  //@Override
  public Mono<Void> deleteById(UUID id) {
    return audiovisualRepository.deleteById(id).log();
  }

  //@Override
  public Mono<Integer> count() {
    return audiovisualRepository.countAudiovisual().log();
  }

  @Override
  public Flux<Audiovisual> findAllPartial() {
    return audiovisualRepository.findAllPartial().log();
  }

  //TODO should flatmap using tag service, timestamp service and termdef service
  @Override
  public Flux<Audiovisual> findAllFull() {
    //TODO flatMap or concatMap to use (tag/term/ts)Service
    return audiovisualRepository.findAllFull().log();
  }

  //TODO should flatmap using tag service, timestamp service and termdef service
  @Override
  public Mono<Audiovisual> findByIdFull(Long id) {
    //TODO flatMap or concatMap to use (tag/term/ts)Service
    return audiovisualRepository.findByIdFull(id).log();
  }

  @Override
  public Mono<Audiovisual> findByIdPartial(Long id) {
    return audiovisualRepository.findByIdPartial(id).log();
  }

  //TODO should flatmap using tag service, timestamp service and termdef service
  @Override
  public Flux<Audiovisual> findByNameFull(String name) {
    //TODO flatMap or concatMap to use (tag/term/ts)Service
    return audiovisualRepository.findByNameFull(name).log();
  }

  @Override
  public Flux<Audiovisual> findByNamePartial(String name) {
    return audiovisualRepository.findByNamePartial(name).log();
  }

  @Override
  public Flux<Audiovisual> findByNameLike(String name) {
    return audiovisualRepository.findByNameLike(name).log();
  }

  @Override
  public Flux<Audiovisual> findNotesWithTag(String tagName) {
    return audiovisualRepository.findNotesWithTag(tagName).log();
  }

  @Override
  public Flux<Audiovisual> findNotesWithTags(String[] tagArray) {
    return audiovisualRepository.findNotesWithTags(tagArray).log();
  }

  @Override
  public Flux<Audiovisual> findNotesWithDescriptionContainingSubstring(String subString) {
    return audiovisualRepository.findNotesWithDescriptionContainingSubstring(subString).log();
  }

  @Override
  public Flux<Audiovisual> findNotesWithContentContainingSubstring(String subString) {
    return audiovisualRepository.findNotesWithContentContainingSubstring(subString).log();
  }

  @Override
  public Flux<Audiovisual> findNotesWithTerm(String termName) {
    return audiovisualRepository.findNotesWithTerm(termName).log();
  }
}
