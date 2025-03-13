pipeline {
    agent { label 'build-agent' }

    environment {
        // Environment Variables

        // Maven Properties for Version and Timestamp
        MAVEN_VERSION = sh(script: 'mvn help:evaluate -Dexpression=project.version -q -DforceStdout', returnStdout: true).trim()
        BUILD_TIMESTAMP = sh(script: 'date +%Y-%m-%d_%H-%M-%S', returnStdout: true).trim()

        IMAGE_NAME = 'hello-world-app' 
        IMAGE_TAG = "${MAVEN_VERSION}-${BUILD_TIMESTAMP}"  // Combine version and timestamp
        CONTAINER_REGISTRY = 'docker.io'

        // SonarQube Configuration
        SONARQUBE = 'SonarQube-irs'
        SONAR_TOKEN = credentials('sonarqube-token') 
        SONARQUBE_URL = 'http://54.80.16.163:9000'  

        // Artifactory Configuration
        ARTIFACTORY_URL = '54.80.16.163:8086'
        REPOSITORY_PATH = 'repository/helloworld'

        // Security Scanning
        // QUALYS_API_KEY = credentials('qualys-api-key')

        // ServiceNow for Change Management
        // SERVICE_NOW_TOKEN = credentials('servicenow-token')

        // URLs for Different Environments- THIS NEEDS TO BE IDENTIFIED
        // ECP_DEV_URL = 'https://ecp-dev.example.com'
        // ECP_PROD_URL = 'https://ecp-prod.example.com'

        // Testing URLs and Tools
        // SELENIUM_GRID_URL = 'http://selenium-grid.example.com'
        // SOAPUI_TEST_URL = 'http://soapui.example.com'
        // LOADRUNNER_URL = 'http://loadrunner.example.com'

        // OpenShift Configuration
        OPENSHIFT_PROJECT = 'pac'
        TOKEN = credentials('octoken')
        OPENSHIFT_SERVER = 'https://api.c1d4t8z6e7h8o7v.bfk4.p1.openshiftapps.com:6443'
        KUBECONFIG = '~/.kube/config'  
        KUSTOMIZE_PATH = 'k8s/overlays/openshift' 


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

        stage('Build Container Image') {
            steps {
                script {
                    echo "Building container image ${IMAGE_NAME}:${IMAGE_TAG}"
                    sh """
                        docker build -t ${IMAGE_NAME}:${IMAGE_TAG} .
                    """
                }
            }
        }

        // Jacoco Code Coverage
        // stage('Jacoco Code Coverage') {
        //     steps {
        //         script {
        //             echo "Running tests and generating Jacoco code coverage report"
        //             sh '''
        //                 mvn clean install
        //                 mvn test
        //             '''
        //         }
        //     }
        // }

        // Publish Jacoco Report
        // stage('Publish Jacoco Report') {
        //     steps {
        //         script {
        //             echo "Archiving Jacoco code coverage reports"
        //             junit '**/target/test-classes/test-*.xml' 
        //             publishHTML(target: [
        //                 reportName: 'Jacoco Code Coverage Report',
        //                 reportDir: 'target/site/jacoco', 
        //                 reportFiles: 'index.html'
        //             ])
        //         }
        //     }
        // }

        stage('SonarQube Analysis') {
            steps {
                script {
                    echo "Running SonarQube analysis"
                    try {
                        // Run SonarQube analysis and capture the output
                        def output = sh(script: """
                            mvn sonar:sonar \
                                -Dsonar.projectKey=${IMAGE_NAME} \
                                -Dsonar.host.url=${SONARQUBE_URL} \
                                -Dsonar.login=${SONAR_TOKEN}
                        """, returnStdout: true).trim()

                        // Check if the analysis was successful
                        if (output.contains("ANALYSIS SUCCESSFUL")) {
                            echo "SonarQube analysis successful"
                            writeFile file: 'env.properties', text: 'success'
                        } else {
                            // If the output does not contain "ANALYSIS SUCCESSFUL", fail the pipeline
                            echo "SonarQube analysis failed (output does not indicate success)"
                            writeFile file: 'env.properties', text: 'failed'
                            error("SonarQube analysis failed") // Fail the pipeline
                        }
                    } catch (Exception e) {
                        // Catch any exceptions (e.g., command failure) and set status to failed
                        echo "SonarQube analysis failed with error: ${e}"
                        writeFile file: 'env.properties', text: 'failed'
                        error("SonarQube analysis failed") // Fail the pipeline
                    }
                }
            }
        }

        // DAST Qualys Security Scan
        // stage('Qualys Static Container Security Scan') {
        //     steps {
        //         script {
        //             echo "Running static container security scan"
        //             sh "qualys-container-scan --image ${CONTAINER_REGISTRY}/${IMAGE_NAME}:${MAVEN_VERSION}-${BUILD_TIMESTAMP} --api-key ${QUALYS_API_KEY}"
        //         }
        //     }
        // }

        // Testing with Selenium 
        // stage('Run Selenium Tests') {
        //     steps {
        //         script {
        //             echo "Running Selenium tests"
        //             sh '''
        //                 mvn clean test -Dselenium.grid.url=${SELENIUM_GRID_URL}
        //             '''
        //         }
        //     }
        // }

        stage('Push Container Image to Artifactory') {
            steps {
                script {
                    echo "Pushing container image to Artifactory"
                    withCredentials([usernamePassword(credentialsId: 'jenkins-nexus', usernameVariable: 'NEXUS_USER', passwordVariable: 'NEXUS_PASSWORD')]) {
                        sh '''
                            set +x
                            echo $NEXUS_PASSWORD | docker login ${ARTIFACTORY_URL}/${REPOSITORY_PATH}/ -u $NEXUS_USER --password-stdin
                            set -x

                            # Tag the local image with the Artifactory path
                            docker tag ${IMAGE_NAME}:${IMAGE_TAG} ${ARTIFACTORY_URL}/${REPOSITORY_PATH}/${IMAGE_NAME}:${IMAGE_TAG}
                            
                            # Push the Docker image to Artifactory
                            docker push ${ARTIFACTORY_URL}/${REPOSITORY_PATH}/${IMAGE_NAME}:${IMAGE_TAG}
                        '''
                    }
                }
            }
        }

        stage('Update Image And Annotate Manifests with Kustomize (OpenShift)') {
            steps {
                script {
                    // Read the SonarQube Scan status from the environment file
                    def sonarqubeStatus  = "failed"  // Default value
                    if (fileExists('env.properties')) {
                        sonarqubeStatus  = readFile('env.properties').trim()

                    }

                    // Navigate to the overlays/openshift directory
                    dir("${KUSTOMIZE_PATH}") {
                        // Update the image tag in kustomization.yaml
                        sh """
                            echo "Updating image tag in Kustomize deployment"
                            kustomize edit set image ${ARTIFACTORY_URL}/${REPOSITORY_PATH}/${IMAGE_NAME}=${ARTIFACTORY_URL}/${REPOSITORY_PATH}/${IMAGE_NAME}:${IMAGE_TAG}
                        """

                        // Add annotations to the Kustomize deployment
                        sh """
                            echo "Adding SonarQube Scan status to Kustomize deployment"
                            kustomize edit add annotation --force sonarqube-scan:${sonarqubeStatus}
                        """
                    }
                }
            }
        }

        stage('Deploy to OpenShift with Kustomize') {
            steps {
                script {
                    echo "Deploying container image to OpenShift using Kustomize"
                    withCredentials([string(credentialsId: 'octoken', variable: 'OC_TOKEN')]) {
                        sh '''
                            # Login to OpenShift
                            set +x
                            oc login --token ${OC_TOKEN} ${OPENSHIFT_SERVER}
                            set -x
                            
                            # Set OpenShift project
                            oc project ${OPENSHIFT_PROJECT}
                            
                            # First validate with dry-run to catch any Gatekeeper violations
                            echo "Validating deployment against admission policies..."
                            set -e  # This ensures any command failure causes the script to exit
                            oc apply -k ${KUSTOMIZE_PATH} --dry-run=server -o yaml
                            
                            # If validation passes, proceed with actual deployment
                            echo "Validation successful, proceeding with deployment..."
                            oc apply -k ${KUSTOMIZE_PATH}
                        '''
                    }
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