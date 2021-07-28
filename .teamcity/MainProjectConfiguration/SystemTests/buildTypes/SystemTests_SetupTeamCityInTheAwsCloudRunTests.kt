package SystemTests.buildTypes


import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.ant
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.gradle
import jetbrains.buildServer.configs.kotlin.v2019_2.failureConditions.BuildFailureOnMetric
import jetbrains.buildServer.configs.kotlin.v2019_2.failureConditions.BuildFailureOnText
import jetbrains.buildServer.configs.kotlin.v2019_2.failureConditions.failOnMetricChange
import jetbrains.buildServer.configs.kotlin.v2019_2.failureConditions.failOnText
import jetbrains.buildServer.configs.kotlin.v2019_2.triggers.schedule
import jetbrains.buildServer.configs.kotlin.v2019_2.vcs.GitVcsRoot

object SystemTests_SetupTeamCityInTheAwsCloudRunTests : Template({
    name = "Setup TeamCity in the AWS cloud, run tests"

    artifactRules = """
        smoke-tests/build/reports/tests/** => test-reports.zip
        smoke-tests/build/reports/allure-report/** => allure-report.zip
    """.trimIndent()
//    buildNumberPattern = "%build.counter% (${BuildDistTarGzWar.depParamRefs.buildNumber})"

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
        //param("system.seleniumHub", "%jetbrains.selenium.grid%")
        //param("system.tc.log.dir", "%system.teamcity.inst%/logs")
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
//            gradleParams = "%tests.cmd.params%"
//            jdkHome = "%env.JDK_18_x64%"
        }
//        gradle {
//            name = "build allure report"
//            id = "RUNNER_7620"
//            executionMode = BuildStep.ExecutionMode.RUN_ON_FAILURE
//            tasks = ":smoke-tests:allureReport"
//            buildFile = "build.gradle"
//            jdkHome = "%env.JDK_18%"
//        }
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
        dependency(_Self.buildTypes.BuildDistTarGzWar) {
            snapshot {
                reuseBuilds = ReuseBuilds.ANY
                onDependencyFailure = FailureAction.IGNORE
                onDependencyCancel = FailureAction.CANCEL
                synchronizeRevisions = false
            }

            artifacts {
                id = "ARTIFACT_DEPENDENCY_7010"
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
