Simple High-throughput Annotation Pipeline (SHAP)
=================================================

Installation Notes
------------------

Pre-requisites
--------------

Building
	- Java SDK 1.6 (developed with 1.6.0_17)
	- Git (developed with 1.7.3)
	- Maven build manager (developed with 2.0.4)

Runtime
	- Java SDK 1.6 (tested with 1.6.0_17)
	- PostgreSQL (tested with 8.3.6)
	- Tomcat (tested with 6.0.10)

Discussion
----------

The SHAP codebase is comprised of a web application for accessing analysis results and a server-side command-line based analysis system. The web application is deployed to a Servlet container such as Tomcat. The analysis system is invoked, as you would expect, from the command-line.

The system is built using the Maven build manager. Maven resolves dependencies using remote repositories and eliminates the need to bundle supporting libraries as part of the SHAP project. Therefore if building from source, you will need to have a working installation of Maven on your system. The first time the system is built and depending on your local repository, Maven may need to fetch many dependent libraries. 

Once completed, you will find the deployable WAR file in the "target" folder. This WAR file contains both the web application and the server-side system. Currently, the two modes of operation have not been made into separate codebases.

The WAR file can be extracted to your filesystem and treated as the executable installation of the server-side analysis system.

Only the web application makes use of user accounts. The server-side analysis is accessible to whoever has permission to run the elements of the system. Attention should be paid to who has access or data loss could occur.

Differences between Unix platforms
----------------------------------

Our development and production environments run on CentOS 5. CentOS is a redistribution of Redhat Enterprise Linux (RHEL). You may come across conflicting environmental details that we have overlooked. The common issues are package management differences, application paths and some types of system commands.

**Switching users**

In Redhat based distributions, switching users is accomplished by the command "su". The command "sudo" is not enabled by default. To invoke commands as another user, switch to that user with "su" and then continue your work, remembering to logout when finished.

	su postgres
	...
	logout

In Debian based distributions, sudo is typically enabled by default and commands can be put inline

	sudo postgres [your-command-here]


In Mac OSX "sudo" is also the choice.

**Application paths**

PostgreSQL base location

Redhat

	/var/lib/pgsql


Debian

	/etc/postgresql/{version}/
	

Source distribution
-------------------

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

Binary Distribution

1) Untar the binary distribution 

	tar xzf shap-{version}.tar.gz

2) Go into the SHAP folder

	cd shap

Follow the steps in the source distribution, starting from step 5.

Virtual Appliance (Amazon EC2 AMI)
----------------------------------

To aid potential users in trailing the application, we have made a virtual appliance available, in the form of an Amazon Machine Image (AMI). This publicly available AMI can be found in the AWS EC2 Community section by the name shap-{version}, where {version} is replaced by the release version number.

Currently, we only provide an AMI for the latest release, version 1.1.0. Therefore you should filter the long list of community AMIs with "shap-1.1.0".

1) Log into or create an account on the Amazon Web Service.
2) Once in the system, select the "EC2" tab and click "Launch Instance"
3) Select the "Community AMIs" tab and filter the list for "shap-1.1.0"
4) Click "Select" for the appropriate AMIl=,
5) Configure your instance, as per the Amazon documentation. You can use a micro instance to be eligible for the free tier.
6) Once the instance is created and running, you can log in. The ec2-user has a pre-configured installation of SHAP available in its home directory. The web interface should already be up and running with example data.


Database Setup
--------------

SHAP uses a relational database to store its analysis results. Development has been using PostgreSQL, other SQL compliant databases with concurrent transactional support are a possibility. Inevitable minute details with respect to storage types and SQL implementation means, that for now, this is not offered out of the box and will not be discussed here.

**Note** The system account used to invoke these commands will require superuser authority in PostgreSQL. This may be most easily accomplished using the "postgres" user (distribution dependent, see above).

1) Create a database user with full privileges to the SHAP database.

	createuser -SDRPE shapuser

The predefined password for this user is simply "shap01". If a stronger password is used here remember to update the shap.properties file. You will need to remember this password for step 3. To improve data security PostgreSQL client authentication (pg_hba.conf) can be used. In addition, the web application for SHAP needs only write access to the Users and UserRoles tables, therefore a second PostgreSQL user with read-only (select) access to all other tables could be employed. The analysis pipeline however will still require full access to all tables, so a single restricted user is not an option.

