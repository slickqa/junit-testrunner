pipeline {
    agent {
        docker {
            image 'openjdk:8-jdk-stretch'
            args '-v /data/jenkins/.gnupg:/.gnupg -v /data/docker/gradle:/.gradle -e GRADLE_USER_HOME="/.gradle" -e SLICK_E2E_BASEURL=http://slick/api --network slick --link slick'
        }
    }
    stages {
        stage('Build') {
            steps {
                sh '''
                    PROJ_BUILD_NUMBER=${BUILD_NUMBER}
                    if [ "$BRANCH_NAME" != "master" ]; then PROJ_BUILD_NUMBER="${BRANCH_NAME}-${PROJ_BUILD_NUMBER}"; fi
                    echo "projectBuildNumber=${PROJ_BUILD_NUMBER}" >gradle.properties
                '''
                sh './gradlew clean build -x test'
            }
        }
        stage('Test') { 
            steps {
                sh './gradlew test'
            }
            post {
                always {
                    junit 'build/test-results/test/*.xml'
                }
            }
        }
        stage('Deploy') {
            when {
                branch 'master'
            }
            steps {
                sh './gradlew build publishToNexus closeAndReleaseRepository -x test'
            }
        }
    }
}
