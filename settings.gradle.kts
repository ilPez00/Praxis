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
    }
}

rootProject.name = "PRAXIS"
include(":app")
include(":core:domain")
include(":core:data")
include(":core:ui")
include(":feature:auth")
include(":feature:onboarding")
include(":feature:goals")
include(":feature:matching")
include(":feature:messaging")
