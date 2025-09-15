plugins {
    alias(libs.plugins.freefair.lombok)
    alias(libs.plugins.spotless)
}

dependencies {
    compileOnly(libs.project.lombok)
    annotationProcessor(libs.project.lombok)
    implementation(libs.validation.api)
    implementation(libs.slf4j.api)
    implementation(projects.common)
    implementation(projects.blobStorageService.blobStorageApiCommon)
    implementation(projects.receiverService.receiverApiCommon)
}