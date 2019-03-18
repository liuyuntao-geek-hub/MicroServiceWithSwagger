create a conda environment by issuing the following command
conda env create -f env/environment.yml (this assumes the project is cloned and you are in the root directory for the project)

Activate the created environment by 
source activate cognitive-claims (cognitive-claims is the name of conda environment created by the yml file)

mkdir logs
edit config/logging.conf to point the configuration to your logs directory

Single thread flask application can be started by using the following command
python src/app.py

UI for testing the API can be accessed at 
http://hostname:9080/apidocs

The project structure has sample folder which has the required inputs for testing the applciation.
For UM test cases, follow below mentioned steps
    a) sample/um has 3 files 
        i) claim_line.txt
        ii) UM_auth.txt
        iii) formatted_input.txt  - This file is generated from claim_line.txt (command to generate the test file is python test/prep_data_um.py)
    b) UM also has reference data and for release 1 this reference data is being stored in sqlite. To generate the sqlite file follow below commands
        i) mkdir data (This folder should not be committed to bitbucket, this is at the same level as src/ folder)
        ii) cd data
        iii) sqlite3 cogx.db
        iv) .mode csv 
        v) .import ../sample/um/UM_auth.txt um
        vi) .schema um
        vii) In case you want to drop the table and reimport data - drop table if exists um;
        viii) .exit
    c) To generate sample requests for testing um (after activating your conda environment)
        i) python test/prep_data_ltr.py



For building docker image
docker build -t cognitive_claims -f build/Dockerfile .

For running docker container
docker run -it -p 9080:9080 cognitive_claims
or
docker run -itd -p 9080:9080 cognitive_claims

Still need to figure out how to send the log files to splunk!!