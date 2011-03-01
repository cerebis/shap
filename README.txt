Simple High-throughput Annotation Pipeline (SHAP)

Installation Notes

Pre-requisites
==============

Building
	- Java SDK 1.6 (developed with 1.6.0_17)
	- Git (developed with 1.7.3)
	- Maven build manager (developed with 2.0.4)

Runtime
	- Java SDK 1.6 (tested with 1.6.0_17)
	- PostgreSQL (tested with 8.3.6)
	- Tomcat (tested with 6.0.10)

Discussion
==========

The SHAP codebase is comprised of a web application for accessing analysis results and a server-side command-line based analysis system. The web application is deployed to a Servlet container such as Tomcat. The analysis system is invoked, as you would expect, from the command-line.

The system is built via the Maven build manager. Maven fetches dependent libraries from remote repositories and eliminates bundling them as part of the SHAP project. Therefore if building from source, you will need to have a working installation of Maven on your system. The first time the system is built and depending on your local repository, Maven may need to fetch many dependent libraries. 

Once completed, you will find the deployable WAR file in the "target" folder. This WAR file contains both the web application and the server-side system. Currently, the two modes of  operation have not been made into separate codebases.

The WAR file can be extracted to your filesystem and treated as the executable installation of the server-side analysis system.

Only the web application makes use of user accounts. The server-side analysis is accessible to whomever has permission to run the elements of the system. Attention should be paid to who has access or data loss could occur.

Source distribution
===================

Building from source

1) Obtain the source tree from SourceForge

	git clone git://git.code.sf.net/p/shap/git shap

2) Go into the SHAP folder

	cd shap
	
3) We need to add a few missing dependencies to your local .m2 maven repository. The following command will install BioJava v3, Apache CLI v2 and the Sun DRMAA libraries.

	bin/prep_repo.sh

4) Now launch the maven build of SHAP. As four additional remote repositories are required to satisfy all the dependencies, the fallback process within Maven delays things considerably. This can take more than 10 minutes if you do not have any of the dependent libraries.

	mvn install
	
5) Once completed, there will be a directory "target/" which contains the built WAR file.

	shap-{version}.war
	
This can be deployed to an application server such as Tomcat for web access to analysis results.

6) The server-side analysis tools are contained in the WAR file. Extract this file within the SHAP folder as follows:

	unzip -q target/shap-{version}.war -d war
	
You should now have a folder "war/" containing the contents of the WAR file. All the helper scripts in "bin/" have been written assuming this path.

Binary distribution
===================

1) Untar the binary distribution 

	tar xzf shap-{version}.tar.gz

2) Go into the SHAP folder

	cd shap

Follow the steps in the source distribution, starting from step 5.

Database setup
==============

SHAP uses a relational database to store its analysis results. Development has been using PostgreSQL but any other database which transactional support could be supported. There are inevitably minute details with respect to some storage types which could pop up. Please report any problems to the developer.

Note. The system account used to invoke these commands will need superuser authority in PostgreSQL. This may be most easily accomplished using the "postgres" user.

1) Create a database user with full privileges to the shap database.

	createuser -PE shap-user

The predefined password for this user is simply "shap01". If a stronger password is used here remember to update the shap.properties file. You will need to remember this password for step 3. To improve data security PostgreSQL client authentication (pg_hba.conf) can be used. In addition, the web application for SHAP needs only write access to the Users and UserRoles tables. A further step could be the definition of a second PostgreSQL user with read-only (select) access to all other tables.

2) Create a database for SHAP.

	createdb -O shap-user shap

3) Using a superuser and psql modify the privileges on the shap database.

	psql> grant all on shap to shap-user;

A second limited user could be defined for the web application. Please remember that you will need to update the application post deployment.

	psql> grant select on shap to shap-readonly;
	psql> grant select, insert, delete, update on Users, UserRoles to shap-readonly.

4) In the extracted shap folder from the earlier source or binary installation section, carry over any changes you made to the user in the shap.properties file.

The line should read:

	database.username=shap-user
	database.password={chosen password}

For application servers, if you changed the password, user or database name you will need to update this file post deployment.
	
Setup Analyzers
===============

A tool has been written to help configure an initial set of analyzers. Since these definitions are highly system dependent, it is expected that users of SHAP will want to make modifications before running the tool. Re-running this tool will not delete previous definitions and since analyzer names are unique, you will need to manually delete rows from the Analyzers table if you wish to use the same names.

The XML file defining analyzers can be found at:

	war/WEB-INF/spring/analyzer-config.xml

This file follows the Spring bean definition schema. A few detectors and annotators have been defined. All defined analyzers mentioned in the the "configuration" bean will be created.

Once you are ready, run the tool

	bin/configSetup.sh

A more user-friendly approach to analyzer definition is planned. This is an obvious need now that SHAP has been released to the public.

Note. Analyzer working temporary directories must be read/write accessible to all machines which will participate in analysis. On grid systems this must be a shared directory.

