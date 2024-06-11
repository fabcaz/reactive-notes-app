package com.reactivenotesapp.repositories;

import com.reactivenotesapp.domain.Audiovisual;
import com.reactivenotesapp.domain.AudiovisualType;
import com.reactivenotesapp.domain.Tag;
import io.r2dbc.spi.Connection;
import io.r2dbc.spi.Row;
import org.springframework.r2dbc.core.DatabaseClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.sql.Timestamp;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.reactivenotesapp.repositories.RepoUtils.*;
import static com.reactivenotesapp.repositories.TableAliasStatements.*;
import static com.reactivenotesapp.domain.Audiovisual.fmtLocalTime;

//note: most of the functionality is not specific to Audiovisual notes but needs the WHERE clasue to filter
// non-Audiovisuals; TODO extract nonspecific segments of queries when implementing other types;
//  segments are StringBuilder-ed in a method anyways.
public class CustomAudiovisualRepositoryImpl implements CustomAudiovisualRepository {

  private static final String PARTIAL_FIELDS_NOTE_COLS;// = String.format("%s.id, %s.name, %s.url, SUBSTR(%s.description, 1, 80), %s.category_id, %s.created_date, %s.last_modified_date", TAL_NT, TAL_NT, TAL_NT, TAL_NT, TAL_NT, TAL_NT, TAL_NT);

  //meant to be used when also selecting tags and timestamps
  private static final String FULL_FIELDS_NOTE_COLS;// = String.format("%s.id, %s.name, %s.url, %s.description, %s.content, %s.category_id, %s.created_date, %s.last_modified_date", TAL_NT);
  //private static final String INNERJOIN_NOTETYPE_TABLE_WHERE_AUDIOVISUAL = String.format("INNER JOIN "+ AS_NOTE_X_NOTE_TYPE_TABLE);
  private static final String WHERE_AUDIOVISUAL;

  private static final String SELECT_AUDIOVISUAL_NOTE_IDS;
  private static final String SEMI_COLON =";";

  static{
    FULL_FIELDS_NOTE_COLS = SELECT_NT;

    PARTIAL_FIELDS_NOTE_COLS = String.format("%s.id, %s.name, %s.url, SUBSTR(%s.description, 1, 80) AS description, %s.category_id, %s.created_date, %s.last_modified_date",
            TAL_NT, TAL_NT, TAL_NT, TAL_NT, TAL_NT, TAL_NT, TAL_NT);

    SELECT_AUDIOVISUAL_NOTE_IDS = new StringBuilder(frontPad(String.format("SELECT DISTINCT %s.note_id", TAL_NXNTT)))
            .append(frontPad(" FROM " + AS_NOTE_X_NOTE_TYPE_TABLE))
            .append(frontPad(" INNER JOIN " + AS_NOTE_TYPE_TABLE))
            .append(frontPad(String.format(" ON %s.type_id = %s.id", TAL_NXNTT, TAL_NTT)))
            .append(frontPad(String.format(" WHERE %s.name = 'audiovisual'", TAL_NTT)))
            .toString();

    WHERE_AUDIOVISUAL = new StringBuilder(String.format("WHERE %s.id IN (", TAL_NT))
            .append(SELECT_AUDIOVISUAL_NOTE_IDS)
            .append(")")// closing outer where clause
            .toString();



  }
//TODO add the methods in here to the baseService interface
  private DatabaseClient dbClient;

  public CustomAudiovisualRepositoryImpl(DatabaseClient dbClient) {
    this.dbClient = dbClient;
  }

  @Override
  public Mono<Integer> countAudiovisual() {
    String countQuery = String.format("SELECT COUNT(*) AS %s FROM %s %;", COUNT_ALIAS, AS_NOTE_TABLE, WHERE_AUDIOVISUAL);

    return executeCountQuery(dbClient, countQuery);
  }

