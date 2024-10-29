plugins {
	kotlin("jvm")
	kotlin("kapt")
}

kotlin {
	jvmToolchain(20)
}

repositories {
	mavenCentral()
}

dependencies {
	compileOnly("org.jetbrains.kotlin:kotlin-compiler-embeddable:2.0.10")
	compileOnly("com.google.auto.service:auto-service-annotations:1.0.1")
	kapt("com.google.auto.service:auto-service:1.0.1")	
}
