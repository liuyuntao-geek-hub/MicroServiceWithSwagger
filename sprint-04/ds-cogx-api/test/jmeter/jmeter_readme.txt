Step 1 - run:
    pythone test/prep_data_um_under_jemeter.py

Running against local
 cd /dv/app/ve2/ccp/cogx/phi/gbd/r000/bin/perf/jmeterWorkSpace/report
C:\java\jmeter\apache-jmeter-5.1.1\bin\jmeter

 cd C:\java\git\repos\cognitiveclaim_dev\sprint-02\ds-cogx-api\test\jmeter
./execJmeter.sh -w C:\java\git\repos\cognitiveclaim_dev\sprint-02\ds-cogx-api\test\jmeter -j C:\java\jmeter\apache-jmeter-5.1.1\bin\jmeter  -v cogx_um_ltr.csv  -x CognitiveClaimPost_processUM.jmx  -t 5 -c 5 -r 1 -p local_report

C:\java\jmeter\apache-jmeter-5.1.1\bin\jmeter -n  -t CognitiveClaimPost_processUM.jmx -Jthreads=5 -Jcount=5 -Jrampup=1 -Jcsvfile=cogx_um_ltr.csv  -l local_report/report.csv -e -o local_report


./execJmeter.sh -c 200 -r 1 -t 10 -p uat_report -v cogx_um_ltr_uat.csv


./execJmeter.sh -c 2 -r 1 -t 3 -p uat_report -v cogx_um_ltr_uat.csv
./execJmeter.sh -c 2 -r 1 -t 3 -p local_report -v cogx_um_ltr.csv
./execJmeter.sh -c 200 -r 10 -t 20 -p local_report -v cogx_um.csv
    - This run the default

 cd /dv/app/ve2/ccp/cogx/phi/gbd/r000/bin/perf/jmeterWorkSpace/report
./execJmeter.sh -c 2 -r 1 -t 3 -p uat_report -v cogx_um_uat.csv
./execJmeter.sh -c 200 -r 10 -t 20 -p uat_report -v cogx_um_uat.csv
    - This run the default
