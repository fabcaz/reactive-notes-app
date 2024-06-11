#!/bin/bash
set -e

javaFileDir="../../src/main/java/com/reactivenotesapp/repositories/"
filename="TableAliasStatements"
fileExt=".java"
bakPrefix="_$(echo $(date +"%F_%T"))"
bakExt=".bak"
bakDir="../../.tmp_bakups/mysql/table_alias_bak"

javaFile=$javaFileDir$filename$fileExt
bakFile=$filename$bakPrefix$bakExt

if ! [ -d $bakdir  ]; then
  mkdir -p $bakdir
fi

mv $javaFile $bakDir/$bakFile


../awk/create_alias_statements.awk ../../mysql/2-notesdb-schema.sql > $javaFile
