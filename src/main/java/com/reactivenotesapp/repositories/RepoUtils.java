package com.reactivenotesapp.repositories;

import com.reactivenotesapp.domain.BaseNoteEntity;
import io.r2dbc.spi.Connection;
import io.r2dbc.spi.Row;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.r2dbc.mapping.OutboundRow;
import org.springframework.r2dbc.core.DatabaseClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

//TODO figure out what to do if executeXXXQuery returns empty or errors out. In the CustomRepos using these methods.
public class RepoUtils {

  public static final String COUNT_ALIAS = "countAlias";

  public static Mono<Integer> executeCountQuery(DatabaseClient dbClient, String countQuery){
    return Mono.from(dbClient.getConnectionFactory().create())
      .flatMap(conn ->
        Mono.from(conn.createStatement(countQuery).execute())
      )
      .flatMap( res ->
          Mono.from(
            res.map((row, metadata) -> row.get(COUNT_ALIAS, Integer.class))
          )
      );
    }

  public static Mono<Integer> executeUpdateQuery(DatabaseClient dbClient, String updateQuery) {

    return Mono.from(dbClient.getConnectionFactory().create())
      .flatMap(conn ->
        Mono.from(conn.createStatement(updateQuery).execute())
      )
      .flatMap(res ->
        Mono.from(res.getRowsUpdated())
      );
  }

  public static Mono<Integer> executeDeleteQuery(DatabaseClient dbClient, String deleteQuery){

    return Mono.from(dbClient.getConnectionFactory().create())
      .flatMap(conn ->
        Mono.from(conn.createStatement(deleteQuery).execute())
      )
      .flatMap(res ->
        Mono.from(res.getRowsUpdated())
      );
  }


  public static <T> Flux<T> executeSelectQuery(DatabaseClient dbClient, String selectQuery, Converter<Row, T> converter){

    return Mono.from(dbClient.getConnectionFactory().create())
      .flatMapMany(conn ->
        Mono.from(conn.createStatement(selectQuery).execute())
      )
      .flatMap( res ->
        Mono.from(
          res.map(((row, rowMetadata) -> converter.convert(row)))
        )
      );
  }

  //TODO refactor with an OutBoundRow @WriteConverter
  public static <T> Mono<T> executeInsertQuery(DatabaseClient dbClient, String insertQuery, String selectQuery, Converter<Row, T> converter){

    Mono<Connection> connection = Mono.from(dbClient.getConnectionFactory().create());

    return connection
      .flatMapMany(conn ->
        Mono.from(conn.createStatement(insertQuery).execute())
      )
      .flatMap(res -> Mono.from(res.getRowsUpdated()))
      .log()
      .then(
        connection.flatMap(conn ->
          Mono.from(conn.createStatement(selectQuery).execute())
        )
      )
      .flatMap( res ->
        Mono.from(
          res.map(((row, rowMetadata) -> converter.convert(row)))
        )
      );
  }
}
