import com.install4j.gradle.Install4jTask
import org.gradle.api.tasks.PathSensitivity.RELATIVE
import org.gradle.kotlin.dsl.support.serviceOf

plugins {
    id("com.install4j.gradle") version ("11.0.0.1")
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

// TODO: Specify Install4J license information
install4j {
    license = "FLOAT:taskmaster.jre.saic.com"
}

tasks {

    val dataDir = project.layout.buildDirectory.dir("data")

    val unzipMapData by registering(Sync::class) {

        val archiveOperations = serviceOf<ArchiveOperations>()
        from(mapDataPath.asFileTree.map { archiveOperations.zipTree(it) })
        into(dataDir)
    }

    // FIXME: Configuration cache problems found in this build.
    //  Task `:basic-map-data:installer` of type `com.install4j.gradle.Install4jTask`: invocation of 'Task.project' at execution time is unsupported.
    //  From configuration-cache-report.html:
    //  Exception at `com.install4j.gradle.Install4jTask._init_$lambda$1(Unknown Source)`
    register<Install4jTask>("installer") {

        dependsOn(unzipMapData)

        // Inputs
        val configFile: File = file("basic-map-data.install4j")
        inputs.file(configFile)
            .withPathSensitivity(RELATIVE)

        val mapDataInstallerBaseName = "Basic-Map-Data"
        inputs.property("mapDataInstallerBaseName", mapDataInstallerBaseName)

        val linuxInstallerName = "Install-$mapDataInstallerBaseName-" +
                "Linux"
        inputs.property("linuxInstallerName", linuxInstallerName)

        val mapDataVersion = "1.0"
        inputs.property("mapDataVersion", mapDataVersion)

        val mapDataDir: Provider<Directory> = dataDir.map {
            it.dir("mapdata")
        }
        inputs.dir(mapDataDir)
            .withPathSensitivity(RELATIVE)

        val windowInstallerName = "Install-$mapDataInstallerBaseName-" +
                "Windows"
        inputs.property("windowInstallerName", windowInstallerName)

        // Outputs
        val installerOutputDir =
            project.layout.buildDirectory.dir("installers/${project.name}")

        val install4jOutputsFile: Provider<RegularFile> = installerOutputDir.map {
            it.file("output.txt")
        }
        outputs.file(install4jOutputsFile)

        val install4jUpdatesFile: Provider<RegularFile> = installerOutputDir.map {
            it.file("updates.xml")
        }
        outputs.file(install4jUpdatesFile)

        val linuxInstallerFile: Provider<RegularFile> = installerOutputDir.map {
            it.file("$linuxInstallerName.sh")
        }
        outputs.file(linuxInstallerFile)

        val windowsInstallerFile: Provider<RegularFile> = installerOutputDir.map {
            it.file("$windowInstallerName.exe")
        }
        outputs.file(windowsInstallerFile)

        val debuggingOutputFile: Provider<RegularFile> = installerOutputDir.map {
            it.file("$name-configuration.txt")
        }
        outputs.file(debuggingOutputFile)

        // Configuration
        val variableMap: MapProperty<String, Any> =
            objects.mapProperty(String::class, Any::class)

        variableMap.put("installerOutputDir", installerOutputDir.get().asFile.path)
        variableMap.put("linuxInstallerName", linuxInstallerName)
        variableMap.put("mapDataDir", mapDataDir.get().asFile.path)
        variableMap.put("mapDataInstallerBaseName", mapDataInstallerBaseName)
        variableMap.put("mapDataVersion", mapDataVersion)
        variableMap.put("windowInstallerName", windowInstallerName)

        projectFile = configFile
        variables = variableMap

        doFirst {

            val outputFile = debuggingOutputFile.get().asFile
            outputFile.parentFile.mkdirs()

            outputFile.writeText("projectFile = $configFile\n")
            variableMap.get().forEach { entry: Map.Entry<String, Any> ->
                outputFile.appendText(
                    "${entry.key} = ${entry.value}\n"
                )
            }
        }
    }

    register<DefaultTask>("writeConfiguration") {

        dependsOn(unzipMapData)

        // Inputs
        val configFile: File = file("basic-map-data.install4j")
        inputs.file(configFile)
            .withPathSensitivity(RELATIVE)

        val mapDataInstallerBaseName = "Basic-Map-Data"
        inputs.property("mapDataInstallerBaseName", mapDataInstallerBaseName)

        val linuxInstallerName = "Install-$mapDataInstallerBaseName-" +
                "Linux"
        inputs.property("linuxInstallerName", linuxInstallerName)

        val mapDataVersion = "1.0"
        inputs.property("mapDataVersion", mapDataVersion)

        val mapDataDir: Provider<Directory> = dataDir.map {
            it.dir("mapdata")
        }
        inputs.dir(mapDataDir)
            .withPathSensitivity(RELATIVE)

        val windowInstallerName = "Install-$mapDataInstallerBaseName-" +
                "Windows"
        inputs.property("windowInstallerName", windowInstallerName)

        // Outputs
        val installerOutputDir =
            project.layout.buildDirectory.dir("installers/${project.name}")

        val install4jOutputsFile: Provider<RegularFile> = installerOutputDir.map {
            it.file("output.txt")
        }
        outputs.file(install4jOutputsFile)

        val install4jUpdatesFile: Provider<RegularFile> = installerOutputDir.map {
            it.file("updates.xml")
        }
        outputs.file(install4jUpdatesFile)

        val linuxInstallerFile: Provider<RegularFile> = installerOutputDir.map {
            it.file("$linuxInstallerName.sh")
        }
        outputs.file(linuxInstallerFile)

        val windowsInstallerFile: Provider<RegularFile> = installerOutputDir.map {
            it.file("$windowInstallerName.exe")
        }
        outputs.file(windowsInstallerFile)

        val debuggingOutputFile: Provider<RegularFile> = installerOutputDir.map {
            it.file("$name-configuration.txt")
        }
        outputs.file(debuggingOutputFile)

        // Configuration
        val variableMap: MapProperty<String, Any> =
            objects.mapProperty(String::class, Any::class)

        variableMap.put("installerOutputDir", installerOutputDir.get().asFile.path)
        variableMap.put("linuxInstallerName", linuxInstallerName)
        variableMap.put("mapDataDir", mapDataDir.get().asFile.path)
        variableMap.put("mapDataInstallerBaseName", mapDataInstallerBaseName)
        variableMap.put("mapDataVersion", mapDataVersion)
        variableMap.put("windowInstallerName", windowInstallerName)

        doFirst {

            val outputFile = debuggingOutputFile.get().asFile
            outputFile.parentFile.mkdirs()

            outputFile.writeText("projectFile = $configFile\n")
            variableMap.get().forEach { entry: Map.Entry<String, Any> ->
                outputFile.appendText(
                    "${entry.key} = ${entry.value}\n"
                )
            }
        }
    }

    register<DefaultTask>("build") {
        dependsOn(
            getByName("installer"),
            getByName("writeConfiguration"),
        )
    }
}
