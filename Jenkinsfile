pipeline {
    agent any

    parameters {
        choice(
            name: 'RUN_MODE',
            choices: ['local', 'grid', 'browserstack', 'saucelabs'],
            description: 'Target execution environment'
        )
        choice(
            name: 'BROWSER',
            choices: ['chrome', 'firefox', 'edge', 'safari'],
            description: 'Target browser for test execution'
        )
        choice(
            name: 'SUITE_FILE',
            choices: [
                'src/test/resources/testng.xml',
                'src/test/resources/testng-cloud.xml',
                'src/test/resources/testng-crossbrowser.xml'
            ],
            description: 'TestNG Suite XML file to run'
        )
        booleanParam(
            name: 'HEADLESS',
            defaultValue: true,
            description: 'Run browser in headless mode (applicable to Local & Grid Chrome/Firefox)'
        )
        string(
            name: 'TEST_FILTER',
            defaultValue: '',
            description: 'Optional single test class filter (e.g., CheckoutTest, ContactUsTest). Leave empty for full suite.'
        )
    }

    environment {
        JAVA_HOME = tool name: 'Java-17', type: 'jdk'
        MAVEN_HOME = tool name: 'Maven-3.9', type: 'maven'
        PATH = "${JAVA_HOME}/bin:${MAVEN_HOME}/bin:${env.PATH}"
        // Master encryption key for AES-256 password decryption
        APP_MASTER_KEY = credentials('APP_MASTER_KEY') // Configured in Jenkins Credentials Store
    }

    options {
        timeout(time: 90, unit: 'MINUTES')
        buildDiscarder(logRotator(numToKeepStr: '30'))
        ansiColor('xterm')
    }

    stages {
        stage('Initialize & Pre-Flight Checks') {
            steps {
                echo "=========================================================="
                echo " Starting E-Commerce Automation Pipeline"
                echo " RunMode    : ${params.RUN_MODE}"
                echo " Browser    : ${params.BROWSER}"
                echo " Suite File : ${params.SUITE_FILE}"
                echo " Headless   : ${params.HEADLESS}"
                echo "=========================================================="
                sh 'java -version'
                sh 'mvn -version'
            }
        }

        stage('Start Docker Selenium Grid') {
            when {
                expression { return params.RUN_MODE == 'grid' }
            }
            steps {
                echo "Starting Selenium Grid 4 Hub and Browser Nodes..."
                sh 'docker-compose up -d --wait'
                sh 'curl -s http://localhost:4444/status | grep -q \'"ready": true\''
                echo "Selenium Grid is operational and ready for sessions."
            }
        }

        stage('Execute Automated Tests') {
            steps {
                withCredentials([
                    usernamePassword(
                        credentialsId: 'BROWSERSTACK_CREDENTIALS',
                        usernameVariable: 'BROWSERSTACK_USERNAME',
                        passwordVariable: 'BROWSERSTACK_ACCESSKEY'
                    ),
                    usernamePassword(
                        credentialsId: 'SAUCELABS_CREDENTIALS',
                        usernameVariable: 'SAUCELABS_USERNAME',
                        passwordVariable: 'SAUCELABS_ACCESSKEY'
                    ),
                    usernamePassword(
                        credentialsId: 'TEST_USER_CREDENTIALS',
                        usernameVariable: 'TEST_USER_EMAIL',
                        passwordVariable: 'TEST_USER_PASSWORD'
                    )
                ]) {
                    script {
                        def mavenCmd = "mvn clean test"
                        mavenCmd += " -Drunmode=${params.RUN_MODE}"
                        mavenCmd += " -Dbrowser=${params.BROWSER}"
                        mavenCmd += " -Dheadless=${params.HEADLESS}"
                        mavenCmd += " -DsuiteFile=${params.SUITE_FILE}"

                        if (params.TEST_FILTER && !params.TEST_FILTER.trim().isEmpty()) {
                            mavenCmd += " -Dtest=${params.TEST_FILTER.trim()}"
                        }

                        // Pass credentials securely via environment variables
                        sh """
                            export BROWSERSTACK_USERNAME="${BROWSERSTACK_USERNAME}"
                            export BROWSERSTACK_ACCESSKEY="${BROWSERSTACK_ACCESSKEY}"
                            export SAUCELABS_USERNAME="${SAUCELABS_USERNAME}"
                            export SAUCELABS_ACCESSKEY="${SAUCELABS_ACCESSKEY}"
                            export TEST_USER_EMAIL="${TEST_USER_EMAIL}"
                            export TEST_USER_PASSWORD="${TEST_USER_PASSWORD}"
                            ${mavenCmd}
                        """
                    }
                }
            }
        }
    }

    post {
        always {
            // Generate Allure Report
            script {
                try {
                    allure([
                        includeProperties: false,
                        jdk: '',
                        properties: [],
                        reportBuildPolicy: 'ALWAYS',
                        results: [[path: 'target/allure-results']]
                    ])
                } catch (Exception e) {
                    echo "Allure plugin step notice: ${e.message}"
                }
            }

            // Archive TestNG XML and Surefire Reports
            junit testResults: 'target/surefire-reports/*.xml', allowEmptyResults: true
            archiveArtifacts artifacts: 'target/surefire-reports/**, logs/**', allowEmptyArchive: true
        }

        cleanup {
            // Teardown Grid if it was started in this pipeline
            script {
                if (params.RUN_MODE == 'grid') {
                    echo "Stopping Docker Selenium Grid..."
                    sh 'docker-compose down'
                }
            }
        }

        success {
            echo "✔ All tests passed successfully!"
        }

        failure {
            echo "❌ Build failed. Please inspect Allure Report and screenshots."
        }
    }
}
