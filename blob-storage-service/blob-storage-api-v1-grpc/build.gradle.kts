import com.google.protobuf.gradle.id

plugins {
    alias(libs.plugins.freefair.lombok)
    alias(libs.plugins.spotless)
    alias(libs.plugins.protobuf)
}

sourceSets {
    main {
        java {
            srcDirs("build/generated/source/proto/main/grpc")
            srcDirs("build/generated/source/proto/main/reactor")
            srcDirs("build/generated/source/proto/main/java")
        }
    }
}

protobuf {
    protoc {
        with (libs.protobuf.protoc.get()) {
            artifact = "${module}:${version}"
        }
    }

    plugins {
        id("grpc") {
            with (libs.protoc.gen.grpc.java.get()) {
                artifact = "${module}:${version}"
            }
        }

        id("reactor") {
            with(libs.reactor.grpc.lib.get()) {
                artifact = "${module}:${version}"
            }
        }
    }

    generateProtoTasks {
        all().forEach { task ->
            task.plugins {
                create("reactor")
                create("grpc")
            }
        }
    }
    sourceSets {
        main {
            proto {
                srcDirs("../../specs/proto")
            }
        }
    }
}

dependencies {
    compileOnly(libs.project.lombok)
    annotationProcessor(libs.project.lombok)
    implementation(projects.blobStorageService.blobStorageApiCommon)
    implementation(libs.annotation.api)
    implementation(libs.protobuf.protoc)
    implementation(libs.reactor.grpc.lib)
    implementation(libs.reactor.grpc.stubs)
    implementation(libs.grpc.netty)
    implementation(libs.grpc.protobuf)
    implementation(libs.protobuf.java)
    implementation(libs.grpc.stub)
    implementation(libs.mapstruct)
    annotationProcessor(libs.mapstruct.processor)
    implementation(libs.slf4j.api)
    implementation(libs.reactor.core)
    testImplementation(libs.mockito.core)
    annotationProcessor(libs.mapstruct.processor)
    testImplementation(projects.blobStorageService.testUtils)
}