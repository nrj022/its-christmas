rootProject.name = "Z Card"
include(":app")
include(":unityLibrary")
project(":unityLibrary").projectDir = File("unity_export/unityLibrary")
include(":unityLibrary:mobilenotifications.androidlib")
include(":unityLibrary:FirebaseApp.androidlib")

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
            dirs("${project(":unityLibrary").projectDir}/libs")
        }
        // Unity GeneratedLocalRepo 경로 추가
        maven {
            url = uri("${project(":unityLibrary").projectDir}/GeneratedLocalRepo/Firebase/m2repository")
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