  //note: save, saveAll | row DNE in table therefore no id; use LAST_INSERT_ID() for single insert, or SELECT
  // `auto_increment` FROM INFORMATION_SCHEMA.TABLES WHERE table_name = 'tableName' for multiple (may not be accurate
  // if dels occured) https://stackoverflow.com/a/6761702
  //TODO consider moving tag, term and ts functionality to separate repos and manage crud from service layer
  /**
   * Inserts new note entry and, if applicable, entries for note-tag, note-termdef and note-timestamp relations.
   * @param au
   * @return the row in note_table corresponding to the new entry; Tags, terms and timestamps should be requested in the service layer
   */
  @Override
  public Mono<Audiovisual> save(Audiovisual au) {

    String insertQuery = String.format("INSERT INTO note_table %s VALUES (%s, %s, %s, %s, %s);", INSERT_NT,
      au.getName().get(),
      au.getUrl().get(), au.getDescription(),
      au.getContent().orElse("null"),
      au.getCategory().orElse("DEFAULT"));

    String selectQuery = String.format("SELECT %s FROM %s ORDER BY last_modified_date DESC LIMIT 1;",
        FULL_FIELDS_NOTE_COLS, AS_NOTE_TABLE);

    Mono<Connection> connection = Mono.from(dbClient.getConnectionFactory().create());

    return connection.flatMap(conn ->
              Mono.from(conn.createStatement(insertQuery).execute())
            )
            .flatMap( res -> Mono.from(res.getRowsUpdated())) //note: may be empty since not an update statement
            .log()
            .then(
                   connection.flatMap(conn ->
                     Mono.from(conn.createStatement(selectQuery).execute())
                   )
            )
            //note: assumes that the insert above was successful TODO could check that the created_date timestamp is approximately equal to LocalDateTime.now() but still doesn't guaranty that the fetched note is the one inserted above
            .flatMap(res ->
                    Mono.from(res.map((row, metadata) ->
                            fullAudiovisualReadConvert(row)
                    ))
            )
      //TODO this should be donne in servcie layer via tag, ter, ts services
            .flatMap(  newau -> {
                      saveTags(newau.getId().get(), au);
                      saveTerms(newau.getId().get(), au);
                      saveTimestamps(newau.getId().get(), au);
                      return Mono.just(newau);
                    }
            );
  }



  @Override
  public Flux<Audiovisual> saveAll(Iterable<Audiovisual> notes) {
    return Flux.fromIterable(notes)
      .switchIfEmpty(Mono.error(() -> new IllegalArgumentException("Iterable<Audiovisual> should not be empty")))
      .concatMap(this::save);
  }

  //TODO create a DTO or command obj that contains list of changesTuples then refactor. i.e. ('action', 'field', 'value') like (add, tags, orm) or (remove, terms, mapping); edit should be spectial case with oldVal and newVal

  @Override
  public Mono<Audiovisual> update(Audiovisual au) {

    int auVersion = au.getVersion().get();

    StringBuilder sb = new StringBuilder(String.format("UPDATE %s SET", NOTE_TABLE));

    //note: currently, validated obj should have those attrs filled
    if( au.getName().isPresent() ){
			sb.append(frontPad(
				String.format("name = %s,", au.getName().get())
      ));
    }
    if( au.getUrl().isPresent() ){
			sb.append(frontPad(
				String.format("url = %s,", au.getUrl().get())
      ));
    }
    if( au.getDescription().isPresent() ){
			sb.append(frontPad(
				String.format("description = %s,", au.getDescription().get())
      ));
    }
    if( au.getCategory().isPresent() ){
			sb.append(frontPad(
				String.format("category = %s,", au.getCategory().get())
      ));
    }
    if( au.getContent().isPresent() ){
			sb.append(frontPad(
				String.format("content = %s,", au.getContent().get())
      ));
    }
    if( au.getVersion().isPresent() ){
			sb.append(frontPad(
				String.format("version = %s,", au.getVersion().get())
      ));
    }


    sb.append(frontPad(String.format("version = %d", auVersion + 1)));
    sb.append(frontPad(String.format("WHERE id = %d AND version = %d", au.getId().get(), auVersion)));
    sb.append(SEMI_COLON);

    String updateQuery = sb.toString();

    String selectQuery = String.format("SELECT %s %s ORDER BY last_modified_date DESC LIMIT 1;",
        FULL_FIELDS_NOTE_COLS, AS_NOTE_TABLE);

    Mono<Connection> connection = Mono.from(dbClient.getConnectionFactory().create());

    return connection.flatMap( conn ->
                Mono.from(conn.createStatement(updateQuery).execute())
            )
            //TODO next part is same as in save(Audiovisual) -> should extract to method and Mono.transform()
            .flatMap( res -> Mono.from(res.getRowsUpdated()))
            .log()
            .thenMany(
                    connection.flatMapMany(conn ->
                      Mono.from(conn.createStatement(selectQuery).execute())
                    )
            )
            .flatMap(res ->
                    res.map((row, metadata) ->
                      fullAudiovisualReadConvert(row)
                    )
            )
            .single()
            ;
  }

  @Override
  public Mono<Integer> deleteById(Long id){
    String deleteQuery = String.format("DELETE FROM %s WHERE id = %d;", NOTE_TABLE, id);

    return executeDeleteQuery(dbClient, deleteQuery);
  }


