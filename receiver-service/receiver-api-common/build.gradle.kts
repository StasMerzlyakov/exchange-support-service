plugins {
    alias(libs.plugins.freefair.lombok)
    alias(libs.plugins.spotless)
}

dependencies {
    implementation(libs.validation.api)
    implementation(projects.common)
    compileOnly(libs.project.lombok)
    annotationProcessor(libs.project.lombok)
}