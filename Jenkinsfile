def services = [
    'MedDiscovery',
    'MedConfigService',
    'MedGateway',
    'MedUserService',
    'MedCoreService',
    'MedChatBootService',
    'MedNotificationService'
]

pipeline {
    agent any

    options {
        ansiColor('xterm')
        disableConcurrentBuilds()
        timestamps()
    }

    parameters {
        string(name: 'DOCKERHUB_NAMESPACE', defaultValue: 'your-dockerhub-namespace', description: 'Docker Hub namespace')
        string(name: 'DOCKERHUB_CREDENTIALS_ID', defaultValue: 'dockerhub-credentials', description: 'Jenkins credentials id for Docker Hub')
        string(name: 'SONARQUBE_SERVER', defaultValue: 'SonarQubeServer', description: 'Configured SonarQube server name in Jenkins')
        string(name: 'SONAR_PROJECT_KEY', defaultValue: 'medback', description: 'SonarQube project key')
        string(name: 'SONAR_PROJECT_NAME', defaultValue: 'MedBack', description: 'SonarQube project name')
        string(name: 'IMAGE_TAG', defaultValue: '', description: 'Optional Docker tag override')
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Initialize') {
            steps {
                script {
                    env.EFFECTIVE_IMAGE_TAG = params.IMAGE_TAG?.trim() ? params.IMAGE_TAG.trim() : env.BUILD_NUMBER
                }
            }
        }

        stage('Maven Build') {
            steps {
                sh 'chmod +x mvnw'
                sh './mvnw clean package -DskipTests'
            }
        }

        stage('SonarQube Analysis') {
            steps {
                withSonarQubeEnv("${params.SONARQUBE_SERVER}") {
                    sh """
                        ./mvnw sonar:sonar \
                          -DskipTests \
                          -Dsonar.projectKey=${params.SONAR_PROJECT_KEY} \
                          -Dsonar.projectName=${params.SONAR_PROJECT_NAME}
                    """
                }
            }
        }

        stage('Quality Gate') {
            steps {
                timeout(time: 10, unit: 'MINUTES') {
                    waitForQualityGate abortPipeline: true
                }
            }
        }

        stage('Docker Build') {
            steps {
                script {
                    services.each { serviceName ->
                        def imageName = "${params.DOCKERHUB_NAMESPACE}/${serviceName.toLowerCase()}:${env.EFFECTIVE_IMAGE_TAG}"
                        sh "docker build -t ${imageName} -f ${serviceName}/Dockerfile ."
                    }
                }
            }
        }

        stage('Docker Push') {
            steps {
                withCredentials([usernamePassword(
                        credentialsId: params.DOCKERHUB_CREDENTIALS_ID,
                        usernameVariable: 'DOCKERHUB_USERNAME',
                        passwordVariable: 'DOCKERHUB_PASSWORD'
                )]) {
                    sh 'echo "$DOCKERHUB_PASSWORD" | docker login -u "$DOCKERHUB_USERNAME" --password-stdin'
                    script {
                        services.each { serviceName ->
                            def imageName = "${params.DOCKERHUB_NAMESPACE}/${serviceName.toLowerCase()}:${env.EFFECTIVE_IMAGE_TAG}"
                            sh "docker push ${imageName}"
                            sh "docker tag ${imageName} ${params.DOCKERHUB_NAMESPACE}/${serviceName.toLowerCase()}:latest"
                            sh "docker push ${params.DOCKERHUB_NAMESPACE}/${serviceName.toLowerCase()}:latest"
                        }
                    }
                }
            }
        }
    }

    post {
        always {
            junit allowEmptyResults: true, testResults: '**/target/surefire-reports/*.xml'
            cleanWs deleteDirs: true, disableDeferredWipeout: true
        }
        success {
            echo "MedBack pipeline completed successfully."
        }
        failure {
            echo "MedBack pipeline failed. Review the stage logs for details."
        }
    }
}
