Create a conda environment by issuing the following command
conda env create -f env/environment.yml (this assumes the project is cloned and you are in the root directory for the project)

Activate the created environment by 
source activate cognitive-claims (cognitive-claims is the name of conda environment created by the yml file)

```
mkdir logs
```
  - edit config/logging.conf to point the configuration to your logs directory
  - This has been enhanced to use environment variable COGX_LOGS. Setup differs between windows and mac

Flask application can be started by using the following command (this assume conda environment is activated)
python src/app.py

UI for testing the API can be accessed at 
http://{hostname}:9080/apidocs

The project structure has sample folder which has the required inputs for testing the applciation.
For UM test cases, follow below mentioned steps
1. sample/um has 3 files 
    - claim_line.txt
    - UM_auth.txt
    - formatted_input.generated  - This file is generated from claim_line.txt (command to generate the test file is python test/prep_data_um.py)
2. UM also has reference data and for release 1 this reference data is being stored in sqlite. To generate the sqlite file follow below commands
    ```
    - mkdir data (This folder should not be committed to bitbucket, this is at the same level as src/ folder)
    -  cd data
    - sqlite3 cogx.db
    - .mode csv 
    - .import ../sample/um/UM_auth.txt um
    - .schema um
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


For building docker image
docker build -t cognitive_claims -f build/Dockerfile .

For running docker container
docker run -it -p 9080:9080 cognitive_claims
or
docker run -itd -p 9080:9080 cognitive_claims

Still need to figure out how to send the log files to splunk!!