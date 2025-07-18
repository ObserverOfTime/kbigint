import java.io.ByteArrayOutputStream
import org.gradle.internal.os.OperatingSystem
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import org.jetbrains.kotlin.gradle.tasks.CInteropProcess

val os: OperatingSystem = OperatingSystem.current()
val libsDir = layout.buildDirectory.get().dir("tmp").dir("libs")
val libtommathDir = projectDir.resolve("src/nativeInterop/libtommath")

fun KotlinNativeTarget.libtommath() {
    compilations.configureEach {
        cinterops.create("tommath") {
            includeDirs.allHeaders(libtommathDir)
            extraOpts("-libraryPath", libsDir.dir(konanTarget.name))
        }
    }
}

plugins {
    `maven-publish`
    signing
    alias(libs.plugins.kotlin.mpp)
    alias(libs.plugins.android.library)
    alias(libs.plugins.dokka)
}

kotlin {
    jvm()

    androidTarget {
        withSourcesJar(true)
        publishLibraryVariants("release")
    }

    js {
        outputModuleName.set(project.name)

        browser {
            webpackTask {
                esModules.set(true)
            }

            testTask {
                useKarma {
                    useChromiumHeadless()
                }
            }
        }

        nodejs {
            testTask {
                useMocha()
            }
        }

        useEsModules()

        @Suppress("OPT_IN_USAGE")
        compilerOptions.target.set("es2015")
    }

    if (os.isLinux) {
        linuxX64 { libtommath() }
        linuxArm64 { libtommath() }
    }

    if (!os.isMacOsX) {
        mingwX64 { libtommath() }
    }

    if (!os.isWindows) {
        macosArm64 { libtommath() }
        macosX64 { libtommath() }
        iosArm64 { libtommath() }
        iosX64 { libtommath() }
        iosSimulatorArm64 { libtommath() }
    }

    applyDefaultHierarchyTemplate()

    jvmToolchain(17)

    sourceSets {
        commonMain {
            languageSettings {
                @OptIn(ExperimentalKotlinGradlePluginApi::class)
                compilerOptions {
                    freeCompilerArgs.add("-Xexpect-actual-classes")
                }
            }

            dependencies {
                implementation(libs.kotlin.stdlib)
            }
        }

        commonTest {
            dependencies {
                implementation(libs.kotlin.test)
            }
        }

        jsMain {
            resources.srcDir(file("src/javascript"))
        }

        jsTest {
            resources.srcDir(file("src/javascript"))
        }
    }
}

