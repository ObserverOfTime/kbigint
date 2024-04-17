# Module KBigInt Serialization

The serialization module of the KBigInt library.

## Add the dependency

### Maven Central Repository

**Not available yet.**

### GitHub Package Registry

Add these to `build.gradle.kts`:

```kotlin
dependencies {
    implementation("io.github.observeroftime.kbigint:kbigint")
    implementation("io.github.observeroftime.kbigint:kbigint-serialization")
}

repositories {
    maven {
        url = uri("https://maven.pkg.github.com/observeroftime/kbigint")
        credentials {
            username = project.findProperty("gpr.user") as String? ?: System.getenv("GITHUB_ACTOR")
            password = project.findProperty("gpr.key") as String? ?: System.getenv("GITHUB_TOKEN")
        }
    }
}
```

You need to set your GitHub credentials either in the `gpr.user` & `gpr.token` properties,
or in the `GITHUB_ACTOR` & `GITHUB_TOKEN` environment variables. (`GITHUB_ACTOR` is automatically
set in GitHub actions and `GITHUB_TOKEN` can be set to `${{github.token}}` in `env`.)

## Use the library

There are three ways to enable (de)serialization of `KBigInt` classes.
The examples use `kotlinx-serialization-json` but any format should work.

### Serializers module

You can declare a new encoder/decoder instance using the serializers module.

```kotlin
import io.github.observeroftime.kbigint.KBigInt
import io.github.observeroftime.kbigint.serialization.module
import kotlinx.serialization.json.*

val json = Json { serializersModule = module }

val number = KBigInt("9007199254740991")
val encoded = json.encodeToJsonElement(number)
val decoded = json.decodeFromJsonElement(encoded)
```

### Serializable type alias

The serializable type alias defined can be used explicitly in type parameters.

**NOTE: this method only works in JVM & Android.**

```kotlin
import io.github.observeroftime.kbigint.serialization.KBigInt
import kotlinx.serialization.json.*

val number = KBigInt("9007199254740991")
val encoded = Json.encodeToJsonElement<KBigInt>(number)
val decoded = Json.decodeFromJsonElement<KBigInt>(encoded)
```
### Serialization strategy

You can use the serializer as the serialization strategy parameter.

```kotlin
import io.github.observeroftime.kbigint.KBigInt
import io.github.observeroftime.kbigint.serialization.KBigIntSerializer
import kotlinx.serialization.json.*

val number = KBigInt("9007199254740991")
val encoded = Json.encodeToJsonElement(KBigIntSerializer, number)
val decoded = Json.decodeFromJsonElement(KBigIntSerializer, encoded)
```
