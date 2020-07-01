pipeline {
    agent any

    stages {

        stage("Compile Stage"){
            steps {
                echo 'compiling the application'
                withMaven(maven: 'maven_3_6_3') {
                    sh 'mvn clean compile'
                }
            }
        }


         stage("Test Stage"){
            steps {
                echo 'testing the application'
                 withMaven(maven: 'maven_3_6_3') {
                    sh 'mvn test'
                }
            }
        }


         stage("Package Stage"){
            steps {
                echo 'packaging the application'
                 withMaven(maven: 'maven_3_6_3') {
                    sh 'mvn package'
                }
            }
        }

    }

}