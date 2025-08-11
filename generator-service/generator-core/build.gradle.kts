plugins {
    alias(libs.plugins.freefair.lombok)
}

dependencies {
    compileOnly(libs.project.lombok)
    annotationProcessor(libs.project.lombok)
    implementation(libs.jackson.databind)
    implementation(libs.slf4j.api)
    implementation(projects.blobStorageService.blobStorageApiCommon)
    implementation(projects.common)
    implementation(projects.models.xsd)
    implementation(projects.models.json)
    implementation(libs.jakarta.bind.api)
    implementation(libs.validation.api)
    testImplementation(libs.json.unit.assertj)
    testImplementation(libs.mockito.core)
}

