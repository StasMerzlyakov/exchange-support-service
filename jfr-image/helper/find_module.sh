#!/bin/bash
#

module=`echo $1 | sed -r 's/\//\./g'`


../../gradlew run --args="$module"
