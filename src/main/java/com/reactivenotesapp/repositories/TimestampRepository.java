package com.reactivenotesapp.repositories;

import com.reactivenotesapp.domain.Timestamp;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/*
mysql> SELECT * FROM time_stamp_table AS tst WHERE tst.time LIKE '01%';
+---------+----------+------------------+
| note_id | time     | content          |
+---------+----------+------------------+
|      12 | 01:00:07 | name9_ts6:guilds |
+---------+----------+------------------+
1 row in set (0.00 sec)

  mysql> SELECT * FROM time_stamp_table AS tst WHERE tst.time LIKE '%01%';
  +---------+----------+--------------------------------+
  | note_id | time     | content                        |
  +---------+----------+--------------------------------+
  |       6 | 00:01:30 | name3_ts1:python               |
  |       6 | 00:04:01 | name3_ts3:python               |
  |      10 | 00:01:00 | name7_ts1:perma_infrastructure |
  |      12 | 00:01:00 | name9_ts1:guilds               |
  |      12 | 01:00:07 | name9_ts6:guilds               |
  +---------+----------+--------------------------------+
  5 rows in set (0.01 sec)

  mysql> SELECT * FROM time_stamp_table AS tst WHERE tst.time = '00:04:01';
  +---------+----------+------------------+
  | note_id | time     | content          |
  +---------+----------+------------------+
  |       6 | 00:04:01 | name3_ts3:python |
  +---------+----------+------------------+
  1 row in set (0.00 sec)

  mysql>
// todo TS BEHAVE LIKE STRINGS
*/
public interface TimestampRepository extends CustomTimestampRepository{
  }
