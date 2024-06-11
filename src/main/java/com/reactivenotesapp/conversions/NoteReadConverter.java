package com.reactivenotesapp.conversions;

import com.reactivenotesapp.domain.Audiovisual;
import com.reactivenotesapp.domain.BaseNoteEntity;
import io.r2dbc.spi.Row;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.util.Lazy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class NoteReadConverter implements Converter<Row, BaseNoteEntity> {

  public static final List<Lazy<Converter<Row, ? extends BaseNoteEntity>>> noteConverters;

  /*
  order of converters should match the order of notetypes in the notetype_table
  TODO refactor. Maybe extract and read from file.
   */
  static {
    List<Lazy<Converter<Row, ? extends BaseNoteEntity>>> converters = new ArrayList<>();

    converters.add(Lazy.of(new AudiovisualReadConverter()));

    noteConverters = Collections.unmodifiableList(converters);



  }

  @Override
  public BaseNoteEntity convert(Row source) {
    return noteConverters.get(
            source.get("notetype_id", Integer.class)
    ).get().convert(source);
  }
}
