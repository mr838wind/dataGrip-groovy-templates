#!/bin/zsh
## dto,dao 각각 폴더로 구분 


source_dir='/Users/wind/DEV/workspace-datagrip'
desc_dir='/Users/wind/DEV/workspace-datagrip-tmp/arrange'

echo '>>> source_dir=$source_dir'
echo '>>> desc_dir=$desc_dir'

rm -rf $desc_dir/*
mkdir $desc_dir/dto
mkdir $desc_dir/dao
mkdir $desc_dir/service
mkdir $desc_dir/mybatis
mkdir $desc_dir/test
mkdir $desc_dir/controller

for dir in $source_dir/*; do
    echo "\n>>>>>==dir=$dir"
    for file in $dir/*; do
    	##echo $file
    	if [[ "$file" =~ ".*DTO.java" ]]; then
            echo "### handling file=$file"
            cp $file $desc_dir/dto
        elif [[ "$file" =~ ".*SearchCriteria.java" ]]; then
        	echo "### handling file=$file"
            cp $file $desc_dir/dto
        elif [[ "$file" =~ ".*Mapper.java" ]]; then
        	echo "### handling file=$file"
            cp $file $desc_dir/dao
        elif [[ "$file" =~ ".*Service.java" ]]; then
        	echo "### handling file=$file"
            cp $file $desc_dir/service
        elif [[ "$file" =~ ".*Mapper.xml" ]]; then
        	echo "### handling file=$file"
            cp $file $desc_dir/mybatis
        elif [[ "$file" =~ ".*ServiceTest.java" ]]; then
        	echo "### handling file=$file"
            cp $file $desc_dir/test
        elif [[ "$file" =~ ".*Controller.java" ]]; then
        	echo "### handling file=$file"
            cp $file $desc_dir/controller
        fi
    done
done
