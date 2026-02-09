# ARShop ProGuard Rules

# ============================================================================
# General Android
# ============================================================================
-keepattributes *Annotation*
-keepattributes Signature
-keepattributes InnerClasses
-keepattributes EnclosingMethod

# Keep line numbers for debugging stack traces
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# ============================================================================
# Data Models
# ============================================================================
# Keep all model classes to prevent serialization issues
-keep class com.arshop.data.model.** { *; }
-keep class com.arshop.domain.model.** { *; }

# ============================================================================
# Firebase
# ============================================================================
-keepattributes Signature
-keepattributes *Annotation*
-keepattributes EnclosingMethod
-keepattributes InnerClasses

# Firebase Authentication
-keep class com.google.firebase.auth.** { *; }
-dontwarn com.google.firebase.auth.**

# Firestore
-keep class com.google.firebase.firestore.** { *; }
-keep class com.google.firestore.v1.** { *; }
-dontwarn com.google.firebase.firestore.**
-keepclassmembers class com.google.firebase.firestore.** {
    <fields>;
    <methods>;
}

# Firebase Storage
-keep class com.google.firebase.storage.** { *; }
-dontwarn com.google.firebase.storage.**

# Firebase Analytics
-keep class com.google.firebase.analytics.** { *; }
-dontwarn com.google.firebase.analytics.**

# Firebase Messaging
-keep class com.google.firebase.messaging.** { *; }
-dontwarn com.google.firebase.messaging.**

# ============================================================================
# ARCore and Sceneview
# ============================================================================
-keep class com.google.ar.** { *; }
-keep class com.google.ar.core.** { *; }
-keep class com.google.ar.sceneform.** { *; }
-dontwarn com.google.ar.**

# Sceneview
-keep class io.github.sceneview.** { *; }
-keep class io.github.sceneview.ar.** { *; }
-dontwarn io.github.sceneview.**

# Filament (used by Sceneview)
-keep class com.google.android.filament.** { *; }
-dontwarn com.google.android.filament.**

# ============================================================================
# Payment SDKs
# ============================================================================

# Stripe
-keep class com.stripe.** { *; }
-keep class com.stripe.android.** { *; }
-keep class com.stripe.android.model.** { *; }
-keep class com.stripe.android.view.** { *; }
-dontwarn com.stripe.**
-keepclassmembers class com.stripe.android.model.** {
    <fields>;
}

# PayPal
-keep class com.paypal.** { *; }
-keep class com.paypal.android.** { *; }
-dontwarn com.paypal.**
-dontwarn org.apache.http.**
-dontwarn android.net.http.AndroidHttpClient

# Razorpay
-keep class com.razorpay.** { *; }
-dontwarn com.razorpay.**
-keepclassmembers class com.razorpay.** {
    <fields>;
    <methods>;
}

# ============================================================================
# Networking - Retrofit & OkHttp
# ============================================================================

# Retrofit
-keepattributes Signature, InnerClasses, EnclosingMethod
-keepattributes RuntimeVisibleAnnotations, RuntimeVisibleParameterAnnotations
-keepattributes AnnotationDefault

-keepclassmembers,allowshrinking,allowobfuscation interface * {
    @retrofit2.http.* <methods>;
}

-dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement
-dontwarn javax.annotation.**
-dontwarn kotlin.Unit

-dontwarn retrofit2.KotlinExtensions
-dontwarn retrofit2.KotlinExtensions$*

-if interface * { @retrofit2.http.* <methods>; }
-keep,allowobfuscation interface <1>

# OkHttp
-dontwarn okhttp3.**
-dontwarn okio.**
-dontwarn javax.annotation.**
-dontwarn org.conscrypt.**

-keepnames class okhttp3.internal.publicsuffix.PublicSuffixDatabase

-keep class okhttp3.** { *; }
-keep interface okhttp3.** { *; }

# OkHttp Platform
-keep class okhttp3.internal.platform.** { *; }
-dontwarn okhttp3.internal.platform.**

# ============================================================================
# Gson
# ============================================================================
-keepattributes Signature
-keepattributes *Annotation*
-keepattributes EnclosingMethod

-dontwarn sun.misc.**

-keep class com.google.gson.** { *; }
-keep class * implements com.google.gson.TypeAdapter
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer

# Keep generic signature of Call, Response (R8 full mode strips signatures from non-kept items)
-keep,allowobfuscation,allowshrinking interface retrofit2.Call
-keep,allowobfuscation,allowshrinking class retrofit2.Response

