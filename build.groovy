pipeline {
    agent any
    tools {
        maven 'mvn'
    }
    options {
        timeout(unit: 'MINUTES', time: 10)
    }
    stages {
        stage('Start') {
            failFast true
            parallel {
                stage("Start notification to VK Teams") {
                    steps {
                        def MESSAGE = """Started build $BUILD_DISPLAY_NAME for project: <a href="$GIT_URL">\"$JOB_BASE_NAME\"</a>

Branch: $GIT_BRANCH
Last commit:
$GIT_COMMIT - @[$GIT_COMMITTER_EMAIL]"""

                        imSendMessage(MESSAGE: MESSAGE, CHAT_ID: "v.korobov@corp.mail.ru")

                    }
                }
                stage('Start checking project directory') {
                    steps {
                        sh "set"
                        sh "pwd"
                        sh "ls -l"
                    }
                }
            }
        }
        stage('Build'){
            steps {
                sh 'mvn --version'
                sh 'mvn package'
            }
        }
    }
    post {
        always {
            sh "set"
            archiveArtifacts artifacts: "target/*"
            imSendMessage(MESSAGE: "Build completed", CHAT_ID: "v.korobov@corp.mail.ru")
        }
    }
}
