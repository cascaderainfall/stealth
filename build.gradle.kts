buildscript {

    ext {
        kotlin = '1.7.20'
        androidGradlePlugin = '7.3.1'
        hiltGradlePlugin = '2.44.2'

        hilt = '1.0.0'

        core = '1.9.0'
        appCompat = '1.5.1'
        constraintLayout = '2.1.4'
        recyclerview = '1.2.1'
        fragment = '1.5.4'
        lifecycle = '2.5.1'
        coordinatorlayout = '1.2.0'
        viewpager2 = '1.0.0'
        preference = '1.2.0'

        navigation = '2.5.3'

        room = '2.4.3'
        datastore = '1.0.0'

        paging = '3.1.1'

        work = '2.7.1'

        material = '1.7.0'

        browser = '1.4.0'

        splashscreen = '1.0.0'

        touchImageView = '3.0.3'

        exoPlayer = '2.18.1'

        retrofit = '2.9.0'

        moshi = '1.14.0'

        okio = '3.2.0'

        coil = '2.2.2'

        jsoup = '1.15.3'

        drawer = '1.0.3'

        coroutines = '1.4.1'

        jUnit = '4.13.2'
        test = '1.1.4'
        testRunner = '1.5.1'
        espresso = '3.5.0'
    }

    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath "com.android.tools.build:gradle:$androidGradlePlugin"
        classpath "org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlin"
        classpath "com.google.dagger:hilt-android-gradle-plugin:$hiltGradlePlugin"
        classpath "androidx.navigation:navigation-safe-args-gradle-plugin:$navigation"
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
        maven { url 'https://jitpack.io' }
    }
}

task clean(type: Delete) {
    delete rootProject.buildDir
}