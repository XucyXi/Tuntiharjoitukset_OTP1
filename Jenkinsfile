pipeline {
    agent any
    tools {
        maven 'DefaultMaven'
    }
    environment {
        PATH = "C:\\Program Files\\Docker\\Docker\\resources\\bin;${env.PATH}"
        DOCKER_HOST = 'tcp://localhost:2375'
        DOCKER_HUB_USER = 'jerevla'
        IMAGE_NAME = 'temperature-converter'
        DOCKERHUB_CREDENTIALS_ID = 'docker-hub-password'
    }
    stages {
        stage('Checkout') {
            steps {
                git branch: 'main', url: 'https://github.com/XucyXi/Tuntiharjoitukset_OTP1.git'
            }
        }
        stage('Build') {
            steps {
                bat 'mvn clean install'
            }
        }
        stage('Test') {
            steps {
                bat 'mvn test'
            }
        }
        stage('Code Coverage') {
            steps {
                bat 'mvn jacoco:report'
            }
        }
        stage('Publish Test Results') {
            steps {
                junit '**/target/surefire-reports/*.xml'
            }
        }
        stage('Publish Coverage Report') {
            steps {
                jacoco()
            }
        }

        stage('Debug Docker') {
            steps {
                bat 'where docker'
                bat 'echo %PATH%'
            }
        }

        stage('Build Docker Image') {
            steps {
                script {
                    docker.build("${env.DOCKER_HUB_USER}/${env.IMAGE_NAME}:latest")
                }
            }
        }
        stage('Push to Docker Hub') {
            steps {
                script {
                    docker.withRegistry('https://index.docker.io/v1/', "${env.DOCKERHUB_CREDENTIALS_ID}") {
                        docker.image("${env.DOCKER_HUB_USER}/${env.IMAGE_NAME}:latest").push()
                    }
                }
            }
        }
    }
}