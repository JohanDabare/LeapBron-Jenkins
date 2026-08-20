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
                script {
                    def siteStatus = sh(returnStatus: true, script: 'mvn -B site')
                    def dashboardStatus = sh(returnStatus: true, script: '''
                        printf '%s\n' '<!doctype html>' '<html lang="en">' '<head>' '<meta charset="UTF-8">' '<title>Code Quality Report</title>' '<style>body { font-family: Arial, sans-serif; margin: 24px; color: #222; } section { margin-top: 24px; } table { border-collapse: collapse; width: 100%; } th, td { border: 1px solid #ccc; padding: 8px; text-align: left; } th { background: #f0f0f0; }</style>' '</head>' '<body>' '<h1>Code Quality Report</h1>' '<p>This page combines the Checkstyle and SpotBugs results.</p>' '<section><h2>Checkstyle</h2><p>These are the Checkstyle findings.</p>' '<div id="checkstyle-results">' > target/site/quality-report.html
                        awk 'index($0, "<body") {inside=1; next} index($0, "</body>") {exit} inside {print}' target/site/checkstyle.html >> target/site/quality-report.html
                        printf '%s\n' '</div></section>' '<section><h2>SpotBugs</h2><p>These are the SpotBugs findings.</p>' '<div id="spotbugs-results">' >> target/site/quality-report.html
                        awk 'index($0, "<body") {inside=1; next} index($0, "</body>") {exit} inside {print}' target/site/spotbugs.html >> target/site/quality-report.html
                        printf '%s\n' '</div></section>' '</body>' '</html>' >> target/site/quality-report.html
                    ''')
                    def checkstyleStatus = sh(returnStatus: true, script: 'mvn -e checkstyle:check')
                    def spotbugsStatus = sh(returnStatus: true, script: 'mvn -B spotbugs:check')

                    echo "Report generation exit code: ${siteStatus}"
                    echo "Combined report exit code: ${dashboardStatus}"
                    echo "Checkstyle exit code: ${checkstyleStatus}"
                    echo "SpotBugs exit code: ${spotbugsStatus}"

                    if (siteStatus != 0 || dashboardStatus != 0 || checkstyleStatus != 0 || spotbugsStatus != 0) {
                        error 'Quality checks failed. See the archived HTML reports for details.'
                    }

                    echo 'Quality check completed successfully.'
                }
            }
            post {
                always {
                    archiveArtifacts artifacts: 'target/site/quality-report.html, target/site/checkstyle.html, target/site/spotbugs.html', fingerprint: true
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
