import jetbrains.buildServer.configs.kotlin.*
import jetbrains.buildServer.configs.kotlin.buildFeatures.exposeAwsCredentialsToEnvVars
import jetbrains.buildServer.configs.kotlin.buildSteps.script
import jetbrains.buildServer.configs.kotlin.projectFeatures.awsConnection

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

version = "2022.04"

project {
    description = "Test dist"

    buildType(BuildDistTarGzWar)

    features {
        awsConnection {
            id = "PROJECT_EXT_4"
            name = "Amazon Web Services"
            credentialsType = static {
                accessKeyId = "TEST"
                secretAccessKey = "credentialsJSON:fad50697-d27a-4ed2-b864-206bfa9debff"
                regionName = "us-east-1"
                useSessionCredentials = true
                sessionDuration = "60"
                stsEndpoint = "https://sts.amazonaws.com"
            }
        }
    }
}

object BuildDistTarGzWar : BuildType({
    name = "BuildDist (tar.gz)"

    artifactRules = "Test.tar.gz => "

    steps {
        script {
            name = "Generate Test dist"
            scriptContent = "echo SOMETHING > Test.tar.gz"
        }
    }

    features {
        exposeAwsCredentialsToEnvVars {
            chosenAwsConnectionId = "PROJECT_EXT_4"
        }
    }
})
