@library('jenkins-infra/pipeline-library')
pipeline {
  agent any
  stages {
    stage('build') {
      steps {
buildPlugin(platforms: ['linux'], jdkVersions: [8])

office365ConnectorSend message: "builded", status:"success", webhookUrl:'https://outlook.office.com/webhook/f4349b83-aafc-465a-bd3f-e9dbe1787dd3@ed578c83-748c-4770-bc33-99af375801a8/JenkinsCI/69821aa51bed47609719ae87248c7b14/6207b6d6-0e65-4b7c-9040-549821dcc490'

      }
    }
  }
  environment {
    test = '12'
  }
}