  @Override
  public Flux<Audiovisual> findAllPartial() {
    String selectQuery = partialAudiovisualSb("").append(SEMI_COLON).toString();

    return executeSelectQueryPartialResult(dbClient, selectQuery);
  }

  @Override
  public Flux<Audiovisual> findAllFull() {
    String selectQuery = new StringBuilder("SELECT " + FULL_FIELDS_NOTE_COLS)
            .append(frontPad("FROM " + AS_NOTE_TABLE))
            .append(frontPad(WHERE_AUDIOVISUAL))
            .append(SEMI_COLON)
            .toString();

    return executeSelectQueryFullResult(dbClient, selectQuery);

  }

  @Override
  public Mono<Audiovisual> findByIdFull(Long id) {
    String selectQuery = new StringBuilder("SELECT " + FULL_FIELDS_NOTE_COLS)
            .append(frontPad("FROM " + AS_NOTE_TABLE))
            .append(frontPad(WHERE_AUDIOVISUAL))
            .append(frontPad(String.format("AND %s.id = %s", TAL_NT, id.toString())))
            .append(SEMI_COLON)
            .toString();

    return executeSelectQueryFullResult(dbClient, selectQuery)
            .singleOrEmpty();
  }

  @Override
  public Mono<Audiovisual> findByIdPartial(Long id) {
    String selectQuery = new StringBuilder("SELECT " + PARTIAL_FIELDS_NOTE_COLS)
            .append(frontPad("FROM " + AS_NOTE_TABLE))
            .append(frontPad(WHERE_AUDIOVISUAL))
            .append(frontPad(String.format("AND %s.id = %s", TAL_NT, id.toString())))
            .append(SEMI_COLON)
            .toString();

    return executeSelectQueryPartialResult(dbClient, selectQuery)
            .singleOrEmpty();
  }

  @Override
  public Flux<Audiovisual> findByNameFull(String name) {
    String selectQuery = new StringBuilder("SELECT " + FULL_FIELDS_NOTE_COLS)
            .append(frontPad("FROM " + AS_NOTE_TABLE))
            .append(frontPad(WHERE_AUDIOVISUAL))
            .append(frontPad(String.format("AND %s.name = %s", TAL_NT, name)))
            .append(SEMI_COLON)
            .toString();

    return executeSelectQueryFullResult(dbClient, selectQuery);
  }

  @Override
  public Flux<Audiovisual> findByNamePartial(String name) {
    String selectQuery  = new StringBuilder("SELECT " + PARTIAL_FIELDS_NOTE_COLS)
            .append(frontPad("FROM " + AS_NOTE_TABLE))
            .append(frontPad(WHERE_AUDIOVISUAL))
            .append(frontPad(String.format("AND %s.name = %s", TAL_NT, name)))
            .append(SEMI_COLON)
            .toString();

    return executeSelectQueryPartialResult(dbClient, selectQuery);
  }

  @Override
  public Flux<Audiovisual> findByNameLike(String name) {
    String selectQuery = new StringBuilder("SELECT " + PARTIAL_FIELDS_NOTE_COLS)
            .append(frontPad("FROM " + AS_NOTE_TABLE))
            .append(frontPad(WHERE_AUDIOVISUAL))
            .append(frontPad(String.format("AND %s.name = %%%s%%", TAL_NT, name)))
            .append(SEMI_COLON)
            .toString();

    return executeSelectQueryPartialResult(dbClient, selectQuery);
  }

  //note: could be done in service layer using tagService; more logical but probably less efficient
  @Override
  public Flux<Audiovisual> findNotesWithTag(String tagName) {
    StringBuilder sb = new StringBuilder(
            String.format("INNER JOIN ( " +
                    "SELECT DISTINCT %s.note_id AS tagged_note " +
                    "FROM %s WHERE %s.tag_id = '%s'" +
                    ")note_tag_notes " +
                    "ON %s.id = note_tag_notes.tagged_note", TAL_NXTT, AS_NOTE_X_TAG_TABLE, TAL_NXTT, tagName, TAL_NT)
    );

    String selectQuery = partialAudiovisualSb(sb).append(SEMI_COLON).toString();

    return executeSelectQueryPartialResult(dbClient, selectQuery);
  }

