package SystemTests_BasicScenarios

import SystemTests_BasicScenarios.buildTypes.*
import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.Project

object Project : Project({
    id("SystemTests_BasicScenarios")
    name = "Basic Scenarios"
    description = "Basic functionality like run a build or use REST api"

    buildType(SystemTests_E2eTeamCityTestOnEc2)
})
