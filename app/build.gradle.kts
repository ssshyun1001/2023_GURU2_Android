plugins {
    id ("com.android.application")
    id ("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.example.guru2_dsjouju_app"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.guru2_dsjouju_app"
        minSdk = 24
        targetSdk = 34
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // 추가한 implementation
    implementation(libs.play.services.location)
    implementation(libs.androidx.mediarouter)
    implementation(libs.gson) // Gson 의존성 추가
    implementation(libs.play.services.maps) // Maps library
    implementation(libs.play.services.location) // Location library
    implementation(libs.android.maps.utils) // Maps utils library

    // Retrofit
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    // 추가: OkHttp Logging Interceptor (디버깅을 위해)
    implementation("com.squareup.okhttp3:logging-interceptor:5.0.0-alpha.4")
    //Google Play Services 라이브러리가 포함되어 있는지 확인
    implementation("com.google.android.gms:play-services-maps:18.0.2")
    implementation("com.google.android.gms:play-services-location:21.0.1")
    //편의점 API 접근
    implementation("org.simpleframework:simple-xml:2.7.1")
    implementation("com.squareup.retrofit2:converter-simplexml:2.9.0")


    implementation("androidx.core:core-ktx:1.10.1")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.9.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    /*
    gradle 오류 대비 "형식 implementation

    implementation("com.google.android.gms:play-services-maps:18.0.0") // Maps library
    implementation("com.google.android.gms:play-services-location:21.0.1") // Location library
    implementation("com.google.maps.android:android-maps-utils:2.4.0") // Maps utils library
    */
}
