plugins {
    alias(libs.plugins.android.application)
    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin")
    alias(libs.plugins.google.gms.google.services)
}

android {
    namespace = "com.das.entrega1"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.das.entrega1"
        minSdk = 24
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
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    // Servicios de Google Play (Para la ubicación)
    implementation("com.google.android.gms:play-services-location:21.2.0")

    // Volley (Para hacer las peticiones a Internet en 2º plano)
    implementation("com.android.volley:volley:1.2.1")
    implementation(libs.firebase.storage)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    // Librería de Firebase Storage
    implementation("com.google.firebase:firebase-storage:20.3.0")
    implementation("com.google.firebase:firebase-messaging:23.4.0")
    // Glide para descargar y mostrar imágenes de Internet (Tema 16)
    implementation("com.github.bumptech.glide:glide:4.7.1")

    // Google Maps (Punto 2)
    implementation("com.google.android.gms:play-services-maps:18.1.0")
}

secrets {
    // Optionally specify a different file name containing your secrets.
    // The plugin defaults to "local.properties"
    propertiesFileName = "secrets.properties"
    // A properties file containing default secret values. This file can be
    // checked in version control.
    defaultPropertiesFileName = "local.default.properties"
    // Configure which keys should be ignored by the plugin by providing
    //regular expressions.
    // "sdk.dir" is ignored by default.
    ignoreList.add("keyToIgnore") // Ignore the key "keyToIgnore"
    ignoreList.add("sdk.*") // Ignore all keys matching the regexp
}