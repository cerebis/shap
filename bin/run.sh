#!/bin/bash

which gfind &> /dev/null > /dev/null
if [ $? -eq 0 ]
then
	FIND=gfind
else
	FIND=find
fi

CLASSPATH=war/WEB-INF/classes`$FIND local-lib war/WEB-INF/lib -name '*.jar' -printf ":%p"`

java -cp $CLASSPATH $*

