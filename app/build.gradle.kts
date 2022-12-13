apply plugin: 'com.android.application'
apply plugin: 'kotlin-android'
apply plugin: 'kotlin-parcelize'
apply plugin: 'kotlin-kapt'
apply plugin: 'dagger.hilt.android.plugin'
apply plugin: "androidx.navigation.safeargs.kotlin"

def keystorePropertiesFile = rootProject.file("keystore.properties")
def keystoreProperties = new Properties()

if (keystorePropertiesFile.exists()) {
    keystoreProperties.load(new FileInputStream(keystorePropertiesFile))
} else {
    keystoreProperties['storeFile'] = 'keystore.jks'
}

android {
    namespace "com.cosmos.unreddit"

    compileSdkVersion 33

    defaultConfig {
        applicationId "com.cosmos.unreddit"
        minSdkVersion 21
        targetSdkVersion 33
        versionCode 16
        versionName "2.0.3"

        testInstrumentationRunner "androidx.test.runner.AndroidJUnitRunner"

        vectorDrawables.useSupportLibrary = true

        javaCompileOptions {
            annotationProcessorOptions {
                arguments += ["room.schemaLocation": "$projectDir/schemas".toString()]
            }
        }
    }

    signingConfigs {
        release {
            keyAlias keystoreProperties['keyAlias']
            keyPassword keystoreProperties['keyPassword']
            storeFile file(keystoreProperties['storeFile'])
            storePassword keystoreProperties['storePassword']
        }
    }

    buildTypes {
        release {
            minifyEnabled true
            shrinkResources true
            proguardFiles getDefaultProguardFile('proguard-android-optimize.txt'), 'proguard-rules.pro'
            signingConfig signingConfigs.release
        }
        debug {
            applicationIdSuffix ".dev"
            debuggable true
        }
    }

    buildFeatures {
        viewBinding true
        dataBinding true
    }

    compileOptions {
        sourceCompatibility 1.8
        targetCompatibility 1.8
    }

    kotlinOptions {
        jvmTarget = "1.8"
        freeCompilerArgs += ["-opt-in=kotlinx.coroutines.ExperimentalCoroutinesApi"]
    }
}

dependencies {
    implementation fileTree(dir: "libs", include: ["*.jar"])

    implementation "com.google.dagger:hilt-android:$rootProject.hiltGradlePlugin"
    kapt "com.google.dagger:hilt-android-compiler:$rootProject.hiltGradlePlugin"
    kapt "androidx.hilt:hilt-compiler:$rootProject.hilt"

    implementation "androidx.hilt:hilt-navigation-fragment:$rootProject.hilt"
    implementation "androidx.hilt:hilt-work:$rootProject.hilt"

    implementation "androidx.core:core-ktx:$rootProject.core"
    implementation "androidx.appcompat:appcompat:$rootProject.appCompat"
    implementation "androidx.constraintlayout:constraintlayout:$rootProject.constraintLayout"
    implementation "androidx.recyclerview:recyclerview:$rootProject.recyclerview"
    implementation "androidx.fragment:fragment-ktx:$rootProject.fragment"
    implementation "androidx.lifecycle:lifecycle-livedata-ktx:$rootProject.lifecycle"
    implementation "androidx.lifecycle:lifecycle-viewmodel-ktx:$rootProject.lifecycle"
    implementation "androidx.lifecycle:lifecycle-runtime-ktx:$rootProject.lifecycle"
    implementation "androidx.coordinatorlayout:coordinatorlayout:$rootProject.coordinatorlayout"
    implementation "androidx.viewpager2:viewpager2:$rootProject.viewpager2"
    implementation "androidx.preference:preference-ktx:$rootProject.preference"

    implementation "androidx.navigation:navigation-fragment-ktx:$rootProject.navigation"
    implementation "androidx.navigation:navigation-ui-ktx:$rootProject.navigation"

    implementation "androidx.room:room-runtime:$rootProject.room"
    kapt "androidx.room:room-compiler:$rootProject.room"
    implementation "androidx.room:room-ktx:$rootProject.room"
    implementation "androidx.datastore:datastore-preferences:$rootProject.datastore"

    implementation "androidx.paging:paging-runtime-ktx:$rootProject.paging"

    implementation "androidx.work:work-runtime-ktx:$rootProject.work"

    implementation "com.google.android.material:material:$rootProject.material"

    implementation "androidx.browser:browser:$rootProject.browser"

    implementation "androidx.core:core-splashscreen:$rootProject.splashscreen"

    implementation "com.squareup.retrofit2:retrofit:$rootProject.retrofit"
    implementation "com.squareup.retrofit2:converter-moshi:$rootProject.retrofit"

    implementation "com.squareup.moshi:moshi:$rootProject.moshi"
    kapt "com.squareup.moshi:moshi-kotlin-codegen:$rootProject.moshi"
    implementation("com.squareup.moshi:moshi-adapters:$rootProject.moshi")

    implementation("com.squareup.okio:okio:$rootProject.okio")

    implementation "io.coil-kt:coil:$rootProject.coil"
    implementation "io.coil-kt:coil-gif:$rootProject.coil"

    implementation "com.github.MikeOrtiz:TouchImageView:$rootProject.touchImageView"

    implementation "com.google.android.exoplayer:exoplayer:$rootProject.exoPlayer"

    implementation "org.jsoup:jsoup:$rootProject.jsoup"

    implementation "com.drakeet.drawer:drawer:$rootProject.drawer"

    testImplementation "junit:junit:$rootProject.jUnit"

    androidTestImplementation "androidx.test:runner:$rootProject.testRunner"
    androidTestImplementation "androidx.test.ext:junit:$rootProject.test"
    androidTestImplementation "androidx.test.espresso:espresso-core:$rootProject.espresso"

}