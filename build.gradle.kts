import ProjectVersions.unethicaliteVersion
import org.jetbrains.kotlin.util.capitalizeDecapitalize.toLowerCaseAsciiOnly

buildscript {
    repositories {
        mavenCentral()
        gradlePluginPortal()
    }
}

plugins {
    kotlin("jvm") version "1.6.21"
    kotlin("kapt") version "1.6.21"
}

project.extra["GithubUrl"] = "https://github.com/yuri-moens/sluwe-plugins"
project.extra["GithubUserName"] = "yuri-moens"
project.extra["GithubRepoName"] = "sluwe-plugins"

apply<JavaLibraryPlugin>()
apply<BootstrapPlugin>()
apply<VersionPlugin>()
apply<UpdateVersionsPlugin>()
apply<CheckstylePlugin>()

allprojects {
    group = "io.reisub.devious"

    project.extra["PluginProvider"] = "yuri-moens"
    project.extra["ProjectSupportUrl"] = "https://github.com/yuri-moens/sluwe-plugins/issues"
    project.extra["PluginLicense"] = "3-Clause BSD License"

    apply<MavenPublishPlugin>()
    apply<JavaPlugin>()
    apply(plugin = "java-library")
    apply(plugin = "checkstyle")
    apply(plugin = "kotlin")

    configure<CheckstyleExtension> {
        toolVersion = Libraries.Versions.checkstyle
        maxWarnings = 0
        isShowViolations = true
        isIgnoreFailures = false
    }

    repositories {
        mavenCentral()
        mavenLocal()
    }

    dependencies {
        annotationProcessor(Libraries.lombok)
        annotationProcessor(Libraries.pf4j)

        compileOnly("net.unethicalite:http-api:$unethicaliteVersion+")
        compileOnly("net.unethicalite:runelite-api:$unethicaliteVersion+")
        compileOnly("net.unethicalite:runelite-client:$unethicaliteVersion+")
        compileOnly("net.unethicalite.rs:runescape-api:$unethicaliteVersion+")

        if (project.name != "utils") {
            compileOnly(project(":utils"))
        }

        compileOnly(Libraries.okhttp3)
        compileOnly(Libraries.gson)
        compileOnly(Libraries.guice)
        compileOnly(Libraries.javax)
        compileOnly(Libraries.lombok)
        compileOnly(Libraries.pf4j)
    }

    configure<JavaPluginConvention> {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    configure<PublishingExtension> {
        repositories {
            maven {
                url = uri("$buildDir/repo")
            }
        }
        publications {
            register("mavenJava", MavenPublication::class) {
                artifactId = artifactId.replace(" ", "-").toLowerCaseAsciiOnly()
                from(components["java"])
            }
        }
    }

    tasks {
        compileKotlin {
            kotlinOptions {
                jvmTarget = "11"
                freeCompilerArgs = listOf("-Xjvm-default=all-compatibility")
            }
            sourceCompatibility = "11"
        }

        withType<JavaCompile> {
            options.encoding = "UTF-8"
        }

        withType<AbstractArchiveTask> {
            isPreserveFileTimestamps = false
            isReproducibleFileOrder = true
            dirMode = 493
            fileMode = 420
        }
    }
}

tasks {
    register<Delete>("bootstrapClean") {
        delete("release/")
    }
}
