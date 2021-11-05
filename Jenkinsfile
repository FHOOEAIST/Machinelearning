pipeline {
    agent {
      label 'master'
    }

    options {
        gitLabConnection('Gitlab')
        gitlabBuilds(builds: ['setup', 'build'])
    }

    stages {
        stage('prepare') {
            steps {
                script{
                    try {
                        sh '''
                        containerId=jenkins
                        docker network create neo4jmltest
                        docker network connect neo4jmltest $containerId
                        docker container run --rm --network neo4jmltest --name neo4j-graph-ml-db -d aist.fh-hagenberg.at:18444/repository/docker-util/aist-neo4j-with-apoc
                        '''
                    } catch(ex) {
                        echo 'could not start docker container, trying to reboot it...'
                        sh '''
                        containerId=jenkins
                        docker network disconnect neo4jmltest $containerId || true
                        docker container stop neo4j-graph-ml-db || true
                        docker network rm neo4jmltest
                        docker network create neo4jmltest
                        docker network connect neo4jmltest $containerId
                        docker container run --rm --network neo4jmltest --name neo4j-graph-ml-db -d aist.fh-hagenberg.at:18444/repository/docker-util/aist-neo4j-with-apoc
                        '''
                        currentBuild.result = 'SUCCESS' // If we get this far, override the FAILURE build result from the exception
                    } finally {
                        sh 'sleep 30s'
                    }

                }

            }
            post {
                failure {
                    updateGitlabCommitStatus name: 'setup', state: 'failed'
                }
                success {
                    updateGitlabCommitStatus name: 'setup', state: 'success'
                }
                unstable {
                    updateGitlabCommitStatus name: 'setup', state: 'failed'
                }
            }
        }


        stage('build') {
            steps {
                sh '''
                mvn -Dmaven.javadoc.skip=true -Pjenkins -Psonar-coverage clean install sonar:sonar -Dsonar.scm.disabled=true
                '''
            }
            post {
                always {
                  junit '**/target/surefire-reports/TEST-*.xml'
                   sh '''
                  containerId=jenkins
                  docker network disconnect neo4jmltest $containerId
                    docker container stop neo4j-graph-ml-db
                    docker network rm neo4jmltest
                '''
                }
                failure {
                    updateGitlabCommitStatus name: 'build', state: 'failed'
                }
                success {
                    updateGitlabCommitStatus name: 'build', state: 'success'
                }
                unstable {
                    updateGitlabCommitStatus name: 'build', state: 'failed'
                }
            }
        }
    }
}

