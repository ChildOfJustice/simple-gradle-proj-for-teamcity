package SystemTests_BasicScenarios.buildTypes

import _Self.buildTypes.BuildDistTarGzWar
import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.gradle
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.script

object SystemTests_E2eTeamCityTestOnEc2 : BuildType({
    templates(SystemTests.buildTypes.SystemTests_SetupTeamCityInTheAwsCloudRunTests)
    name = "TeamCity e2e test on AWS EC2 with cloudprofile for agents"
    description = "Uploads built TeamCity.gz.tar and tests it in the AWS cloud with agents (CloudProfile feature). Everything runs on EC2 instances."

    artifactRules = """
        acceptance-tests/**/reports/geb => screenshots-geb.zip
        smoke-tests/pages => screenshots.zip
        smoke-tests/build/reports/tests=> testNG
    """.trimIndent()

    params {
        param("runTestsTasks", "testE2eTeamCityOnEc2")

        param("env.TEST_AWS_ACCESS_KEY_ID", "AKIAEXAMPLE")
        password("env.TEST_AWS_SECRET_ACCESS_KEY", "credentialsJSON:a6957c4b-d4ec-4c34-a197-92991a934de6", display = ParameterDisplay.HIDDEN)
        param("env.TEST_AWS_ENDPOINT", "https://s3.eu-central-1.amazonaws.com")
        param("env.TEST_BUCKET_NAME", "teamcity-artifact-publishing-tests-artifact-storage-bucket")
        param("env.TEST_AGENT_INSTANCE_TYPE", "t2.micro")
        param("env.TEST_CF_ROLE_ARN", "arn:aws:iam::913206223978:role/TeamCityCloudFormationRoleForE2eTests")
        param("env.TEST_TEAMCITY_DIST_DIR", "%teamcity.distrib%")
    }

    steps {
        script {
            name = "Check artifact dependencies"
            id = "RUNNER_4"
            scriptContent = "ls %teamcity.distrib%"
        }
        gradle {
            name = "Run tests"
            id = "RUNNER_5309"
            tasks = "testE2eTeamCityOnEc2"
            buildFile = "build.gradle"
            gradleParams = "--info"
        }
        stepsOrder = arrayListOf("RUNNER_4", "RUNNER_5309")
    }

    dependencies {
        artifacts(BuildDistTarGzWar) {
            id = "ARTIFACT_DEPENDENCY_7010"
            artifactRules = "TeamCity*.tar.gz=>%teamcity.distrib%"
        }
    }
})
