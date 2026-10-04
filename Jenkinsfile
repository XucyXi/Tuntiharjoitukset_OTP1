pipeline {
    agent any

    tools {
        maven 'DefaultMaven'
    }

    environment {
        PATH = "C:\\Users\\merli\\AppData\\Local\\Programs\\DockerDesktop\\resources\\bin;${env.PATH}"
        IMAGE = 'jerevla/temperature-converter'
    }

    stages {
        stage('Checkout') {
            steps {
                git branch: 'main', url: 'https://github.com/XucyXi/Tuntiharjoitukset_OTP1.git'
            }
        }

        stage('Build & Test') {
            steps {
                bat 'mvn -B clean test'
            }
            post {
                always {
                    junit 'target/surefire-reports/*.xml'
                    jacoco execPattern: 'target/jacoco.exec',
                           classPattern: 'target/classes',
                           sourcePattern: 'src/main/java'
                }
            }
        }

        stage('Build Docker Image') {
            steps {
                bat "docker build -t %IMAGE%:latest ."
            }
        }

        stage('Push to Docker Hub') {
            steps {
                withCredentials([usernamePassword(credentialsId: 'docker-hub-password',
                                                  usernameVariable: 'DH_USER',
                                                  passwordVariable: 'DH_PASS')]) {
                    bat 'echo %DH_PASS%|docker login -u %DH_USER% --password-stdin'
                    bat "docker push %IMAGE%:latest"
                }
            }
        }
    }

    post {
        always {
            bat 'docker logout'
        }
    }
}
