import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.gradle
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.script
import jetbrains.buildServer.configs.kotlin.v2019_2.failureConditions.BuildFailureOnMetric
import jetbrains.buildServer.configs.kotlin.v2019_2.failureConditions.BuildFailureOnText
import jetbrains.buildServer.configs.kotlin.v2019_2.failureConditions.failOnMetricChange
import jetbrains.buildServer.configs.kotlin.v2019_2.failureConditions.failOnText
import jetbrains.buildServer.configs.kotlin.v2019_2.vcs.GitVcsRoot

/*
The settings script is an entry point for defining a TeamCity
project hierarchy. The script should contain a single call to the
project() function with a Project instance or an init function as
an argument.

VcsRoots, BuildTypes, Templates, and subprojects can be
registered inside the project using the vcsRoot(), buildType(),
template(), and subProject() methods respectively.

To debug settings scripts in command-line, run the

    mvnDebug org.jetbrains.teamcity:teamcity-configs-maven-plugin:generate

command and attach your debugger to the port 8000.

To debug in IntelliJ Idea, open the 'Maven Projects' tool window (View
-> Tool Windows -> Maven Projects), find the generate task node
(Plugins -> teamcity-configs -> teamcity-configs:generate), the
'Debug' option is available in the context menu for the task.
*/

version = "2021.1"

project {
    description = "TeamCity trunk (future 2021.1)"

    buildType(BuildDistTarGzWar)

    subProject(SystemTests)
}

object BuildDistTarGzWar : BuildType({
    name = "BuildDist (tar.gz)"

    artifactRules = "TeamCity.tar.gz => "

    steps {
        script {
            name = "Generate TeamCity dist"
            scriptContent = "echo SOMETHING > TeamCity.tar.gz"
        }
    }
})


object SystemTests : Project({
    name = "System Tests"
    description = "Tests for TeamCity server started from a distribution package"

    vcsRoot(SystemTests_TeamCityE2eTestsVcsRoot)

    template(SystemTests_SetupTeamCityInTheAwsCloudRunTests)

    subProject(SystemTests_BasicScenarios)
})

