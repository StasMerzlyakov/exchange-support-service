plugins {
    alias(libs.plugins.freefair.lombok)
    alias(libs.plugins.spring.boot)
}

dependencies {
    implementation(projects.common)
    implementation(projects.blobStorageService.blobStorageApiV1Grpc)
    implementation(projects.blobStorageService.blobStorageApiV1Jackson)
    implementation(projects.blobStorageService.blobStorageApiCommon)
    implementation(projects.blobStorageService.blobStorageCore)
    implementation(platform(libs.spring.boot.dependencies))

    compileOnly(libs.project.lombok)
    annotationProcessor(libs.project.lombok)

    implementation(libs.spring.boot.starter.webflux)
    implementation(libs.spring.starter.grpc)
    implementation(libs.mapstruct)
    annotationProcessor(libs.mapstruct.processor)

    implementation(libs.starter.actuator)
    implementation(libs.starter.aop)
    implementation(libs.minio)
    implementation(libs.blockhound)

    testImplementation(libs.junit.jupiter)
}