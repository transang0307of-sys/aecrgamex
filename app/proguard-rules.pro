# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.

# Keep Kotlin metadata
-keepattributes *Annotation*
-keepclassmembers class **$WhenMappings {
    <fields>;
}

# Keep ViewModel
-keep class * extends androidx.lifecycle.ViewModel {
    <init>();
}

# Keep Compose
-keep class androidx.compose.** { *; }

# Keep app model classes
-keep class com.aecr.gamex.data.** { *; }
-keep class com.aecr.gamex.utils.** { *; }

# Kotlin coroutines
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
