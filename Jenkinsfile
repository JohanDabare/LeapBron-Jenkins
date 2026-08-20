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
        stage('Build') {
            steps {
                sh 'mvn -B clean package'
            }
        }
        stage('Quality Check') {
            steps {
                sh 'mvn -B site'
                echo 'Checkstyle and SpotBugs HTML reports generated.'
                sh 'mvn -e checkstyle:check'
                sh 'mvn -B spotbugs:check'
                echo 'Quality check completed successfully.'
            }
            post {
                always {
                    archiveArtifacts artifacts: 'target/site/checkstyle.html, target/site/spotbugs.html', fingerprint: true
                }
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
