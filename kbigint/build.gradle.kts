import org.jetbrains.kotlin.gradle.dsl.KotlinVersion
import org.jetbrains.kotlin.gradle.tasks.KotlinCompilationTask

plugins {
    `maven-publish`
    signing
    alias(libs.plugins.kotlin.mpp)
    alias(libs.plugins.android.library)
}

kotlin {
    jvm()

    androidTarget {
        publishLibraryVariants("release")
    }

    js(IR) {
        moduleName = project.name

        nodejs()
        useEsModules()
        generateTypeScriptDefinitions()
        binaries.library()
    }

    linuxX64 {
        compilations.configureEach {
            cinterops.create("tommath") {
                val vendor = defFile.parentFile.resolveSibling("vendor")
                includeDirs.allHeaders(vendor.resolve("include"))
                extraOpts("-libraryPath", vendor.resolve("lib"))
            }
        }
        binaries.staticLib {
            baseName = project.name
        }
    }

    jvmToolchain(17)

    sourceSets {
        named("commonMain") {
            dependencies {
                implementation(libs.kotlin.stdlib)
            }
        }
    }
}

android {
    namespace = group.toString()
    compileSdk = 34
    defaultConfig {
        minSdk = 21
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    buildFeatures {
        resValues = false
    }
}

publishing {
    publications {
        create<MavenPublication>("kbigint") {
            from(components["kotlin"])
            pom {
                name.set("KBigInt")
                description.set("Kotlin Multiplatform BigInteger library")
                url.set("https://observeroftime.github.io/kbigint/")
                inceptionYear.set("2024")
                licenses {
                    license {
                        name.set("Apache License 2.0")
                    }
                }
                developers {
                    developer {
                        id.set("ObserverOfTime")
                        name.set("Ioannis Somos")
                        email.set("chronobserver@disroot.org")
                        url.set("https://github.com/ObserverOfTime")
                    }
                }
                scm {
                    url.set("https://github.com/ObserverOfTime/kbigint")
                }
                ciManagement {
                    system.set("GitHub Actions")
                    url.set("https://github.com/ObserverOfTime/kbigint/actions")
                }
            }
        }
    }

    repositories {
        maven {
            name = "GitHub"
            url = uri("https://maven.pkg.github.com/ObserverOfTime/kbigint")
            credentials {
                username = System.getenv("GITHUB_REPOSITORY_OWNER")
                password = System.getenv("GITHUB_TOKEN")
            }
        }

        maven {
            name = "local"
            url = uri(layout.buildDirectory.dir("repos"))
        }
    }
}

signing {
    isRequired = false
    sign(publishing.publications["kbigint"])
    if (System.getenv("CI") != null) {
        val key = System.getenv("SIGNING_KEY")
        val password = System.getenv("SIGNING_PASSWORD")
        useInMemoryPgpKeys(key, password)
    }
}

tasks.withType(KotlinCompilationTask::class).configureEach {
    compilerOptions {
        apiVersion.set(KotlinVersion.KOTLIN_1_9)
        languageVersion.set(KotlinVersion.KOTLIN_1_9)
        freeCompilerArgs.add("-Xexpect-actual-classes")
    }
}
