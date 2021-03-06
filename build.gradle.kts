plugins {
    val kotlinVersion = "1.4.0"

    application
    kotlin("jvm") version kotlinVersion
    kotlin("kapt") version kotlinVersion
    kotlin("plugin.allopen") version kotlinVersion
    id("com.github.johnrengelman.shadow") version "6.0.0"
}

group = "cn.elmi.example.ddd"
version = "1.0.0-SNAPSHOT"

allprojects {
    apply(plugin = "application")
    apply(plugin = "kotlin")
    apply(plugin = "kotlin-kapt")
    apply(plugin = "kotlin-allopen")
    apply(from = rootProject.file("gradle/ktlint.gradle.kts"))

    repositories {
        mavenCentral()
        jcenter()
    }

    configurations.create("developmentOnly")

    dependencies {
        val micronautVersion = "2.0.0.M3"

        implementation(kotlin("stdlib-jdk8"))
        implementation(kotlin("reflect"))

        implementation(platform("io.micronaut:micronaut-bom:$micronautVersion"))
        implementation("io.micronaut:micronaut-http-client")
        implementation("io.micronaut:micronaut-http-server-netty")
        implementation("io.micronaut:micronaut-runtime")
        implementation("javax.annotation:javax.annotation-api")

        runtimeOnly("ch.qos.logback:logback-classic:1.2.3")
        runtimeOnly("com.fasterxml.jackson.module:jackson-module-kotlin:2.9.8")

        kapt(platform("io.micronaut:micronaut-bom:$micronautVersion"))
        kapt("io.micronaut:micronaut-inject-java")
        kapt("io.micronaut:micronaut-validation")

        testImplementation(platform("io.micronaut:micronaut-bom:$micronautVersion"))
        testImplementation("io.micronaut.test:micronaut-test-kotlintest")
        testImplementation("io.mockk:mockk:1.9.3")
        testImplementation("io.kotlintest:kotlintest-runner-junit5:3.3.2")

        kaptTest(platform("io.micronaut:micronaut-bom:$micronautVersion"))
        kaptTest("io.micronaut:micronaut-inject-java")
    }

    tasks {
        compileKotlin {
            kotlinOptions {
                jvmTarget = JavaVersion.VERSION_1_8.toString()
                freeCompilerArgs = listOf("-Xjsr305=strict")
                javaParameters = true
            }
        }

        compileTestKotlin {
            kotlinOptions {
                jvmTarget = JavaVersion.VERSION_1_8.toString()
                javaParameters = true
            }
        }

        test {
            failFast = true
            useJUnitPlatform()
            testLogging {
                events("passed", "skipped", "failed")
            }
            classpath += configurations.getByName("developmentOnly")
        }

    }

    allOpen {
        annotation("io.micronaut.aop.Around")
    }
}

tasks {
    shadowJar {
        mergeServiceFiles()
    }

    withType<JavaExec> {
        classpath += configurations.getByName("developmentOnly")
        jvmArgs("-noverify", "-XX:TieredStopAtLevel=1", "-Dcom.sun.management.jmxremote")
    }
}

application {
    mainClassName = "cn.elmi.example.ddd.Application"
}