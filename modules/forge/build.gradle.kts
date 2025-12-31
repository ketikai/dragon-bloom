plugins {
    id("com.gtnewhorizons.retrofuturagradle") version "2.0.2"
}

val minecraftVersion = "1.12.2"

group = "${rootProject.group}.forge"

minecraft {
    mcVersion.set(minecraftVersion)

    // Enable assertions in the mod's package when running the client or server
    extraRunJvmArguments.add("-ea:${project.group}")

    // Exclude some Maven dependency groups from being automatically included in the reobfuscated runs
    groupsToExcludeFromAutoReobfMapping.addAll(
            "com.diffplug",
            "com.diffplug.durian",
            "net.industrial-craft",
    )
}

repositories {
    // RetroFuturaGradle
    maven {
        name = "GTNH Maven"
        url = uri("https://nexus.gtnewhorizons.com/repository/public/")
    }
}

dependencies {
    shadow(project(":modules:${rootProject.name}-tags"))
    shadow(project(":modules:${rootProject.name}-protocol")) {
        isTransitive = false
    }
}

tasks.processResources {
    includeEmptyDirs = false
    val props = mapOf(
            "mod_id" to rootProject.name,
            "mod_name" to rootProject.name,
            "mod_version" to version,
            "minecraft_version" to minecraftVersion,
    )
    filesMatching(listOf("assets/**/*.lang", "**/mcmod.info", "**/pack.mcmeta")) {
        expand(props)
    }
    val assetsDir = "assets/${rootProject.name}"
    eachFile {
        if (path.startsWith("assets/")) {
            print("$path >> ")
            path = assetsDir + path.substring(6)
            println(path)
        }
    }
}

tasks.shadowJar {
    manifest {
        attributes(mapOf(
            "FMLCorePlugin" to "pers.ketikai.minecraft.forge.dragonbloom.DragonBloomCore",
            "FMLCorePluginContainsFMLMod" to true,
            "ForceLoadAsMod" to true,
        ))
    }
    finalizedBy(tasks.reobfJar)
}

tasks.reobfJar {
    dependsOn(tasks.shadowJar)
    inputJar.set(tasks.shadowJar.get().archiveFile)
}
