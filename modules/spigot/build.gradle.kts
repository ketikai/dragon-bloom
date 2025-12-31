import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.nio.charset.Charset

val minecraftVersion = "1.12.2"

group = "${rootProject.group}.spigot"

repositories {
    maven {
        name = "spigotmc-public"
        url = uri("https://hub.spigotmc.org/nexus/content/groups/public/")
    }
    maven {
        name = "spigotmc-snapshots"
        url = uri("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    }
}

dependencies {
    compileOnly("org.apache.logging.log4j:log4j-api:2.22.1")
    compileOnly("org.spigotmc:spigot-api:${minecraftVersion}-R0.1-SNAPSHOT")

    shadow(project(":modules:${rootProject.name}-tags"))
    shadow(project(":modules:${rootProject.name}-protocol")) {
        isTransitive = false
    }
}

tasks.processResources {
    includeEmptyDirs = false
    val props = mapOf(
        "name" to rootProject.name,
        "version" to version,
    )
    filesMatching(listOf("plugin.yml")) {
        expand(props)
    }
}

tasks.register("downloadSpigotBuildTools") {
    group = "spigot"
    val from = uri("https://hub.spigotmc.org/jenkins/job/BuildTools/lastSuccessfulBuild/artifact/target/BuildTools.jar")
    val into = project.file("/run/")
    val path = "$into/${from.path.substringAfterLast('/')}"
    enabled = !file(path).exists()
    doFirst {
        if (!into.exists()) {
            into.mkdirs()
        }
        val conn = from.toURL().openConnection() as HttpURLConnection
        conn.requestMethod = "GET"
        conn.connectTimeout = 5000
        conn.inputStream.use { input ->
            println("Downloading BuildTools.jar from $from")
            FileOutputStream(path).use { output ->
                val buffer = ByteArray(1024)
                var read: Int

                while ((input.read(buffer).also { read = it }) != -1) {
                    output.write(buffer, 0, read)
                    output.flush()
                }
            }
        }
        when (val responseCode = conn.responseCode) {
            HttpURLConnection.HTTP_OK -> {
                println("Successfully downloaded BuildTools.jar")
            }
            else -> {
                throw RuntimeException("Failed to download BuildTools.jar [response code: $responseCode]")
            }
        }
    }
}

tasks.register<JavaExec>("buildSpigotCore") {
    group = "spigot"
    val jar = project.file("/run/BuildTools.jar")
    enabled = !project.file("/run/spigot-${minecraftVersion}.jar").exists()
    dependsOn("downloadSpigotBuildTools")
    executable(
        javaToolchains.launcherFor {
            languageVersion.set(project.java.toolchain.languageVersion)
            vendor.set(project.java.toolchain.vendor)
        }.get().executablePath.toString()
    )
    jvmArgs("-Xmx2G", "-Xms2G", "-Dfile.encoding=${Charset.defaultCharset().name()}")
    workingDir = project.file("/run/")
    classpath = files(jar)
    args("--rev", minecraftVersion)
}

tasks.register<Copy>("copyPluginJarToServer") {
    group = "spigot"
    dependsOn("buildSpigotCore", tasks.shadowJar)
    from(tasks.shadowJar.get().archiveFile)
    into(project.file("/run/plugins/"))
    rename {
        if (it.endsWith(".jar")) {
            "${project.name}.jar"
        } else {
            it
        }
    }
}

tasks.register<JavaExec>("runServer") {
    group = "spigot"
    val jar = project.file("/run/spigot-${minecraftVersion}.jar")
    dependsOn("copyPluginJarToServer")
    executable(
        javaToolchains.launcherFor {
            languageVersion.set(project.java.toolchain.languageVersion)
            vendor.set(project.java.toolchain.vendor)
        }.get().executablePath.toString()
    )
    jvmArgs("-Xmx2G", "-Xms2G", "-Dfile.encoding=${Charset.defaultCharset().name()}")
    workingDir = project.file("/run/")
    classpath = files(jar)
    standardInput = System.`in`
    args("nogui")
}
