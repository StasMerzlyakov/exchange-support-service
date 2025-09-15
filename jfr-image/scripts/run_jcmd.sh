#!/bin/sh
#

PID=`ps --no-headers --pid "$$" -N | grep java | awk '{print $1}' | head -n 1`
FILENAME=$1
DURATION=${2:-duration=60s}

CUR_DIR=`pwd`

jcmd $PID JFR.start $DURATION filename=$FILENAME settings=${CUR_DIR}/exception_all.jfc