# With R8 full mode generic signatures are stripped for classes that are not
# kept. Suspend functions are wrapped in continuations where the type argument
# is used.
-keep,allowobfuscation,allowshrinking class kotlin.coroutines.Continuation

# ============================================================================
# Kotlin Coroutines
# ============================================================================
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}

-keepclassmembernames class kotlinx.** {
    volatile <fields>;
}

-dontwarn kotlinx.coroutines.**

# Coroutines Flow
-keep class kotlinx.coroutines.flow.** { *; }

# ============================================================================
# Kotlin Serialization
# ============================================================================
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt

-keepclassmembers class kotlinx.serialization.json.** {
    *** Companion;
}
-keepclasseswithmembers class kotlinx.serialization.json.** {
    kotlinx.serialization.KSerializer serializer(...);
}

-keep,includedescriptorclasses class com.arshop.**$$serializer { *; }
-keepclassmembers class com.arshop.** {
    *** Companion;
}
-keepclasseswithmembers class com.arshop.** {
    kotlinx.serialization.KSerializer serializer(...);
}

# ============================================================================
# Dagger Hilt
# ============================================================================
-dontwarn com.google.errorprone.annotations.**

-keep class dagger.hilt.** { *; }
-keep class javax.inject.** { *; }
-keep class * extends dagger.hilt.android.lifecycle.HiltViewModel {
    <init>(...);
}

-keepclasseswithmembernames class * {
    @dagger.* <fields>;
}

-keepclasseswithmembernames class * {
    @dagger.* <methods>;
}

# ============================================================================
# Room Database
# ============================================================================
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *
-dontwarn androidx.room.paging.**

-keep class androidx.room.** { *; }
-keep interface androidx.room.** { *; }

-keepclassmembers class * extends androidx.room.RoomDatabase {
    <methods>;
}

# ============================================================================
# AndroidX and Material Design
# ============================================================================
-keep class com.google.android.material.** { *; }
-dontwarn com.google.android.material.**
-dontnote com.google.android.material.**

-keep class androidx.** { *; }
-keep interface androidx.** { *; }

# Lifecycle
-keep class androidx.lifecycle.** { *; }
-dontwarn androidx.lifecycle.**

# Navigation
-keep class androidx.navigation.** { *; }
-keepnames class androidx.navigation.fragment.NavHostFragment

# ============================================================================
# Coil Image Loading
# ============================================================================
-keep class coil.** { *; }
-keep interface coil.** { *; }
-dontwarn coil.**

# ============================================================================
# DataStore
# ============================================================================
-keep class androidx.datastore.*.** { *; }
-keepclassmembers class * extends androidx.datastore.preferences.protobuf.GeneratedMessageLite {
    <fields>;
}

# ============================================================================
# Keep @Keep Annotations
# ============================================================================
-keep @androidx.annotation.Keep class * {*;}

-keepclasseswithmembers class * {
    @androidx.annotation.Keep <methods>;
}

-keepclasseswithmembers class * {
    @androidx.annotation.Keep <fields>;
}

-keepclasseswithmembers class * {
    @androidx.annotation.Keep <init>(...);
}

# ============================================================================
# Parcelable
# ============================================================================
-keep class * implements android.os.Parcelable {
    public static final android.os.Parcelable$Creator *;
}

-keepclassmembers class * implements android.os.Parcelable {
    public static final ** CREATOR;
}

# ============================================================================
# Enum
# ============================================================================
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# ============================================================================
# Native Methods
# ============================================================================
-keepclasseswithmembernames class * {
    native <methods>;
}

# ============================================================================
# ViewBinding and DataBinding
# ============================================================================
-keep class * extends androidx.viewbinding.ViewBinding {
    <init>(android.view.View);
    public static *** bind(android.view.View);
    public static *** inflate(android.view.LayoutInflater);
}

# ============================================================================
# Debugging
# ============================================================================
# Uncomment for debugging ProGuard issues
# -printconfiguration build/outputs/proguard-configuration.txt
# -printusage build/outputs/proguard-usage.txt
# -printseeds build/outputs/proguard-seeds.txt

# ============================================================================
# Remove Logging in Release
# ============================================================================
-assumenosideeffects class android.util.Log {
    public static *** d(...);
    public static *** v(...);
    public static *** i(...);
}

