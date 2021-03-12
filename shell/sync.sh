#!/bin/bash
# copy 00-wind-gen groovy files to datagrip Extenshions > schema
# windows: install git-bash
##  https://violetboralee.medium.com/intellij-idea%EC%99%80-git-bash-%EC%97%B0%EB%8F%99%ED%95%98%EA%B8%B0-63e8216aa7de

## property
##_OUT_DIR=C:/Users/mr838/AppData/Roaming/JetBrains/DataGrip2020.3/extensions/com.intellij.database/schema/
_OUT_DIR=/Users/wind/Library/Preferences/DataGrip2019.3/extensions/com.intellij.database/schema/
_IN_DIR=$(pwd)/../

## copy files
echo ">>== sync start"
echo ">>== from ${_IN_DIR} "
echo ">>== to ${_OUT_DIR} "
echo ">>== copy files and dirs: 00-wind-gen.groovy, 00-wind-gen-config.json, template/* "

cp ${_IN_DIR}/00-wind-gen.groovy ${_OUT_DIR}
cp ${_IN_DIR}/00-wind-gen-config.json ${_OUT_DIR}
cp -r ${_IN_DIR}/template ${_OUT_DIR}

echo ">>== sync success"



