#!/bin/bash
script_dir="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
workdir="$script_dir"
cd $workdir

source ./env.sh

run_seconds=$1
if [ "" = "$run_seconds" ]; then
    run_seconds=10
fi

echo "run jni ${run_seconds}s"
LD_LIBRARY_PATH=$workdir $JAVA_HOME/bin/java -Xmx12m -jar demo-jni.jar performance $run_seconds

if ! [ $? -eq 0 ]; then
  echo "run jni failed"
else
  echo "run jni succeed"
fi
