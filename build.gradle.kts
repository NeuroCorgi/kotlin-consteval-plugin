import org.jetbrains.kotlin.gradle.plugin.PLUGIN_CLASSPATH_CONFIGURATION_NAME
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	kotlin("jvm") version "2.0.21"
}

kotlin {
	jvmToolchain(20)
}

repositories {
	mavenCentral()
}

dependencies {
	PLUGIN_CLASSPATH_CONFIGURATION_NAME(project(":consteval-plugin"))
}
