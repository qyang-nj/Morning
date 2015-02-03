#!/bin/bash

PACKAGE_NAME="com.morning"
TEMP_LOG_FILE="/tmp/dumpalarm.log"

#save the dump file
adb shell dumpsys alarm > ${TEMP_LOG_FILE}

#nowRTC=1422947621656=2015-02-03 02:13:41 nowELAPSED=31272025
now=$(grep 'nowRTC.*nowELAPSED' $TEMP_LOG_FILE)

#1422947621656
nowRTC=$(echo $now | cut -d'=' -f2)

#31272025 (remove tailing '\r')
nowELAPSED=$(echo $now | cut -d'=' -f4 | tr -d '\r')

count=0

for line in $(awk "/Alarm.*${PACKAGE_NAME}/{print NR}" ${TEMP_LOG_FILE})
do
	printf "===============================================================================================\n"
	printf "[ Alarm $((++count)) ]"
	cat $TEMP_LOG_FILE | awk "NR>=${line}&&NR<=$((line+2))"

	whenElapsed=$(cat $TEMP_LOG_FILE | awk NR==$((line+1)) | awk '{print $2}' | cut -d'=' -f2)
	whenRTC=$(expr $whenElapsed - $nowELAPSED + $nowRTC + 1)

	printf "[ Time ] "
	date --date "@$((whenRTC / 1000))" #"+%Y-%m-%d %T"

	printf "===============================================================================================\n\n"
done
