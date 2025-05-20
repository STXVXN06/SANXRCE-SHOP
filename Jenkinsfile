pipeline {
    agent any
    options {
        buildDiscarder(logRotator(numToKeepStr: '5'))
    }
    stages {
        stage('Build') {
            steps {
                sh './mvnw clean install site surefire-report:report'
                sh 'tree'
                publishHTML(target: [
                    reportDir: 'target/site',
                    reportFiles: 'index.html',
                    reportName: 'Surefire Report',
                    keepAll: true,
                    alwaysLinkToLastBuild: true
                ])
            }
        }
    }
}
