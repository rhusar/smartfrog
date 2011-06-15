#!/bin/bash
# Start hadoop dfs daemons.
# Run this on master node.
# for cloudera distribution specify the user

bin=`dirname "$0"`
bin=`cd "$bin"; pwd`

. "$bin"/hadoop-config.sh

# start dfs daemons
# start namenode after datanodes, to minimize time namenode is up w/o data
# note: datanodes will log connection errors until namenode starts
"$bin"/hadoop-daemon.sh --config $HADOOP_CONF_DIR $1 namenode
"$bin"/hadoop-daemon.sh --config $HADOOP_CONF_DIR $1 jobtracker