2) Create a database for SHAP.

	createdb -O shapuser shap

3) In the extracted SHAP folder from the earlier source or binary installation section, carry over any changes you made to the user in the shap.properties file (war/WEB-INF/classes/shap.properties).

The line should read:

	database.username=shapuser
	database.password={chosen password}

For application servers, if you changed the password, user or database name you will need to update this file post deployment.

4) Make sure the PostgreSQL server has been configured to listen for TCP connections. SHAP connects to the database server by TCP, whether it is hosted on the same system or not. Without this feature being enabled, any attempt by SHAP to connect to the database will fail.

For Redhat based distributions

	/var/lib/pgsql/data/postgresql.conf

On Debian based distributions, this becomes

	/etc/postgresql/{version}/main/postgresql.conf

Uncomment the "listen_address" line. If the web application, annotation pipeline all reside on the same physical server, you need only listen to the localhost IP address.

	listen_address = 'localhost'

5) PostgreSQL provides fine-grained control of client authentication. The SHAP user needs permission to authenticate by password which is commonly not part of PostgreSQLs default configuration. Client authentication is defined in the filet:

On Redhat based distributions

	/var/lib/pgsql/data/pg_hba.conf

On Debian based distributions, this becomes

	/etc/postgresql/{version}/main/pg_hba.conf

The order of rules is important. The more explicit the rule, the earlier it should come. It is recommended to place the SHAP rules before the default rules. Add the following lines to permit file and TCP/IP socket connections to the SHAP DB with password authentication from the localhost.

	local shap shapuser md5
	host  shap shapuser 127.0.0.1/32 md5

6) For the changes to take effect, PostgreSQL will need to be restarted.

On systems with sysvconfig, with root authority invoke the following command.

	service postgresql restart

7) On first invocation, SHAP will automatically create its table structure.
	
Setup Analyzers
---------------

A tool has been written to help configure an initial set of analyzers. Since these definitions are highly system dependent, it is expected that users of SHAP will want to make modifications before running the tool. Analyzer names must be unique, with multiple invocations you may run into naming conflicts. To aid in experimenting with an initial setup, you have to option to purge the previous database. **WARNING** This will purge all data from the database, not just analyser configurations -- use it wisely.

An example configuration file can be found at:

	helpers/analyzer-config.xml

This file follows the Spring bean definition schema. A few detectors and annotators have been defined. All defined analyzers mentioned in the "configuration" bean will be created.

Once you are ready, run the tool

	bin/configSetup.sh <analyzer XML file>

A more user-friendly approach to analyzer definition is planned. This is an obvious need now that SHAP has been released to the public.

**Note** The scratch path must be read/write accessible to all machines which will participate in analysis and on clustered systems this must be a shared location.

Analyzer Supporting Tools
-------------------------

Helper scripts have been written for: Aragorn and Metagene. The scripts simply wrap each tool to standardise the command-line interfaces and convert output to XML format.

A source code patch has been created for HMMER release version 2.3.2. This patch adds the option for XML output to Hmmpfam. It also includes an alternate makefile (src/Makefile.local) for the Intel compiler. In our testing, ICC generated executables are 2-fold faster than those produced by GNU GCC v4.

To patch HMMER 2.3.2

	cd to the HMMER-2.3.2 directory
	patch -p1 < {path-to-patch}/hmmer-2.3.2-patch.txt
	make clean && make install

Note. All underlying tools, whether called directly or as a wrapper script must be accessible from any machine that intends to execute analysis jobs. Nothing stops users of SHAP from defining multiple analysers which reflect differing runtime environments, such as a local and grid definition.

Configuration of execution environment
---------------------------------------

The system architecture on which the server-side analysis pipeline will be run dictates the configuration of SHAP's task execution. SHAP is capable processing work either as regular local processes or by submission to a GRID system.   Configuration details are found in the two files:

	war/WEB-INF/classes/shap.properties
	war/WEB-INF/spring/delegate-context.xml

The shap.properties file contains more commonly changed details, such as the number of concurrent and queued tasks, the working directory location and if using GRID processing, the general details required to submit jobs to an appropriate queue. 

	analysis.executor.threads=20
	analysis.jobdaemon.maxqueued=15
	analysis.workdir=/tmp
	analysis.sge.specification=-o /dev/null -e /dev/null -w e -p 0 -b yes -V -shell yes -l hp=TRUE

