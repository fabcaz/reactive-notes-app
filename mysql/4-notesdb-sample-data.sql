USE notesdb;


INSERT INTO category_table (name) VALUES
  ("health_fitness"), 
  ("permaculture"),
  ("tech"),
  ("woodworking");

INSERT INTO tag_table (name, category_id) VALUES
  ("computer_hardware", "tech"),
  ("network_security", "tech"),
  ("database", "tech"),
  ("java_lang", "tech"),
  ("java_project_reactor", "tech"),
  ("python_lang", "tech"),
  ("orm", "tech"),
  ("guilds", "permaculture"),
  ("perma_infrastructure", "permaculture"),
  ("health", "health_fitness"),
  ("fitness", "health_fitness"),
  ("joint_health", "health_fitness"),
  ("inflamation", "health_fitness"),
  ("hypertrophy", "health_fitness"),
  ("wood_joint", "woodworking");

INSERT INTO note_type_table VALUES
  (null, "blog"), 
  (null, "audiovisual")
  ;

INSERT INTO note_table (id, name, url,
  description, content, category_id,
  created_date, last_modified_date,  version) VALUES
  (null, "zone 5 guilds", null,
    "note on some zone 5 guilds", "content of noe on zone 5 guilds", "permaculture",
    STR_TO_DATE('01,5,2013','%d,%m,%Y'), STR_TO_DATE('01,8,2013','%d,%m,%Y'), 0),

  (null, "green roof installation", "https://www.greenroofblog.com",
    "thing to know when installing a green roof", "content of green roof note",  "permaculture",
    STR_TO_DATE('01,5,2013','%d,%m,%Y'), STR_TO_DATE('06,5,2013','%d,%m,%Y'), 0),
  
  (null, "Reactive Spring. Josh Long, Pivotal", "https://www.youtube.com/watch?v=Z5q-CXbvM1E",
    "josh long presentation/demo of reactive spring", null,  "tech",
    STR_TO_DATE('28,7,2016','%d,%m,%Y'), STR_TO_DATE('09,8,2016','%d,%m,%Y'), 0),

  (null, "name1", "https://domain1.com",
    "desc1:tech", "content1:tech", "tech",
    STR_TO_DATE('01,5,2013','%d,%m,%Y'), STR_TO_DATE('01,5,2013','%d,%m,%Y'), 0),

  (null, "name2", "https://domain2.com",
    "desc2:tech", "content2:tech", "tech",
    STR_TO_DATE('01,5,2013','%d,%m,%Y'), STR_TO_DATE('01,5,2013','%d,%m,%Y'), 0),
  
  (null, "name3", "https://domain3.com",
    "desc3:tech", "content3:tech", "tech",
    STR_TO_DATE('01,5,2013','%d,%m,%Y'), STR_TO_DATE('01,5,2013','%d,%m,%Y'), 0),

  (null, "name4", "https://domain4.com",
    "desc4:tech", "content4:tech", "tech",
    STR_TO_DATE('01,5,2013','%d,%m,%Y'), STR_TO_DATE('01,5,2013','%d,%m,%Y'), 0),

  (null, "name5", "https://domain5.com",
    "desc5:tech", "content5:tech", "tech",
    STR_TO_DATE('01,5,2013','%d,%m,%Y'), STR_TO_DATE('01,5,2013','%d,%m,%Y'), 0),

  (null, "name6", "https://domain6.com",
    "desc6:permaculture", "content6:permaculture", "permaculture",
    STR_TO_DATE('01,5,2013','%d,%m,%Y'), STR_TO_DATE('01,5,2013','%d,%m,%Y'), 0),

  (null, "name7", "https://domain7.com",
    "desc7:permaculture", "content7:permaculture", "permaculture",
    STR_TO_DATE('01,5,2013','%d,%m,%Y'), STR_TO_DATE('01,5,2013','%d,%m,%Y'), 0),

  (null, "name8", "https://domain8.com",
    "desc8:permaculture", "content8:permaculture", "permaculture",
    STR_TO_DATE('01,5,2013','%d,%m,%Y'), STR_TO_DATE('01,5,2013','%d,%m,%Y'), 0),

  (null, "name9", "https://domain9.com",
    "desc9:permaculture", "content9:permaculture", "permaculture",
    STR_TO_DATE('01,5,2013','%d,%m,%Y'), STR_TO_DATE('01,5,2013','%d,%m,%Y'), 0),

  (null, "name10", "https://domain10.com",
    "desc10:woodworking", "content10:woodworking", "woodworking",
    STR_TO_DATE('01,5,2013','%d,%m,%Y'), STR_TO_DATE('01,5,2013','%d,%m,%Y'), 0),

  (null, "name11", "https://domain11.com",
    "desc11:woodworking", "content11:woodworking", "woodworking",
    STR_TO_DATE('01,5,2013','%d,%m,%Y'), STR_TO_DATE('01,5,2013','%d,%m,%Y'), 0),

  (null, "name12", "https://domain12.com",
    "desc12:woodworking", "content12:woodworking", "woodworking",
    STR_TO_DATE('01,5,2013','%d,%m,%Y'), STR_TO_DATE('01,5,2013','%d,%m,%Y'), 0),

  (null, "name13", "https://domain13.com",
    "desc13:woodworking", "content13:woodworking", "woodworking",
    STR_TO_DATE('01,5,2013','%d,%m,%Y'), STR_TO_DATE('01,5,2013','%d,%m,%Y'), 0),

  (null, "name14", "https://domain14.com",
    "desc14:woodworking", "content14:woodworking", "woodworking",
    STR_TO_DATE('01,5,2013','%d,%m,%Y'), STR_TO_DATE('01,5,2013','%d,%m,%Y'), 0),

  (null, "name15", "https://domain15.com",
    "desc15:health_fitness", "content15:health_fitness", "health_fitness",
    STR_TO_DATE('01,5,2013','%d,%m,%Y'), STR_TO_DATE('01,5,2013','%d,%m,%Y'), 0),

  (null, "name16", "https://domain16.com",
    "desc16:health_fitness", "content16:health_fitness", "health_fitness",
    STR_TO_DATE('01,5,2013','%d,%m,%Y'), STR_TO_DATE('01,5,2013','%d,%m,%Y'), 0),

  (null, "name17", "https://domain17.com",
    "desc17:health_fitness", "content17:health_fitness", "health_fitness",
    STR_TO_DATE('01,5,2013','%d,%m,%Y'), STR_TO_DATE('01,5,2013','%d,%m,%Y'), 0),

  (null, "name18", "https://domain18.com",
    "desc18:health_fitness", "content18:health_fitness", "health_fitness",
    STR_TO_DATE('01,5,2013','%d,%m,%Y'), STR_TO_DATE('01,5,2013','%d,%m,%Y'), 0),

  (null, "name19", "https://domain19.com",
    "desc19:health_fitness", "content19:health_fitness", "health_fitness",
    STR_TO_DATE('01,5,2013','%d,%m,%Y'), STR_TO_DATE('01,5,2013','%d,%m,%Y'), 0),

  (null, "name20", "https://domain20.com",
    "desc20:health_fitness", "content20:health_fitness", "health_fitness",
    STR_TO_DATE('01,5,2013','%d,%m,%Y'), STR_TO_DATE('01,5,2013','%d,%m,%Y'), 0)
  ;

