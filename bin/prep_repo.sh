#!/bin/bash
#
# Install missing dependencies to local user repository.
#

mvn install:install-file \
	-Dfile=local-lib/sun-drmaa-1.0.jar \
	-DgroupId=shap-extra \
	-DartifactId=sun-drmaa \
	-Dversion=1.0 \
	-Dpackaging=jar \
	-DgeneratePom=true

mvn install:install-file \
	-Dfile=local-lib/biojava3-core-3.0.2-SNAPSHOT.jar \
	-DgroupId=shap-extra \
	-DartifactId=biojava3-core \
	-Dversion=3.0.2-SNAPSHOT \
	-Dpackaging=jar \
	-DgeneratePom=true
	
mvn install:install-file \
	-Dfile=local-lib/commons-cli-2.0-beta2-SNAPSHOT.jar \
	-DgroupId=shap-extra \
	-DartifactId=commons-cli \
	-Dversion=2.0-beta2-SNAPSHOT \
	-Dpackaging=jar \
	-DgeneratePom=true
