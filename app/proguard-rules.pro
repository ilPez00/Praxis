# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.

# Keep data classes
-keep class com.praxis.app.data.model.** { *; }

# Keep Compose
-keep class androidx.compose.** { *; }
