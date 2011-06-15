#!/bin/bash
# Start hadoop map reduce daemons.
# for cloudera distribution specify the user

bin=`dirname "$0"`
bin=`cd "$bin"; pwd`

. "$bin"/hadoop-config.sh

# start mapred daemons
# start jobtracker first to minimize connection errors at startup

# start as a different user only for cloudera distribution

# sudo -u sup "$bin"/hadoop-daemon.sh --config $HADOOP_CONF_DIR $1 tasktracker
# sudo -u sup "$bin"/hadoop-daemon.sh --config $HADOOP_CONF_DIR $1 datanode

"$bin"/hadoop-daemon.sh --config $HADOOP_CONF_DIR $1 tasktracker
"$bin"/hadoop-daemon.sh --config $HADOOP_CONF_DIR $1 datanode