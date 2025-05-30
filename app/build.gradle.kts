plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("kotlin-kapt")

    // Add the Google services Gradle plugin
    id("com.google.gms.google-services")

    // Add the secret gradle plugin plugin (.env)
    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin")
}

android {
    namespace = "com.example.nutrisaver"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.nutrisaver"
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

            //ini variabel buat env
//            buildConfigField("String", "BASE_URL", "http://10.0.2.2:3000/")
//            buildConfigField("String","API_PATH_PREFIX","api")
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
    implementation(libs.androidx.runtime.livedata)
    implementation(libs.androidx.foundation.android)
//    implementation(libs.androidx.navigation.compose.jvmstubs)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    // Import the Firebase BoM
    implementation(platform(libs.firebase.bom.v33130))

    // Add the dependency for the Firebase Authentication library
    // When using the BoM, you don't specify versions in Firebase library dependencies
    implementation(libs.google.firebase.auth)
    implementation(libs.androidx.navigation.compose)

    implementation (libs.retrofit) // Retrofit library
    implementation (libs.converter.gson) // Gson converter for Retrofit
    implementation (libs.converter.moshi)

    // Moshi dependencies
    implementation (libs.moshi)
    implementation (libs.moshi.kotlin)
    implementation (libs.kotlinx.coroutines.android) // Kotlin Coroutines (if using coroutines with Retrofit)

    implementation(libs.androidx.material)
    // Material3 is already included via libs.androidx.material3 above
    implementation("io.coil-kt:coil-compose:2.4.0")

    // Google Authentication dependencies
    implementation(libs.androidx.credentials)
    implementation(libs.androidx.credentials.play.services.auth)
    implementation(libs.googleid)
    implementation(libs.play.services.auth)

}