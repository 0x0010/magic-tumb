#!/bin/sh

TARGET_DIR=../target
CLASSPATH=
set_env() {
  for i in `ls $TARGET_DIR/*.jar`
  do
    CLASSPATH=$CLASSPATH:$i
  done
}

if [ $1 = 'init' ] || [ $1 = 'execute' ] || [ $1 = 'delete' ] ; then
  echo 'init db'
  set_env;
  java -cp $CLASSPATH com.iamdigger.magictumblr.dbtools.DBTools $1
else
  echo 'unknown command'
fi;
