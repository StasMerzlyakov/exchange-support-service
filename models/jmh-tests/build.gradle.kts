plugins {
    alias(libs.plugins.mijmh)
}

dependencies {
    implementation(projects.models.xsd)
    implementation(libs.jakarta.bind.api)
    testRuntimeOnly(libs.jaxb.runtime)
}