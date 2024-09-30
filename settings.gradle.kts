pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
}

@Suppress("UnstableApiUsage")
dependencyResolutionManagement {
    repositories {
        mavenCentral()
    }
}

// Feature flag implementing strictness during stabilization of configuration
// caching. Gradle documentation recommends enabling it to prepare for flag
// removal and making the linked features the default.  See also
// https://docs.gradle.org/current/userguide/configuration_cache.html
enableFeaturePreview("STABLE_CONFIGURATION_CACHE")

include("basic-map-data")
