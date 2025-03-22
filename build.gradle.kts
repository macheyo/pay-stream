// Root build file
plugins {
    id("io.quarkus") version "3.19.2" apply false
}

allprojects {
    group = "com.justice"
    version = "1.0.0-SNAPSHOT"

    repositories {
        mavenCentral()
        mavenLocal()
    }
}

// This task helps to run all services at once
// Note: You'll need to configure each service with a different port
tasks.register("runAll") {
    description = "Run all services"
    // Uncomment as you add more services
    dependsOn(":apps:transaction-service:quarkusDev")
//    dependsOn(":apps:processing-service:quarkusDev")
}
