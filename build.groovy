pipeline {
    agent any

    stages {
        stage('Start') {
            steps {
                imSendMessage(MESSAGE: "Build Step!\n\nWORKSPACE: $WORKSPACE", CHAT_ID: "v.korobov@corp.mail.ru")
            }
        }
    }
    post {
        always {
            imSendMessage(MESSAGE: "Post Build Step!\n\nWORKSPACE: $WORKSPACE", CHAT_ID: "v.korobov@corp.mail.ru")
        }
    }
}