The delegate-context.xml file controls whether local (org.mzd.shap.exec.LocalDelegate) or GRID (org.mzd.shap.exec.GridDelegate) execution will be used job processing.

By default, SHAP has been configured to use local processes and the parallelism is limited a single process. Analysis throughput generally increases for larger values, assuming the local environment has a sufficient parallel CPU resources. On GRID systems, the maximum number of concurrent tasks is often dictated by queue constraints rather than absolute cluster size. SHAP will only queue as many jobs as is defined by analysis.jobdaemon.maxqueued. With GRID processing, it is recommended that analysis.jobdaemon.maxqueued to be set larger than analysis.executor.threads.

SHAP Server-side Commands
-------------------------

Import Data (importData.sh)
---------------------------

This tool is used for the creation of user-defined objects and importation of biological data. User-defined objects include Project and Sample containers, and importation of biological data from FASTA and XML formatted source files, as well as coverage information if applicable.

Importable data types
- DNA sequence in multi-fasta format. 
- Average read-depth or coverage information.
- Sequence objects in XML
- Feature objects in XML

**User-defined**

Creating a project or sample is straight-forward. The name of a project must be unique across the entire system. Names with spaces should be enclosed in double-quotes. A description is mandatory. 

A new project "myproject" would be created as follows:

	bin/importData.sh --add-project --project myproject --description "my first project ever!"

A sample can be added to an existing project, referring to the project by the chosen name. Sample names must be unique per project. A description is mandatory. 

A sample would be created in an existing project "myproject" as follows:

	bin/importData.sh --add-sample --project myproject --sample sampleA --description "Assembled forest soil A"

**Multi-fasta**

Multi-fasta DNA can be imported into existing Sample objects, sequence names must be unique per sample. 

Fasta sequence would be imported to "myproject, sampleA" as follows:

	bin/importData.sh --import-sequence forest-contigs.fna --project myproject --sample sampleA

**Coverage**

Coverage data can be used to infer cellular abundance and we find it convenient to include, though not mandatory. Coverage data can only be imported into pre-existing Sequence objects. The file format is simply a tab delimited text file of sequence name and coverage value. Only one sample can be referred to per importation.

Example file format

	----BEGIN----
	seq01	1.2
	seq02	20.3
	----END----

Coverage data is imported to an existing project and sample "myproject, sampleA" as follows:

	bin/importData.sh --set-coverage forest-coverage.csv --project myproject --sample sampleA

**XML objects**

Sequence and Feature XML objects permit the importation of externally annotated data. Below are example definitions following our XML schema. The schema are simply serialised versions of the application objects org.mzd.shap.domain.Sequence and org.mzd.shap.domain.Feature. Multiple objects can be imported at once, though the two object types cannot by mixed in a single importation step.

A Sequence object defined in XML

~~~~~~
<sequence coverage="1.1" desc="test-description" name="myseq" taxonomy="UNCLASSIFIED">
  <data>acgt</data>
  <feature partial="false" type="Undefined" conf="2.2">
    <location start="1" end="12" strand="Forward" frame="1"/>
    <data>MAR*</data>
  </feature>
  <feature partial="false" type="OpenReadingFrame">
    <location start="1" end="12" strand="Forward" frame="1"/>
    <data>MAR*</data>
  </feature>
</sequence>
~~~~~~

A feature object defined in XML

~~~~~~
<feature partial="false" type="Undefined" conf="2.2">
  <location start="1" end="12" strand="Forward" frame="1"/>
  <data>MAR*</data>
</feature>
~~~~~~

A sequence defined in valid XML as above would be imported to an existing project and sample "myproject, sampleB" as follows:

	bin/importData.sh --import-sequence-xml external-annotation.xml --project myproject --sample sampleB

Export Data (exportData.sh)
---------------------------

After analysis has been completed, results and imported data can be exported from the system in various formats. 

References to result objects (eg. a sample) must be unique. The most succinct reference type is by an object's globally unique database identifier. This value is assigned by the database system at creation or importation time. To use this form of reference, users may need to refer to the web application to learn what value was assigned to a given object.

