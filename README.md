# exemplar-pipeline-ws

tasks for eCI/CD exemplar pipeline v1:

- Build container image

- Jacoco code coverage

- Qualys static container security scan

- Push Container image to Nexus Artifact Repository

- Pull Container image from Nexus Artifact Repository

- Change Management Integration - ServiceNow client

- Deploy to ECP - Dev

- ECP Smoke Test - minimal `curl`

- Deploy to ECP - Test (SAT, PTE, etc.)

- Deploy to ECP - Prod

- Deploy to IEP - Dev

- IEP Smoke Test - minimal `curl`

- Deploy to IEP - Test (SAT, PTE, etc.)

- Deploy to IEP - Prod

- Selenium Grid for UI testing

- SoapUI for API testing

- LoadRunner for performance testing

- Qualys Web Scan for DAST

Java application with Docker support, unit testing, code coverage, and CI/CD integration using Jenkins, SonarQube, Artifactory, Qualys, and more.

Project Overview
This project demonstrates a simple Java application packaged as a JAR file. It includes:

    Unit Testing with JUnit 5.
    Code Coverage using JaCoCo.
    CI/CD Pipeline integrated with Jenkins, with stages for:
        Container image build and security scans.
        Deployments to different environments (ECP and IEP).
        Testing (UI, API, Performance).


Technologies

    Java 8 (JDK 1.8)
    Maven for project management and build automation
    JUnit 5 for unit tests
    JaCoCo for code coverage
    Docker (via Podman) for containerization
    Jenkins for CI/CD
    SonarQube for static code analysis
    Artifactory for container image storage
    Qualys for security scans
    Selenium Grid, SoapUI, LoadRunner for various tests


Requirements
    JDK 8 or higher
    Maven 3.x
    Podman or Docker installed
    Jenkins instance with the following plugins:
        Jenkins Pipeline Plugin
        JUnit Plugin
        HTML Publisher Plugin
    Access to:
        SonarQube instance
        Artifactory repository for Docker images
        Qualys API key for security scanning
        ServiceNow instance for change management
        URLs for various environments (ECP, IEP, etc.)

Setup
Clone the Repository

git clone https://github.com/excellaco/exemplar-pipeline-ws.git
cd exemplar-pipeline-ws


Maven Build
To build the project locally:
Ensure you have JDK 8 and Maven installed.
Run the following command to build and run tests:

    mvn clean install
        This will: Compile the code. Run unit tests using JUnit 5.Generate code coverage reports using JaCoCo.

To generate the Jacoco reports:


mvn jacoco:report


CI/CD Pipeline (Jenkins)
This project is set up with Jenkins for automated CI/CD. The pipeline includes the following stages:

Pipeline Stages
    Build Container Image: Builds the Docker image for the application.
    Jacoco Code Coverage: Runs tests and generates code coverage reports.
    Publish Jacoco Report: Archives Jacoco code coverage reports to Jenkins.
    Qualys Static Container Security Scan: Scans the container image for security vulnerabilities.
    Push Container Image to Artifactory: Pushes the built image to Artifactory.
    Pull Container Image from Artifactory: Pulls the image back from Artifactory for deployment.
    ServiceNow Change Management: Creates a change request in ServiceNow.
    Deploy to ECP and IEP: Deploys the application to ECP and IEP environments.
    Selenium Grid for UI Testing: Runs UI tests using Selenium Grid.
    SoapUI for API Testing: Runs API tests using SoapUI.
    LoadRunner for Performance Testing: Runs performance tests using LoadRunner.
    Qualys Web Scan for DAST: Performs a Dynamic Application Security Testing (DAST) scan.


Jenkinsfile

You can find the pipeline definition in the Jenkinsfile in the root of the repository. This file defines the stages for the CI/CD pipeline and integrates with various tools for testing, security scanning, deployment, and reporting.

Example Jenkins Pipeline Execution
The pipeline will perform the following steps:

    Build Container Image using Podman.
    Run Unit Tests and generate code coverage with JaCoCo.
    Scan the container image for vulnerabilities using Qualys.
    Push the image to Artifactory for storage.
    Deploy to different environments, such as ECP Dev, IEP Dev, ECP Prod, etc.
    Perform Smoke Tests, UI Tests, API Tests, Performance Tests, and Security Scans.


Configuration

    SonarQube: Ensure you have a SonarQube server set up and configured with the project for static code analysis.
    Artifactory: Configure your Artifactory repository URL and credentials in the pipeline.
    Qualys: Set up your Qualys API Key in Jenkins credentials.
    ServiceNow: Configure your ServiceNow Token for change management integration.

Example Jenkins Command to Run Pipeline
To run the pipeline manually from Jenkins, ensure your Jenkins instance is set up with the appropriate credentials and environment variables.

Testing
Unit Tests
Unit tests are written using JUnit 5. To run the tests locally, use the following Maven command:

mvn test


Code Coverage
Code coverage is generated using JaCoCo. After running the tests, JaCoCo will create a report that can be found in the following directory:

target/site/jacoco/index.html


Docker Support
The project includes a Dockerfile for containerizing the application. The container is built during the Jenkins pipeline and stored in Artifactory for deployment.

Build the Container Image Locally
You can build the Docker image locally using the following command:

podman build -t hello-world-app:latest .

Run the Container Locally
To run the container locally:


podman run -d -p 8080:8080 hello-world-app:latest


Deployment
The application is deployed to multiple environments (Dev, Test, Prod) via the Jenkins pipeline. The pipeline uses curl commands to trigger deployments to ECP and IEP environments.

Conclusion
This project demonstrates a comprehensive CI/CD pipeline with a focus on:

    Automated testing (unit, integration, UI, API, performance)
    Containerization with Podman/Docker
    Code coverage and security scans
    Integration with tools like SonarQube, Artifactory, Qualys, and ServiceNow