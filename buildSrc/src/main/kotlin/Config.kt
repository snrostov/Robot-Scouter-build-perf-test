

object Config {
    private val kotlinVersion = "1.2.41"
//    private val kotlinVersion = "1.3-M2"

    object SdkVersions {
        val compile = 28
        val target = 28
        val min = 16
    }

    object Plugins {
        val android = "com.android.tools.build:gradle:3.2.0-alpha17"
        val kotlin = "org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion"

        val google = "com.google.gms:google-services:3.2.1"
        val firebase = "com.google.firebase:firebase-plugins:1.1.5"
        val fabric = "io.fabric.tools:gradle:1.25.4"
        val publishing = "com.github.SUPERCILEX:gradle-play-publisher:1c37a81392"

        val ktlint = "com.github.shyiko:ktlint:0.23.1"
    }

    object Libs {
        object Kotlin {
            val jvm = "org.jetbrains.kotlin:kotlin-stdlib-jdk8:$kotlinVersion"
            val js = "org.jetbrains.kotlin:kotlin-stdlib-js:$kotlinVersion"
            val coroutines = "org.jetbrains.kotlinx:kotlinx-coroutines-core:0.22.5"
            val ktx = "androidx.core:core-ktx:0.3"
        }

        object Anko {
            private val version = "0.10.5"

            val common = "org.jetbrains.anko:anko-common:$version"
            val coroutines = "org.jetbrains.anko:anko-coroutines:$version"
            val appCompat = "org.jetbrains.anko:anko-appcompat-v7-commons:$version"
            val design = "org.jetbrains.anko:anko-design:$version"
        }

        object Support {
            private val version = "27.1.1"

            val multidex = "com.android.support:multidex:1.0.3"
            val v4 = "com.android.support:support-v4:$version"
            val appCompat = "com.android.support:appcompat-v7:$version"
            val design = "com.android.support:design:$version"
            val palette = "com.android.support:palette-v7:$version"
            val cardView = "com.android.support:cardview-v7:$version"
            val emoji = "com.android.support:support-emoji-appcompat:$version"
            val customTabs = "com.android.support:support-emoji-appcompat:$version"
            val pref = "com.android.support:preference-v7:$version"

            val constraint = "com.android.support.constraint:constraint-layout:1.1.1"
        }

        object Arch {
            private val version = "1.1.1"
            private val workVersion = "1.0.0-alpha02"

            val common = "android.arch.lifecycle:common-java8:$version"
            val extensions = "android.arch.lifecycle:extensions:$version"
            val work = "android.arch.work:work-runtime-ktx:$workVersion"
            val workFirebase = "android.arch.work:work-firebase:$workVersion"
        }

        object Firebase {
            val core = "com.google.firebase:firebase-core:16.0.0"
            val auth = "com.google.firebase:firebase-auth:16.0.1"
            val firestore = "com.google.firebase:firebase-firestore:17.0.1"
            val functions = "com.google.firebase:firebase-functions:16.0.1"
            val storage = "com.google.firebase:firebase-storage:16.0.1"
            val config = "com.google.firebase:firebase-config:16.0.0"
            val indexing = "com.google.firebase:firebase-appindexing:15.0.1"
            val messaging = "com.google.firebase:firebase-messaging:17.0.0"
            val invites = "com.google.firebase:firebase-invites:16.0.0"
            val perf = "com.google.firebase:firebase-perf:16.0.0"

            val crashlytics = "com.crashlytics.sdk.android:crashlytics:2.9.2"
        }

        object PlayServices {
            private val version = "15.0.1"

            val auth = "com.google.android.gms:play-services-auth:$version"
            val nearby = "com.google.android.gms:play-services-nearby:$version"
        }

        object FirebaseUi {
            private val version = "c280098444"

            val firestore =
                    "com.github.SUPERCILEX.FirebaseUI-Android:firebase-ui-firestore:$version"
            val auth = "com.github.SUPERCILEX.FirebaseUI-Android:firebase-ui-auth:$version"
            val facebook = "com.facebook.android:facebook-login:4.33.0"
            val twitter = "com.twitter.sdk.android:twitter-core:3.1.1@aar"
        }

        object Misc {
            private val leakCanaryVersion = "1.5.4"
            private val retrofitVersion = "2.4.0"
            private val poiVersion = "3.17"

            private val glideVersion = "4.7.1"

            val leakCanary = "com.squareup.leakcanary:leakcanary-android:$leakCanaryVersion"
            val leakCanaryNoop =
                    "com.squareup.leakcanary:leakcanary-android-no-op:$leakCanaryVersion"
            val retrofit = "com.squareup.retrofit2:retrofit:$retrofitVersion"
            val retrofitGson = "com.squareup.retrofit2:converter-gson:$retrofitVersion"
            val gson = "com.google.code.gson:gson:2.8.4"
            val poi = "com.github.SUPERCILEX.poi-android:poi:$poiVersion"
            val poiProguard = "com.github.SUPERCILEX.poi-android:proguard:$poiVersion"

            val glide = "com.github.bumptech.glide:glide:$glideVersion"
            val glideRv = "com.github.bumptech.glide:recyclerview-integration:$glideVersion"
            val snap = "com.github.rubensousa:gravitysnaphelper:1.5"
            val permissions = "pub.devrel:easypermissions:1.2.0"
            val mttp = "uk.co.samuelwall:material-tap-target-prompt:2.9.0"
            val billing = "com.android.billingclient:billing:1.1"
            val licenses = "net.yslibrary.licenseadapter:licenseadapter:2.0.2"
        }
    }
}
