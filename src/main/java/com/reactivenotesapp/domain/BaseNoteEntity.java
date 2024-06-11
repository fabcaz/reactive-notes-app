package com.reactivenotesapp.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.lang.Nullable;

import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;
import java.sql.Timestamp;
import java.util.*;

public class BaseNoteEntity {

  //auto gen
  @Id
  @PositiveOrZero
  private Long id;

  
  @NotNull
  @Size(min = 2, max = 50)
  private String name;
  
  @NotNull
  @Size(max = 255)
  private String url;

  @NotNull
  private Timestamp createdDate;

  @NotNull
  private Timestamp lastModifiedDate;

  
  @NotNull
  @Size(min = 50, max = 500)
  private String description;

  @Nullable
  @Size(max = 36)
  private String category;
  
  @Nullable
  @Size(min = 50, max = 500)
  private String content;

  @Version
  private int version;

  @Nullable
  private Map<String, String> termDefs; //note: maybe make this obj to include metadata such as timestamp mentioned in multiple notes

  @ManyToMany
  @JoinTable(name = "note_tag",
  joinColumns = @JoinColumn(name = "note_id"),
  inverseJoinColumns = @JoinColumn(name = "tag_id"))
  @Nullable
  List<Tag> tags;


  public BaseNoteEntity() {
  }

  public BaseNoteEntity(Long id, String name, String url, Timestamp createdDate, Timestamp lastModifiedDate, String description, int version){
    this(id, name, url, createdDate, lastModifiedDate, description, version, null, null);
  }


  public BaseNoteEntity(Long id,String name, String url, Timestamp createdDate, Timestamp lastModifiedDate, String description, int version, String category, String content) {
    this(id, name, url, createdDate, lastModifiedDate, description, version, category, content, new HashMap<>(0), new ArrayList<>(0));


  }

  public BaseNoteEntity(Long id, String name, String url, Timestamp createdDate, Timestamp lastModifiedDate, String description, int version, String category, String content, Map<String, String> termDefs, List<Tag> tags) {
    this.id = id;
    this.name = name;
    this.url = url;
    this.createdDate = createdDate;
    this.lastModifiedDate = lastModifiedDate;
    this.description = description;
    this.version = version;
    this.category = category;
    this.content = content;
    this.termDefs = new HashMap<>(termDefs);
    this.tags = new ArrayList<>(tags);
  }

  public Optional<Long> getId() {
    return Optional.of(id);
  }

  public Optional<String> getName() {
    return Optional.of(name);
  }

  public void setName(String name) {
    this.name = name;
  }

  public Optional<String> getUrl() {
    return Optional.of(url);
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public Optional<Timestamp> getCreatedDate() {
    return Optional.of(createdDate);
  }

  public void setCreatedDate(Timestamp createdDate) {
    this.createdDate = createdDate;
  }

  public Optional<Timestamp> getLastModifiedDate() {
    return Optional.of(lastModifiedDate);
  }

  public void setLastModifiedDate(Timestamp lastModifiedDate) {
    this.lastModifiedDate = lastModifiedDate;
  }

  public Optional<String> getDescription() {
    return Optional.of(description);
  }

  public void setDescription(String description) {
    this.description = description;
  }

  @Nullable
  public Optional<String> getCategory() {
    return Optional.ofNullable(category);
  }

  public void setCategory(@Nullable String category) {
    this.category = category;
  }

  public Optional<String> getContent() {
    return Optional.ofNullable(content);
  }

  public void setContent(String content) {
    this.content = content;
  }

  public Optional<Integer> getVersion() {
    return Optional.of(version);
  }

  public void setVersion(int version) {
    this.version = version;
  }

  public Optional<Map<String, String>> getTermDefs() {
    return Optional.ofNullable(termDefs);
  }

  public void setTermDefs(@Nullable Map<String, String> termDefs) {
    this.termDefs = termDefs;
  }

  public void addTermDef(String term, String def) {
    termDefs.put(term, def);
  }

  public void removeTermDef(String term) {
    termDefs.remove(term);
  }

  public Optional<String> getDef(String term){
    return Optional.ofNullable(termDefs.get(term));
  }

  public Optional<List<Tag>> getTags(){
    return Optional.ofNullable(tags);
  }

  public void setTags(@Nullable List<Tag> tags) {
    this.tags = tags;
  }

  public void addTag(Tag tag){
    if(!containsTag(tag)){
      tags.add(tag);
    }
  }

  public boolean containsTag(Tag tag){

    List<Tag> tags = getTags().orElseThrow(() -> new RuntimeException("tags List DNE"));

    for(Tag t : tags){
      if(t.equals(tag)) return true;
    }
    return false;
  }

  public void removeTag(Tag tag){
    if (tag.getName().isEmpty()) return;
    String tagName = tag.getName().get();
    tags.removeIf(t -> t.getName().equals(tagName));

  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    BaseNoteEntity that = (BaseNoteEntity) o;
    return id.equals(that.id) && name.equals(that.name) && Objects.equals(url, that.url) && Objects.equals(description, that.description) && Objects.equals(content, that.content);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, name, url, description, content);
  }
}
