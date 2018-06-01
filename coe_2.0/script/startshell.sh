#!/usr/bin/bash
# Program:      startshell.sh
# Date:         02/12/2009
# Edited:	    Sasi(Anthem)
# Purpose:      To make it work in linux
# Edited:	    Rajesh Potu
# Purpose:      Reusable UNIX shell script to start a set of shell scripts called
#               thru a list file; which has list of all the shell scripts with complete
#               paths . This script has restart ability and can start from the shell
#               script that has failed .This shell scripts picks the environmental
#               varaibles from .profile of the executing ID and can scripts in the
#               list file have positional parameters.
# Location:     /$PMDIR/ca_scripts
# Author:       Bharat Bhallam
#
# Exampleusage: $ /home/aa66797/startshell.sh <listfilename>
# PARAMTERS : $1= List_File_Name
#             $2= comma seperated numbers/numeric value/range/ALL
#             Ex: startshell.sh script.lst 1,2,3,4,5,6,67,7,8,9,57,34 or
#		  startshell.sh script.lst 1-4/ALL
# Changes
# 01/21/2011  changed the logic to create dotdone files in a common dotdone directory that align with each executing ID.
#
#----------------------------------------------------------------------------------------------
#                       Checking for list file existence
#----------------------------------------------------------------------------------------------

. $HOME/.bash_profile

export DONEFILE=${DONE_LOG}/donefiles
listfile="$1"
echo $listfile

