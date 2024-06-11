package com.reactivenotesapp.conversions;

import com.reactivenotesapp.domain.Audiovisual;
import com.reactivenotesapp.domain.AudiovisualType;
import io.r2dbc.spi.Row;
import org.springframework.core.convert.converter.Converter;

import java.sql.Timestamp;


public class AudiovisualPartialReadConverter implements Converter<Row, Audiovisual> {
  @Override
  public Audiovisual convert(Row source) {
    return new Audiovisual(
            source.get("id", Long.class),
            source.get("name", String.class),
            source.get("url", String.class),
            source.get("created_date", Timestamp.class),
            source.get("last_modified_date", Timestamp.class),
            source.get("description", String.class),
            source.get("version", Integer.class),
            //TODO add a audiovisualType table in schema and inner join in query in reposImpl
            //source.get("audiovisualType", AudiovisualType.class),
            AudiovisualType.VIDEO,
            null,
            source.get("category_id", String.class)
    );
  }
}
