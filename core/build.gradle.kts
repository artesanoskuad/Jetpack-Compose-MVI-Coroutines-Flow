plugins {
  androidLib
  kotlinAndroid
}

android {
  compileSdkVersion(appConfig.compileSdkVersion)
  buildToolsVersion(appConfig.buildToolsVersion)

  defaultConfig {
    minSdkVersion(appConfig.minSdkVersion)
    targetSdkVersion(appConfig.targetSdkVersion)

    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
  }

  buildTypes {
    getByName("release") {
      isMinifyEnabled = true
      proguardFiles(
        getDefaultProguardFile("proguard-android-optimize.txt"),
        "proguard-rules.pro"
      )
    }
  }

  buildFeatures {
    compose = true
  }
  composeOptions {
    kotlinCompilerExtensionVersion = deps.compose.version
  }
}

dependencies {
  implementation(deps.jetbrains.coroutinesCore)
  implementation(deps.jetbrains.coroutinesAndroid)

  implementation(deps.androidx.coreKtx)
  implementation(deps.androidx.material)

  implementation(deps.lifecycle.commonJava8)
  implementation(deps.lifecycle.runtimeKtx)

  implementation(deps.compose.foundation)
}
