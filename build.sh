#!/bin/bash
script_dir="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
workdir="$script_dir"
cd $workdir

rm -rf target
mvn clean package || { echo "failed"; exit 1; }
mkdir target
for file in $(find . -name "*.jar"); do mv $file target; done

cp scripts/* target
chmod +x target/*.sh
cp libffidemo/src/main/resources/libffidemo.so target/

echo "build finished, see target directory"

