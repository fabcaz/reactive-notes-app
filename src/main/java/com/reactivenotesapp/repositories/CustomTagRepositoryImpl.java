package com.reactivenotesapp.repositories;

import com.reactivenotesapp.domain.Tag;
import io.r2dbc.spi.Connection;
import io.r2dbc.spi.Row;
import org.springframework.r2dbc.core.DatabaseClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static com.reactivenotesapp.repositories.RepoUtils.COUNT_ALIAS;
import static com.reactivenotesapp.repositories.RepoUtils.executeCountQuery;
import static com.reactivenotesapp.repositories.RepoUtils.executeDeleteQuery;
import static com.reactivenotesapp.repositories.TableAliasStatements.*;

public class CustomTagRepositoryImpl implements CustomTagRepository {

  // maybe switchIfEmpty to Mono.error() for create

  private DatabaseClient dbClient;

  public CustomTagRepositoryImpl(DatabaseClient dbClient) {
    this.dbClient = dbClient;
  }

  @Override
  public Mono<Integer> count() {
    String countQuery = String.format("SELECT COUNT(*) AS %s FROM %s;", COUNT_ALIAS, TAG_TABLE);

    return executeCountQuery(dbClient, countQuery);
  }


  @Override
  public Mono<Integer> countByNoteId(Long note_id) {
    String countQuery = String.format("SELECT COUNT(*) AS %s FROM %s WHERE note_id = %d;",
      COUNT_ALIAS, NOTE_X_TAG_TABLE, note_id);

    return executeCountQuery(dbClient, countQuery);
  }

  @Override
  public Mono<Integer> countOccurrences(String tag_id) {
    String countQuery = String.format("SELECT COUNT(*) AS %s FROM %s WHERE tag_id = %d;",
      COUNT_ALIAS, NOTE_X_TAG_TABLE, tag_id);

    return executeCountQuery(dbClient, countQuery);
  }

  @Override
  public Mono<Tag> save(Tag tag) {

    String tagName = tag.getName().get();
    String insertQuery = String.format("INSERT INTO %s %s VALUES (%s, %s);",
      TAG_TABLE, INSERT_TT, tagName,
      tag.getCategory().orElse("DEFAULT"));

    String selectQuery = String.format("SELECT %s FROM %s WHERE name = '%s';",
      SELECT_TT, AS_TAG_TABLE, tagName);

    Mono<Connection> connection = Mono.from(dbClient.getConnectionFactory().create());
    return connection.flatMap(conn ->
        Mono.from(conn.createStatement(insertQuery).execute())
      )
      .flatMap( res -> Mono.from(res.getRowsUpdated())) //note: may be empty since not an update statement
      .log()
      //rest of the pipe verifies that the tag was indeed inserted
      .then(
        connection.flatMap( conn ->
          Mono.from(conn.createStatement(selectQuery).execute())
        )
      )
      .flatMap(res ->
        Mono.from(
          res.map((row, metadata) ->
            tagReadConverter(row)
          )
        )
      );
  }

  @Override
  public Flux<Tag> saveAll(Iterable<Tag> tags) {
    return Flux.fromIterable(tags)
      .switchIfEmpty(Mono.error(() -> new IllegalArgumentException("Iterable<Tag> should not be empty")))
      .concatMap(this::save);
  }

  @Override
  public Mono<Integer> deleteById(String id) {
    String deleteQuery = String.format("DELETE FROM %s WHERE name = '%s';", TAG_TABLE, id);

    return executeDeleteQuery(dbClient, deleteQuery);
  }

  @Override
  public Flux<Tag> findAll() {
    String selectQuery = String.format("SELECT %s FROM %s;", SELECT_TT, AS_TAG_TABLE);

    Mono<Connection> connection = Mono.from(dbClient.getConnectionFactory().create());

    return connection.flatMapMany(conn -> conn.createStatement(selectQuery).execute())
      .flatMap( res ->
        Mono.from(res.map( (row, metadata) -> tagReadConverter(row)))
      );
  }

  @Override
  public Flux<Tag> findById(String id) {
    String selectQuery = String.format("SELECT %s FROM %s WHERE %s.name = %s;",
      SELECT_TT, AS_TAG_TABLE, id);

    Mono<Connection> connection = Mono.from(dbClient.getConnectionFactory().create());

    return connection.flatMapMany(conn -> conn.createStatement(selectQuery).execute())
      .flatMap( res ->
        Mono.from(res.map( (row, metadata) -> tagReadConverter(row)))
      );
  }

  @Override
  public Flux<Tag> findByName(String name) {
    return findById(name);
  }

  @Override
  public Flux<Tag> findByNameLike(String name) {
    return findByName("%" + name + "%");
  }

  @Override
  public Flux<Tag> findByNoteId(Long note_id) {

    String selectQuery = String.format("SELECT %s FROM %s INNER JOIN %s ON %s.name = %s.tag_id WHERE %s.note_id = %d;",
      SELECT_TT, AS_TAG_TABLE, AS_NOTE_X_TAG_TABLE,
      TAL_TT   , TAL_NXTT    , TAL_NXTT,
      note_id);

    Mono<Connection> connection = Mono.from(dbClient.getConnectionFactory().create());

    return connection.flatMapMany(conn -> conn.createStatement(selectQuery).execute())
      .flatMap( res ->
        Mono.from(res.map( (row, metadata) -> tagReadConverter(row)))
      );
  }

  private Tag tagReadConverter(Row row){
    return new Tag(
      row.get("name", String.class),
      row.get("category_id", String.class)
    );
  }

}