if [ $# -eq 0 ]; then
        eval echo " list: 0403-016 Cannot find or open the file \; Check the parameters. Usage \<list file\> \[\<position of job\(s\)-n\>\] " | mail -s "RED:Parameter Error:$HOST" $EMAIL
   	exit 991
fi

listfile_name=`echo "$1" | awk -F / '{print $NF}'`
if eval test ! -f $listfile; then
	eval echo " Listfile $listfile not found on $HOST.Return Error Code:990 " | mail -s "RED:${listfile} not found:$HOST" $EMAIL
	exit 990
fi
#-----------------------------------------------------------------------------------------------
#          No of passed arguments - one; execute all the shell scripts which are in list file 
#-----------------------------------------------------------------------------------------------
if [ $# -eq 1 ]; then

	var1=1
	while read job
	do		
		job_name=`echo "${job}" | cut -f1 -d" " | gawk -F/ '{ print $NF}'`
                echo $job
                echo $job_name
			if eval test ! -f ${DONEFILE}/${job_name}.${listfile_name}.${var1}.done ; then
				      sh $job
#----------------------------------------------------------------------------------------------
#       Checking status of the shellscript;email shared mail box incase of failure
#----------------------------------------------------------------------------------------------
           			steprc=$?
                		if [ $steprc -ne 0 ]; then
                  		eval echo " The execution of ${job_name} on line number ${var1} failed in the listfile $1. "| mail -s "RED:${job_name}:${listfile_name}:$HOST" $EMAIL
					exit $steprc

#----------------------------------------------------------------------------------------------
#       Creating .done file for all the scripts that executed succesfully
#----------------------------------------------------------------------------------------------
                		else 
					eval touch ${DONEFILE}/${job_name}.${listfile_name}.${var1}.done
                		fi
        		fi
		var1=`expr $var1 + 1`
	done < $1
fi
#------------------------------------------------------------------------------------------------------------------
#        					No of passed arguments- 2
#------------------------------------------------------------------------------------------------------------------
if [ $# -eq 2 ];then

	var3=1
	total_len=`cat $1 | wc -l`
	comma_cnt=`echo "$2" | awk -F , '{print NF}'`
	hypn_cnt=`echo "$2" | awk -F - '{print NF}'`
	first_num=`echo "$2" | awk -F - '{print $1}'`
	secnd_num=`echo "$2" | awk -F - '{print $2}'`
	
	if [ "$secnd_num" = "ALL" ]; then
		secnd_num=$total_len
	fi
	
	if [ $hypn_cnt = 2 ];then
#----------------------------------------------------------------------------------------------------------------------------------
#        Checking the position of the 2nd number(in second augument)with the maxsize of list file, if it greater then send mail and exit from script.
#----------------------------------------------------------------------------------------------------------------------------------
		if [ "$secnd_num" -gt "$total_len" ]; then
		     eval echo "Incorrect parameters passed\; check the second number $secnd_num, the maximum number of scripts available in the list file \- $total_len" | mail -s "RED:Parameter Error:$HOST" $EMAIL
		     exit 991
		fi
#----------------------------------------------------------------------------------------------------------------------------------
#        Checking if 1st number is greater than the 2nd number, abort the shell and send and mail and exit from script.
#----------------------------------------------------------------------------------------------------------------------------------
		if [ "$first_num" -gt "$secnd_num" ]; then
		     eval echo "First number $first_num should be less than second number i.e. $secnd_num" | mail -s "RED:Parameter Error:$HOST" $EMAIL
		     exit 991
		fi
	fi
      
	while true
	do
		if [ $hypn_cnt = 2 ];then
	      	var2=1
			while read job
			do
				job_name=`echo "${job}" | gawk -F/ '{ print $NF}'| cut -f1 -d" "`

				if [ $var2 -lt $first_num ]; then
					eval touch ${DONEFILE}/${job_name}.${listfile_name}.${var2}.done
					var2=`expr $var2 + 1`
				else
					break
				fi
			done < $1
	   		if [ $first_num -le $secnd_num ];then
		 		job=`cat $1 | head -$first_num | tail -1`
				job_name=`cat "$1" | head -$first_num | tail -1 | gawk -F/ '{ print $NF}'| cut -f1 -d" "`
		     	
				if eval test ! -f ${DONEFILE}/${job_name}.${listfile_name}.${first_num}.done ; then
					
					 sh $job
#----------------------------------------------------------------------------------------------
#       Checking status of the passed shellscript;email shared mail box incase of failure
#----------------------------------------------------------------------------------------------
               			steprc=$?

					if [ $steprc -ne 0 ]; then
                  			eval echo " The execution of shell script ${job_name} on line number ${first_num} failed in the listfile "$1"\; Error code ${steprc}." | mail -s "RED:{$job_name}:line number ${first_num}:${listfile_name}:$HOST" $EMAIL
						exit $steprc
					else
					  	eval touch ${DONEFILE}/${job_name}.${listfile_name}.${first_num}.done
					fi
				fi
			
				first_num=`expr $first_num + 1`
                 	else
		    		break
			fi
	    	else
	        	if [ $var3 -le $comma_cnt ];then
		    		line_num=`echo "$2" | awk -F , -v var4=$var3 '{print $var4}'`

		    		if [ $total_len -ge $line_num ];then
					job_name=`cat "$1" | head -$line_num | tail -1 | gawk -F/ '{ print $NF}'| cut -f1 -d" "`

					job=`cat "$1" | head -$line_num | tail -1`
			  		if eval test ! -f ${DONEFILE}/${job_name}.${listfile_name}.${line_num}.done ; then
						 sh $job
#----------------------------------------------------------------------------------------------
#       Checking status of the passed shellscript;email shared mail box incase of failure
#----------------------------------------------------------------------------------------------
               				steprc=$?
							
						if [ $steprc -ne 0 ]; then
                  				eval echo " The execution of script ${job_name} on line number ${line_num} failed in the listfile "$1"\; Error code ${steprc}." | mail -s "RED:${job_name}:line number ${line_num}:${listfile_name}:$HOST" $EMAIL
							exit $steprc
						else
                                                        eval touch ${DONEFILE}/${job_name}.${listfile_name}.${line_num}.done
						fi
			  		fi
			  		var3=`expr $var3 + 1`
		   		else
					eval echo "The passed value $line_num is greater than the list file size $total_len" | mail -s "RED:Parameter Error:$HOST" $EMAIL
					exit 991
				fi
	        	else
		    		break
	    		fi
	    	fi
	done
fi
#----------------------------------------------------------------------------------------------
#     Deleting the .done files after all the scripts in the list file executed succesfully
#----------------------------------------------------------------------------------------------
var5=1
while read job
do
	job_name=`echo "${job}" | gawk -F/ '{ print $NF}'| cut -f1 -d" "`
  	eval rm -f ${DONEFILE}/${job_name}.${listfile_name}.${var5}.done
	steprc=$?
		if [ $steprc -ne 0 ]; then
			eval echo " Unable to delete the ${job_name}.${listfile_name}.${var5}.done.\; Error code $steprc "| mail -s "RED:.done error:$HOST" $EMAIL
			exit $steprc
		fi
	var5=`expr $var5 + 1`
done < $1
