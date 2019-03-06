pipeline {
  agent any
  stages {
    stage('build') {
      agent {
        node {
          label 'test'
        }

      }
      steps {
        sh 'echo "build"'
      }
    }
    stage('test') {
      steps {
        sh 'echo "test"'
      }
    }
    stage('Approve') {
      steps {
        input 'Approve?'
      }
    }
    stage('Done') {
      steps {
        sh 'echo "Done"'
      }
    }
  }
}