-- note:  fake notes  1 BLOG ;  2 VIDEO 
INSERT INTO note_x_note_type_table VALUES
  (1, 1),
  (2, 1),
  (3, 2),
  -- -----------------------------------------------------------
  (4, 1),
  (5, 2),
  (6, 2),
  (7, 1),
  (8, 1),
  -- -----------------------------------------------------------
  (9, 1),
  (10, 2),
  (11, 1),
  (12, 2),
  -- -----------------------------------------------------------
  (13, 1),
  (14, 1),
  (15, 1),
  (16, 2),
  -- -----------------------------------------------------------
  (18, 1),
  (19, 2),
  (20, 1),
  (21, 2),
  (22, 1),
  (23, 1)
  ;

INSERT INTO time_stamp_table VALUES
  (3, '00:18:18', "flow control"),
  (3, '00:20:05', "reactor publishers"),
  (3, '00:26:05', "more interesting things"),

  -- rm those notetype 1 // done
  -- -----------------------------------------------------------
  (5, "00:04:00", "name2_ts1:java"),
  (5, "00:05:50", "name2_ts2:java"),
  (5, "00:14:04", "name2_ts3:java"),
  (5, "00:17:12", "name2_ts4:java"),
  (6, "00:01:30", "name3_ts1:python"),
  (6, "00:03:45", "name3_ts2:python"),
  (6, "00:04:01", "name3_ts3:python"),
  -- -----------------------------------------------------------
  (10, "00:01:00", "name7_ts1:perma_infrastructure"),
  (10, "00:03:00", "name7_ts2:perma_infrastructure"),
  (12, "00:01:00", "name9_ts1:guilds"),
  (12, "00:05:00", "name9_ts2:guilds"),
  (12, "00:26:00", "name9_ts3:guilds"),
  (12, "00:48:24", "name9_ts4:guilds"),
  (12, "00:51:30", "name9_ts5:guilds"),
  (12, "01:00:07", "name9_ts6:guilds"),
  -- -----------------------------------------------------------
  (16, "00:05:00", "name13_ts1:wood_joint"),
  (16, "00:07:00", "name13_ts1:wood_joint"),
  -- -----------------------------------------------------------
  (19, "00:07:00", "name16_ts1:inflamation")
  ;

INSERT INTO note_x_tag_table VALUES
  (3, "java_lang"),
  (3, "java_project_reactor"),
  -- -----------------------------------------------------------
  (4, "computer_hardware"),
  (4, "network_security"),
  (5, "java_lang"),
  (5, "orm"),
  (5, "database"),
  (6, "python_lang"),
  (7, "java_lang"),
  (7, "java_project_reactor"),
  (8, "java_lang"),
  -- -----------------------------------------------------------
  (9, "guilds"),
  (10, "perma_infrastructure"),
  (11, "perma_infrastructure"),
  (12, "guilds"),
  -- -----------------------------------------------------------
  (13, "wood_joint"),
  (14, "wood_joint"),
  (15, "wood_joint"),
  (16, "wood_joint"),
  -- -----------------------------------------------------------
  (18, "joint_health"),
  (19, "inflamation"),
  (20, "inflamation"),
  (21, "inflamation"),
  (22, "fitness"),
  (22, "hypertrophy"),
  (23, "perma_infrastructure"),
  (23, "health")
 ;

UPDATE note_table 
  SET multiple_categories = true
  WHERE id = 22
  ;

INSERT INTO term_def_table (note_id, term, definition) VALUES
  (5, "term1", "def1:name2"),
  (5, "term2", "def2:name2"),
  (6, "term3", "def3:name3"),
  (6, "term1", "def1:name3"),
  (7, "term4", "def4:name4"),
  (15, "term5", "def5:name12"),
  (15, "term6", "def6:name12")
  ;
