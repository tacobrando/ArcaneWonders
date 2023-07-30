import org.gradle.api.JavaVersion
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.tasks.compile.JavaCompile
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    java
    `maven-publish`
    kotlin("jvm") version "1.9.0"
    id("com.github.johnrengelman.shadow") version "7.1.2"
    id("me.tagavari.nmsremap") version "1.0.0"
    `distribution`
}

group = "dev.tacobrando"
version = "1.0-ALPHA"

repositories {
    gradlePluginPortal()
    mavenLocal()
    mavenCentral()
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven("https://oss.sonatype.org/content/repositories/snapshots")
    maven("https://oss.sonatype.org/content/repositories/central")
    maven("https://maven.enginehub.org/repo/")
}

dependencies {
    compileOnly("com.sk89q.worldedit:worldedit-bukkit:7.2.13-SNAPSHOT")
    compileOnly("org.spigotmc:spigot:1.20.1-R0.1-SNAPSHOT:remapped-mojang")
    implementation("org.jetbrains.kotlin:kotlin-script-runtime")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

}

val targetJavaVersion = 17
java {
    val javaVersion = JavaVersion.toVersion(targetJavaVersion)
    sourceCompatibility = javaVersion
    targetCompatibility = javaVersion
    if (JavaVersion.current() < javaVersion) {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(targetJavaVersion))
        }
    }
}

tasks.withType<JavaCompile>().configureEach {
    if (targetJavaVersion >= 10 || JavaVersion.current().isJava10Compatible) {
        options.release.set(targetJavaVersion)
    }
}

tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions.jvmTarget = "17"
}

tasks.named<Copy>("processResources") {
    val props = mapOf("version" to version)
    inputs.properties(props)
    filteringCharset = "UTF-8"
    filesMatching("plugin.yml") {
        expand(props)
    }
}

tasks.named<ShadowJar>("shadowJar") {
    archiveClassifier.set("shadow")
    // export final jar to server plugins folder
    doLast {
        val exportPath = project.findProperty("plugin.export")

        if (exportPath is String) {
            copy {
                from(archiveFile.get())
                into(File(exportPath))
                rename(".*", "ArcaneWonders.jar")
            }
            println("Exported plugin to $exportPath")
        } else {
            println("Provide plugin export path in 'gradle.properties'")
        }
    }
}

tasks.named<Jar>("jar") {
    dependsOn("shadowJar")
}

publishing {
    publications {
        register("maven", MavenPublication::class) {
            from(components["java"])
        }
    }
}
