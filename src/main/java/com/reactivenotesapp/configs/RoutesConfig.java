package com.reactivenotesapp.configs;

import com.reactivenotesapp.web.handlers.AudiovisualNoteHandler;
import com.reactivenotesapp.web.handlers.TagHandler;
//import com.reactivenotesapp.web.routes.AudiovisualNoteRoutes;
//import com.reactivenotesapp.web.routes.TagRoutes;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.*;

import static org.springframework.web.reactive.function.server.RouterFunctions.route;

//Maybe move this to webflux config class instead
//Maybe instantiate deps in @Bean methods instead of marking them as @Components; spring-core section 1.12.5 warning
@Configuration
public class RoutesConfig {

  public final static String API_V1_PATH = "/api/v1";
  public final static String TAG_PATH = "/tag";
  public final static String AUDIOVISUAL_PATH = "/audiovisual";

  public final static String AUDIOVISUAL_ID_PATH_VAR = "note_id";

  public final static String TAG_ID_PATH_VAR = "tag_id";

//  private TagRoutes tagRoutes;
//
//  private AudiovisualNoteRoutes audiovisualNoteRoutes;
//
//  @Autowired
//  public Routes(TagRoutes tagRoutes, AudiovisualNoteRoutes audiovisualNoteRoutes) {
//    this.tagRoutes = tagRoutes;
//    this.audiovisualNoteRoutes = audiovisualNoteRoutes;
//  }

  @Bean public RouterFunction<ServerResponse> tagRoutes(TagHandler tagHandler){
    return RouterFunctions.route()
            .GET(TAG_PATH, tagHandler::listTags)
            .POST(TAG_PATH, tagHandler::createTag)
            .PUT(TAG_PATH, tagHandler::updateTag)
            .DELETE(String.format("%s/{%s}",TAG_PATH, TAG_ID_PATH_VAR), tagHandler::deleteTag)
            .build();

  }

  @Bean public RouterFunction<ServerResponse> audiovisualNoteRoutes(AudiovisualNoteHandler audiovisualNoteHandler){
    return RouterFunctions.route()
            .GET(AUDIOVISUAL_PATH, audiovisualNoteHandler::listNotes)
            .GET(String.format("%s/{%s}",AUDIOVISUAL_PATH, AUDIOVISUAL_ID_PATH_VAR), audiovisualNoteHandler::getNoteById)
            .POST(AUDIOVISUAL_PATH, audiovisualNoteHandler::createNote)
            .PUT(AUDIOVISUAL_PATH, audiovisualNoteHandler::updateNote)
            .DELETE(String.format("%s/{%s}",AUDIOVISUAL_PATH, AUDIOVISUAL_ID_PATH_VAR), audiovisualNoteHandler::deleteNote)
            .GET(String.format("%s/{%s}",AUDIOVISUAL_PATH, "/count"), audiovisualNoteHandler::countNotes)
            .build();

  }

//  @Bean public RouterFunction<ServerResponse> errorRoutes(ErrorHandler errorHandler){
//
//
//  }

  @Bean
  public RouterFunction<ServerResponse> testRoute(){
    HandlerFunction<ServerResponse> fn = (serverRequest) ->
            ServerResponse.ok()
                    .contentType(MediaType.TEXT_HTML)
                    .bodyValue("test route for testing");

    return RouterFunctions.route()
            .GET("/test", fn)
            .build();
  }

  @Bean
  public RouterFunction<ServerResponse> routes(RouterFunction<ServerResponse> tagRoutes,
                                               RouterFunction<ServerResponse> audiovisualNoteRoutes) {

    return route()
            .nest(RequestPredicates.path(API_V1_PATH), builder ->
                            builder.add(tagRoutes)
                                   .add(audiovisualNoteRoutes)
//                                    .add(errorRoutes)
                                    .add(testRoute())
                    ).
            build();
  }

}
