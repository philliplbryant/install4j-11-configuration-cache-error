import org.gradle.api.tasks.wrapper.Wrapper.DistributionType.ALL

version = 1.0

tasks {
    // Use this task to upgrade Gradle in order to keep
    // gradle-wrapper.properties in sync.
    named<Wrapper>("wrapper").configure {
        gradleVersion = "8.10.2"
        distributionType = ALL
    }
}
