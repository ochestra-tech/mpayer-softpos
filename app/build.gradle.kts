plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "ng.mpayer.softpos"
    compileSdk = 36

    defaultConfig {
        applicationId = "ng.mpayer.softpos"
        minSdk = libs.versions.minSdk.get().toInt()
        targetSdk = libs.versions.targetSdk.get().toInt()
        versionCode = libs.versions.versionCode.get().toInt()
        versionName = libs.versions.versionName.get()

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        vectorDrawables {
            useSupportLibrary = true
        }

        // Build config fields
        buildConfigField("String", "HOST_IP", "\"192.168.1.100\"")
        buildConfigField("int", "HOST_PORT", "8080")
        buildConfigField("String", "TERMINAL_ID", "\"12345678\"")
        buildConfigField("String", "MERCHANT_ID", "\"123456789012345\"")
        buildConfigField("String", "ACQUIRER_ID", "\"123456\"")
        proguardFiles("proguard-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            isDebuggable = false

            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )

            buildConfigField("boolean", "MOCK_RESPONSES", "false")
            buildConfigField("boolean", "ENABLE_LOGGING", "false")
        }
        debug {
            isMinifyEnabled = false
            isDebuggable = true
            applicationIdSuffix = ".debug"
            versionNameSuffix = "-debug"

            buildConfigField("boolean", "MOCK_RESPONSES", "true")
            buildConfigField("boolean", "ENABLE_LOGGING", "true")
        }

        create("staging") {
            initWith(getByName("debug"))
            isMinifyEnabled = true
            isDebuggable = false
            applicationIdSuffix = ".staging"
            versionNameSuffix = "-staging"

            buildConfigField("String", "HOST_IP", "\"staging.payment-host.com\"")
            buildConfigField("int", "HOST_PORT", "443")
            buildConfigField("boolean", "MOCK_RESPONSES", "false")
        }
    }


    kotlinOptions {
        // Enable experimental APIs

    }

    buildFeatures {
        compose = true
        buildConfig = true
        viewBinding = false
    }

    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.kotlinCompilerExtensionVersion.get()
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
            excludes += "/META-INF/DEPENDENCIES"
            excludes += "/META-INF/LICENSE"
            excludes += "/META-INF/LICENSE.txt"
            excludes += "/META-INF/NOTICE"
            excludes += "/META-INF/NOTICE.txt"
        }
    }

    testOptions {
        unitTests {
            isIncludeAndroidResources = true
        }
    }
    buildToolsVersion = "36.0.0"
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

dependencies {

    // Compose BOM - manages all compose library versions
    implementation(platform(libs.androidx.compose.bom))

    // Kotlin BOM
    implementation(platform(libs.kotlin.bom))

    // Core Android bundles
    implementation(libs.bundles.core)
    implementation(libs.bundles.compose)

    // Navigation
    implementation(libs.androidx.navigation.compose)

    // Security
    implementation(libs.androidx.security.crypto.ktx)

    // Network (optional for production)
    implementation(libs.bundles.network)

    // Jetpack Compose BOM
    implementation(libs.ui)
    implementation(libs.ui.graphics)
    implementation(libs.ui.tooling.preview)
    implementation(libs.material3)
    implementation(libs.androidx.material.icons.extended)


    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)

    // Testing
    testImplementation(libs.bundles.testing)
    androidTestImplementation(libs.bundles.android.testing)
    androidTestImplementation(platform(libs.androidx.compose.bom))

    // Debug tools
    debugImplementation(libs.bundles.debug)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}

// Tasks for code quality
tasks.register("detekt") {
    description = "Run detekt static analysis"
    // Add detekt configuration when needed
}

tasks.register("ktlintCheck") {
    description = "Run ktlint code style check"
    // Add ktlint configuration when needed
}