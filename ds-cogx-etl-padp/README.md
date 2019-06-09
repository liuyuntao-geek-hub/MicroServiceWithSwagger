
================================================================================================
**************** Developer Note for Download and deployment the code ************************
================================================================================================

Step 1 - Download the code with git clone to a directory on local

------------------
git clone https://AFxxxxx@bitbucket.anthem.com/scm/padp/ds-cogx-etl-padp.git
git checkout xxxxx

--------------------

Step 2 - Open ScalaIDE and import maven project
	- Modify the .project and pom.xml if you need to change the project name when having duplicate project on local
	pom.xml:
	-------------------------
		<artifactId>ds-cogx-padp-etl-sprint03</artifactId>
	-------------------------
	
	.project
	------------------------------
		<name>ds-cogx-padp-etl-Sprint03</name>
	------------------------------
	
Step 3 - Modify pom.xml if want to have your customized build
	pom.xml:
	-------------------------
		<version>2.0.2</version>
	-------------------------
		This will create the jar in target folder as ds-cogx-padp-etl-2.0.2.jar

Step 4 - Update the conf/application_dev.properties and conf/application_local.properties with your own login info
example
--------------------------------------------
credentialproviderurl="jceks://hdfs/dv/hdfsapp/ve2/ccp/cogx/phi/gbd/r000/bin/jcekstore/af35352Cogx.jceks"
username=af35352
password=af35352CogxPass
--------------------------------------------

Step 5 - Teradata UM solution: 
	com.anthem.cogx.etl.TeradataUM
	Instruction is located inside the above package as cogx_teradataUM_readme.txt
	
------------------------------------------------




