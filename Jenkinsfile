pipeline {
    agent any
        tools {
            maven 'Maven3'
        }
    
    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }
        stage('Quality Check') {
            steps {
                sh 'mvn -B checkstyle:checkstyle'
                echo 'Checkstyle HTML report generated at target/site/checkstyle.html'
                sh 'mvn -e checkstyle:check'
                sh 'mvn -B spotbugs:check'
                echo 'Quality check completed successfully.'
            }
            post {
                always {
                    archiveArtifacts artifacts: 'target/site/checkstyle.html', allowEmptyArchive: true
                }
            }
        }
        stage('Build') {
            steps {
                
                sh 'mvn -B clean package'
            }
        }
        stage('Test') {
            steps {
                sh 'mvn -B test'
            }
            post {
                always {
                    junit 'target/surefire-reports/*.xml'
                }
            }
        }
        stage('Archive') {
            steps {
                archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
            }
        }
    }
}
