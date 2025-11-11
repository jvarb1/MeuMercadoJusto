import java.util.Properties
import java.io.FileInputStream

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("org.jetbrains.kotlin.plugin.serialization") version "2.0.0"
    id("com.google.devtools.ksp") version "2.0.21-1.0.27"
}

android {
    namespace = "br.com.joaovictor.meumercadojusto"
    compileSdk = 36

    defaultConfig {
        applicationId = "br.com.joaovictor.meumercadojusto"
        minSdk = 24
        targetSdk = 35  // Compatível com Android 15 (API 35)
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        
        // Carregar credenciais do Supabase e API do local.properties
        val localProperties = Properties()
        val localPropertiesFile = rootProject.file("local.properties")
        if (localPropertiesFile.exists()) {
            localProperties.load(FileInputStream(localPropertiesFile))
        }
        
        buildConfigField("String", "SUPABASE_URL", "\"${localProperties.getProperty("supabase.url", "")}\"")
        buildConfigField("String", "SUPABASE_KEY", "\"${localProperties.getProperty("supabase.key", "")}\"")
        buildConfigField("String", "API_GOVERNO_URL", "\"${localProperties.getProperty("api.governo.url", "")}\"")
        buildConfigField("String", "API_GOVERNO_KEY", "\"${localProperties.getProperty("api.governo.key", "")}\"")
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

// Estratégia de resolução de dependências para evitar conflitos
configurations.all {
    resolutionStrategy {
        // Forçar versões compatíveis do AndroidX Core (estáveis e compatíveis)
        // Versão 1.15.0 resolve o problema do setStylusHandwritingEnabled no Android 15
        force("androidx.core:core-ktx:1.15.0")
        force("androidx.core:core:1.15.0")
        
        // Forçar versões compatíveis do Activity
        force("androidx.activity:activity-compose:1.9.2")
        force("androidx.activity:activity-ktx:1.9.2")
        force("androidx.activity:activity:1.9.2")
        
        // Preferir versões estáveis
        preferProjectModules()
    }
}

dependencies {
    val room_version = "2.8.1"
    val lifecycle_version = "2.9.3"
    val supabase_version = "3.0.0"
    
    // Room
    implementation("androidx.room:room-runtime:$room_version")
    implementation("androidx.room:room-ktx:$room_version")
    ksp("androidx.room:room-compiler:$room_version")

    // Lifecycle & ViewModel
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:$lifecycle_version")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:$lifecycle_version")
    
    // Supabase
    implementation(platform("io.github.jan-tennert.supabase:bom:$supabase_version"))
    implementation("io.github.jan-tennert.supabase:postgrest-kt")
    implementation("io.github.jan-tennert.supabase:realtime-kt")
    implementation("io.github.jan-tennert.supabase:storage-kt")
    implementation("io.github.jan-tennert.supabase:auth-kt")
    // Ktor client para Supabase
    // O ktor-client-android já inclui o core necessário
    implementation("io.ktor:ktor-client-android:2.3.12")
    // Adicionar explicitamente o core para garantir que todos os plugins estejam disponíveis
    implementation("io.ktor:ktor-client-core:2.3.12")
    
    // Networking
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")

    implementation("androidx.compose.material:material-icons-extended:1.7.8")
    implementation("io.coil-kt:coil-compose:2.6.0")
    implementation(libs.androidx.navigation.compose)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}