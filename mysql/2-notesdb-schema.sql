USE notesdb;

/*
- odd table naming convention to accommodate for unique table aliases
  -- _table suffix
  -- fully decompose class names (e.g. noteType -> note_type)
  -- junction tables concat decomposed class names with 'x' (e.g. note and 
    tag -> note_x_tag_table; note and noteType -> note_x_note_type_table)

- notes may occasionally conceptually belong to multiple categories through
  their tags; if a note's topic relates to multiple categories then the note
  should be assigned the most appropriate category and multiple tags.


- categories are broad conceptual groups; it is easier in some cases for a user
  to remember the category of a note than its tags. It is also intended to be
  used to index note_table and tag_table for common queries.

- tags are meant to be precise subcategories and should conceptually fall under 
  a single category.

- note_table.multiple_categories potentially useful metadata. When searching for 
  note who's name and tags have been forgotten for instance. For Stats.

- each create stmnt should have comment on same line for expected insert and 
  select tuples in that order; to awk final static strings used for queries.
  --awk should split SELECT tuple by coma and prepend '%.' to each field to 
    create a formated string, then add the java var name tuple.len times
  --TODO refactor awk script to use pattern range to collect 
    non-(DEFAULT | AUTO_INCREMENT) fields for INSERT stmnt, and all fields for 
    SELECT stmnt.

 */

CREATE TABLE category_table ( -- (name) (name)

  name VARCHAR(36),

  PRIMARY KEY (name)
);

CREATE TABLE note_table ( -- (name,url,description,content,category_id) (id,name,url,description,content,category_id,created_date,last_modified_date,version)

  id BIGINT UNSIGNED AUTO_INCREMENT,
  name VARCHAR(50) NOT NULL,
  url VARCHAR(255), -- maybe store in separate table to decompose
  description VARCHAR(500) NOT NULL,
  content VARCHAR(500),
  category_id VARCHAR(36) NOT NULL DEFAULT "CATEGORY_UNDECIDED",
  created_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  last_modified_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  multiple_categories BOOLEAN DEFAULT false, -- TODO look into CASE stmnt or decide in the reactive repo method before the query

  version SMALLINT DEFAULT 0, 
  PRIMARY KEY (id),
  FOREIGN KEY (category_id) REFERENCES category_table (name) ON DELETE RESTRICT, -- note: would SET DEFAULT but this is rejected by InnoDB
  INDEX idx_by_category (category_id ASC)
);

-- to determine which class to map to
CREATE TABLE note_type_table ( -- (id,name) (id,name)

  id TINYINT UNSIGNED AUTO_INCREMENT,
  name VARCHAR(36) NOT NULL,

  PRIMARY KEY (id)
);


CREATE TABLE note_x_note_type_table ( -- (note_id,type_id) (note_id,type_id)
  
  note_id BIGINT UNSIGNED,
  type_id TINYINT UNSIGNED,

  PRIMARY KEY (note_id, type_id),
  FOREIGN KEY (note_id) REFERENCES note_table (id) ON DELETE CASCADE,
  FOREIGN KEY (type_id) REFERENCES note_type_table (id) ON DELETE RESTRICT -- note: would cascade on delete but first need to reasign a type of affected notes
);


CREATE TABLE tag_table ( -- (name,category_id) (name,category_id)

  name VARCHAR(36),
  category_id VARCHAR(36) NOT NULL DEFAULT  "CATEGORY_UNDECIDED",

  PRIMARY KEY (name),
  FOREIGN KEY (category_id) REFERENCES category_table (name) ON DELETE RESTRICT, -- note: would SET DEFAULT but this is rejected by InnoDB
  INDEX idx_by_category (category_id ASC)
);

CREATE TABLE note_x_tag_table ( -- (note_id,tag_id) (note_id,tag_id)

  note_id BIGINT UNSIGNED,
  tag_id VARCHAR(36),

  PRIMARY KEY (note_id, tag_id),
  FOREIGN KEY (note_id) REFERENCES note_table (id) ON DELETE CASCADE,
  FOREIGN KEY (tag_id) REFERENCES tag_table (name) ON DELETE RESTRICT ON UPDATE RESTRICT, -- tag changes should be done on a per note basis
  INDEX idx_by_tag (tag_id ASC)
);

CREATE TABLE term_def_table ( -- (note_id,term,definition)  (note_id,term,definition) 

  note_id BIGINT UNSIGNED,
  term VARCHAR(36),
  definition VARCHAR(255) NOT NULL,

  PRIMARY KEY (note_id, term),
  FOREIGN KEY (note_id) REFERENCES note_table (id) ON DELETE CASCADE,
  INDEX idx_by_note (note_id),
  INDEX idx_by_term (term)
);

CREATE TABLE time_stamp_table ( -- (note_id,time,content)  (note_id,time,content) 

  note_id BIGINT UNSIGNED,
  time TIME,
  content VARCHAR(255) NOT NULL,

  PRIMARY KEY (note_id, time),
  FOREIGN KEY (note_id) REFERENCES note_table (id) ON DELETE CASCADE,
  INDEX idx_by_note (note_id, time ASC)
);
