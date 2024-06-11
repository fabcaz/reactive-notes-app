package com.reactivenotesapp.domain;

import javax.validation.constraints.NotNull;
import java.sql.Timestamp;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class Audiovisual  extends BaseNoteEntity {

  @NotNull
  private AudiovisualType type;

  private static final DateTimeFormatter timeFmt = DateTimeFormatter.ofPattern("HH:mm:ss");



//TODO replace with list<Timestamps>
  @NotNull
  private Map<LocalTime, String> timeStamps;

  public Audiovisual() {
  }

  public Audiovisual(Long id, String name, String url, Timestamp createdDate, Timestamp lastModifiedDate, String description, int version, AudiovisualType type) {
    this( id,  name,  url, createdDate, lastModifiedDate, description, version,  type,  null, null);

  }

  public Audiovisual(Long id, String name, String url, Timestamp createdDate, Timestamp lastModifiedDate, String description, int version, AudiovisualType type, String category, String content ) {
    this( id,  name,  url, createdDate, lastModifiedDate, description, version,  type, category, content, new HashMap<>(0), new ArrayList<>(0));

  }

  public Audiovisual(Long id, String name, String url, Timestamp createdDate, Timestamp lastModifiedDate, String description, int version, AudiovisualType type, String category, String content, Map<String, String> termDefs, List<Tag> tags) {
    this( id,  name,  url, createdDate, lastModifiedDate, description, version,  type,  category, content, new HashMap<>(0), new ArrayList<>(0), new HashMap<>(0));

  }

  public Audiovisual(Long id, String name, String url, Timestamp createdDate, Timestamp lastModifiedDate, String description, int version, AudiovisualType type, String category, String content, Map<String, String> termDefs, List<Tag> tags, Map<LocalTime, String> timeStamps) {
    super(id, name, url,createdDate, lastModifiedDate, description, version, category, content, termDefs, tags);
    this.type = type;
    this.timeStamps = new HashMap<>(timeStamps);
  }

  public Optional<AudiovisualType> getType() {
    return Optional.of(type);
  }

  public void setType(AudiovisualType type) {
    this.type = type;
  }

  public Optional<Map<LocalTime, String>> getTimeStamps() {
    return Optional.of(timeStamps);
  }

  public void setTimeStamps(Map<LocalTime, String> timeStamps) {
    this.timeStamps = timeStamps;
  }

  public void addTimeStamp(String time, String content) {
    timeStamps.put(
            LocalTime.parse(time, timeFmt),
            content
    );
  }

  public void removeTimeStamp(String time) {
    timeStamps.remove(LocalTime.parse(time, timeFmt));
  }

  public Optional<String> getTimeStamp(String time){
    return Optional.ofNullable(
            timeStamps.get(LocalTime.parse(time, timeFmt))
    );
  }

  public static String fmtLocalTime(LocalTime lt){
    return lt.format(timeFmt);
  }
}
