package com.reactivenotesapp.repositories;

import com.reactivenotesapp.domain.Term;
import io.r2dbc.spi.Connection;
import io.r2dbc.spi.Row;
import org.springframework.r2dbc.core.DatabaseClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static com.reactivenotesapp.repositories.RepoUtils.*;
import static com.reactivenotesapp.repositories.TableAliasStatements.*;

//TODO sole difference in count*() across repos is the query; should extract lines in common to a util class (will need a helper for variable num of binds when refactoring the string.formats out)
//TODO no need for alias when only one table; should remove unnecessary strings in formatted strings
public class CustomTermRepositoryImpl implements CustomTermRepository {

  private DatabaseClient dbClient;

  public CustomTermRepositoryImpl(DatabaseClient dbClient) {
    this.dbClient = dbClient;
  }


  @Override
  public Mono<Integer> count() {
    String countQuery = String.format("SELECT COUNT(*) AS %s FROM %s;", COUNT_ALIAS, AS_TERM_DEF_TABLE);

    return executeCountQuery(dbClient, countQuery);
  }

  @Override
  public Mono<Integer> countByNoteId(Long note_id) {
    String countQuery = String.format("SELECT COUNT(*) AS %s FROM %s WHERE note_id = %d;",
      COUNT_ALIAS, AS_TERM_DEF_TABLE, note_id);

    return executeCountQuery(dbClient, countQuery);
  }

  @Override
  public Mono<Integer> countOccurrences(String term) {
    String countQuery = String.format("SELECT COUNT(*) AS %s FROM %s WHERE term = %s;",
      COUNT_ALIAS, AS_TERM_DEF_TABLE, term);

    return executeCountQuery(dbClient, countQuery);
  }

  @Override
  public Mono<Term> save(Term term) {
    Long note_id = term.getNote_id().get();
    String termName = term.getTerm().get();
    String definition = term.getDefinition().get();

    String insertQuery = String.format("INSERT INTO %s %s VALUES (%s,%s,%s);",
      TERM_DEF_TABLE, INSERT_TDT, note_id,
      termName      , definition);


    String whereClause = whereIdClauseWithTableAlias(note_id, termName);
    String selectQuery = String.format("SELECT %s FROM %s %s;",
      SELECT_TDT, AS_TERM_DEF_TABLE, whereClause);

    Mono<Connection> connection = Mono.from(dbClient.getConnectionFactory().create());

    return connection.flatMap( conn ->
        Mono.from(conn.createStatement(insertQuery).execute())
      )
      .flatMap(res -> Mono.from(res.getRowsUpdated()))
      .log()
      .then(
        connection.flatMap(conn ->
          Mono.from(conn.createStatement(selectQuery).execute())
        )
      )
      .flatMap(res ->
        Mono.from(
          res.map((row, metadata) ->
            termReadConverter(row)
          )
        )
      );
  }


  @Override
  public Flux<Term> saveAll(Iterable<Term> terms) {
    return Flux.fromIterable(terms)
      .switchIfEmpty(Mono.error(() -> new IllegalArgumentException("Iterable<Term> should not be empty")))
      .concatMap(this::save);
  }


  //TODO add IF NOT EXISTS to allow changing term field
  @Override
  public Mono<Integer> update(Term term) {
    Long note_id = term.getNote_id().get();
    String termName = term.getTerm().get();
    String definition = term.getDefinition().get();

    String whereClause = whereIdClause(note_id, termName);
    String updateQuery= String.format("UPDATE %s SET definition = %s;",
      TERM_DEF_TABLE, definition, whereClause);

    return executeUpdateQuery(dbClient, updateQuery);
  }

  @Override
  public Mono<Integer> deleteById(Long note_id, String term) {

    String whereClause = whereIdClause(note_id, term);
    String deleteQuery = String.format("DELETE FROM %s %s;", TERM_DEF_TABLE, whereClause);

    return executeDeleteQuery(dbClient, deleteQuery);
  }

  @Override
  public Flux<Term> findAll() {
    String selectQuery = String.format("SELECT %s FROM %s;", SELECT_TDT, AS_TERM_DEF_TABLE);

    return executeSelectQuery(dbClient, selectQuery);
  }

  @Override
  public Mono<Term> findById(Long note_id, String term) {

    String whereClause = whereIdClauseWithTableAlias(note_id, term);
    String selectQuery = String.format("SELECT %s FROM %s %s;",
      SELECT_TDT, AS_TERM_DEF_TABLE, whereClause);

    return executeSelectQuery(dbClient, selectQuery).singleOrEmpty();
  }

  @Override
  public Flux<Term> findByTerm(String term) {
    String selectQuery = String.format("SELECT %s FROM %s WHERE %s.term ='%s';",
      SELECT_TDT, AS_TERM_DEF_TABLE, TAL_TDT,
      term);

    return executeSelectQuery(dbClient, selectQuery);
  }

  @Override
  public Flux<Term> findByTermLike(String term) {
    String selectQuery = String.format("SELECT %s FROM %s WHERE %s.term like'%s';",
      SELECT_TDT, AS_TERM_DEF_TABLE, TAL_TDT,
      term);

    return executeSelectQuery(dbClient, selectQuery);
  }

  @Override
  public Flux<Term> findByNoteId(Long note_id) {
    String selectQuery = String.format("SELECT %s FROM %s WHERE %s.note_id ='%d';",
      SELECT_TDT, AS_TERM_DEF_TABLE, TAL_TDT,
      note_id);

    return executeSelectQuery(dbClient, selectQuery);
  }

  private static Term termReadConverter(Row row){
    return new Term(
      row.get("note_id", Long.class),
      row.get("term", String.class),
      row.get( "definition",String.class)
    );
  }

  private static Flux<Term> executeSelectQuery(DatabaseClient dbClient, String selectQuery){

    Mono<Connection> connection = Mono.from(dbClient.getConnectionFactory().create());

    return connection.flatMapMany(conn -> conn.createStatement(selectQuery).execute())
      .flatMap( res ->
        Mono.from(res.map( (row, metadata) -> termReadConverter(row)))
      );
  }
  // TODO create method that takes a list of col names in RepoUtils and replace
  private String whereIdClause(Long note_id, String term){
    return String.format("WHERE note_id = %d AND term = '%s'", note_id, term);
  }

  private String whereIdClauseWithTableAlias(Long note_id, String term){
    return String.format("WHERE %s.note_id = %d AND %s.term = '%s'",TAL_TDT, note_id, TAL_TDT, term);
  }
}
