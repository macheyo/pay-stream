plugins {
    java
}

dependencies {
    // JSON processing
    implementation("com.fasterxml.jackson.core:jackson-databind:2.14.2")

    // Validation
    implementation("jakarta.validation:jakarta.validation-api:3.0.2")

    // Test dependencies
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.9.2")
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
    options.compilerArgs.add("-parameters")
}