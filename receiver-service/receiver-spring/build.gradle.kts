plugins {
    alias(libs.plugins.freefair.lombok)
    alias(libs.plugins.spring.boot)
}

dependencies {
    implementation(projects.common)
    implementation(projects.receiverService.receiverApiV1Swagger)
    implementation(projects.receiverService.receiverApiCommon)
    implementation(projects.receiverService.receiverCore)

    implementation(projects.blobStorageService.blobStorageApiV1GrpcClientBlocking)
    implementation(projects.blobStorageService.blobStorageApiCommon)
    implementation(libs.mapstruct)
    annotationProcessor(libs.mapstruct.processor)
    implementation(libs.grpc.netty)
    implementation(libs.grpc.protobuf)
    implementation(libs.protobuf.java)
    implementation(libs.grpc.stub)
    implementation(libs.spring.boot.starter.validation)
    implementation(libs.spring.boot.starter.cache)

    implementation(platform(libs.spring.boot.dependencies))

    compileOnly(libs.project.lombok)
    annotationProcessor(libs.project.lombok)

    implementation(libs.spring.boot.starter.web)
    implementation(libs.spring.kafka)
    implementation(libs.spring.boot.starter.jdbc)
    implementation(libs.springdoc.openapi.starter.webmvc.ui)

    implementation(libs.swagger.codegen) {
        exclude(module = libs.slf4j.simple.get().name)
    }

    implementation(libs.starter.actuator)
    implementation(libs.starter.aop)

    testImplementation(libs.junit.jupiter)
    testImplementation(libs.json.unit.assertj)
    testImplementation(libs.spring.boot.starter.test)
    testImplementation(libs.testcontainer.postgresql)
    runtimeOnly(libs.flyway.core)
    runtimeOnly(libs.flyway.database.postgresql)
    runtimeOnly(libs.postgresql)

}