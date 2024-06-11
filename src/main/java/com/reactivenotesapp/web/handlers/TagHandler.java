package com.reactivenotesapp.web.handlers;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
public class TagHandler {

  public Mono<ServerResponse> listTags(ServerRequest request) {
  }
  public Mono<ServerResponse> createTag(ServerRequest request) {
  }
  public Mono<ServerResponse> updateTag(ServerRequest request) {
  }
  public Mono<ServerResponse> deleteTag(ServerRequest request) {
  }
}
