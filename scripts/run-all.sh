#!/bin/bash
script_dir="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
workdir="$script_dir"
cd $workdir

if [ "" = "$1" ]; then
    echo "usage: run-all.sh <seconds>"
    exit 0
fi
./run-jni.sh "$1"
./run-jna.sh "$1"
./run-jnr.sh "$1"
./run-panama.sh "$1"

echo "all done"

