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

rootProject.name = "GroceryB2B"

// Step 1 modules (Auth). More feature modules (shop-setup, home, catalog,
// cart, checkout, order-tracking, order-history, core-database) will be
// added to this file as each step is built.
include(":app")
include(":core:core-common")
include(":core:core-network")
include(":core:core-ui")
include(":core:core-database")
include(":feature:auth")
include(":feature:shop-setup")
include(":feature:home")
