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

Once completed, you will find the deployable WAR file in the "target" folder. This WAR file contains both the web application and the server-side system. Currently, the two modes of  operation have not been made into separate codebases.

The WAR file can be extracted to your filesystem and treated as the executable installation of the server-side analysis system.

Only the web application makes use of user accounts. The server-side analysis is accessible to whomever has permission to run the elements of the system. Attention should be paid to who has access or data loss could occur.

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

Database Setup
--------------

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
---------------

A tool has been written to help configure an initial set of analyzers. Since these definitions are highly system dependent, it is expected that users of SHAP will want to make modifications before running the tool. Re-running this tool will not delete previous definitions and since analyzer names are unique, you will need to manually delete rows from the Analyzers table if you wish to use the same names.

The XML file defining analyzers can be found at:

	war/WEB-INF/spring/analyzer-config.xml

This file follows the Spring bean definition schema. A few detectors and annotators have been defined. All defined analyzers mentioned in the the "configuration" bean will be created.

Once you are ready, run the tool

	bin/configSetup.sh

A more user-friendly approach to analyzer definition is planned. This is an obvious need now that SHAP has been released to the public.

Note. Analyzer working temporary directories must be read/write accessible to all machines which will participate in analysis. On grid systems this must be a shared directory.

Analyzer Supporting Tools
-------------------------

A number of underlying analysis tools used output formats or commandline syntaxes which were awkward. A few of these tools have been wrapped within shell scripts. In some cases the output has also been reformatted to XML. A sourcecode patch has been created for Hmmpfam from the HMMER v2.3.2 release that adds the option for XML output.

Helper scripts exist for Aragorn and Metagene. These modify both the command-line and convert output to an XML schema.

For Hmmpfam a patch must be applied to v2.3.2 to add XML output as an option. This patch also includes a makefile (src/Makefile.local) for the Intel compiler. In our testing, these executables are 2-fold faster than those produced by GNU GCC v4.

	change to HMMER-2.3.2 source package
	patch -p1 < {path-to-patch}/hmmer-2.3.2-patch.txt

Note. All underlying tools, whether called directly or as a wrapper script must be accessible from any machine that intends to execute analysis jobs. Nothing stops users of SHAP from defining multiple analysers which reflect differing runtime environments, such as a local and grid definition.

SHAP Server-side Commands
-------------------------

Import Data (importData.sh)
---------------------------

Data definition and importation, this command permits the definition of Projects, and Samples container objects as well as the importation of various data types.

Importable data types
- DNA sequence in multi-fasta format. 
- Average read-depth or coverage information.
- Sequence objects in XML
- Feature objects in XML

Multi-fasta

Multi-fasta DNA can be imported into existing Sample objects. Sequence names must be unique per Sample.

Coverage

Coverage data can be used to infer cellular abundance and we find it convenient to include. Coverage data can only be imported into pre-existing Sequence objects. The file format is simply a tab delimited text file of sequence name and coverage value. Only one sample can be referred to per importation.

Eg.

seq01	1.2
seq02	20.3

XML objects

Sequence and Feature XML objects permit the importation of externally annotated data. Below are example definitions following our XML schema. The schema are simply serialised versions of the application objects org.mzd.shap.domain.Sequence and org.mzd.shap.domain.Feature. Multiple objects can be imported at once, though the two object types cannot by mixed in a single importation step.

A Sequence object defined in XML

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

A feature object defined in XML

<feature partial="false" type="Undefined" conf="2.2">
  <location start="1" end="12" strand="Forward" frame="1"/>
  <data>MAR*</data>
</feature>

Export Data (exportData.sh)
---------------------------

After analysis has been completed, results and imported data can be exported from the system in various formats.

Exported data formats
- Multi-fasta DNA or Protein sequence (per Sample, Sequence or Feature)
- Genbank (per Sample or Sequence)
- Annotation CSV tables (per Sample or Sequence)

Multi-fasta

Samples, sequences and features can all be exported in FASTA format. DNA both as source scaffolds and gene sequences and protein translations of coding genes.

Genbank 

Genbank exports are defined per annotated Sequence. All sequences in a samples may be exported at once, though the result will be a multi-genbank file.

Annotation tables

CVS tables of annotation results per Sample, Sequence or Feature.

JobControl (jobControl.sh)
--------------------------

Jobs for analysis are submitted and restarted with this tool. It acts as both the submission agent and processing daemon. We presently do not run the processing daemon as a separate detached entity.

Job submission

Job submission is done by defining a desired analysis plan in XML. The plan is then submitted for processing as follows:

	jobControl.sh --submit myplan.xml

A plan contains one or more targets and one or more steps. Steps must act on objects existing at submission time. This limitation means a single plan cannot both define a detection process and subsequent annotation on its results.

Targets can be a Sample, Sequence of Feature. Samples are referenced by project and sample name, Sequences and Features are referenced by database identifier.

An example annotation plan which would annotation all sequences in test-sample with two annotators.

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

UserControl (userControl.sh)
----------------------------

Currently, user accounts only apply to web access to annotation results. Any account with administration privileges is able to create/remove/modify other users through the web UI. This command is provided as a means of creating accounts from the command-line and is useful at installation time and if the administration account has been accidentally lost.
