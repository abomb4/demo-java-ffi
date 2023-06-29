#!/bin/bash
script_dir="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
workdir="$script_dir"
cd $workdir

source ./env.sh

run_seconds=$1
if [ "" = "$run_seconds" ]; then
    run_seconds=10
fi

echo "run jnr ${run_seconds}s"
$JAVA_HOME/bin/java -Xmx12m -jar demo-jnr.jar performance $run_seconds

if ! [ $? -eq 0 ]; then
  echo "run jnr failed"
else
  echo "run jnr succeed"
fi

