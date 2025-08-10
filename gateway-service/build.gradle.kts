plugins {
    alias(libs.plugins.freefair.lombok)
    alias(libs.plugins.spring.boot)
}

dependencies {
    implementation(projects.common)
    implementation(platform(libs.spring.boot.dependencies))
    implementation(platform(libs.spring.cloud.dependencies))

    compileOnly(libs.project.lombok)
    annotationProcessor(libs.project.lombok)

    implementation(libs.spring.cloud.starter.gateway.server.webflux)
    implementation(libs.spring.boot.starter.webflux)
    implementation(libs.spring.boot.starter.data.redis.reactive)

    implementation(libs.starter.actuator)
    implementation(libs.starter.aop)
    implementation(libs.spring.boot.starter.validation)

    implementation(libs.java.uuid.generator)


    /*implementation(platform(libs.opentelemetry.instrumentation.bom))
    implementation(libs.opentelemetry.instrumentation.annotations)
    implementation(libs.micrometer.registry.prometheus)
    implementation(libs.micrometer.tracing.bridge.otel)
    implementation(libs.opentelemetry.exporter.zipkin) */


    implementation(libs.spring.cloud.starter.circuitbreaker.reactor.resilience4j)

    testImplementation(libs.spring.boot.starter.test)
    testImplementation(libs.reactor.test)
    testImplementation(libs.spring.cloud.contract.wiremock)
    testImplementation(libs.redis.testcontainers)
}
