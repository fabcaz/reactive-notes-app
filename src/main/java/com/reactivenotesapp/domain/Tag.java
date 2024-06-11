package com.reactivenotesapp.domain;

import org.springframework.data.annotation.Id;

import javax.annotation.processing.Generated;
import javax.persistence.ManyToMany;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.*;
import java.util.stream.Collectors;

public class Tag {
//names should be unique and smaller than uuid so will use as pk
//  @Id
//  private UUID id;


  @Id
  @Size(min = 2, max = 36)
  private String name;

  private String category;

  @ManyToMany(mappedBy = "tags")
  List<BaseNoteEntity> notes = new ArrayList<>();

  public Tag(String name) {
    this(name, null,null);
  }

  public Tag(String name, String category){
    this(name, category,null);
  }
  public Tag(String name, String category, List<BaseNoteEntity> notes){
    this.name = name;
    this.category = category;
    this.notes = notes;
  }

//  public Optional<UUID> getId() {
//    return Optional.of(id);
//  }

  public Optional<String> getName() {
    return Optional.of(name);
  }

  public void setName(String name) {
    this.name = name;
  }

  public Optional<String> getCategory() {
    return Optional.of(category);
  }

  public void setCategory(String category) {
    this.category = category;
  }

  public boolean addNote(BaseNoteEntity note){
    return notes.add(note);
  }

  public boolean removeNote(BaseNoteEntity note){
    return notes.remove(note);
  }

  public Optional<List<BaseNoteEntity>> getNoteByName(String name) {
    return Optional.of(
            getNotes().orElseGet(List::of).stream()
                    .filter(note -> note.getName().get().equals(name))
                    .collect(Collectors.toList())
    );
  }

  public Optional<BaseNoteEntity> getNoteByid(Long id) {
    return Optional.ofNullable(
            getNotes().orElseGet(List::of).stream()
                    .filter(note -> note.getId().get().equals(id))
                    .collect(Collectors.toList()).get(0)
    );
  }

  public Optional<List<BaseNoteEntity>> getNotes() {
    return Optional.ofNullable(notes);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Tag tag = (Tag) o;
    return name.equals(tag.name);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name);
  }
}
