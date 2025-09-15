import com.diffplug.gradle.spotless.SpotlessExtension
import com.diffplug.gradle.spotless.SpotlessPlugin
import name.remal.gradle_plugins.sonarlint.SonarLint
import name.remal.gradle_plugins.sonarlint.SonarLintExtension
import name.remal.gradle_plugins.sonarlint.SonarLintPlugin


plugins {
    java
    alias(libs.plugins.spotless) apply false
    alias(libs.plugins.sonarlint) apply false
}

group = "ru.otus.exchange"

val projectVersion: String by project
version = projectVersion


repositories {
    mavenLocal()
    mavenCentral()
}

allprojects {
    plugins.apply(JavaPlugin::class.java)
    extensions.configure<JavaPluginExtension> {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

    repositories {
        mavenLocal()
        mavenCentral()
    }

    tasks.withType<JavaCompile> {
        options.encoding = "UTF-8"
        options.compilerArgs.addAll(
            listOf(
                "-Xlint:all,-serial,-processing"
            )
        )
    }

    apply<SonarLintPlugin>()
    configure<SonarLintExtension> {
    }


    apply<SpotlessPlugin>()
    configure<SpotlessExtension> {
        java {
            removeUnusedImports()
            trimTrailingWhitespace()
            palantirJavaFormat("2.72.0")
        }
    }


    tasks.withType<SonarLint> {
        dependsOn("spotlessApply")
        exclude("src/test/resources/*")
    }

    tasks.withType<Test> {
        useJUnitPlatform()
        testLogging.showExceptions = true
        jvmArgs = listOf("-XX:+StartAttachListener")
        reports {
            junitXml.required.set(true)
            html.required.set(true)
        }
    }

    dependencies {
        compileOnly(rootProject.libs.spring.context) // sonarlint required org.springframework.core.NestedIOException
        testImplementation(platform(rootProject.libs.junit.bom))
        testImplementation(platform(rootProject.libs.testcontainers.bom))
        testImplementation(rootProject.libs.junit.jupiter)
        testRuntimeOnly(rootProject.libs.junit.platform.launcher)
    }

    configurations.all {
        resolutionStrategy {
            failOnVersionConflict()

            force(rootProject.libs.slf4j.api)
            force(rootProject.libs.slf4j.simple)
            force(rootProject.libs.slf4j.ext)
            force(rootProject.libs.guava)
            force(rootProject.libs.jakarta.bind.api)
            force(rootProject.libs.project.lombok)
            force(rootProject.libs.awaitility)

            force(rootProject.libs.hamcrest)
            force(rootProject.libs.hamcrest.core)
            force(rootProject.libs.byte.buddy)
            force(rootProject.libs.byte.buddy.agent)

            force(rootProject.libs.junit.jupiter.api)

            force(rootProject.libs.commons.io)
            force(rootProject.libs.jgit)
            force(rootProject.libs.commons.compress)

            force(rootProject.libs.json.smart)

            force(rootProject.libs.commons.codec)

            force(rootProject.libs.gson)
            force(rootProject.libs.error.prone.annotations)

            force(rootProject.libs.commons.lang3)
            force(rootProject.libs.annotations)

            force(rootProject.libs.testcontainers)

            force(rootProject.libs.httpclient5)
            force(rootProject.libs.jboss.logging)
            force(rootProject.libs.classmate)

            force(rootProject.libs.httpcore5)

            force(rootProject.libs.jackson.databind)
            force(rootProject.libs.jackson.dataformat.yaml)
            force(rootProject.libs.jackson.dataformat.toml)
            force(rootProject.libs.jackson.annotations)
            force(rootProject.libs.jackson.core)
            force(rootProject.libs.jackson.bom)
            force(rootProject.libs.jackson.datatype.guava)

            force(rootProject.libs.jackson.datatype.jdk8)
            force(rootProject.libs.jackson.jsr310)
            force(rootProject.libs.jackson.module.parameter.names)

            force(rootProject.libs.jackson.jaxrs.json.provider)
            force(rootProject.libs.jackson.jaxrs.base)
            force(rootProject.libs.jackson.module.jaxb.annotations)

            force(rootProject.libs.kotlin.stdlib.jdk8)
            force(rootProject.libs.kotlin.stdlib.common)
            force(rootProject.libs.kotlin.stdlib)

            force(rootProject.libs.jaxb.api)
            force(rootProject.libs.junit.bom)

            force(rootProject.libs.reactor.core)
            force(rootProject.libs.reactor.test)

            force(rootProject.libs.micrometer.core)
            force(rootProject.libs.micrometer.observation)
            force(rootProject.libs.protobuf.java)
            force(rootProject.libs.proto.google)
            force(rootProject.libs.zipkin.reporter)
            force(rootProject.libs.netty.common)
            force(rootProject.libs.netty.handler)
            force(rootProject.libs.netty.transport)
            force(rootProject.libs.netty.buffer)
            force(rootProject.libs.netty.codec)
            force(rootProject.libs.netty.codec.http2)
            force(rootProject.libs.netty.codec.http)
            force(rootProject.libs.netty.handler.proxy)
            force(rootProject.libs.netty.transport.native.unix.common)

            force(rootProject.libs.spring.security.crypto)

            force(rootProject.libs.spring.boot.starter.webflux)
            force(rootProject.libs.spring.boot.starter)
            force(rootProject.libs.spring.boot.starter.validation)
            force(rootProject.libs.spring.boot.configuration.processor)

            force(rootProject.libs.spring.boot.properties.migrator)
            force(rootProject.libs.zipkin.sender.okhttp3)

            force(rootProject.libs.spring.web)
            force(rootProject.libs.spring.core)

            force(rootProject.libs.findbugs.jsr305)
            force(rootProject.libs.snakeyaml)
            force(rootProject.libs.activation.api)

            force(rootProject.libs.hibernate.validator)
            force(rootProject.libs.jackson.module.jakarta.xmlbind.annotations)

            force(rootProject.libs.jmustache)
            force(rootProject.libs.validation.api)
            force(rootProject.libs.spring.boot.autoconfigure)
            force(rootProject.libs.spring.webmvc)
            force(rootProject.libs.kafka.clients)

            force(rootProject.libs.checker.qual)
        }
    }
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21)) // Or your desired version
    }
}

ext {
    val specDir = layout.projectDirectory.dir("./specs")
    set("openapi-spec-v1", specDir.file("openapi/blob-storage-api-v1.yaml").toString())
    set("proto-spec-v1", specDir.file("proto/blob-storage-api-v1.proto").toString())
}


tasks.test {
    useJUnitPlatform()
}
