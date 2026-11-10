# Pravidla ProGuard / R8 pro Spirala (:app a :protocol)
-keepattributes *Annotation*, InnerClasses, Signature

# Zachování serializátorů kotlinx.serialization pro komunikační protokol
-keepclassmembers class cz.teatrosvjotlo.spirala.protocol.** {
    *** Companion;
}
-keepclasseswithmembers class cz.teatrosvjotlo.spirala.protocol.** {
    kotlinx.serialization.KSerializer serializer(...);
}
-keep,includedescriptorclasses class cz.teatrosvjotlo.spirala.protocol.**$$serializer { *; }
