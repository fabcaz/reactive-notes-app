package com.reactivenotesapp.web.handlers;

import com.reactivenotesapp.ReactiveNotesApp;
import com.reactivenotesapp.configs.RoutesConfig;
import com.reactivenotesapp.domain.Audiovisual;
import com.reactivenotesapp.services.AudiovisualService;
import com.reactivenotesapp.web.errors.PropertyNotFound;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ServerWebInputException;
import reactor.core.publisher.Mono;
import org.apache.commons.logging.Log;


import org.springframework.validation.Validator;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


@Component
public class AudiovisualNoteHandler {

  // TODO extract template names to own static class or propertyfile
  // TODO review infrastructure code to see if it logs outcome when handling result or view resolution
  // TODO find other way to handle errors than return a template for that error
  private final String TEMPLATE_AUDIOVISUAL_LIST = "audiovisualNoteList";
  private final String TEMPLATE_AUDIOVISUAL_NOTE = "audiovisualNoteTemplate";
  private final String TEMPLATE_AUDIOVISUAL_NOTE_UPDATE = "audiovisualNoteUpdateTemplate";
  private final String TEMPLATE_UNPROCESSABLE_ENTITY = "422Template";
  private final String TEMPLATE_NOTFOUND = "404Template";
  private final String TEMPLATE_INTERNAL_SERVER_ERROR = "500Template";

  private final String MODEL_ATTR_NOTE = "audiovisualNote";
  private final String MODEL_ATTR_NOTE_LIST = "audiovisualNoteList";

  private final String PATH_AUDIOVISUAL = RoutesConfig.API_V1_PATH + RoutesConfig.AUDIOVISUAL_PATH;

  private final Log log = LogFactory.getLog(AudiovisualNoteHandler.class);
  private final Validator validator;

  private AudiovisualService audiovisualService;

  //private TagService tagService;


  public AudiovisualNoteHandler( Validator validator, AudiovisualService audiovisualService) {
    this.validator = validator;
    this.audiovisualService = audiovisualService;
  }

  public Mono<ServerResponse> listNotes(ServerRequest request) {
    Map<String, Object> model = new HashMap<>();
    model.put(MODEL_ATTR_NOTE_LIST, audiovisualService.findAllFull());

    return ServerResponse.ok().contentType(MediaType.TEXT_HTML)
            .render(TEMPLATE_AUDIOVISUAL_LIST, model); //TODO should be same template for all kinds of notes
  }

  public Mono<ServerResponse> getNoteById(ServerRequest request) {
    Map<String, Object> model = new HashMap<>(1);
    return audiovisualService.findByIdFull(
              Long.valueOf(request.pathVariable(RoutesConfig.AUDIOVISUAL_ID_PATH_VAR))
            )
            .flatMap((note) -> {
              model.put(MODEL_ATTR_NOTE, note);
              return ServerResponse.ok().contentType(MediaType.TEXT_HTML)
                      .render(TEMPLATE_AUDIOVISUAL_NOTE, model);
            })
            .switchIfEmpty(ServerResponse.status(HttpStatus.NOT_FOUND).render(TEMPLATE_NOTFOUND));
  }
  public Mono<ServerResponse> createNote(ServerRequest request) {

    Map<String, Object> model = new HashMap<>(1);


    return request.bodyToMono(Audiovisual.class)
            .doOnNext(this::validate)
            .flatMap(audiovisualService::create)
            .flatMap( newNote -> {
              model.put(MODEL_ATTR_NOTE, newNote);

              return ServerResponse.created(
                URI.create(PATH_AUDIOVISUAL + newNote.getId().orElseThrow(PropertyNotFound::new))
              ).build();
            })
            .onErrorResume(PropertyNotFound.class, (err) -> ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .render(TEMPLATE_INTERNAL_SERVER_ERROR));

  }
  public Mono<ServerResponse> updateNote(ServerRequest request) {
    return request.bodyToMono(Audiovisual.class)
            .doOnNext(this::validate)
            .flatMap(audiovisualService::update)
            .flatMap( newNote -> {
              Map<String, Object> model = new HashMap<>(1);
              model.put(MODEL_ATTR_NOTE, newNote);

              return ServerResponse.created(
                      URI.create(PATH_AUDIOVISUAL + newNote.getId().orElseThrow(PropertyNotFound::new))
              ).build();
            })
            .onErrorResume(err -> {
              if (err instanceof ServerWebInputException){
                return ServerResponse.status(HttpStatus.UNPROCESSABLE_ENTITY)
                        .render(TEMPLATE_UNPROCESSABLE_ENTITY);
              } else if (err instanceof PropertyNotFound){
                return ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .render(TEMPLATE_INTERNAL_SERVER_ERROR);
              } else {
                /*
                DataAccessException and TransientDataAccessResourceException (instanceof DataAccessException) may be
                thrown by R2dbcEntityTemplate
                 */
                return Mono.error(err);
              }
            });
  }
  public Mono<ServerResponse> deleteNote(ServerRequest request) {
    // 0 -> noContent()
    // 1 -> badRequest()
    return audiovisualService.deleteById(UUID.fromString(request.pathVariable(RoutesConfig.AUDIOVISUAL_ID_PATH_VAR)))
            .then(ServerResponse.accepted().render("redirect:"+ PATH_AUDIOVISUAL));
    //note_id comes from path param
  }

//maybe put a method that also returns other stats
  public Mono<ServerResponse> countNotes(ServerRequest request) {
    return audiovisualService.count()
            .flatMap(count -> {
              Map<String, Object> model = new HashMap<>(1);
              return ServerResponse.ok().bodyValue(count);
            });
  }

  // TODO can make custom repo implemented (along reactiveCrudRepo) by the note repos
  // TODO can just add a list of notes on tag abjects and use a tag service to fetch the notes; via findAll() for each tag


  public Mono<ServerResponse> listNotesWithTag(ServerRequest request) {
    return ServerResponse.status(HttpStatus.NOT_IMPLEMENTED).build();
  }
  public Mono<ServerResponse> listNotesWithTags(ServerRequest request) {
    return ServerResponse.status(HttpStatus.NOT_IMPLEMENTED).build();
  }

  public Mono<ServerResponse> listNotesWithDescriptionContainingSubstring(ServerRequest request) {
    return ServerResponse.status(HttpStatus.NOT_IMPLEMENTED).build();
  }
  public Mono<ServerResponse> listNotesWithContentContainingSubstring(ServerRequest request) {
    return ServerResponse.status(HttpStatus.NOT_IMPLEMENTED).build();
  }
  public Mono<ServerResponse> listNotesWithTerm(ServerRequest request) {
    return ServerResponse.status(HttpStatus.NOT_IMPLEMENTED).build();
  }

  private void validate(Audiovisual audiovisual) {
    Errors errors = new BeanPropertyBindingResult(audiovisual, "audiovisual");
    validator.validate(audiovisual, errors);
    if (errors.hasErrors()) {
      throw new ServerWebInputException(errors.toString());
    }
  }





}
