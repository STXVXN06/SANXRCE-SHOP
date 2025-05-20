pipeline {
    agent any
    options {
        buildDiscarder(logRotator(numToKeepStr: '5'))
    }
    stages {
        stage('Build') {
            steps {
                sh './mvnw clean install site surefire-report:report'
                sh 'tree target/site' // para ver que existe la carpeta y archivos
            }
        }
    }
    post {
        always {
            publishHTML([
                reportDir: 'target/site',
                reportFiles: 'surefire-report.html',
                reportName: 'Surefire Report',
                keepAll: true,
                alwaysLinkToLastBuild: true
            ])
        }
    }
}