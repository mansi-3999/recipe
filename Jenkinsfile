pipeline {
    agent any
    environment {
        DOCKERHUB_CREDENTIALS = credentials('dockerhub-creds')
    }
    stages {
        stage('Build Backend') {
            steps {
                dir('backend') {
                    sh 'docker build -t $DOCKERHUB_USERNAME/recipe-backend:latest .'
                }
            }
        }
        stage('Build Frontend') {
            steps {
                dir('recipe-frontend') {
                    sh 'docker build -t $DOCKERHUB_USERNAME/recipe-frontend:latest .'
                }
            }
        }
        stage('Push Images') {
            steps {
                withCredentials([usernamePassword(credentialsId: 'dockerhub-creds', usernameVariable: 'DOCKERHUB_USERNAME', passwordVariable: 'DOCKERHUB_PASSWORD')]) {
                    sh 'echo $DOCKERHUB_PASSWORD | docker login -u $DOCKERHUB_USERNAME --password-stdin'
                    sh 'docker push $DOCKERHUB_USERNAME/recipe-backend:latest'
                    sh 'docker push $DOCKERHUB_USERNAME/recipe-frontend:latest'
                }
            }
        }
    }
}
