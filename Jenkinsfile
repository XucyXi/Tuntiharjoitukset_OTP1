pipeline {
    agent any
    tools {
        maven 'DefaultMaven'
    }
    environment {
        DOCKER_HUB_USER = 'jerevla'
        IMAGE_NAME = 'temperature-converter'
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
                bat 'C:\\ProgramData\\DockerDesktop\\version-bin\\docker.exe build -t jerevla/temperature-converter:latest .'
            }
        }
        stage('Push to Docker Hub') {
            steps {
                withCredentials([string(credentialsId: 'docker-hub-password', variable: 'DOCKER_PASS')]) {
                    bat 'C:\\ProgramData\\DockerDesktop\\version-bin\\docker.exe login -u jerevla -p %DOCKER_PASS%'
                    bat 'C:\\ProgramData\\DockerDesktop\\version-bin\\docker.exe push jerevla/temperature-converter:latest'
                }
            }
        }
    }
}