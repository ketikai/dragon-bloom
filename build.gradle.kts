plugins {
    id("idea")
    id("com.gradleup.shadow") version "9.3.0" apply false
}

group = "pers.ketikai.minecraft"
version = "0.1.0-SNAPSHOT"

subprojects {
    val buildScript = project.file("build.gradle.kts")
    if (!buildScript.exists() || buildScript.isDirectory) {
        return@subprojects
    }

    version = rootProject.version

    apply {
        plugin("idea")
        plugin("java")
        plugin("java-library")
        plugin("com.gradleup.shadow")

        from(rootProject.file("/gradle/languages.gradle"))
        from(rootProject.file("/gradle/repositories.gradle"))
        from(rootProject.file("/gradle/dependencies.gradle"))
        from(rootProject.file("/gradle/resources.gradle"))
        from(rootProject.file("/gradle/packages.gradle"))
    }
}