#!/bin/bash
script_dir="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
workdir="$script_dir"
cd $workdir

source ./env.sh

run_seconds=$1
if [ "" = "$run_seconds" ]; then
    run_seconds=10
fi

echo "run panama ${run_seconds}s"
$JAVA_HOME/bin/java --enable-preview --enable-native-access=ALL-UNNAMED -Xmx12m -jar demo-panama.jar performance $run_seconds

if ! [ $? -eq 0 ]; then
  echo "run panama failed"
else
  echo "run panama succeed"
fi

