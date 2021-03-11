#!/bin/bash
# copy 00-wind-gen groovy files to datagrip Extenshions > schema

## property
_OUT_DIR=/Users/wind/Library/Preferences/DataGrip2019.3/extensions/com.intellij.database/schema/
_IN_DIR=$(pwd)/../

## copy files
echo ">>== sync start"
echo ">>== from ${_IN_DIR} "
echo ">>== to ${_OUT_DIR} "
echo ">>== copy files and dirs: 00-wind-gen.groovy, 00-wind-gen.config, template/* "

cp ${_IN_DIR}/00-wind-gen.groovy ${_OUT_DIR}

cp ${_IN_DIR}/00-wind-gen.config ${_OUT_DIR}

cp -r ${_IN_DIR}/template ${_OUT_DIR}

echo ">>== sync success"



