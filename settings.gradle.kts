rootProject.name = "dragon-bloom"

pluginManagement {
    repositories {
        mavenLocal()
        maven {
            name = "aliyun-public"
            url = uri("https://maven.aliyun.com/repository/public")
        }
        // RetroFuturaGradle
        maven {
            name = "GTNH Maven"
            url = uri("https://nexus.gtnewhorizons.com/repository/public/")
        }
        gradlePluginPortal()
        mavenCentral()
    }
}

plugins {
    // Automatic toolchain provisioning
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

fun Settings.includeModules(modules: String = "modules") {
    fun findBuildScripts(modulesDirectory: File): List<File> {
        val moduleIds = mutableListOf<File>()
        for (file in modulesDirectory.listFiles()!!) {
            if (file.isDirectory) {
                moduleIds.addAll(findBuildScripts(file))
            } else if (file.name == "build.gradle.kts") {
                moduleIds.add(file)
            }
        }
        return moduleIds
    }

    val modulesDirectory = file("${rootProject.projectDir}/${modules}/")
    if (!modulesDirectory.exists()) {
        throw IllegalStateException("Modules directory is not exists.")
    }
    if (!modulesDirectory.isDirectory) {
        throw IllegalStateException("Modules directory file must be a directory.")
    }
    val modulesPath = modulesDirectory.absolutePath
    println()
    println("> Modules: $modules")
    val buildScripts = findBuildScripts(modulesDirectory)
    val prefixLength = modulesPath.length - modules.length - 1
    buildScripts.forEach {
        val moduleId = it.parentFile.absolutePath
            .substring(prefixLength).replace('\\', ':').replace('/', ':')
        if (moduleId.isBlank() || moduleId == ":") {
            return
        }
        include(moduleId)
        val project = project(moduleId)
        project.name = "${rootProject.name}${moduleId.substring(modules.length + 1)}"
            .replace(':', '-')
        println(">> included $moduleId (${project.name})")
    }
    println()
}

includeModules()