# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# 保留行号信息，崩溃栈可读
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# 应用自身代码全部保留：避免 R8 误删被反射/Gson/Retrofit 用到的类
-keep class homes.gensokyo.enigma.** { *; }

# Gson 序列化数据类：字段名即 JSON key，混淆会破坏映射
-keepattributes Signature
-keepattributes *Annotation*
-keep class homes.gensokyo.enigma.bean.** { *; }
-keep class homes.gensokyo.enigma.logic.database.model.** { *; }

# Gson TypeToken：R8 full mode 会剥掉匿名子类的泛型签名(Signature)，导致 fromJson 拿到错误类型
-keep class com.google.gson.reflect.TypeToken { *; }
-keep class * extends com.google.gson.reflect.TypeToken { *; }
-keep,allowobfuscation class com.google.gson.Gson$TypeToken
-dontnote com.google.gson.internal.UnsafeAllocator

# Retrofit 接口方法依赖注解与返回类型做动态代理
-keep,allowobfuscation,allowshrinking interface homes.gensokyo.enigma.interface.** { *; }
-keep class kotlin.coroutines.Continuation
-keep class retrofit2.** { *; }
-keepattributes RuntimeVisibleAnnotations, RuntimeVisibleParameterAnnotations

# OkHttp/Coil 基础保活
-dontwarn okhttp3.**
-dontwarn okio.**
-dontwarn coil.**
