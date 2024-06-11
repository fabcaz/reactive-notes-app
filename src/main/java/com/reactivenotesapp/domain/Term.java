package com.reactivenotesapp.domain;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;
import java.util.Optional;

public class Term {

  @NotNull
  @PositiveOrZero
  private Long note_id;

  @NotNull
  @Size(min = 3, max = 36)
  private String term;

  @NotNull
  @Size(min = 3, max = 255)
  private String definition;

  public Term() {
  }

  public Term(Long note_id, String term, String definition) {
    this.note_id = note_id;
    this.term = term;
    this.definition = definition;
  }

  public Optional<Long> getNote_id() {
    return Optional.of(note_id);
  }

  public void setNote_id(Long note_id) {
    this.note_id = note_id;
  }

  public Optional<String> getTerm() {
    return Optional.of(term);
  }

  public void setTerm(String term) {
    this.term = term;
  }

  public Optional<String> getDefinition() {
    return Optional.of(definition);
  }

  public void setDefinition(String definition) {
    this.definition = definition;
  }
}
