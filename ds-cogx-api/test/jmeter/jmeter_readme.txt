Step 1 - run:
    pythone test/prep_data_um_under_jemeter.py

Running against local
 cd /dv/app/ve2/ccp/cogx/phi/gbd/r000/bin/perf/jmeterWorkSpace/report
./execJmeter.sh -c 2 -r 1 -t 3 -p local_report -v cogx_um.csv
./execJmeter.sh -c 200 -r 10 -t 20 -p local_report -v cogx_um.csv
    - This run the default

 cd /dv/app/ve2/ccp/cogx/phi/gbd/r000/bin/perf/jmeterWorkSpace/report
./execJmeter.sh -c 2 -r 1 -t 3 -p uat_report -v cogx_um_uat.csv
./execJmeter.sh -c 200 -r 10 -t 20 -p uat_report -v cogx_um_uat.csv
    - This run the default