  //note: could be done in service layer using tagService; more logical but probably less efficient
  @Override
  public Flux<Audiovisual> findNotesWithTags(String[] tagArray) {

    String listOfTags = arrToString(tagArray);

    StringBuilder sb = new StringBuilder(
            String.format("INNER JOIN ( " +
            "SELECT DISTINCT %s.note_id AS tagged_note " +
            "FROM %s WHERE %s.tag_id " +
            "IN %s )note_tag_notes " +
            "ON %s.id = note_tag_notes.tagged_note", TAL_NXTT, AS_NOTE_X_TAG_TABLE, TAL_NXTT, listOfTags, TAL_NT)
    );

    String selectQuery = partialAudiovisualSb(sb).append(SEMI_COLON).toString();

    return executeSelectQueryPartialResult(dbClient, selectQuery);
  }

  @Override
  public Flux<Audiovisual> findNotesWithDescriptionContainingSubstring(String subString) {

    String selectQuery = partialAudiovisualSb("")
            .append(
                    frontPad(
                            String.format("AND %s.description LIKE '%%%s%%'", TAL_NT, subString)
                    )
            )
            .append(SEMI_COLON)
            .toString();

    return executeSelectQueryPartialResult(dbClient, selectQuery);
  }

  @Override
  public Flux<Audiovisual> findNotesWithContentContainingSubstring(String subString) {
    String selectQuery = partialAudiovisualSb("")
            .append(
                    frontPad(
                            String.format("AND %s.content LIKE '%%%s%%'", TAL_NT, subString)
                    )
            )
            .append(SEMI_COLON)
            .toString();

    return executeSelectQueryPartialResult(dbClient, selectQuery);
  }

  //TODO replace 'termed_note' with %s to use join_new_col var, %s.term with %s.%s to use where_right_col var, and
  // note_term_note with %s for join_alias var. To extract string since it is same as tag join query.
  @Override
  public Flux<Audiovisual> findNotesWithTerm(String termName) {

    StringBuilder sb = new StringBuilder(
            String.format("INNER JOIN ( " +
                    "SELECT DISTINCT %s.note_id AS termed_note " +
                    "FROM %s WHERE %s.term = '%s'" +
                    ")note_term_notes " +
                    "ON %s.id = note_term_notes.termed_note", TAL_TDT, AS_TERM_DEF_TABLE, TAL_TDT, termName, TAL_NT)
    );

    String selectQuery = partialAudiovisualSb(sb).append(SEMI_COLON).toString();

    return executeSelectQueryPartialResult(dbClient, selectQuery);
  }

  /**
   * Helper that prepends the given string with select partial note columns segment, and appends with where clause
   * for audiovisual type. Does not append SEMI_COLON.
   * @param middle Segment of the query relating to the method it is used in.
   * @return full query string
   */
  private static StringBuilder partialAudiovisualSb(CharSequence middle){

    StringBuilder sb = new StringBuilder("SELECT " + PARTIAL_FIELDS_NOTE_COLS);
    sb.append(frontPad("FROM " + AS_NOTE_TABLE));

    sb.append(middle);

    sb.append(frontPad(WHERE_AUDIOVISUAL));
    /*
    SELECT DISTINCT nxntt.note_id
    FROM note_x_note_type_table AS nxntt
    INNER JOIN note_type_table AS ntt
    ON nxntt.type_id = ntt.id
    WHERE ntt.name = 'audiovisual'
      */
        //should prepend appended strings with space

  return sb;


  }
  /**
   * Pads given characterSequence with spaces at front. To be used for appending segments to a query StringBuilder.
   * @param charseq
   * @return
   */
  private static String frontPad(CharSequence charseq){
    return " " + charseq;
  }

  /**
   * Create list string from a String array for IN statement.
   * @param arr
   * @return
   */
  private static String arrToString(String[] arr){

    for(int i = 0 ; i< arr.length ; i++) arr[i] = "\"" + arr[i] + "\"";
    return Arrays.toString(arr)
            .replace("[","(")
            .replace("]",")");

  }

  // quickfix for error about customConverter.convert not being static
  private static Audiovisual partialAudiovisualReadConvert(Row row){
    return new Audiovisual(
      row.get("id", Long.class),
      row.get("name", String.class),
      row.get("url", String.class),
      row.get("created_date", Timestamp.class),
      row.get("last_modified_date", Timestamp.class),
      row.get("description", String.class),
      row.get("version", Integer.class),
      //TODO add a audiovisualType table in schema and inner join in query in reposImpl
      //source.get("audiovisualType", AudiovisualType.class),
      AudiovisualType.VIDEO,
      null,
      row.get("category_id", String.class)
    );
  }

