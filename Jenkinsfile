pipeline {

    environment {
        SERVER_EDIT_SECRET = credentials('meta-server-edit-secret')
    }

    // Use a java11 agent globally as we're mostly doing Java stuff
    agent {
        label 'java11'
    }

    stages {
        stage('Build') {
            steps {
                sh './gradlew build'
            }
        }

        stage('Containerize') {
            steps {
                // Prepare GAR credentials for Jib - takes a bit extra handling in the Gradle file
                withCredentials([file(credentialsId: 'jenkins-gar-sa', variable: 'GOOGLE_APPLICATION_CREDENTIALS')]) {
                    sh "./gradlew --console=plain jib" // Jib outputs some gibberish progress logging we skip with "plain"
                }
            }
        }

        stage('Prep CD') {
            steps {
                // Customize Kubernetes manifests for deployment (TODO: Likely could tweak here for test/preview envs)
                sh './gradlew prepareCD'

                // Stash the modified 'k8s' directory so we can use it in the next stage (on a new agent)
                stash includes: 'k8s/**', name: 'k8s-stash'
            }
        }

        stage('k8s deploy') {
            agent {
                label 'kubectl' // We swap from the Java build agent to a Kubernetes agent for deployment
            }
            steps {
                // Unstash the 'k8s' directory on the new agent
                unstash 'k8s-stash'
                sh 'ls -la k8s'

                // Deploy to Kubernetes - this build agent uses a secondary container for kubectl
                container("utility") {
                    withKubeConfig(credentialsId: 'utility-admin-kubeconfig-sa-token') {
                        sh 'sed -i "s|SERVER_EDIT_SECRET_FROM_JENKINS|${SERVER_EDIT_SECRET}|g" k8s/secrets.yaml'
                        sh 'kubectl get ns'
                        sh 'kubectl create ns meta-server || true' // Don't break if namespace already exists
                        sh 'kubectl apply -f k8s/ -n meta-server'
                        echo "Deployment complete, site should be available shortly at https://meta-server.terasology.io"
                    }
                }
            }
        }
    }
}
