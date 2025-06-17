import java.util.Properties

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
        google()
        mavenCentral()
        mavenLocal()

        maven(url = "https://jitpack.io")

        maven {
            url = uri("https://maven.pkg.github.com/Luscii/actions-sdk-android")

            credentials {
                Properties().apply { load(file("local.properties").inputStream()) }.run {
                    username = getProperty("maven.username")
                    password = getProperty("maven.password")
                }
            }
        }
    }
}

rootProject.name = "My Application"
include(":app")
 