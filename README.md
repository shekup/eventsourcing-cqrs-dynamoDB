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
</br>
Test in browser 'http://localhost:8080/'

# docker build and run
The project uses spotify docker plugin, which has three goals: build, push, and tag.  </br>
The goal can be added in pom.xml (for example 'build' goal in along with spotify plugin) so that if 'mvn package' is executed it will create the docker image.  </br>
The goal can be kept separate (as here): Add the spotify plugin but dont define goal, add goal in 'mvn package' command, such as, 'mvn package dockerfile:build' (ref - https://codefresh.io/howtos/using-docker-maven-maven-docker/) </br>

The docker also requires the repo name in lower case only, Otherwise, an error can occur such as 'Repo name "shekup/Onboarding" must contain only lowercase..'.  The name of repo is picked from pom.xml: <artifactId>onboarding</artifactId> and <docker.image.prefix>shekup</docker.image.prefix> </br>

Easiest way to create dockerfile in Windows is using Notepad++.  While saving name the file as 'dockerfile' and extension as 'All'.  <br>

amazon corretto is used in project and confirm the version used.  The version may differ from the SDK version, such as, The SDK version used in project is '11.0.9.12.1'  the docker image for same is 'amazoncorretto:11' </br>

Execute the command to successfully build 'mvn package -Dmaven.test.skip=true dockerfile:build' </br>
Since dockerfile and pom are at same location, the maven will pick the dockerfile, read it, download the base image, create the application image, and upload it to local server </br>
Execute the command to run 'docker run -p 8081:8080 -t shekup/onboarding' </br>
Test in browser 'http://localhost:8081/'
</br>Read more - https://nullbeans.com/spring-boot-and-docker-example-on-windows/
