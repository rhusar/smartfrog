#!/bin/bash
# Start hadoop map reduce daemons.
# for cloudera distribution specify the user

bin=`dirname "$0"`
bin=`cd "$bin"; pwd`

. "$bin"/hadoop-config.sh

# start mapred daemons
# start jobtracker first to minimize connection errors at startup


"$bin"/hadoop-daemon.sh --config $HADOOP_CONF_DIR $1 tasktracker
"$bin"/hadoop-daemon.sh --config $HADOOP_CONF_DIR $1 datanode