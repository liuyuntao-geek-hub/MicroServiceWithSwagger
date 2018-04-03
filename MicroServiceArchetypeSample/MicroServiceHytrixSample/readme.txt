
==========================================================
Use the Anthem Maven Archetype to create MicroService:
==========================================================
Step 1 - The Maven Archetype is available here:

https://sit-interlock.anthem.com/docker-catalog/catalog.xml
- Save to: C:\java\git\repos\geekhub\MicroServiceWithSwagger\catalog.xml
	- Must remove leading space

Step 2 - Create Maven Project: 
C:\java\git\repos\geekhub\MicroServiceWithSwagger\MicroServiceArchetypeSample

Add local Archetype catalog: C:\java\git\repos\geekhub\MicroServiceWithSwagger\MicroServiceArchetypeSample\Catalog.xml

Step 3 - Select: docker-prototype-ms-hystrix
Group Id: MicroServiceAnthemArchetypeSample
Artifact id: MicroServiceHytrixSample
Package: com.anthem.smartpcp.Sample

Step 4 - Errors and Issues:

https://va10pwprpo001.us.ad.wellpoint.com/artifactory/maven-releases
Add SSL Cert for the above link
- Chrome: -> security -> certificate validate -> export to file 
	C:\java\installation\certificate\MSArchetype.cer
	
cd C:\java\jdk8\jre\lib\security
keytool -import -trustcacerts -alias MSArchetypeSMARTCert -file C:\java\jdk8\jre\lib\security\MSArchetype.cer -keystore cacerts
	password: changeit

keytool -import -trustcacerts -alias MSClouderaCert -file C:\java\jdk8\jre\lib\security\ClouderaRepoCert_v2.cer -keystore cacerts
	password: changeit


Tricks: it might still not working even if you did the right thing: Then, check the Repo and remove the half downloaded files
- C:\java\maven\localrepoNew\com\anthem\emep\dckr\microsvc\docker-prototype-ms-hystrix\15.0


MicroService Docker cheat sheet
https://confluence.anthem.com/pages/viewpage.action?spaceKey=MI&title=Creating+new+services+in+docker
-----------------------------------------
Add the following to the Scala_setting.xml
-----------------------------------------
    <server>
      <id>repo-prod-public</id>
      <username>af35352</username>
      <password>{*****************=}</password>
    </server>
     <server>
      <id>MicroServiceAnthem</id>
      <username>af35352</username>
      <password>*****************}</password>
    </server>


       <mirror> 
            <id>repo-prod-public</id> 
            <mirrorOf>anthem</mirrorOf> 
            <url>https://artifactory.anthem.com:443/artifactory/public</url> 
        </mirror>          
       
      
         <mirror> 
             <id>cloudera</id>
             <mirrorOf>cloudera-releases</mirrorOf> 
             <url>https://repository.cloudera.com/artifactory/cloudera-repos</url> 
         </mirror>
      
          <mirror> 
                  <id>splicemachine</id> 
                  <mirrorOf>splicemachine-public</mirrorOf> 
                  <url>http://repository.splicemachine.com/nexus/content/groups/public</url>
        </mirror>
      
         <mirror> 
                  <id>mavenCentral</id> 
                  <mirrorOf>central</mirrorOf> 
                  <url>http://repo1.maven.org/maven2/</url>
        </mirror>     
 
               <mirror> 
                  <id>MicroServiceAnthem</id> 
                  <mirrorOf>*</mirrorOf> 
                  <url>https://va10pwprpo001.us.ad.wellpoint.com/artifactory/maven-releases</url>
        </mirror>   


---------------------------------------------------------------
Note: <url>https://artifactory.anthem.com:443/artifactory/public</url> 
	= This must be your repo in the pom.xml otherwise it will not work
	
Step 5 - Build the maven Project by right click the pom.xml -> maven test

Step 6 - Add default configure into bootstrap.yml:

defaultReadTimeout: 100
defaultConnectionTimeout: 100
-----------------------------	
server:
 port: 8090
-----------------------------	

	or
Change code to:
-----------------------------	
	@Value("${defaultConnectionTimeout:100}")
    private int connectionTimeout;
	
	@Value("${defaultReadTimeout:100}")
    private int readTimeout;
-----------------------------    
	- This will read from bootstrap.yml

Step 7 - Test run the Hyrix Sample:

Right click 

http://localhost:8080/
http://localhost:8090/

===================================================================