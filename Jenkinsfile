pipeline {
    agent { label 'build-agent' }

    environment {
        // Environment Variables
        IMAGE_NAME = 'hello-world-app' 
        IMAGE_TAG = 'latest'
        CONTAINER_REGISTRY = 'docker.io'

        // SonarQube Configuration
        SONARQUBE = 'SonarQube-irs'
        SONAR_TOKEN = credentials('sonarqube-token') 
        SONARQUBE_URL = 'http://your-sonarqube-server-url'  

        // Artifactory Configuration (We will add new one)
        ARTIFACTORY_REPO = 'https://artifactory.example.com/api/docker/repo'

        // // Security Scanning
        // //QUALYS_API_KEY = credentials('qualys-api-key')

        // // ServiceNow for Change Management
        // //SERVICE_NOW_TOKEN = credentials('servicenow-token')

        // // URLs for Different Environments- THIS NEEDS TO BE IDENTIFIED
        // ECP_DEV_URL = 'https://ecp-dev.example.com'
        // ECP_PROD_URL = 'https://ecp-prod.example.com'

        // // Testing URLs and Tools
        // SELENIUM_GRID_URL = 'http://selenium-grid.example.com'
        // SOAPUI_TEST_URL = 'http://soapui.example.com'
        // LOADRUNNER_URL = 'http://loadrunner.example.com'

        // OpenShift Configuration
        OPENSHIFT_PROJECT = 'dedicated-admin'
        OPENSHIFT_SERVER = 'https://api.c1d4t8z6e7h8o7v.bfk4.p1.openshiftapps.com:6443'
        KUBECONFIG = '~/.kube/config'  
        KUSTOMIZE_PATH = 'k8s/overlays/openshift' 

        // Maven Properties for Version and Timestamp
        MAVEN_VERSION = sh(script: 'mvn help:evaluate -Dexpression=project.version -q -DforceStdout', returnStdout: true).trim()
        BUILD_TIMESTAMP = sh(script: 'date +%Y-%m-%d_%H-%M-%S', returnStdout: true).trim()
    }

    stages {
        stage('Init') {
            steps {
                script {
                    def PULL_REQUEST = env.BRANCH_NAME !=~ /^main$/ && env.BRANCH_NAME !=~ /^release\/.+/
                    def TRUNK_BRANCH = env.BRANCH_NAME ==~ /^main$/
                    def FOR_RELEASE = env.BRANCH_NAME ==~ /^release\/.+/

                    echo "PULL_REQUEST: ${PULL_REQUEST}"
                    echo "TRUNK_BRANCH: ${TRUNK_BRANCH}"
                    echo "FOR_RELEASE: ${FOR_RELEASE}"
                }
            }
        }

        // Building the container image
        stage('Build Container Image') {
            steps {
                script {
                    echo "Building container image ${IMAGE_NAME}:${MAVEN_VERSION}-${BUILD_TIMESTAMP}"
                    sh """
                        docker build -t ${CONTAINER_REGISTRY}/${IMAGE_NAME}:${MAVEN_VERSION}-${BUILD_TIMESTAMP} .
                    """
                }
            }
        }

        // Jacoco Code Coverage
        stage('Jacoco Code Coverage') {
            steps {
                script {
                    echo "Running tests and generating Jacoco code coverage report"
                    sh '''
                        mvn clean install
                        mvn test
                    '''
                }
            }
        }

        // Publish Jacoco Report
        stage('Publish Jacoco Report') {
            steps {
                script {
                    echo "Archiving Jacoco code coverage reports"
                    junit '**/target/test-classes/test-*.xml' 
                    publishHTML(target: [
                        reportName: 'Jacoco Code Coverage Report',
                        reportDir: 'target/site/jacoco', 
                        reportFiles: 'index.html'
                    ])
                }
            }
        }

        // The SonarQube Analysis
        stage('SonarQube Analysis') {
            steps {
                script {
                    echo "Running SonarQube analysis"
                    sh '''
                        mvn sonar:sonar -Dsonar.projectKey=${IMAGE_NAME} -Dsonar.host.url=${SONARQUBE_URL} -Dsonar.login=${SONAR_TOKEN}
                    '''
                }
            }
        }

        // DAST Qualys Security Scan
        stage('Qualys Static Container Security Scan') {
            steps {
                script {
                    echo "Running static container security scan"
                    sh "qualys-container-scan --image ${CONTAINER_REGISTRY}/${IMAGE_NAME}:${MAVEN_VERSION}-${BUILD_TIMESTAMP} --api-key ${QUALYS_API_KEY}"
                }
            }
        }

        // Testing with Selenium 
        stage('Run Selenium Tests') {
            steps {
                script {
                    echo "Running Selenium tests"
                    sh '''
                        mvn clean test -Dselenium.grid.url=${SELENIUM_GRID_URL}
                    '''
                }
            }
        }

        // Push container image to Artifactory (or Nexus)
        stage('Push Container Image to Artifactory') {
            steps {
                script {
                    echo "Pushing container image to Artifactory"
                    sh """
                        docker login ${ARTIFACTORY_REPO} -u ${ARTIFACTORY_USERNAME} -p ${ARTIFACTORY_PASSWORD}
                        docker tag ${CONTAINER_REGISTRY}/${IMAGE_NAME}:${MAVEN_VERSION}-${BUILD_TIMESTAMP} ${ARTIFACTORY_REPO}/${IMAGE_NAME}:${MAVEN_VERSION}-${BUILD_TIMESTAMP}
                        docker push ${ARTIFACTORY_REPO}/${IMAGE_NAME}:${MAVEN_VERSION}-${BUILD_TIMESTAMP}
                    """
                }
            }
        }

        // Deploying to OpenShift using Kustomize
        stage('Deploy to OpenShift with Kustomize') {
            steps {
                script {
                    echo "Deploying container image to OpenShift using Kustomize"
                    sh """
                        # Login to OpenShift
                        oc login ${OPENSHIFT_SERVER} --kubeconfig ${KUBECONFIG}
                        
                        # Set OpenShift project
                        oc project ${OPENSHIFT_PROJECT}
                        
                        # Apply Kustomize overlay to deploy to OpenShift
                        kubectl apply -k ${KUSTOMIZE_PATH}
                    """
                }
            }
        }
    }

    post {
        always {
            echo 'The exemplar Pipeline finished!'
        }

        success {
            echo 'The Exemplar Pipeline successfully completed!'
        }

        failure {
            echo 'The exemplar Pipeline failed!'
        }
    }
}
