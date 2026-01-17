import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    kotlin("multiplatform")
    id("org.jetbrains.compose")
    id("org.jetbrains.kotlin.plugin.compose")
}

repositories {
    google()
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
}

kotlin {
    jvm {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
        }
    }
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(compose.runtime)
                implementation(compose.foundation)
                implementation(compose.material3)
                implementation(compose.ui)
                implementation(compose.components.resources)
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.9.0")
                implementation("org.json:json:20240303")
                implementation("com.squareup.okhttp3:okhttp:4.12.0")
            }
        }
        val jvmMain by getting {
            dependencies {
                implementation(compose.desktop.currentOs)
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-swing:1.9.0")
                val javafxVersion = "17.0.10"
                implementation("org.openjfx:javafx-base:$javafxVersion:win")
                implementation("org.openjfx:javafx-graphics:$javafxVersion:win")
                implementation("org.openjfx:javafx-controls:$javafxVersion:win")
                implementation("org.openjfx:javafx-web:$javafxVersion:win")
                implementation("org.openjfx:javafx-swing:$javafxVersion:win")
                implementation("org.openjfx:javafx-media:$javafxVersion:win")
            }
        }
    }
}

compose.desktop {
    application {
        mainClass = "org.echo.project.MainKt"

        // FIXED JVM ARGS: Removed the "Unknown module" error while keeping security open
        jvmArgs += listOf(
            "--add-opens=java.desktop/sun.awt=ALL-UNNAMED",
            "--add-opens=java.desktop/java.awt.event=ALL-UNNAMED",
            "--add-opens=java.base/java.lang=ALL-UNNAMED",
            "--add-opens=java.base/java.net=ALL-UNNAMED",
            "--add-opens=java.base/sun.net.www.protocol.https=ALL-UNNAMED",
            "-Dprism.order=sw"
        )

        nativeDistributions {
            targetFormats(TargetFormat.Exe)
            packageName = "EchoApp"
            packageVersion = "1.0.0"

            // THESE ARE THE ENGINES THAT MAKE LOGIN & IMAGES WORK IN THE EXE
            modules(
                "java.sql",
                "java.naming",
                "java.desktop",
                "java.management",
                "jdk.unsupported",
                "jdk.jsobject",
                "jdk.crypto.ec",   // THE FIX FOR HTTPS LOGIN
                "java.xml",
                "java.net.http"
            )

            windows {
                shortcut = true
                console = false
            }
        }
    }
}