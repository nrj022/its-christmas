rootProject.name = "Z Card"
include(":app")
include(":unityLibrary")
project(":unityLibrary").projectDir = File("unity_export/unityLibrary")
include(":unityLibrary:mobilenotifications.androidlib")

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
    repositoriesMode.set(RepositoriesMode.PREFER_SETTINGS)
    repositories {
        flatDir {
            dirs(rootProject.projectDir.resolve("libs"))
        }

        google()
        mavenCentral()
    }
}
include(":core:database")
include(":core:designsystem")
include(":core:data")
include(":core:domain")
include(":feature")
