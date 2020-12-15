# eventsourcing-cqrs-dynamoDB

# build
mvn --version	Prints out the version of Maven you are running.
mvn clean	Clears the target directory into which Maven normally builds your project.
mvn package	Builds the project and packages the resulting JAR file into the target directory.
mvn package -Dmaven.test.skip=true	Builds the project and packages the resulting JAR file into the target directory - without running the unit tests during the build.

# run
cd target
java -jar Onboarding-1.0-SNAPSHOT.war
