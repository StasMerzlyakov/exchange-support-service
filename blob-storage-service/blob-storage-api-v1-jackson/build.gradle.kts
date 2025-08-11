plugins {
    alias(libs.plugins.spotless)
    alias(libs.plugins.openapi.generator)
}

sourceSets {
    main {
        java.srcDir(layout.buildDirectory.dir("generate-resources/main/src/main/java"))
    }
}



openApiGenerate {
    val openapiGroup = "${rootProject.group}.api.v1"
    generatorName.set("java")
    packageName.set(openapiGroup)
    apiPackage.set("$openapiGroup.api")
    modelPackage.set("$openapiGroup.models")
    inputSpec.set(rootProject.ext["openapi-spec-v1"] as String)

    globalProperties.apply {
        put("models", "")
        put("modelDocs", "false")
    }

    configOptions.set(
        mapOf(
            "dateLibrary" to "string",
            "enumPropertyNaming" to "MACRO_CASE",
            "collectionType" to "list",
            "library" to "resttemplate"
        )
    )
}

tasks.spotlessJava {
    inputs.files(openApiGenerate.outputDir)
    dependsOn(tasks.named("openApiGenerate"))
}

tasks.named("compileJava") {
    dependsOn(tasks.named("openApiGenerate"))
}

dependencies {
    implementation(libs.annotation.api)
    implementation(libs.findbugs.jsr305)
    implementation(libs.jackson.databind)
    implementation(libs.jackson.jsr310)
}