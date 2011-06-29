#!/bin/bash
# params: $1 bin dir, $2 action $3 component

bin="$1"
echo "bin dir is $bin"
echo "action is $2"
echo "node is $3"
. "$bin/hadoop-config.sh"
"$bin/hadoop-daemon.sh" --config $HADOOP_CONF_DIR "$2" "$3"
