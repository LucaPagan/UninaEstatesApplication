plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin")
    id("kotlin-parcelize")
    id ("org.jetbrains.kotlin.plugin.serialization") version ("1.9.23") // Usa la tua versione di Kotlin
}

secrets {
    propertiesFileName = "secrets.properties"
    defaultPropertiesFileName = "local.defaults.properties"
}


android {
    namespace = "com.dieti.dietiestates25"
    compileSdk = 35
    defaultConfig {
        applicationId = "com.dieti.dietiestates25"
        minSdk = 34
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
    implementation("androidx.datastore:datastore-preferences:1.0.0")

    //Google Maps
    implementation("com.google.android.gms:play-services-maps:19.2.0")
    // Maps Compose Library
    implementation("com.google.maps.android:maps-compose:4.3.3")
    // Core Android
    implementation(libs.androidx.core.ktx.v1160)
    implementation(libs.androidx.lifecycle.runtime.ktx.v287)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.activity.compose.v1101)

    // Compose
    implementation(libs.ui)
    implementation(libs.ui.graphics)
    implementation(libs.ui.tooling.preview)
    implementation(libs.material3)
    implementation(libs.androidx.material.icons.extended)
    implementation(platform(libs.androidx.compose.bom.v20250400))

    // Retrofit (Per le chiamate API)
    implementation(libs.retrofit)
    implementation(libs.converter.gson)

    // Coil (Per caricare le immagini dagli URL/Byte)
    implementation(libs.coil.compose)

    // Coroutines (Per gestire le chiamate asincrone)
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")

    // Navigation
    implementation(libs.androidx.navigation.compose)

    // Gson per la serializzazione JSON (usato in PreferenceManager)
    implementation(libs.gson)
    implementation(libs.androidx.espresso.core)
    implementation(libs.androidx.compose.runtime.livedata)

    // Debug
    debugImplementation(libs.ui.tooling)
    debugImplementation(libs.ui.test.manifest)

    implementation(libs.androidx.material.icons.extended)
    implementation(platform(libs.androidx.compose.bom.v20250400))
    implementation(libs.androidx.material.icons.extended)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.compose.material3.material3)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.navigation.runtime.ktx)
    implementation(libs.androidx.navigation.runtime.android)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    implementation(libs.androidx.foundation)
    implementation(libs.androidx.compose.material3.material3)
    implementation(libs.coil.compose)
    implementation(libs.androidx.core.ktx.v1100)
    implementation(libs.androidx.activity.ktx)
    implementation(libs.accompanist.permissions)

    implementation(libs.androidx.material.icons.extended)

    // OKHTTP (Il motore sotto Retrofit, utile per loggare le richieste)
    implementation("com.squareup.okhttp3:logging-interceptor:4.11.0")

    // GSON (Per convertire JSON in Oggetti)
    implementation(libs.gson)
}