// Jenkins Docker Ecr: https://octopus.com/blog/jenkins-docker-ecr
// SSH Agent: https://www.jenkins.io/doc/pipeline/steps/ssh-agent/

pipeline {
    agent any
    
    environment {
        VERSION_NAME = sh(
            script: "grep -o \"^\\s*versionName\\s*.*\" app/build.gradle | awk '{ print \$2 }' | tr -d \\''\"'", returnStdout: true
        ).trim()
        APPLICATION_ID = sh(
            script: "grep -o \"applicationId\\s.*\" app/build.gradle | awk \'{ print \$2 }\' | tr -d \\''\"'", returnStdout: true
        ).trim()

        PARTNER = 'Bkplus'

        ADS_CONFIG_REPO = 'https://github.com/Apero-Partner/Android-Ads-Config.git'
        ADS_CONFIG_FOLDER = 'Android-Ads-Config'
        ADS_CONFIG_FILE = 'bkplus-flashlight.adsconf'

        RELEASE_KEY_PATH = 'Android-Ads-Config/key/release.jks'

        RELEASE_FOLDER = "../Release/${PARTNER}/${APPLICATION_ID}/${VERSION_NAME}/${BUILD_TIMESTAMP}"

        GITHUB_CREDENTIAL_ID = "Github-Account-JiraSupport"
        BRANCH_MASTER = 'main'
        OUTPUT_LINK = "https://${PARTNER}.apero.vn/${APPLICATION_ID}/${VERSION_NAME}/${BUILD_TIMESTAMP}"

	    DISCORD_WEBHOOK_URL = "https://discord.com/api/webhooks/1100673079887282216/YFjr1OvbR65RDp1av5cqGRko1emXxUbm3kRYl4q0s2W8Q650LS4xjbXrwgUAVsOf__7H"
    }

    stages {
        stage('Git SCM') {
            when {
                branch BRANCH_MASTER
            }

            steps {
                echo "Code Version = $VERSION_NAME"
                echo "Application Id = $APPLICATION_ID"
                echo "Partner = $PARTNER"
                echo "Ads Config Repo = $ADS_CONFIG_REPO"
                echo "Ads Config Folder = $ADS_CONFIG_FOLDER"
                echo "Ads Config File = $ADS_CONFIG_FILE"
                echo "Release Key Path = $RELEASE_KEY_PATH"
                echo "Github Credential Id = $GITHUB_CREDENTIAL_ID"

                script {
                    checkout scm

                    withCredentials([gitUsernamePassword(credentialsId: "$GITHUB_CREDENTIAL_ID",
                                     gitToolName: 'git-tool')]) {
                        if(fileExists("./$ADS_CONFIG_FOLDER")) {
                            echo 'Android Ads Config existed!!!'
                            sh "cd $ADS_CONFIG_FOLDER && git pull"
                        } else {
                            sh "git clone $ADS_CONFIG_REPO"
                        }
                    }
                    sh 'ls -lah'
                }

                echo "Prepare Ads Config"
                sh """
                    ./$ADS_CONFIG_FOLDER/composeAdsConfig.sh ./$ADS_CONFIG_FOLDER/adsConfig/$ADS_CONFIG_FILE ./app/build.gradle
                    mkdir -p app/key
                    cp $RELEASE_KEY_PATH app/key/release.jks
                """
            }
        }

        stage('Build') {
            when {
                branch BRANCH_MASTER
            }

            steps {
                sh '''
                    chmod +x gradlew
                    ./gradlew clean
                    ./gradlew app:assembleAppDev
                    ./gradlew app:bundleAppProduct
                '''
            }
        }

        stage('Deploy Artifact') {
            when {
                branch BRANCH_MASTER
            }

    		steps {
    		    script {
        		    if(fileExists("../Release/$PARTNER/$APPLICATION_ID")) {
        		       echo "File ../Release/$PARTNER/$APPLICATION_ID exist skip!!!"
        		    } else {
        		        echo "Prearing ../Release/$PARTNER/$APPLICATION_ID"
        		        sh "mkdir ../Release/$PARTNER/$APPLICATION_ID"
        		    }
        		    if(fileExists("../Release/$PARTNER/$APPLICATION_ID/$VERSION_NAME")) {
        		        echo "File ../Release/$PARTNER/$APPLICATION_ID/$VERSION_NAME exist skip!!!"
        		    } else {
        		        echo "Prearing ../Release/$PARTNER/$APPLICATION_ID/$VERSION_NAME"
        		        sh "mkdir ../Release/$PARTNER/$APPLICATION_ID/$VERSION_NAME"
        		    }
    		    }

    		    sh """
    		        mkdir $RELEASE_FOLDER
    		        cp -r app/build/outputs/apk $RELEASE_FOLDER/apk
    		        cp -r app/build/outputs/bundle $RELEASE_FOLDER/bundle
    		    """
    		}
        }

        stage('Discord Notification') {
            when {
                branch BRANCH_MASTER
            }


            steps {
                discordSend(
                    description: "${currentBuild.currentResult}\n\nBuild: ${env.BUILD_NUMBER} \nOutput at: \n${env.OUTPUT_LINK}\n\nMore info at: \n${env.BUILD_URL}",
                    unstable: true,
                    link: env.BUILD_URL,
                    result: "${currentBuild.currentResult}",
                    title: "${JOB_NAME}",
                    webhookURL: env.DISCORD_WEBHOOK_URL
                )
	        }
        }
    }

    post {
        failure {
            discordSend(
                description: "${currentBuild.currentResult}\n\nBuild: ${env.BUILD_NUMBER} \nOutput at: \n${env.OUTPUT_LINK}\n\nMore info at: \n${env.BUILD_URL}",
                unstable: true,
                link: env.BUILD_URL,
                result: "${currentBuild.currentResult}",
                title: "${JOB_NAME}",
                webhookURL: env.DISCORD_WEBHOOK_URL
            )
        }
    }
}
