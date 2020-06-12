@file:Suppress("HasPlatformType")

import ConfigExtend.apiDep
import ConfigExtend.createBaseLibraryProjectConfig
import ConfigExtend.implementationDep
import ConfigExtend.loadTesterDependencies
import org.apache.commons.httpclient.HttpClient
import org.jetbrains.kotlin.cli.jvm.main

plugins {
    id(Plugins.library)
    id(Plugins.kotlin_android)
    id(Plugins.kotlin_android_extensions)
}

val projectPath = "${project.projectDir}"
val libsPath = File(projectPath, "libs").absolutePath

/// # 下载ArcFace 3.0依赖库
val arcSoftFaceJar = File(libsPath, "arcsoft_face.jar").absolutePath
val arcSoftFaceImageUtilJar = File(libsPath, "arcsoft_image_util.jar").absolutePath
val libArcSoftFace = File(libsPath, "armeabi-v7a/libarcsoft_face.so").absolutePath
val libArcSoftFaceEngine = File(libsPath, "armeabi-v7a/libarcsoft_face_engine.so").absolutePath
val libArcSoftImageUtil = File(libsPath, "armeabi-v7a/libarcsoft_image_util.so").absolutePath
//创建v7a so仓库
val armeabiV7a = File(libsPath, "armeabi-v7a")
armeabiV7a.mkdirs()
ConfigExtend
        .checkOrDownloadFile(arcSoftFaceJar, "http://note.novakj.cn/libs/arcface/arcsoft_face.jar")
ConfigExtend
        .checkOrDownloadFile(libArcSoftFace, "http://note.novakj.cn/libs/arcface/armeabi-v7a/libarcsoft_face.so")
ConfigExtend
        .checkOrDownloadFile(libArcSoftFaceEngine, "http://note.novakj.cn/libs/arcface/armeabi-v7a/libarcsoft_face_engine.so")
ConfigExtend
        .checkOrDownloadFile(arcSoftFaceImageUtilJar, "http://note.novakj.cn/libs/arcfaceImageutil/arcsoft_image_util.jar")
ConfigExtend
        .checkOrDownloadFile(libArcSoftImageUtil, "http://note.novakj.cn/libs/arcfaceImageutil/armeabi-v7a/libarcsoft_image_util.so")

android {
    createBaseLibraryProjectConfig(
            Config.LIB_VERSION_CODE,
            Config.LIB_VERSION_NAME
    )

    //配置'jniLibs'路径指向libs
    sourceSets["main"].jniLibs.srcDir("libs")
}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    getDependencies().apply {
        loadTesterDependencies()
        apiDep(Deps.AndroidX.app_compat)
        apiDep(Deps.AndroidX.core_ktx)
        apiDep(Deps.Kotlin.stdlib)
        apiDep(Deps.J3Code.core)
    }
}