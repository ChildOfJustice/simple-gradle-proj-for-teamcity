package SystemTests

import SystemTests.buildTypes.*
import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.Project
import jetbrains.buildServer.configs.kotlin.v2019_2.vcs.GitVcsRoot

object Project : Project({
    id("SystemTests")
    name = "System Tests"
    description = "Tests for TeamCity server started from a distribution package"

    vcsRoot(SystemTests_TeamCityE2eTestsVcsRoot)

    template(SystemTests_SetupTeamCityInTheAwsCloudRunTests)

    subProject(SystemTests_BasicScenarios.Project)
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