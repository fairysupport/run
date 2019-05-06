#!/bin/bash

RUN_DIR=`pwd`
sudo -S touch ${RUN_DIR}/fairysupport_sample.txt
sudo -S sh -c "echo 'add text' >> ${RUN_DIR}/fairysupport_sample.txt"
sudo -S cat ${RUN_DIR}/fairysupport_sample.txt
