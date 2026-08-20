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
                        printf '%s\n' '<!doctype html>' '<html lang="en">' '<head>' '<meta charset="UTF-8">' '<meta name="viewport" content="width=device-width, initial-scale=1">' '<title>Code Quality Report</title>' '<style>:root { --ink: #102a43; --muted: #627d98; --line: #d9e2ec; --paper: #f5f7fa; --white: #ffffff; --mint: #0f766e; --mint-soft: #d7f5ef; --coral: #c2410c; --coral-soft: #ffedd5; } * { box-sizing: border-box; } body { margin: 0; background: var(--paper); color: var(--ink); font-family: Georgia, "Times New Roman", serif; } .shell { max-width: 1180px; margin: 0 auto; padding: 32px 20px 56px; } .hero { padding: 34px; border-radius: 18px; background: var(--ink); color: var(--white); box-shadow: 0 16px 40px rgba(16, 42, 67, .16); } .eyebrow { margin: 0 0 10px; color: #9fe7dc; font: 700 12px Arial, sans-serif; letter-spacing: 2px; text-transform: uppercase; } h1 { margin: 0; font-size: clamp(32px, 5vw, 56px); line-height: 1; } .lede { max-width: 650px; margin: 16px 0 0; color: #d9e2ec; font: 16px/1.6 Arial, sans-serif; } .metrics { display: grid; grid-template-columns: repeat(2, minmax(0, 1fr)); gap: 16px; margin: 22px 0; } .metric { padding: 20px; border: 1px solid var(--line); border-radius: 14px; background: var(--white); } .metric-label { margin: 0 0 8px; color: var(--muted); font: 700 12px Arial, sans-serif; letter-spacing: 1px; text-transform: uppercase; } .metric-value { margin: 0; font-size: 24px; } .metric-quality { color: var(--mint); } .metric-bugs { color: var(--coral); } .report-card { overflow: hidden; margin-top: 22px; border: 1px solid var(--line); border-radius: 14px; background: var(--white); box-shadow: 0 8px 24px rgba(16, 42, 67, .06); } .report-heading { display: flex; align-items: center; justify-content: space-between; gap: 16px; padding: 20px 24px; border-bottom: 1px solid var(--line); } .report-heading h2 { margin: 0; font-size: 25px; } .badge { padding: 7px 10px; border-radius: 999px; background: var(--mint-soft); color: var(--mint); font: 700 11px Arial, sans-serif; letter-spacing: 1px; text-transform: uppercase; } .report-body { padding: 20px 24px; overflow-x: auto; } .report-body table { width: 100%; border-collapse: collapse; font: 14px/1.5 Arial, sans-serif; } .report-body th, .report-body td { padding: 10px 12px; border: 1px solid var(--line); text-align: left; vertical-align: top; } .report-body th { background: #edf2f7; color: var(--ink); } .footer { margin-top: 28px; color: var(--muted); font: 13px Arial, sans-serif; } @media (max-width: 640px) { .shell { padding: 18px 12px 36px; } .hero { padding: 25px 20px; } .metrics { grid-template-columns: 1fr; } .report-heading { align-items: flex-start; flex-direction: column; padding: 18px; } .report-body { padding: 14px; } }</style>' '</head>' '<body>' '<main class="shell">' '<header class="hero"><p class="eyebrow">Automated delivery gate</p><h1>Code Quality Report</h1><p class="lede">A clear, combined view of the checks that protect this build from style problems and likely defects.</p></header>' '<div class="metrics"><article class="metric"><p class="metric-label">Style review</p><p class="metric-value metric-quality">Checkstyle</p></article><article class="metric"><p class="metric-label">Defect review</p><p class="metric-value metric-bugs">SpotBugs</p></article></div>' '<section class="report-card"><div class="report-heading"><h2>Checkstyle</h2><span class="badge">Code standards</span></div><div class="report-body">' > target/site/quality-report.html
                        awk 'index($0, "<body") {inside=1; next} index($0, "</body>") {exit} inside {print}' target/site/checkstyle.html >> target/site/quality-report.html
                        printf '%s\n' '</div></section>' '<section class="report-card"><div class="report-heading"><h2>SpotBugs</h2><span class="badge">Defect detection</span></div><div class="report-body">' >> target/site/quality-report.html
                        awk 'index($0, "<body") {inside=1; next} index($0, "</body>") {exit} inside {print}' target/site/spotbugs.html >> target/site/quality-report.html
                        printf '%s\n' '</div></section>' '<p class="footer">Generated automatically by the Jenkins quality pipeline.</p>' '</main>' '</body>' '</html>' >> target/site/quality-report.html
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
                    publishHTML([
                        reportDir: 'target/site',
                        reportFiles: 'quality-report.html',
                        reportName: 'Code Quality Report',
                        keepAll: true,
                        alwaysLinkToLastBuild: true,
                        allowMissing: false
                    ])
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
