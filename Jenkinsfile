def dockerServices = [
    [
        module    : 'MedUserService',
        dockerfile: 'MedUserService/Dockerfile',
        imageName : 'medback-user-service'
    ],
    [
        module    : 'MedCoreService',
        dockerfile: 'MedCoreService/Dockerfile',
        imageName : 'medback-core-service'
    ],
    [
        module    : 'MedNotificationService',
        dockerfile: 'MedNotificationService/Dockerfile',
        imageName : 'med-notification-service'
    ],
    [
        module    : 'MedChatBootService',
        dockerfile: 'MedChatBootService/Dockerfile',
        imageName : 'med-chatboot-service'
    ],
    [
        module    : 'MedConfigService',
        dockerfile: 'MedConfigService/Dockerfile',
        imageName : 'med-config-service'
    ],
    [
        module    : 'MedDiscovery',
        dockerfile: 'MedDiscovery/Dockerfile',
        imageName : 'med-discovery'
    ],
    [
        module    : 'MedGateway',
        dockerfile: 'MedGateway/Dockerfile',
        imageName : 'med-gateway'
    ]
]

def backendModules = dockerServices.collect { it.module }.join(',')
def artifactPatterns = (dockerServices.collect { "${it.module}/target/*.jar" } + ['**/target/site/jacoco/**/*']).join(', ')

pipeline {
    agent any

    options {
        disableConcurrentBuilds()
        skipDefaultCheckout(true)
        timestamps()
    }

    parameters {
        string(
            name: 'REPOSITORY_URL',
            defaultValue: 'https://github.com/M-Kheireddine/MedBack',
            description: 'Optional Git repository URL used when this job is configured as an inline Pipeline script.'
        )
        string(
            name: 'REPOSITORY_BRANCH',
            defaultValue: 'main',
            description: 'Git branch checked out when REPOSITORY_URL is provided.'
        )
        string(
            name: 'DOCKERHUB_NAMESPACE',
            defaultValue: 'kheireddinemechergui',
            description: 'Docker Hub namespace used for pushed images.'
        )
        string(
            name: 'DOCKERHUB_CREDENTIALS_ID',
            defaultValue: '',
            description: 'Jenkins credentials id containing the Docker Hub username and password/token.'
        )
        string(
            name: 'IMAGE_TAG',
            defaultValue: '10',
            description: 'Optional image tag override. When empty, the Jenkins build number is used.'
        )
        string(
            name: 'SONAR_TOKEN_CREDENTIALS_ID',
            defaultValue: 'SONAR-CLOUD-TOKEN',
            description: 'Jenkins secret text credential id containing the SonarCloud token.'
        )
    }

    environment {
        MAVEN_OPTS = '-Dmaven.test.failure.ignore=false'
    }

    stages {
        stage('Checkout') {
            steps {
                script {
                    if (binding.hasVariable('scm')) {
                        checkout scm
                    } else if (params.REPOSITORY_URL?.trim()) {
                        git branch: params.REPOSITORY_BRANCH.trim(), url: params.REPOSITORY_URL.trim()
                    } else {
                        error("Source checkout is not configured. Use 'Pipeline script from SCM' or provide REPOSITORY_URL and REPOSITORY_BRANCH parameters.")
                    }

                    env.EFFECTIVE_IMAGE_TAG = params.IMAGE_TAG?.trim() ? params.IMAGE_TAG.trim() : env.BUILD_NUMBER
                }
                sh 'chmod +x mvnw'
            }
        }

        stage('Maven Test') {
            steps {
                sh "mvn -B -ntp -pl ${backendModules} -am clean verify"
            }
        }

        stage('SonarCloud Analysis') {
            steps {
                withCredentials([
                    string(
                        credentialsId: params.SONAR_TOKEN_CREDENTIALS_ID,
                        variable: 'SONAR_TOKEN'
                    )
                ]) {
                    sh "mvn -B -ntp -pl ${backendModules} -am " +
                       "org.sonarsource.scanner.maven:sonar-maven-plugin:5.7.0.6970:sonar " +
                       "-DskipTests " +
                       "-Dsonar.token=\$SONAR_TOKEN " +
                       "-Dsonar.qualitygate.wait=true " +
                       "-Dsonar.qualitygate.timeout=300"
                }
            }
        }

        stage('Maven Build') {
            steps {
                sh "mvn -B -ntp -pl ${backendModules} -am package -DskipTests"
            }
        }

        stage('Docker Build') {
            steps {
                script {
                    dockerServices.each { service ->
                        String imageRepository = "${params.DOCKERHUB_NAMESPACE}/${service.imageName}"
                        sh """
                            docker build \
                              -f ${service.dockerfile} \
                              -t ${imageRepository}:${env.EFFECTIVE_IMAGE_TAG} \
                              -t ${imageRepository}:latest \
                              .
                        """
                    }
                }
            }
        }

        stage('Docker Push') {
            steps {
                withCredentials([
                    usernamePassword(
                        credentialsId: params.DOCKERHUB_CREDENTIALS_ID,
                        usernameVariable: 'DOCKERHUB_USERNAME',
                        passwordVariable: 'DOCKERHUB_PASSWORD'
                    )
                ]) {
                    sh 'echo "$DOCKERHUB_PASSWORD" | docker login -u "$DOCKERHUB_USERNAME" --password-stdin'
                    script {
                        dockerServices.each { service ->
                            String imageRepository = "${params.DOCKERHUB_NAMESPACE}/${service.imageName}"
                            sh "docker push ${imageRepository}:${env.EFFECTIVE_IMAGE_TAG}"
                            sh "docker push ${imageRepository}:latest"
                        }
                    }
                    sh 'docker logout'
                }
            }
        }
    }

    post {
        always {
            junit allowEmptyResults: true, testResults: '**/target/surefire-reports/*.xml'
            archiveArtifacts allowEmptyArchive: true, artifacts: artifactPatterns
            cleanWs deleteDirs: true, disableDeferredWipeout: true
        }
        success {
            echo "MedBack CI/CD pipeline completed successfully."
        }
        failure {
            echo "MedBack CI/CD pipeline failed. Check the Jenkins stage logs for details."
        }
    }
}
