#!/bin/bash

script_dir="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
workdir="$script_dir"
cd $workdir

path_to_jextract="$HOME/.local/software/java/jextract-20/bin/jextract"
$path_to_jextract --source -t org.example.panama /home/x4x/Workspaces/idea/java-ffi-article/libffidemo/src/main/c/libffidemo.h --output src/main/java
