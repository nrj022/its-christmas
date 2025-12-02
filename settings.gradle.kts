rootProject.name = "It\'s Christmas"
include(":app")
include(":unityLibrary")
project(":unityLibrary").projectDir =
    File("D:\\Unity\\its-christmas-unity\\AndroidBuild\\unityLibrary")
include(":unityLibrary:mobilenotifications.androidlib")
project(":unityLibrary:mobilenotifications.androidlib").projectDir =
    File("D:\\Unity\\its-christmas-unity\\AndroidBuild\\unityLibrary\\mobilenotifications.androidlib")
include(":unityLibrary:FirebaseApp.androidlib")
project(":unityLibrary:FirebaseApp.androidlib").projectDir =
    File("D:\\Unity\\its-christmas-unity\\AndroidBuild\\unityLibrary\\FirebaseApp.androidlib")

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
        maven { url = uri("D:/Unity/its-christmas-unity/Assets/GeneratedLocalRepo/Firebase/m2repository") }

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
