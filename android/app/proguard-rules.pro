# Mantener clases de Firebase y Google
-keepattributes Signature
-keepattributes *Annotation*

-keep class com.google.firebase.** { *; }
-keep class com.google.android.gms.** { *; }

-dontwarn com.google.firebase.**
-dontwarn com.google.android.gms.**
