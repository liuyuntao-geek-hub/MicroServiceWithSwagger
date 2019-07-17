#!/bin/sh

#=================================================================================================================================================
# Title            : RUNJMETER
# ProjectName      : Cognitive Claim API
# Filename         : runJmeter.sh
# Description      : Shell Script for Jmeter test for ds-cogx-api
# Developer        : Anthem
# Created on       : March 2019
# Location         : US
# Date           Auth           Ver#     Modified By(Name)                 Change and Reason for Change
# ----------    -----        -----------------------------               --------------------------------------
# 2019/03/28    Deloitte         1     Initial Version
#====================================================================================================================================================
#-----------------------------------------------------------------------------------------------------------------------------------
#       execJmeter.sh
#             -w <workdirecory:JMX file home>
#             -j <Jmeter installation home>
#             -v <csv file location and file name>
#             -x <jmx file name>
#             -t <Jmeter Threads number>
#             -c <Jmeter request count>
#             -r <Jmeter rampup time>
#       Please follow the following exmple:
#       execJmeter.sh -w /home/workstation/pyCharmWorkSpace/pycharm/ds-cogx-api/ds-cogx-api/jmeter/script
#              -j /home/workstation/install/jmeter
#              -v /home/workstation/pyCharmWorkSpace/pycharm/ds-cogx-api/ds-cogx-api/sample/um/cogx_um.csv
#              -x SwaggerBaseService.jmx
#              -t 5
#              -c 5
#              -r 1
#-----------------------------------------------------------------------------------------------------------------------------------
#====================================================================================================================================================
#
#=====================================================================================================================================================


JMETERHOME=/dv/app/ve2/ccp/cogx/phi/gbd/r000/bin/perf/jmeter
CSVFILE=/dv/app/ve2/ccp/cogx/phi/gbd/r000/bin/perf/jmeterWorkSpace/cogx_um.csv
WORKDIR=/dv/app/ve2/ccp/cogx/phi/gbd/r000/bin/perf/jmeterWorkSpace/
JMXFILE=CognitiveClaimPost_processUM.jmx
REPORT=local_report

JTREADS=3
JCOUNT=3
JRAMPUP=1

while getopts ":w:j:v:x:t:c:r:p:h" opt; do
    case $opt in
        h|\?)
            echo "-----------------------------------------------------------------------------------------------------------------------------------"
            echo "       execJmeter.sh "
            echo "             -w <workdirecory:JMX file home> "
            echo "             -j <Jmeter installation home> "
            echo "             -v <csv file location and file name> "
            echo "             -x <jmx file name>"
            echo "             -t <Jmeter Threads number> "
            echo "             -c <Jmeter request count> "
            echo "             -r <Jmeter rampup time>"
            echo "             -p <Jmeter reporting folder>"
            echo "       Please follow the following exmple: "
            echo "       execJmeter.sh -w /home/workstation/pyCharmWorkSpace/pycharm/ds-cogx-api/ds-cogx-api/jmeter/script"
            echo "              -j /home/workstation/install/jmeter"
            echo "              -v /home/workstation/pyCharmWorkSpace/pycharm/ds-cogx-api/ds-cogx-api/sample/um/cogx_um.csv"
            echo "              -x SwaggerBaseService.jmx"
            echo "              -t 5 "
            echo "              -c 5 "
            echo "              -r 1"
            echo "              -p report"
            echo "-----------------------------------------------------------------------------------------------------------------------------------"
            exit 1;;
        :)
            echo "-----------------------------------------------------------------------------------------------------------------------------------"
            echo "       execJmeter.sh "
            echo "             -w <workdirecory:JMX file home> "
            echo "             -j <Jmeter installation home> "
            echo "             -v <csv file location and file name> "
            echo "             -x <jmx file name>"
            echo "             -t <Jmeter Threads number> "
            echo "             -c <Jmeter request count> "
            echo "             -r <Jmeter rampup time>"
            echo "             -p <Jmeter reporting folder>"
            echo "       Please follow the following exmple: "
            echo "       execJmeter.sh -w /home/workstation/pyCharmWorkSpace/pycharm/ds-cogx-api/ds-cogx-api/jmeter/script"
            echo "              -j /home/workstation/install/jmeter"
            echo "              -v /home/workstation/pyCharmWorkSpace/pycharm/ds-cogx-api/ds-cogx-api/sample/um/cogx_um.csv"
            echo "              -x SwaggerBaseService.jmx"
            echo "              -t 5 "
            echo "              -c 5 "
            echo "              -r 1"
            echo "              -p report"
            echo "-----------------------------------------------------------------------------------------------------------------------------------"
            exit 1;;

        w) WORKDIR=$OPTARG;;
        j) JMETERHOME=$OPTARG;;
        v) CSVFILE=$OPTARG;;
        x) JMXFILE=$OPTARG;;
        t) JTREADS=$OPTARG;;
        c) JCOUNT=$OPTARG;;
        r) JRAMPUP=$OPTARG;;
        p) REPORT=$OPTARG;;
    esac
done

echo "========================= Jmeter Load test for ${JMXFILE} Started =========================="


#mkdir -p ${WORKDIR}/archive

CURRENT_PATH=$(pwd)
NOW=$(date +"%Y%m%d_%H%M%S")


echo "Working Directory is: ${WORKDIR}"
echo "Jmeter Home is: ${JMETERHOME}"
echo "CSV Input file is: ${CSVFILE}"
echo "JMX file is: ${JMXFILE}"
echo "Current Path is: ${CURRENT_PATH}"
echo "Current Date Time is: ${NOW}"
echo "Report Folder is: ${REPORT}"

echo "JMeter Threads is: ${JTREADS}"
echo "JMeter Request Count is: ${JCOUNT}"
echo "JMeter Rampup time is: ${JRAMPUP}"

echo "----------------------------------------------------------------------------------------------------"
# Create archive directory if it does not exist


mkdir -p ${WORKDIR}/${REPORT}

echo "${JMETERHOME}/bin/jmeter -n  -t ${WORKDIR}/${JMXFILE} -Jthreads=${JTREADS} -Jcount=${JCOUNT} -Jrampup=${JRAMPUP} -Jcsvfile=${CSVFILE}  -l ${WORKDIR}/${REPORT}/report_${NOW}.csv -e -o ${WORKDIR}/${REPORT}/report_${NOW} "

# original
#/home/yuntao/workstation/install/jmeter/bin/jmeter -n  -t ${WORKDIR}/SwaggerBaseService.jmx -Jthreads=3 -Jcount=3 -Jrampup=3 -l ${WORKDIR}/${REPORT}/report_${NOW}.csv -e -o ${WORKDIR}/${REPORT}/report_${NOW}

# working one
${JMETERHOME}/bin/jmeter -n  -t ${WORKDIR}/${JMXFILE} -Jthreads=${JTREADS} -Jcount=${JCOUNT} -Jrampup=${JRAMPUP} -Jcsvfile=${CSVFILE}  -l ${WORKDIR}/${REPORT}/report_${NOW}.csv -e -o ${WORKDIR}/${REPORT}/report_${NOW}


# move reports to archive folder
#cd /home/yuntao
#echo $(pwd)




echo "========================= Jmeter Load test for ${JMXFILE} Completed =========================="


#====================================================================================================================================================
##  End of Script
#====================================================================================================================================================

