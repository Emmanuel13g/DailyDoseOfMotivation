plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("com.google.gms.google-services")
    id("com.google.devtools.ksp")
    id("com.google.firebase.crashlytics")
}

android {
    namespace = "com.egdcoding.dailydoseofmotivation"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.egdcoding.dailydoseofmotivation"
        minSdk = 24
        targetSdk = 34
        versionCode = 5
        versionName = "1.4"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
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
    implementation(libs.core.ktx)
    implementation(libs.androidx.junit.ktx)
    implementation(libs.androidx.runner)
    implementation(libs.androidx.ui.test.junit4.android)
    implementation(libs.androidx.work.runtime.ktx)
    implementation(libs.transport.runtime)
    implementation(libs.firebase.auth)
    implementation(libs.transport.runtime)
    implementation(libs.firebase.database)
    implementation(libs.androidx.espresso.core.v350)
    implementation(libs.transport.backend.cct)
    implementation(libs.transport.backend.cct)
    implementation(libs.transport.backend.cct)
    implementation(libs.androidx.appcompat)
    testImplementation(libs.junit)
    testImplementation(libs.junit.jupiter)
    testImplementation(libs.testng)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    androidTestImplementation(libs.testng)
    androidTestImplementation(libs.firebase.auth)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    // Additional Compose libraries
    implementation(libs.androidx.foundation)
    implementation(libs.material3)
    implementation(libs.ui)

    implementation(libs.androidx.navigation.compose)

    // Firebase dependencies
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.firestore.ktx)
    implementation(libs.firebase.analytics)

    implementation(libs.play.services.base)
    implementation(libs.play.services.measurement)

    // Room Database
    implementation(libs.androidx.room.runtime)
    ksp(libs.androidx.room.compiler)
    implementation(libs.androidx.room.ktx)

    /** Crashlytics **/

    implementation(libs.firebase.crashlytics.ktx) // Update to the latest version

    implementation(libs.google.firebase.analytics)

    implementation(libs.androidx.material.icons.extended)

    implementation(libs.firebase.crashlytics.ktx.v1861)



    // Mocking dependencies
    testImplementation(libs.mockito.core)
    testImplementation(libs.mockito.kotlin)
    testImplementation(libs.mockk)
    testImplementation(libs.androidx.core.testing)

    testImplementation(libs.com.google.firebase.firebase.firestore.v2400.x2)
    testImplementation(libs.firebase.auth)
    testImplementation(libs.firebase.database)



    // Coroutines test
    testImplementation(libs.kotlinx.coroutines.test)

    // AndroidX test (for UI and integration tests)
    androidTestImplementation(libs.androidx.junit)
    //androidTestImplementation(libs.androidx.espresso.core)


    // Compose UI Testing
    androidTestImplementation(libs.ui.test.junit4)
    androidTestImplementation(libs.androidx.espresso.core.v350)

    configurations.all {
        resolutionStrategy.force("androidx.test.espresso:espresso-core:3.5.0")
    }



    // Firebase Testing (use emulator for local testing)
    androidTestImplementation(libs.firebase.firestore)

    implementation(libs.accompanist.systemuicontroller)


}

apply(plugin = "com.google.gms.google-services")
