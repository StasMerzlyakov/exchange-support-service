plugins {
    alias(libs.plugins.freefair.lombok)
    alias(libs.plugins.spotless)
}

dependencies {
    implementation(projects.blobStorageService.blobStorageApiCommon)
    compileOnly(libs.project.lombok)
    annotationProcessor(libs.project.lombok)
    implementation(libs.slf4j.api)

    implementation(libs.minio)
    implementation(libs.reactor.core)

    testImplementation(libs.assertj.core)
    testImplementation(libs.mockito.core)
    testImplementation(libs.junit.jupiter)
    testImplementation(libs.reactor.test)
    testImplementation(libs.mockito.junit.jupiter)
    testRuntimeOnly(libs.slf4j.simple)
    testImplementation(libs.testcontainer.minio)
    testImplementation(projects.blobStorageService.testUtils)
}