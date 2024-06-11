package com.reactivenotesapp.repositories;

import com.reactivenotesapp.domain.Timestamp;
import io.r2dbc.spi.Row;
import org.springframework.core.convert.converter.Converter;
import org.springframework.r2dbc.core.DatabaseClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalTime;

import static com.reactivenotesapp.repositories.RepoUtils.*;
import static com.reactivenotesapp.repositories.TableAliasStatements.*;

public class CustomTimestampRepositoryImpl implements CustomTimestampRepository {

  DatabaseClient dbClient;

  public CustomTimestampRepositoryImpl(DatabaseClient dbClient) {
    this.dbClient = dbClient;
  }

  @Override
  public Mono<Integer> count() {

    String countQuery = String.format("SELECT COUNT(*) AS %s FROM %s;", COUNT_ALIAS, TIME_STAMP_TABLE);

    return executeCountQuery(dbClient, countQuery);
  }

  @Override
  public Mono<Integer> countByNoteId(Long note_id) {

    String countQuery = String.format("SELECT COUNT(*) AS %s FROM %s WHERE note_id = %d;",
      COUNT_ALIAS, TIME_STAMP_TABLE, note_id);

    return executeCountQuery(dbClient, countQuery);
  }

  @Override
  public Mono<Timestamp> save(Timestamp timestamp) {
    Long note_id = timestamp.getNote_id().get();
    String timeStr = timestamp.getTimeString().get();
    String content = timestamp.getContent().get();

    String insertQuery = String.format("INSERT INTO %s %s VALUES (%s,%s,%s);",
      TIME_STAMP_TABLE, INSERT_TST, note_id,
      timeStr       , content);


    String whereClause = whereIdClauseWithTableAlias(note_id, timeStr);
    String selectQuery = String.format("SELECT %s FROM %s %s;",
      SELECT_TST, AS_TIME_STAMP_TABLE, whereClause);

    return executeInsertQuery(dbClient, insertQuery, selectQuery, timestampReadConverter);
  }


  @Override
  public Flux<Timestamp> saveAll(Iterable<Timestamp> timestamps) {
    return Flux.fromIterable(timestamps)
      .switchIfEmpty(Mono.error(() -> new IllegalArgumentException("Iterable<Timestamp> should not be empty")))
      .concatMap(this::save);
  }

  //TODO add IF NOT EXISTS to allow changing time field
  @Override
  public Mono<Integer> update(Timestamp timestamp) {
    Long note_id = timestamp.getNote_id().get();
    String timeStr = timestamp.getTimeString().get();
    String content = timestamp.getContent().get();

    String whereClause = whereIdClause(note_id, timeStr);
    String updateQuery= String.format("UPDATE %s SET content = %s;",
      TIME_STAMP_TABLE, content, whereClause);

    return executeUpdateQuery(dbClient, updateQuery);
  }

  @Override
  public Mono<Integer> deleteById(Long note_id, String timeStr) {

    String whereClause = whereIdClause(note_id, timeStr);
    String deleteQuery = String.format("DELETE FROM %s %s;", TIME_STAMP_TABLE, whereClause);

    return executeDeleteQuery(dbClient, deleteQuery);
  }

  @Override
  public Flux<Timestamp> findAll() {
    String selectQuery = String.format("SELECT %s FROM %s;", SELECT_TST, AS_TIME_STAMP_TABLE);

    return executeSelectQuery(dbClient, selectQuery, timestampReadConverter);
  }

  @Override
  public Mono<Timestamp> findById(Long note_id, String timeStr) {
    String whereClause = whereIdClause(note_id, timeStr);
    String selectQuery = String.format("SELECT %s FROM %s %s;", SELECT_TST, AS_TIME_STAMP_TABLE, whereClause);

    return executeSelectQuery(dbClient, selectQuery, timestampReadConverter).singleOrEmpty();
  }

  @Override
  public Flux<Timestamp> findByNoteId(Long note_id) {
    String selectQuery = String.format("SELECT %s FROM %s WHERE %s.note_id ='%d';",
      SELECT_TST, AS_TIME_STAMP_TABLE, TAL_TST,
      note_id);

    return executeSelectQuery(dbClient, selectQuery, timestampReadConverter);
  }

  @Override
  public Flux<Timestamp> findByContentLike(String content) {
    String selectQuery = String.format("SELECT %s FROM %s WHERE %s.content ='%s';",
      SELECT_TST, AS_TIME_STAMP_TABLE, TAL_TST,
      content);

    return executeSelectQuery(dbClient, selectQuery, timestampReadConverter);
  }

  //TODO extract Converter to own file
  private static Converter<Row, Timestamp> timestampReadConverter = row -> new Timestamp(
      row.get("note_id", Long.class),
      row.get("time", LocalTime.class),
      row.get("content", String.class)
    );

// TODO create method that takes a list of col names in RepoUtils and replace
  private String whereIdClause(Long note_id, String time){
    return String.format("WHERE note_id = %d AND time = '%s'", note_id, time);
  }

  private String whereIdClauseWithTableAlias(Long note_id, String time){
    return String.format("WHERE %s.note_id = %d AND %s.time = '%s'",TAL_TST, note_id, TAL_TST, time);
  }
}
