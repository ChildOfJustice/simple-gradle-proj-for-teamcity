package _Self.buildTypes

import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.gradle
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.script
import jetbrains.buildServer.configs.kotlin.v2019_2.failureConditions.BuildFailureOnMetric
import jetbrains.buildServer.configs.kotlin.v2019_2.failureConditions.BuildFailureOnText
import jetbrains.buildServer.configs.kotlin.v2019_2.failureConditions.failOnMetricChange
import jetbrains.buildServer.configs.kotlin.v2019_2.failureConditions.failOnText

object BuildDistTarGzWar : BuildType({
    name = "BuildDist (tar.gz)"

    artifactRules = "TeamCity.tar.gz => "

    steps {
        script {
            name = "Generate TeamCity dist"
            id = "RUNNER_2"
//            executionMode = BuildStep.ExecutionMode.RUN_ON_FAILURE
            scriptContent = """echo SOMETHING > TeamCity.tar.gz"""
        }
    }

})
