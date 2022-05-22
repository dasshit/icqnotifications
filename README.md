# ICQ/VK Teams Notifications plugin for Jenkins CI

<img src="https://icq.com/botapi/res/logo_icq_new.png" width="40%"><img src="https://myteam.mail.ru/botapi/res/logo_myteam.png" width="40%">

## Introduction

Plugin for text notifications from Jenkins CI to ICQ/Agent/VK Teams with Bot API

## Getting started

### 1. Create bot with @Metabot

### 2. Build plugin

```bash
git clone https://github.com/dasshit/icqnotifications.git
mvn verify
```

### 3. Install plugin in Jenkins settings

### 4. Configure global plugin settings

## Usage

### Can be set as Build Step

<img src="images/test.png" />

## Or used in Groovy pipelines
```groovy
pipeline {
    agent any

    stages {
        stage('Hello') {
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
```

## LICENSE

Licensed under MIT, see [LICENSE](LICENSE.md)