  /**
   * Only creates notes with data from the note_table since there are repositories for other attributes. Tags, terms and
   * timestamps will be requested and set separately by an endpoint handler for each note returned here.
   * @param row
   * @return Audiovisual instance
   */
  private static Audiovisual fullAudiovisualReadConvert(Row row){
    return new Audiovisual(
            row.get("id", Long.class),
            row.get("name", String.class),
            row.get("url", String.class),
            row.get("created_date", Timestamp.class),
            row.get("last_modified_date", Timestamp.class),
            row.get("description", String.class),
            row.get("version", Integer.class),
            //TODO add a audiovisualType table in schema and inner join in query in reposImpl
            //row.get("audiovisualType", AudiovisualType.class),
            AudiovisualType.VIDEO,
            row.get("category_id", String.class),
            row.get("content", String.class)

    );
  }

  private static Flux<Audiovisual> executeSelectQueryPartialResult(DatabaseClient dbClient, String selectQuery){

    return Mono.from(dbClient.getConnectionFactory().create())
      .flatMapMany(conn -> conn.createStatement(selectQuery).execute())
      .flatMap(res ->
        res.map((row, metadata) ->
          partialAudiovisualReadConvert(row)
        )
      );
  }

  private static Flux<Audiovisual> executeSelectQueryFullResult(DatabaseClient dbClient, String selectQuery){

    return Mono.from(dbClient.getConnectionFactory().create())
      .flatMapMany(conn -> conn.createStatement(selectQuery).execute())
      .flatMap(res ->
        res.map((row, metadata) ->
          fullAudiovisualReadConvert(row)
        )
      );
  }

  private Mono<Integer> saveTags(Long note_id, Audiovisual au){

    if(au.getTags().get().size() != 0){
      StringBuilder sb = new StringBuilder("VALUES ");
      List<Tag> lt = au.getTags().get();

      for(Tag t : lt){
        sb.append(frontPad(
                //(note_id, tag_id)
                String.format("(%s, %s),", note_id, t.getName().get())
        ));
      }
      sb.deleteCharAt(sb.length()-1);// remove excess ','
      String tagValuesSegment = sb.toString();

      String note_x_tagSqlStatement = String.format("INSERT %s INTO %s %s;",
          INSERT_NXTT, NOTE_X_TAG_TABLE, tagValuesSegment);
          
      return Mono.from(dbClient.getConnectionFactory().create())
              .flatMap( conn ->
                      Mono.from(conn.createStatement(note_x_tagSqlStatement).execute())
              ).flatMap(res ->
                Mono.from( res.getRowsUpdated())
              )
              .log();

    }
    return Mono.empty();
  }

  private Mono<Integer> saveTerms(Long note_id, Audiovisual au) {

    if(au.getTermDefs().get().size() != 0){
      StringBuilder sb = new StringBuilder("VALUES ");
      Map<String, String> td = au.getTermDefs().get();
      Set<String> keys = td.keySet();

      for(String k : keys){
        sb.append(frontPad(
                //(note_id, term, definition)
                String.format("(%s, %s, %s),", note_id, k,td.get(k))
        ));
      }
      sb.deleteCharAt(sb.length()-1);// remove excess ','
      String termValuesSegment = sb.toString();

      String term_defSqlStatement = String.format("INSERT %s INTO %s %s;", 
          INSERT_TDT, TERM_DEF_TABLE, termValuesSegment);

      Mono.from(dbClient.getConnectionFactory().create())
              .flatMap( conn ->
                      Mono.from(conn.createStatement(term_defSqlStatement).execute())
              ).flatMap(res ->
                Mono.from( res.getRowsUpdated())
              )
              .log();
    }
    return Mono.empty();
  }

  //TODO refactor. Change arg from Audiovisual to List<Tag>, change to public; to be used by command obj.
  private Mono<Integer> saveTimestamps(Long note_id, Audiovisual au) {

    if(au.getTimeStamps().get().size() != 0){
      StringBuilder sb = new StringBuilder("VALUES ");
      Map< LocalTime,String> ts = au.getTimeStamps().get();
      Set<LocalTime> keys = ts.keySet();

      for(LocalTime locT : keys){
        sb.append(frontPad(
                //(note_id, time, content)
                String.format("(%s, %s, %s),", note_id, fmtLocalTime(locT),ts.get(locT))
        ));
      }
      sb.deleteCharAt(sb.length()-1);// remove excess ','
      String time_stampValuesSegment = sb.toString();

      String timestampSqlStatement = String.format("INSERT %s INTO %s %s;",
          INSERT_TST, TIME_STAMP_TABLE, time_stampValuesSegment);

      Mono.from(dbClient.getConnectionFactory().create())
              .flatMap( it ->
                      Mono.from(it.createStatement(timestampSqlStatement).execute())
              ).flatMap(res ->
                      Mono.from( res.getRowsUpdated())
              )
              .log();
    }
    return Mono.empty();
  }


}
