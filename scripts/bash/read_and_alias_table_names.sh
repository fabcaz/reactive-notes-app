#!/usr/bin/awk -f

# matches CREATE statements and creates aliases from the table name; should be used on reactive-notes-app/mysql/2-notesdb-schema.sql

BEGIN {
  as_col=22;
}

$1 == "CREATE" && $2 == "TABLE"{
  formattedFunc($3); 
}

function formattedFunc (tname){

  abbr="";
  split(tname, tokens, "_");
  for(idx in tokens){ abbr = abbr substr(tokens[idx],0,1)};

  spaceStr="";
  for(i=0;i<as_col-length(tname);i++){ spaceStr = spaceStr " "};

  printf("%s%sAS %s\n", tname, spaceStr, abbr);
}
