pipeline {
    agent any

    environment {
        DOCKER_IMAGE_BACKEND = 'marmita-backend'
        DOCKER_IMAGE_FRONTEND = 'marmita-frontend'
        EC2_HOST = 'ubuntu@18.222.4.220'
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
            environment {
                TESTCONTAINERS_RYUK_DISABLED = 'true'
                TESTCONTAINERS_HOST_OVERRIDE = 'host.docker.internal'
            }
            steps {
                dir('backend') {
                    sh './mvnw test'
                }
            }
        }

        stage('Deploy to EC2') {
            steps {
                sshagent(credentials: ['ec2-ssh-key']) {
                    sh """
                        ssh -o StrictHostKeyChecking=no ${EC2_HOST} '
                            cd ~/marmita-manager &&
                            git pull &&
                            docker compose up -d --build
                        '
                    """
                }
            }
        }
    }

    post {
        success {
            echo 'Deploy realizado com sucesso!'
        }
        failure {
            echo 'Pipeline falhou — verifique os logs acima.'
        }
    }
}