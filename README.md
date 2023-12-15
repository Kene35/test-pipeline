# Introduction
This is a demo2 project, based on SpringBoot, to get you started with using containers and deploying to OpenShift.....

# Quick Start
These are instructions to get the code from [GitHub](https://github.com/Norfolk-Southern/), and then build and run the application locally.
* Clone the demo2 app from Github [here](https://github.com/Norfolk-Southern/demo2)
* Build from the command line by typing **gradlew** in the project root directory  
    This will produce an executable jar file in __build\libs\demo2.jar__
* Run the application from the command line by typing
    - __java -jar build\libs\demo2.jar__
    - Open a browser with URL [Demo](http://localhost:8099)

## Docker Section Deprecated
### [Run Locally in Docker](#docker-local)
If you have docker installed on your work station you can run demo2 under Docker as follows:
1. Copy the executable jar to the the docker/local folder so Docker can build and image
  __copy build\libs\demo2.jar docker\local__
2. Create a local docker image 
  __docker build docker\local\ -t demo2__
3. Check that your image was created local
  __docker images__
3. Now run demo2 (to run under Docker) from the command line
  __docker run -p 8042:8099 demo2__
4. View the app [here](http://localhost:8042/) 
  Note the port number exposed by docker is 8042, which maps to the demo2 app port 8099

# Deploying to OpenShift
The rest of this readme provides details for how to deploy the application to [OpenShift Test](https://master.ocptest01.nscorp.com/)  
## Build Pipeline
The demo 2 app uses the standard Jenkins Java (java 11) pipeline defined in GitHub [here](https://github.com/Norfolk-Southern/eas-build-pipelines/tree/master/common)

## Build
[Gradle Wrapper](https://docs.gradle.org/current/userguide/gradle_wrapper.html) is used to compile and build deployable artifacts.

Pertinent files are:
1. __gradle/wrapper/gradle-wrapper.properties__ 
  The NS Nexus repo [URL](http://repository.nscorp.com:8084/nexus/content/groups/NSRepository/org/gradle/wrapper/gradle/6.5.1/) where Gradle is obtained is defined in this file
2. __src/main/resources/application.properties__ defined the SpringBoot application port (8099)
3. __build.gradle__ - the Gradle build script 
  2.1 Will move all required artifacts to docker folder for docker build
  2.2 Contain jacoco plugin for determining test coverage
4. __settings.gradle__ 
  Optional file but should point to NS private repository if used

Dependent packages/jars defined in the __build.gradle__ are obtained from the [NS Nexus Package Repo](http://repository.nscorp.com:8084/nexus/#view-repositories), they are __not__ stored in GitHub and must not be obtained locally (on the dev machine)

This is the fragement from __build.gradle__ that define the location of the package repo
```
buildscript {
	repositories {
		maven { 
			url "http://repository.nscorp.com:8084/nexus/content/groups/NSRepository"
		}
	}
```
## Docker and OpenShift
This section describes how the build script creates a Docker image that will be deployed to OpenShift.
### Docker
__Dockerfile__ is defined in the __\docker__ folder of the project
__Dockerfile__ defines:
1. The java base image that will be pulled from the [NS Nexus base image repo](http://nsos-pr1-nex.atldc.nscorp.com:8081/#browse/browse:base-images) ubi8 images are the preferred base images
2. latest datadog agent jar for APM
3. The application entry point i.e. the executable 
4. The port via which the SpringBoot executable will be accessed

This is the contents of __Dockerfile__ 
```
FROM nsos-pr1-nex.atldc.nscorp.com:8083/ubi8/openjdk-11
ADD http://repository.nscorp.com:8084/nexus/content/groups/NSRepository/com/datadoghq/dd-java-agent/latest/dd-java-agent-latest.jar /datadog/dd-java-agent.jar
RUN chmod 644 /datadog/dd-java-agent.jar
VOLUME /tmp
COPY *.jar ./app.jar
EXPOSE 8099
ENTRYPOINT java -jar app.jar
```

### [Openshift]
The build pipeline will check for openshift deployment configuration 
PROJECT_NAME and APP_NAME parameters are derived from jenkins environment and github webhook payload

Pertinent files  are:

__OpenShift config maps__ are applied from a project config-repo instead of being stored in the source repository. The folder name where these are kept if is __application name__ with __environment subdirectories__ like this:
  ```
  +-- project-ocp-config.git
  | +-- service1
  |   +-- test
  |     +-- config-map-yaml
  |   +-- qa
  |     +-- config-map-yaml
  |   +-- uat
  |     +-- config-map-yaml
  |   +-- prod
  |     +-- config-map-yaml
  | +-- service2
  |   +-- test
  |     +-- config-map-yaml
  |   +-- qa
  |     +-- config-map-yaml
  |   +-- uat
  |     +-- config-map-yaml
  |   +-- prod
  |     +-- config-map-yaml
```
<<<<<<< HEAD

## SonarQube
The build pipleline includes the reporting of unit test code coverage results. Code scanning will be done after junit testing to ensure proper code coverage reporting. Pertinent SonarQube config files in the root of the project folder are:
1. __sonar-project.properties__  
Has minimal [SonarQube](https://www.sonarqube.org/) properties needed for code quality scanning. The application name and version are overridden by the pipeline as follows:
sonar.projectKey = {jenkins folder name}-{application name from github payload}
sonar.projectName = {jenkins folder name}-{application name from github payload}
sonar.projectVersion = {git tag}

## Veracode

veracode scan is added by the pipeline if the folder has a VERACODE_PROJECT = true setting
application can override jirakey with jirakey: mykey
application can bypass scan by adding veracode.yaml with scan: false to resources folder
evv will be test/prod

**/config/veracode/${evv}/*.yaml
**/src/main/resources/config/veracode/${evv}/*.yaml

### todo add veracode team name
push bump .............
