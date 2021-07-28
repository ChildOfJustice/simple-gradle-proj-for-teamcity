import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.gradle
import jetbrains.buildServer.configs.kotlin.v2019_2.triggers.vcs

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
project(_Self.Project)

//project {

//    buildType(SimpleGradleProjForTeamcity_Build)

//    params {
//        param("env.TEST_AWS_ACCESS_KEY_ID", "minioadmin")
//        password("env.TEST_AWS_SECRET_ACCESS_KEY", "credentialsJSON:a6957c4b-d4ec-4c34-a197-92991a934de6", display = ParameterDisplay.HIDDEN)
//        param("env.TEST_AWS_ENDPOINT", "http://localhost:9000")
//    }

//    features {
//        feature {
//            id = "PROJECT_EXT_5"
//            type = "storage_settings"
//            param("aws.service.endpoint", "")
//            param("aws.use.default.credential.provider.chain", "true")
//            param("temp.credentials.secretKey", "notsosecret")
//            param("aws.external.id", "TeamCity-server-e2eaa432-cef0-4452-b802-63484a537a43")
//            param("aws.environment", "")
//            param("storage.name", "TEST")
//            param("storage.s3.bucket.name", "sardor-test-code")
//            param("storage.type", "S3_storage")
//            param("aws.credentials.type", "aws.access.keys")
//            param("aws.region.name", "eu-central-1")
//            param("temp.credentials.keyId", "EXAMPLE")
//            param("storage.s3.upload.presignedUrl.enabled", "true")
//        }
//        feature {
//            id = "PROJECT_EXT_6"
//            type = "active_storage"
//            param("active.storage.feature.id", "PROJECT_EXT_5")
//        }
//    }


//}

object SimpleGradleProjForTeamcity_Build : BuildType({
    id("Build")
    name = "Build"

    artifactRules = "build/distributions/TestGradle-0.1.0.zip => target"

    vcs {
        root(DslContext.settingsRoot)
    }

    steps {
        gradle {
            tasks = "clean build"
        }
    }

    triggers {
        vcs {
        }
    }
})