android {
    namespace = group.toString()
    compileSdk = 35
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

if (os.isLinux) {
    tasks.getByName<CInteropProcess>("cinteropTommathLinuxX64") {
        doFirst {
            exec {
                executable = "make"
                workingDir = libtommathDir
                args("clean", "libtommath.a")

                environment["ARFLAGS"] = "rcs"
                environment["CFLAGS"] = "-O2 -DMP_NO_FILE -DMP_USE_ENUMS"
            }

            copy {
                from(libtommathDir.resolve("libtommath.a"))
                into(libsDir.dir(konanTarget.name))
            }
        }
    }

    tasks.getByName<CInteropProcess>("cinteropTommathLinuxArm64") {
        outputs.file(libsDir.dir(konanTarget.name).file("libtommath.a"))

        doFirst {
            exec {
                executable = "make"
                workingDir = libtommathDir
                args("clean", "libtommath.a")

                environment["ARFLAGS"] = "rcs"
                environment["CFLAGS"] = "-O2 -DMP_NO_FILE -DMP_USE_ENUMS"
                environment["CROSS_COMPILE"] = "aarch64-linux-gnu-"
            }

            copy {
                from(libtommathDir.resolve("libtommath.a"))
                into(libsDir.dir(konanTarget.name))
            }
        }
    }

    tasks.getByName<CInteropProcess>("cinteropTommathMingwX64") {
        outputs.file(libsDir.dir(konanTarget.name).file("libtommath.a"))

        doFirst {
            exec {
                executable = "make"
                workingDir = libtommathDir
                args("clean", "libtommath.a")

                environment["ARFLAGS"] = "rcs"
                environment["CFLAGS"] = "-O2 -DMP_NO_FILE -DMP_USE_ENUMS" +
                    " -Wno-expansion-to-defined -Wno-declaration-after-statement -Wno-bad-function-cast"
                environment["CROSS_COMPILE"] = "x86_64-w64-mingw32-"
            }

            copy {
                from(libtommathDir.resolve("libtommath.a"))
                into(libsDir.dir(konanTarget.name))
            }
        }
    }
} else if (os.isWindows) {
    tasks.getByName<CInteropProcess>("cinteropTommathMingwX64") {
        outputs.file(libsDir.dir(konanTarget.name).file("libtommath.a"))

        doFirst {
            exec {
                executable = "make"
                workingDir = libtommathDir
                args("clean", "libtommath.a")

                environment["ARFLAGS"] = "rcs"
                environment["CFLAGS"] = "-O2 -DMP_NO_FILE -DMP_USE_ENUMS" +
                    " -Wno-expansion-to-defined -Wno-declaration-after-statement -Wno-bad-function-cast"
                environment["CC"] = "gcc"
            }

            copy {
                from(libtommathDir.resolve("libtommath.a"))
                into(libsDir.dir(konanTarget.name))
            }
        }
    }
} else if (os.isMacOsX) {
    tasks.getByName<CInteropProcess>("cinteropTommathMacosX64") {
        outputs.file(libsDir.dir(konanTarget.name).file("libtommath.a"))

        doFirst {
            val output = ByteArrayOutputStream()
            exec {
                standardOutput = output
                commandLine("xcrun", "--sdk", "macosx", "--show-sdk-path")
            }
            val sysroot = output.use { it.toString().trimEnd() }

            exec {
                executable = "make"
                workingDir = libtommathDir
                args("clean", "libtommath.a")

                environment["ARFLAGS"] = "rcs"
                environment["CFLAGS"] = "-O2 -DMP_NO_FILE -DMP_USE_ENUMS" +
                    " --target=x86_64-apple-macos -isysroot $sysroot -Wno-unused-but-set-variable"
                environment["CC"] = "clang"
            }

            copy {
                from(libtommathDir.resolve("libtommath.a"))
                into(libsDir.dir(konanTarget.name))
            }
        }
    }

    tasks.getByName<CInteropProcess>("cinteropTommathMacosArm64") {
        outputs.file(libsDir.dir(konanTarget.name).file("libtommath.a"))

        doFirst {
            val output = ByteArrayOutputStream()
            exec {
                standardOutput = output
                commandLine("xcrun", "--sdk", "macosx", "--show-sdk-path")
            }
            val sysroot = output.use { it.toString().trimEnd() }

            exec {
                executable = "make"
                workingDir = libtommathDir
                args("clean", "libtommath.a")

                environment["ARFLAGS"] = "rcs"
                environment["CFLAGS"] = "-O2 -DMP_NO_FILE -DMP_USE_ENUMS" +
                    " --target=arm64-apple-macos -isysroot $sysroot -Wno-unused-but-set-variable"
                environment["CC"] = "clang"
            }

            copy {
                from(libtommathDir.resolve("libtommath.a"))
                into(libsDir.dir(konanTarget.name))
            }
        }
    }

    tasks.getByName<CInteropProcess>("cinteropTommathIosArm64") {
        outputs.file(libsDir.dir(konanTarget.name).file("libtommath.a"))

        doFirst {
            val output = ByteArrayOutputStream()
            exec {
                standardOutput = output
                commandLine("xcrun", "--sdk", "iphoneos", "--show-sdk-path")
            }
            val sysroot = output.use { it.toString().trimEnd() }

            exec {
                executable = "make"
                workingDir = libtommathDir
                args("clean", "libtommath.a")

                environment["ARFLAGS"] = "rcs"
                environment["CFLAGS"] = "-O2 -DMP_NO_FILE -DMP_USE_ENUMS" +
                    " --target=arm64-apple-ios -isysroot $sysroot -Wno-unused-but-set-variable"
                environment["CC"] = "clang"
            }

            copy {
                from(libtommathDir.resolve("libtommath.a"))
                into(libsDir.dir(konanTarget.name))
            }
        }
    }

    tasks.getByName<CInteropProcess>("cinteropTommathIosX64") {
        outputs.file(libsDir.dir(konanTarget.name).file("libtommath.a"))

        doFirst {
            val output = ByteArrayOutputStream()
            exec {
                standardOutput = output
                commandLine("xcrun", "--sdk", "iphonesimulator", "--show-sdk-path")
            }
            val sysroot = output.use { it.toString().trimEnd() }

            exec {
                executable = "make"
                workingDir = libtommathDir
                args("clean", "libtommath.a")

                environment["ARFLAGS"] = "rcs"
                environment["CFLAGS"] = "-O2 -DMP_NO_FILE -DMP_USE_ENUMS" +
                    " --target=x86_64-apple-ios-simulator -isysroot $sysroot -Wno-unused-but-set-variable"
                environment["CC"] = "clang"
            }

            copy {
                from(libtommathDir.resolve("libtommath.a"))
                into(libsDir.dir(konanTarget.name))
            }
        }
    }

    tasks.getByName<CInteropProcess>("cinteropTommathIosSimulatorArm64") {
        outputs.file(libsDir.dir(konanTarget.name).file("libtommath.a"))

        doFirst {
            val output = ByteArrayOutputStream()
            exec {
                standardOutput = output
                commandLine("xcrun", "--sdk", "iphonesimulator", "--show-sdk-path")
            }
            val sysroot = output.use { it.toString().trimEnd() }

            exec {
                executable = "make"
                workingDir = libtommathDir
                args("clean", "libtommath.a")

                environment["ARFLAGS"] = "rcs"
                environment["CFLAGS"] = "-O2 -DMP_NO_FILE -DMP_USE_ENUMS" +
                    " --target=arm64-apple-ios-simulator -isysroot $sysroot -Wno-unused-but-set-variable"
                environment["CC"] = "clang"
            }

            copy {
                from(libtommathDir.resolve("libtommath.a"))
                into(libsDir.dir(konanTarget.name))
            }
        }
    }
}

tasks.withType<AbstractPublishToMaven>().configureEach {
    mustRunAfter(tasks.withType<Sign>())
}

tasks.register<Jar>("javadocJar") {
    group = "documentation"
    group = "documentation"
    archiveClassifier.set("javadoc")
    from(files("README.md"))
}

dokka {
    moduleName.set("KBigInt")
    pluginsConfiguration.html {
        footerMessage.set("(c) 2024-2025 ObserverOfTime")
    }
    dokkaSourceSets.configureEach {
        jdkVersion.set(17)
        includes.from(file("README.md"))
    }
    dokkaPublications.configureEach {
        suppressInheritedMembers.set(false)
    }
}

publishing {
    publications.withType(MavenPublication::class) {
        artifact(tasks["javadocJar"])
        pom {
            name.set("KBigInt")
            description.set("Kotlin Multiplatform BigInteger library")
            url.set("https://observeroftime.github.io/kbigint/")
            inceptionYear.set("2024")
            licenses {
                license {
                    name.set("Apache License 2.0")
                    url.set("https://www.apache.org/licenses/LICENSE-2.0")
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
                connection.set("scm:git:git://github.com/ObserverOfTime/kbigint.git")
                developerConnection.set("scm:git:ssh://github.com/ObserverOfTime/kbigint.git")
            }
            ciManagement {
                system.set("GitHub Actions")
                url.set("https://github.com/ObserverOfTime/kbigint/actions")
            }
        }
    }

    repositories {
        maven {
            name = "GitHub"
            url = uri("https://maven.pkg.github.com/observeroftime/kbigint")
            credentials {
                username = System.getenv("GITHUB_ACTOR")
                password = System.getenv("GITHUB_TOKEN")
            }
        }

        maven {
            name = "local"
            url = uri(rootProject.layout.buildDirectory.dir("repo"))
        }
    }
}

signing {
    isRequired = System.getenv("CI") != null
    if (isRequired) {
        val key = System.getenv("SIGNING_KEY")
        val password = System.getenv("SIGNING_PASSWORD")
        useInMemoryPgpKeys(key, password)
    }
    sign(publishing.publications)
}
