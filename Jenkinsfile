pipeline {
    agent any

    environment {
        DOCKER_IMAGE_BACKEND = 'marmita-backend'
        DOCKER_IMAGE_FRONTEND = 'marmita-frontend'
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build Backend') {
            steps {
                dir('backend') {
                    sh './mvnw clean package -DskipTests'
                }
            }
        }

        stage('Test Backend') {
            steps {
                dir('backend') {
                    sh './mvnw test'
                }
            }
        }

        stage('Build Docker Images') {
            steps {
                sh 'docker build -t $DOCKER_IMAGE_BACKEND:$BUILD_NUMBER ./backend'
                sh 'docker build -t $DOCKER_IMAGE_FRONTEND:$BUILD_NUMBER ./frontend'
            }
        }

        stage('Deploy') {
            steps {
                sh 'docker compose up -d --build'
            }
        }
    }

    post {
        success {
            echo 'Pipeline executado com sucesso!'
        }
        failure {
            echo 'Pipeline falhou — verifique os logs acima.'
        }
    }
}