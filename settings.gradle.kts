rootProject.name = "It\'s Halloween"
include(":app")
include(":unityLibrary")
project(":unityLibrary").projectDir = File("D:\\Unity\\its-halloween-unity\\AndroidBuild\\unityLibrary")

include(":unityLibrary:mobilenotifications.androidlib")
project(":unityLibrary:mobilenotifications.androidlib").projectDir =
    File("D:\\Unity\\its-halloween-unity\\AndroidBuild\\unityLibrary\\mobilenotifications.androidlib")

pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        flatDir {
            dirs("${project(":unityLibrary").projectDir}/libs")
        }
        google()
        mavenCentral()
    }
}
include(":core:database")
include(":feature:main")
include(":feature:card")
include(":core:designsystem")
include(":core:data")
include(":core:domain")
