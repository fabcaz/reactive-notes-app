package com.reactivenotesapp.repositories;

import com.reactivenotesapp.domain.Tag;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;
// TODO what is  the point of extending ReactiveCrudRepository if I implement all methods in Custom*RepositoryImpl???? just make *Impl a bean
public interface TagRepository extends  CustomTagRepository { //ReactiveCrudRepository<Tag, String>,

  //note: ID is supposed to be PK -> How to use composite PK as ID here? relying on customImpl for now.

  //note: will not provide update functionality since this could cause issues for notes with the tag in question. Procedure should be to review each note to decide whether to replace the tag.




}
