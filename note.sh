#!/bin/bash

if [ "$#" -ne 1 ] ; then
        echo "$0: exactly 1 arg expected; the function name."
        exit 2
fi

build(){
  mvn package -Dm2_home=$M2
}

buildX(){
  mvn -X package -Dm2_home=$M2
}

extract_manifest(){
  jar -xf target/reactive_notes_app-1.0-SNAPSHOT.jar META-INF/MANIFEST.MF
  vim META-INF/MANIFEST.MF
}

list_jar_content(){
  jar -tf target/reactive_notes_app-1.0-SNAPSHOT.jar
}

printHelp(){
  printf "h    - help\nb    - mvn build\nbrun - build && run\nbtf  - mvn build && jar list\nbtf  - mvn build && jar list && run\nbX   - mvn -X build\nrun  - run jar\ntf   - jar list\nxf   - jar extract\n"

}

run(){
  java -jar target/reactive_notes_app-1.0-SNAPSHOT.jar
}


[ "$1" = "h" ] && printHelp
[ "$1" = "b" ] && build
[ "$1" = "brun" ] && build && run
[ "$1" = "btf" ] && build && list_jar_content
[ "$1" = "btfr" ] && build && list_jar_content && run
[ "$1" = "bX" ] && buildX
[ "$1" = "run" ] && run
[ "$1" = "xf" ] && extract_manifest
[ "$1" = "tf" ] && list_jar_content
