group = "xyz.fteychene.beyondhexa"
version = "1.0-SNAPSHOT"

plugins {
    kotlin("jvm") version "1.3.72"
    kotlin("kapt") version "1.3.72"
}

allprojects {
    repositories {
        mavenCentral()
    }
}

subprojects {
    apply(plugin = "org.jetbrains.kotlin.jvm")
    apply(plugin = "org.jetbrains.kotlin.kapt")

    val arrowVersion = "0.10.4"
    ext {
        set("arrowVersion",  arrowVersion)
    }
    group = parent!!.name
    version = parent!!.version


    dependencies {
        implementation(kotlin("stdlib-jdk8"))
        implementation("io.arrow-kt:arrow-core:$arrowVersion")
        implementation("io.arrow-kt:arrow-optics:$arrowVersion")
        implementation("io.arrow-kt:arrow-syntax:$arrowVersion")
        kapt("io.arrow-kt:arrow-meta:$arrowVersion")
    }

    tasks {
        compileKotlin {
            kotlinOptions.jvmTarget = "1.8"
        }
        compileTestKotlin {
            kotlinOptions.jvmTarget = "1.8"
        }
    }
}