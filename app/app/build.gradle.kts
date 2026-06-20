import java.util.Properties
import kotlin.apply

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    kotlin("plugin.serialization") version "2.1.21"
    id("com.google.protobuf")
    id("app.cash.sqldelight")
}

sqldelight {
    databases {
        create("AppDatabase") {
            packageName.set("com.sum1t.data.sqldelight")
        }
    }
}

protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:3.25.3"
    }

    generateProtoTasks {
        all().forEach { task ->
            task.builtins {
                create("java") {
                    option("lite")
                }
            }
        }
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
        maven("https://oss.sonatype.org/content/repositories/snapshots/")
        maven("https://oss.sonatype.org/content/repositories/releases/")
    }
}

val localProperties = Properties().apply {
    load(rootProject.file("local.properties").inputStream())
}

val baseUrl =
    localProperties.getProperty("BASE_URL")?.trim()?.removeSurrounding("\"").orEmpty()

android {
    namespace = "com.sum1t.preppy"
    compileSdk = 36


    defaultConfig {
        applicationId = "com.sum1t.preppy"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField("String", "BASE_URL", "\"$baseUrl\"")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.navigation.compose)
    implementation(libs.play.services.location)
    implementation(libs.play.services.mlkit.barcode.scanning)
    implementation(libs.androidx.compose.runtime)
    implementation(libs.androidx.compose.animation.core)
    implementation(libs.androidx.compose.ui.geometry)
    implementation(libs.androidx.compose.animation)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)


    implementation(libs.koin.compose)
    implementation(libs.koin.core)
    implementation(libs.koin.compat)
    implementation(libs.koin.android)
    implementation(libs.splashscreen)
    implementation(libs.system.ui)
    debugImplementation(libs.pluto)
    releaseImplementation(libs.pluto.no.op)
    debugImplementation(libs.pluto.plugin)
    releaseImplementation(libs.pluto.plugin.no.op)
    implementation(libs.retrofit)
    implementation(libs.retrofit.gson.convertor)
//    implementation(libs.ktor.client.okHttp)
//    implementation(libs.ktor.client.android)
//    implementation(libs.ktor.client.serialization)
    val ktorVersion = "2.3.2"
    implementation("io.ktor:ktor-client-core:$ktorVersion")
    implementation("io.ktor:ktor-client-okhttp:$ktorVersion")
    implementation("io.ktor:ktor-client-logging:$ktorVersion")
    implementation("io.ktor:ktor-client-content-negotiation:$ktorVersion")
    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")
    implementation("com.google.maps.android:maps-compose:6.1.0")

    implementation(libs.swipe.refresh)
    implementation(libs.accompanist)
    implementation(libs.pager)
    implementation(libs.pager.indicator)
    implementation(libs.coil)

    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.1")

    implementation ("com.google.zxing:core:3.5.2")
    implementation("com.journeyapps:zxing-android-embedded:4.3.0")

    val camerax = "1.3.3"
    implementation("androidx.camera:camera-core:${camerax}")
    implementation("androidx.camera:camera-camera2:${camerax}")
    implementation("androidx.camera:camera-lifecycle:${camerax}")
    implementation("androidx.camera:camera-view:${camerax}")


    implementation("com.google.mlkit:barcode-scanning:17.3.0")

    implementation(libs.mr.mike)
//    implementation("com.github.ihsanbal:LoggingInterceptor:3.1.0") {
//        exclude(group = "org.json", module = "json")
//    }

    // DataStore (Proto)
    implementation("androidx.datastore:datastore:1.1.0")

// Proto support
    implementation("com.google.protobuf:protobuf-javalite:3.25.3")

    implementation("androidx.compose.material:material-icons-extended")
    implementation("androidx.compose.foundation:foundation")

    // SQLDelight
    implementation("app.cash.sqldelight:android-driver:2.0.2")

    implementation("androidx.work:work-runtime-ktx:2.9.0")

    implementation("androidx.glance:glance-appwidget:1.1.0")
//    implementation("androidx.glance:glance-appwidget-previews:1.1.0")
    implementation("androidx.glance:glance-material:1.1.0")
    implementation("androidx.work:work-runtime-ktx:2.9.0")


    implementation("app.cash.sqldelight:coroutines-extensions:2.0.2")
}