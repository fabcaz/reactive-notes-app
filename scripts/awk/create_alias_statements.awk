#!/usr/bin/awk -f

# matches CREATE statements and creates aliases from the table name; should be used on reactive-notes-app/mysql/2-notesdb-schema.sql

BEGIN {
  print "package com.reactivenotesapp.repositories;";
  print;
  print "public class TableAliasStatements {\n";
  print "\t//AS is a prefix rather than a suffix for readability\n";
  print "\t//TAL is an abbreviation for table alias\n";
  print "\t//table name extracted to var also makes bug clearer if table gets ALTERed\n";

}

$1 == "CREATE" && $2 == "TABLE"{
  formattedFunc($3,$6,$7); 
}

END{
print "}"
}

#should  extract processing of $6 and $7 into helpers
function formattedFunc (tname, insert_tuple, select_tuple){

  abbr="";
  split(tname, tokens, "_");
  for(idx in tokens){ abbr = abbr substr(tokens[idx],0,1)};
  ABBR = toupper(abbr);
  
  #java var names
  specifier = "public static final String";

  table_alias_statement_name = "AS_" toupper(tname);
  table_alias_statement_value = "\"" tname " AS " abbr "\"";

  table_alias_name = "TAL_" ABBR;
  table_alias_value = "\"" abbr "\"";

  select_string_name = "SELECT_" ABBR;
  select_string_value = ""; 
  

  insert_string_name = "INSERT_" ABBR;
  insert_string_value = "\"" insert_tuple "\"";

  # select_string_value generation
  select_string="";
  select_fmt_args="";
  #strip parens
  select_tuple = substr(select_tuple, 2, length(select_tuple) - 2)
  select_len = split(select_tuple, select_tuple_elems, ",");
  #append 1..n-1 elems with comma
  for(i=1; i< select_len; i++){
    select_string = select_string "%s." select_tuple_elems[i] ", ";
    select_fmt_args = select_fmt_args table_alias_name ", ";
  };
  #append nth elem without comma
  select_string = select_string "%s." select_tuple_elems[select_len]
  select_fmt_args = select_fmt_args table_alias_name;

  select_string_value = "String.format(\"" select_string "\", " select_fmt_args ")";


  printf("\t%s %s = \"%s\";\n", specifier, toupper(tname), tname);
  printf("\t%s %s = %s;\n", specifier, table_alias_statement_name, table_alias_statement_value);
  printf("\t%s %s = %s;\n", specifier, table_alias_name, table_alias_value);
  printf("\t%s %s = %s;\n", specifier, insert_string_name, insert_string_value);
  printf("\t%s %s = %s;\n\n", specifier, select_string_name, select_string_value);

#  printf("\tpublic static final String AS_%s = \"%s AS %s\";\n",toupper(tname), tname, abbr);
#  printf("\tpublic static final String TAL_%s = \"%s\";\n\n",ABBR, abbr);
#  printf("\tpublic static final String INSERT_%s = \"%s\";\n\n",ABBR, abbr);
#  printf("\tpublic static final String SELECT_%s = \"%s\";\n\n",ABBR, abbr);
}


