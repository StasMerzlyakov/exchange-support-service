pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.9.0"
}

rootProject.name = "exchange-support-service"
include("gateway-service")
include("common")
include("models")
include("models:xsd")
findProject(":models:xsd")?.name = "xsd"
include("models:jmh-tests")
findProject(":models:jmh-tests")?.name = "jmh-tests"
include("jfr-image")
include("jfr-image:helper")
findProject(":jfr-image:helper")?.name = "helper"
include("blob-storage-service")

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
include("blob-storage-service:blob-storage-api-v1-jackson")
findProject(":blob-storage-service:blob-storage-api-v1-jackson")?.name = "blob-storage-api-v1-jackson"
include("blob-storage-service:blob-storage-core")
findProject(":blob-storage-service:blob-storage-core")?.name = "blob-storage-core"
include("blob-storage-service:blob-storage-spring")
findProject(":blob-storage-service:blob-storage-spring")?.name = "blob-storage-spring"
include("blob-storage-service:blob-storage-api-v1-grpc")
findProject(":blob-storage-service:blob-storage-api-v1-grpc")?.name = "blob-storage-api-v1-grpc"
include("blob-storage-service:blob-storage-api-common")
findProject(":blob-storage-service:blob-storage-api-common")?.name = "blob-storage-api-common"
include("blob-storage-service:blob-storage-api-v1-grpc-client-blocking")
findProject(":blob-storage-service:blob-storage-api-v1-grpc-client-blocking")?.name = "blob-storage-api-v1-grpc-client-blocking"
include("blob-storage-service:test-utils")
findProject(":blob-storage-service:test-utils")?.name = "test-utils"
include("receiver-service")
include("receiver-service:receiver-api-v1-swagger")
findProject(":receiver-service:receiver-api-v1-swagger")?.name = "receiver-api-v1-swagger"
include("receiver-service:receiver-spring")
findProject(":receiver-service:receiver-spring")?.name = "receiver-spring"
include("receiver-service:receiver-api-common")
findProject(":receiver-service:receiver-api-common")?.name = "receiver-api-common"
include("receiver-service:receiver-core")
findProject(":receiver-service:receiver-core")?.name = "receiver-core"
include("generator-service")
include("generator-service:generator-core")
findProject(":generator-service:generator-core")?.name = "generator-core"
include("models:json")
findProject(":models:json")?.name = "json"
include("generator-service:generator-spring")
findProject(":generator-service:generator-spring")?.name = "generator-spring"
