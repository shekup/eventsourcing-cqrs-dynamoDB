# eventsourcing-cqrs-dynamoDB

# maven build and run
mvn --version	Prints out the version of Maven you are running. </br>
mvn clean	Clears the target directory into which Maven normally builds your project. </br>
mvn package	Builds the project and packages the resulting JAR file into the target directory. </br>
mvn package -Dmaven.test.skip=true	Builds the project and packages the resulting JAR file into the target directory - without running the unit tests during the build. </br>

<b>package</b> will compile your code and also package it. For example, if your pom says the project is a jar, it will create a jar for you when you package it and put it somewhere in the target directory (by default). <b>install</b> will compile and package, but it will also put the package in your local repository. This will make it so other projects can refer to it and grab it from your local repository.</br>
Maven commands - http://tutorials.jenkov.com/maven/maven-commands.html
</br>
cd target </br>
java -jar Onboarding-1.0-SNAPSHOT.war
