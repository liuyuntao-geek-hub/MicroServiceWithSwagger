### Create a conda environment by issuing the following command

conda env create -f env/environment.yml (this assumes the project is cloned and you are in the root directory for the project)

### Activate the created environment by 

source activate cognitive-claims (cognitive-claims is the name of conda environment created by the yml file)

### From terminal where the git code is cloned issue the following commands

```
mkdir logs
mkdir data (This folder should not be committed to bitbucket, this is at the same level as src/ folder)
export COGX_LOGS={git_directory}/logs/log.out  # Replace {git_directory} with directory strucute on your machine
```
  - edit config/logging.conf to point the configuration to your logs directory
  - This has been enhanced to use environment variable COGX_LOGS. Setup differs between windows and mac


UI for testing the API can be accessed at 

http://{hostname}:9080/apidocs

The project structure has sample folder which has the required inputs for testing the applciation.

## Setting up UM model
For UM test cases, follow below mentioned steps
1. sample/um has 3 files 
    - claim_line.txt
    - UM_auth.txt
    - formatted_input.generated  - This file is generated from claim_line.txt (command to generate the test file is python test/prep_data_um.py)
2. UM also has reference data and for release 1 this reference data is being stored in sqlite. To generate the sqlite file follow below commands
    ```
    - cd data 
    - sqlite3 cogx.db
    - .mode csv 
    - .import ../sample/um/UM_auth.txt um
    - .schema um
    - CREATE INDEX idx_SRC_SBSCRBR_ID ON um (SRC_SBSCRBR_ID);
    - .drop table if exists um;  (In case you want to drop the table and reimport data - )
    - .exit
    ```
3. To generate sample requests for testing um (after activating your conda environment)
    ```
    - python test/prep_data_um.py
    ```
4. To generate uber requests for testing um (after activating your conda environemnt)
    ```
    - python test/prep_data_um_uber.py
    ```
    - The above command will generate a file formatted_uber.generated
    - Each line from the file is a requested that can be used to test UM model using Swagger UI 
    - Do not rename or try to add the generated files to bitbucket


# Setting up DUP model
Dup model is part of release 3. The instructions below assume that the project structure is laid out as part of release 1 and release 2.

Start off by doing a git pull and git checkout feature/sprint-03


## Setup
1. Dup has reference data which is loaded into sqlite. To generate the sqlite file follow below commands
    ```
    - python test/prep_data_dup.py
    - python test/prep_data_dup_uber.py
    - cd data
    - sqlite3 cogx_dup.db
    - .mode csv
    - .import ../sample/dup/history_detail.txt hist_dtl
    - CREATE INDEX idx_lookup_id on hist_dtl (KEY_CHK_DCN_NBR, KEY_CHK_DCN_ITEM_CD, CLM_CMPLTN_DT);
    - .import ../sample/dup/history_header.txt hist_hdr
    - CREATE INDEX idx_dup_claims on hist_hdr (MEMBER_SSN, PAT_MBR_CD, SRVC_FROM_DT, SRVC_THRU_DT);
    - .exit
    ```
2. The above command will generate a file formatted_uber.generated
   
    Each line from the file is a requested that can be used to test UM model using Swagger UI 
    
    Do not rename or try to add the generated files to bitbucket


# Start flask application 
Application can be started by using the following command (this assume conda environment is activated)

python src/app.py


For building docker image
docker build -t cognitive_claims -f build/Dockerfile .

For running docker container
docker run -it -p 9080:9080 cognitive_claims
or
docker run -itd -p 9080:9080 cognitive_claims

Still need to figure out how to send the log files to splunk!!