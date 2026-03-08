includeBuild("build-logic")

pluginManagement {
    repositories {
        google()
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

rootProject.name = "Pulse"

include(":app")

include(":core:core-ui")
include(":core:core-network")
include(":core:core-database")
include(":core:core-model")
include(":core:core-common")

include(":feature:feature-auth")
include(":feature:feature-feed")
include(":feature:feature-post")
include(":feature:feature-profile")
include(":feature:feature-search")
include(":feature:feature-notifications")
include(":feature:feature-comments")