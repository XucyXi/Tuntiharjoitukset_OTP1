pipeline {
    agent any
    tools {
        maven 'DefaultMaven'
    }
    environment {
        PATH = "C:\\Users\\merli\\AppData\\Local\\Programs\\DockerDesktop\\resources\\bin;${env.PATH}"
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
        stage('Build Docker Image') {
            steps {
                script {
                    docker.build("${env.DOCKER_HUB_USER}/${env.IMAGE_NAME}:latest")
                }
            }
        }
        stage('Push to Docker Hub') {
            steps {
                withCredentials([usernamePassword(credentialsId: "${env.DOCKERHUB_CREDENTIALS_ID}", usernameVariable: 'DOCKER_USER', passwordVariable: 'DOCKER_PASS')]) {
                    bat 'docker login -u "%DOCKER_USER%" -p "%DOCKER_PASS%"'
                    bat "docker push ${env.DOCKER_HUB_USER}/${env.IMAGE_NAME}:latest"
                }
            }
        }
    }
}