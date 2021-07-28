package _Self

import _Self.buildTypes.*
import jetbrains.buildServer.configs.kotlin.v2019_2.Project
import jetbrains.buildServer.configs.kotlin.v2019_2.projectFeatures.ProjectReportTab
import jetbrains.buildServer.configs.kotlin.v2019_2.projectFeatures.projectReportTab

object Project : Project({
    description = "TeamCity trunk (future 2021.1)"
    //defaultTemplate = RelativeId("DefaultTemplate")


    buildType(BuildDistTarGzWar)
//    template(SystemTests_SystemTests)
    subProject(SystemTests.Project)
})
