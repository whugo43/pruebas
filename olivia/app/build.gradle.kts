plugins { id("com.android.application") }
android {
    namespace = "com.olivia.sorpresas"
    compileSdk = 35
    defaultConfig {
        applicationId = "com.olivia.sorpresas"
        minSdk = 24
        targetSdk = 35
        versionCode = 4
        versionName = "2.3"
    }
    buildTypes { release { isMinifyEnabled = false } }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}