object SystemTests_SetupTeamCityInTheAwsCloudRunTests : Template({
    name = "Setup TeamCity in the AWS cloud, run tests"

    artifactRules = """
        smoke-tests/build/reports/tests/** => test-reports.zip
        smoke-tests/build/reports/allure-report/** => allure-report.zip
    """.trimIndent()

    params {
        param("system.tc.password", "admin")
        param("teamcity.distrib", "teamcity-home")
        param("runtime_jdk", "")
        checkbox("system.tc.log.debug", "",
                  checked = "true")
        param("system.base.dir", "%teamcity.build.checkoutDir%")
        param("system.tc.port", "8111")
        param("system.teamcity.start.jdk", "%env.JDK_18%")
        param("system.tc.testcases.dir", "%teamcity.build.checkoutDir%/testdata/testcases/")
        param("runTestsTasks", "")
        param("agent_jdk", "")
        select("system.browser", "chrome",
                options = listOf("chrome", "firefox", "ie"))
        param("system.tc.user", "admin")
        param("system.tc.server.opts", """"-Dteamcity.startup.maintenance=false"""")
        param("system.tc.url", "")
        param("tests.cmd.params", "")
    }

    vcs {
        root(SystemTests_TeamCityE2eTestsVcsRoot)
    }

    steps {
        gradle {
            name = "Run tests"
            id = "RUNNER_5309"
            tasks = "clean"
            buildFile = "build.gradle"
        }
    }

    failureConditions {
        executionTimeoutMin = 60
        failOnText {
            id = "BUILD_EXT_1062"
            conditionType = BuildFailureOnText.ConditionType.CONTAINS
            pattern = "An error appeared during cleanup, some of resources could not be deleted"
            failureMessage = "Check AWS cloud for uncleaned resoures"
            reverse = false
        }
        failOnMetricChange {
            id = "BUILD_EXT_1286"
            metric = BuildFailureOnMetric.MetricType.TEST_COUNT
            units = BuildFailureOnMetric.MetricUnit.DEFAULT_UNIT
            comparison = BuildFailureOnMetric.MetricComparison.LESS
            compareTo = value()
            param("anchorBuild", "lastSuccessful")
        }
    }

    dependencies {
        dependency(BuildDistTarGzWar) {
            snapshot {
                reuseBuilds = ReuseBuilds.ANY
                onDependencyFailure = FailureAction.IGNORE
                onDependencyCancel = FailureAction.CANCEL
                synchronizeRevisions = false
            }

            artifacts {
                id = "ARTIFACT_DEPENDENCY_7010"
                buildRule = lastSuccessful()
                artifactRules = "TeamCity*.tar.gz=>%teamcity.distrib%"
            }
        }
    }
})

object SystemTests_TeamCityE2eTestsVcsRoot : GitVcsRoot({
    name = "TeamCityE2eTests"
    url = "https://github.com/ChildOfJustice/TeamCityE2eTestSources.git"
    branch = "master"
    authMethod = password {
        userName = "ChildOfJustice"
        password = "credentialsJSON:7f8f64cb-29c5-40ba-aca1-3a6bf48e113d"
    }
})


object SystemTests_BasicScenarios : Project({
    name = "Basic Scenarios"
    description = "Basic functionality like run a build or use REST api"

    buildType(SystemTests_E2eTeamCityTestOnEc2)
})

object SystemTests_E2eTeamCityTestOnEc2 : BuildType({
    templates(SystemTests_SetupTeamCityInTheAwsCloudRunTests)
    name = "TeamCity e2e test on AWS EC2 with cloudprofile for agents"
    description = "Uploads built TeamCity.gz.tar and tests it in the AWS cloud with agents (CloudProfile feature). Everything runs on EC2 instances."

    artifactRules = """
        acceptance-tests/**/reports/geb => screenshots-geb.zip
        smoke-tests/pages => screenshots.zip
        smoke-tests/build/reports/tests=> testNG
    """.trimIndent()

    params {
        param("env.TEST_BUCKET_NAME", "teamcity-artifact-publishing-tests-artifact-storage-bucket")
        param("env.TEST_AGENT_INSTANCE_TYPE", "t2.micro")
        password("env.TEST_AWS_SECRET_ACCESS_KEY", "credentialsJSON:a6957c4b-d4ec-4c34-a197-92991a934de6", display = ParameterDisplay.HIDDEN)
        param("runTestsTasks", "testE2eTeamCityOnEc2")
        param("env.TEST_CF_ROLE_ARN", "arn:aws:iam::913206223978:role/TeamCityCloudFormationRoleForE2eTests")
        param("env.TEST_TEAMCITY_DIST_DIR", "%teamcity.distrib%")
        param("env.TEST_AWS_ACCESS_KEY_ID", "AKIA5JH2VERVAK5EVI3B")
        param("env.TEST_AWS_ENDPOINT", "https://s3.eu-central-1.amazonaws.com")
    }

    steps {
        script {
            name = "Check artifact dependencies"
            id = "RUNNER_4"
            scriptContent = "ls /smoke-tests/%teamcity.distrib%"
        }
        gradle {
            name = "Run tests"
            id = "RUNNER_5309"
            enabled = false
            tasks = "testE2eTeamCityOnEc2"
            buildFile = "build.gradle"
            gradleParams = "--info"
        }
        stepsOrder = arrayListOf("RUNNER_4", "RUNNER_5309")
    }

    dependencies {
        artifacts(BuildDistTarGzWar) {
            id = "ARTIFACT_DEPENDENCY_7010"
            artifactRules = "TeamCity*.tar.gz=>/smoke-tests/../%teamcity.distrib%"
        }
    }
})
