pipeline {
  agent any
  stages {
    stage('first') {
      steps {
        parallel(
          "first": {
            echo 'hello world'
            
          },
          "": {
            echo 'hello parallel'
            
          }
        )
      }
    }
    stage('second') {
      steps {
        sh 'echo \'done\''
      }
    }
  }
  environment {
    test = '12'
  }
}