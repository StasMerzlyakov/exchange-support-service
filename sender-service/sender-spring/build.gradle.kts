plugins {
    alias(libs.plugins.freefair.lombok)
    alias(libs.plugins.spring.boot)
}

dependencies {
    implementation(projects.common)

    implementation(projects.senderService.senderCore)

    implementation(projects.blobStorageService.blobStorageApiV1GrpcClientBlocking)
    implementation(projects.blobStorageService.blobStorageApiCommon)
    implementation(libs.grpc.netty)
    implementation(libs.grpc.protobuf)
    implementation(libs.protobuf.java)
    implementation(libs.grpc.stub)
    implementation(libs.spring.boot.starter.validation)
    implementation(libs.spring.boot.starter.cache)
    implementation(libs.mapstruct)

    implementation(libs.jackson.databind)
    implementation(libs.slf4j.api)

    implementation(libs.jakarta.bind.api)
    runtimeOnly(libs.jaxb.runtime)

    annotationProcessor(libs.mapstruct.processor)

    implementation(platform(libs.spring.boot.dependencies))

    compileOnly(libs.project.lombok)
    implementation(libs.httpclient5)

    annotationProcessor(libs.project.lombok)

    implementation(libs.spring.boot.starter.web)
    implementation(libs.spring.kafka)
    implementation(libs.spring.boot.starter.data.redis)
    implementation(libs.micrometer.registry.prometheus)

    implementation(libs.spring.boot.starter.jdbc)

    implementation(libs.starter.actuator)
    implementation(libs.starter.aop)

    testImplementation(libs.junit.jupiter)
    testImplementation(libs.json.unit.assertj)
    testImplementation(libs.spring.boot.starter.test)

    runtimeOnly(libs.flyway.core)
    runtimeOnly(libs.flyway.database.postgresql)
    runtimeOnly(libs.postgresql)


}