pluginManagement {
    val quarkusPluginVersion: String by settings
    val quarkusPluginId: String by settings
    repositories {
        mavenCentral()
        gradlePluginPortal()
        mavenLocal()
    }
    plugins {
        id(quarkusPluginId) version quarkusPluginVersion
    }
}

rootProject.name="pay-stream"

// Include all services in the apps directory
//include(":apps:rest-service")
//include(":apps:processing-service")


// Shared libraries/modules
include(":shared:common")
include(":shared:models")

// This ensures all project paths are evaluated relative to the root project
//project(":apps:rest-service").projectDir = file("apps/rest-service")
//project(":apps:processing-service").projectDir = file("apps/processing-service")
project(":shared:common").projectDir = file("shared/common")
project(":shared:models").projectDir = file("shared/models")

