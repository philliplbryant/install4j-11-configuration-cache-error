import com.install4j.gradle.Install4jTask
import org.gradle.api.tasks.PathSensitivity.RELATIVE
import org.gradle.kotlin.dsl.support.serviceOf

plugins {
    base
    id("com.install4j.gradle") version ("11.0.0.2")
}

/**
 * This module contains basic map data.
 */
description = "Basic Map Data"

val mapDataScopeConfigurationName = "mapDataScope"
val mapDataPathConfigurationName = "mapDataPath"

@Suppress("UnstableApiUsage")
configurations {

    // Configuration used to access the Basic Map Data hosted on Nexus.
    dependencyScope(mapDataScopeConfigurationName)

    resolvable(mapDataPathConfigurationName) {

        extendsFrom(configurations[mapDataScopeConfigurationName])
    }
}

val mapDataScope: Configuration = configurations[mapDataScopeConfigurationName]
val mapDataPath: Configuration = configurations[mapDataPathConfigurationName]

dependencies {

    // Don't add ZIP files as "runtimeOnly" dependencies because they'll get
    // included in the Class-Path.
    mapDataScope(
        project.layout.projectDirectory.files(
            "dependencies/dummy-map-data.zip"
        )
    )
}

install4j {
    license = "FLOAT:taskmaster.jre.saic.com"
}

tasks {

    val dataDir: Provider<Directory> = project.layout.buildDirectory.dir("data")

    val unzipMapData by registering(Sync::class) {

        val archiveOperations = serviceOf<ArchiveOperations>()
        from(mapDataPath.asFileTree.map { archiveOperations.zipTree(it) })
        into(dataDir)
    }

    register<Install4jTask>("installer") {

        dependsOn(unzipMapData)

        // Inputs
        val configFile = file(relativePath("basic-map-data.install4j"))
        val mapDataInstallerBaseName = "Basic-Map-Data"
        val linuxInstallerName = "Install-$mapDataInstallerBaseName-Linux"
        val mapDataVersion = "1.0"
        val mapDataDir: Provider<String> = dataDir.map {
            relativePath(it.dir("mapdata"))
        }
        val windowInstallerName = "Install-$mapDataInstallerBaseName-" +
                "Windows"

        // Outputs
        val installerOutputDir: Provider<Directory> =
            project.layout.buildDirectory.dir("installers/${project.name}")
        val installerOutputDirProperty = installerOutputDir.map { relativePath(it.asFile) }
        val install4jOutputsFile: Provider<String> = installerOutputDir.map {
            relativePath(it.file("output.txt"))
        }
        val install4jUpdatesFile: Provider<String> = installerOutputDir.map {
            relativePath(it.file("updates.xml"))
        }
        val linuxInstallerFile: Provider<String> = installerOutputDir.map {
            relativePath(it.file("$linuxInstallerName.sh"))
        }
        val windowsInstallerFile: Provider<String> = installerOutputDir.map {
            relativePath(it.file("$windowInstallerName.exe"))
        }

        // Enable up-to-date checking
        inputs.property("installerOutputDir", installerOutputDirProperty)
        inputs.property("mapDataInstallerBaseName", mapDataInstallerBaseName)
        inputs.property("linuxInstallerName", linuxInstallerName)
        inputs.property("mapDataVersion", mapDataVersion)
        inputs.dir(mapDataDir).withPathSensitivity(RELATIVE)
        inputs.property("windowInstallerName", windowInstallerName)

        outputs.file(install4jUpdatesFile)
        outputs.file(linuxInstallerFile)
        outputs.file(windowsInstallerFile)
        outputs.file(install4jOutputsFile)

        outputs.cacheIf { true }

        // Specify inputs to the Install4J compiler
        projectFile = configFile

        variables.put("installerOutputDir", installerOutputDirProperty)
        variables.put("linuxInstallerName", linuxInstallerName)
        variables.put("mapDataDir", mapDataDir)
        variables.put("mapDataInstallerBaseName", mapDataInstallerBaseName)
        variables.put("mapDataVersion", mapDataVersion)
        variables.put("windowInstallerName", windowInstallerName)
    }

    named("build") {
        dependsOn(getByName("installer"))
    }
}