Alternatively, objects can be referred to by name. In doing so, the complication arises that users must still uniquely define a single object. All project names are unique, however sample names are only unique per project and sequence names only unique per sample. This gives users flexibility in defining their data objects, but means that to uniquely reference a sequence, users will be required to specify its containing sample and project. 

The tool can export the following file formats.

**Multi-fasta**

Samples, sequences and features can all be exported in FASTA format. DNA both as source scaffolds and gene sequences and protein translations of coding genes.

**Genbank**

Genbank exports are defined per annotated Sequence. All sequences in a samples may be exported at once, though the result will be a multi-genbank file.

**Annotation tables**

CVS tables of annotation results per Sample, Sequence or Feature.

Assuming the following data definitions exist,

	Project
		-id: 1
		-name: project01

	Sample
		-id: 2
		-name: sampleA
		-project: project01

	Sequence
		-id: 3
		-name: gene1
		-sample: sampleA

	Sequence
		-id: 4
		-name: gene2
		-sample: sampleA

A by-name reference is formed by concatenating each associated name together with comma delimiters. 

Eg. For sample "sampleA" in project "project01":

	project01,sampleA

Or for sequence "gene2" in sample "sampleA":

	project01,sampleA,gene2

**Invocation examples**

Extract FASTA sequence for all sequences in sampleA, using the sample's database ID.

	bin/exportData.sh --sample-id --list 2 --output-file sample-seq.fna --fasta

Extract the same data, by name reference.

	bin/exportData.sh --by-name --list project01,sampleA --output-file sample-seq.fna --fasta

JobControl (jobControl.sh)
--------------------------

Jobs for analysis are submitted and restarted with this tool. It acts as both the submission agent and processing daemon. Though submission is not tightly coupled to processing, we presently do not run the daemon as a separate detached entity.

Job submission

Job submission is done by defining a desired analysis plan in XML. The plan is then submitted for processing as follows:

	jobControl.sh --submit myplan.xml

A plan contains one or more targets and one or more steps. Steps must act on objects existing at submission time. This limitation means a single plan cannot both define a detection process and subsequent annotation on its results.

Targets can be a Sample, Sequence of Feature. Samples are referenced by project and sample name, Sequences and Features are referenced by database identifier.

An example annotation plan which would annotate all sequences in test-sample with two annotators.

~~~~~~
<plan id="annotation-plan-example">
	<targets>
		<target project-name="test-project" sample-name="test-sample"/>
	</targets>
	<steps>
		<step id="annotation" type="ANNOTATION">
			<analyzer>kegg-pep</analyzer>
			<analyzer>refseq</analyzer>
		</step>
	</steps>
</plan>
~~~~~~

Users can find two example plans in the plans folder. One demonstrates a simple plan for running a detection job, the other a simple annotation job.

Job processing can be effectively placed in the background with the standard Linux command nohup.

	nohup jobControl.sh --submit myplan.xml &

Where now console output will be written to the file "nohup.out", progress can be followed with

	tail -f nohup.out

Interrupting processing

Interrupting task processing is as simple as signalling the running jobControl process. When sent a regular SIGINT (ctrl-c), the daemon will cease to queue new tasks and wait for existing tasks to complete before exiting. Depending on the level of concurrency and queue size, shutdown may take more than a few minutes. If sent a more immediate signal, tasks may be left in an intermediate state.

UserControl (userControl.sh)
----------------------------

Currently, user accounts only apply to web access to annotation results. Any account with administration privileges is able to create/remove/modify other users through the web UI. This tool is provided as a means of creating accounts from the command-line and can be useful if the administration account has been accidentally lost.

Lucene Indexing (index.sh)
--------------------------

The web application which accompanies the pipeline utilises the Apache Lucene search engine. Lucene operates on an document index which must be created. For SHAP the index is currently created by invoking a mass-indexing of the annotation database, which is stored on the filesystem.

This filesystem backed index must by accessible by both the command-line indexing command and the web application. A web application's privileges and capacity to access system resources are dependent on the chosen application server. For Tomcat6, we use the path /opt/shap/lucene for the shared index location. The setting can be modified in shap.properties:

	lucene.index=/opt/shap/lucene

Issue the following to set up this directory for shared access:

	mkdir -p /opt/shap/lucene 
	chgrp tomcat /opt/shap/lucene
	chmod g+srwx,u+rwx /opt/shap/lucene
	
Note: it is recommended that no annotation be performed while indexing is occurring.
