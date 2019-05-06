#!/bin/bash

echo "Hello world"

for param in $*
do
    echo "${param}"
done