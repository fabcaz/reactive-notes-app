package com.reactivenotesapp.domain;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

public class Timestamp {

  public static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("HH:mm:ss");

  @NotNull
  @PositiveOrZero
  private Long note_id;

  @NotNull
  @Size(min = 3, max = 36)
  private LocalTime time;

  @NotNull
  @Size(min = 3, max = 255)
  private String content;

  public Timestamp() {
  }

  public Timestamp(Long note_id, LocalTime time, String content) {
    this.note_id = note_id;
    this.time = time;
    this.content = content;
  }

  public Optional<Long> getNote_id() {
    return Optional.of(note_id);
  }

  public void setNote_id(Long note_id) {
    this.note_id = note_id;
  }

  public Optional<LocalTime> getTime() {
    return Optional.of(time);
  }

  public Optional<String> getTimeString() {
    return Optional.of(time.format(TIME_FMT));
  }

  //TODO could add a setTimeFromString but would need to introduce regex and project reactor here for validation and error; might not want to over complicate this class.
  public void setTime(LocalTime time) {
    this.time = time;
  }

  public Optional<String> getContent() {
    return Optional.of(content);
  }

  public void setContent(String content) {
    this.content = content;
  }
}
