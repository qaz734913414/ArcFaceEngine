import ConfigExtend.apiDep
import ConfigExtend.createBaseLibraryProjectConfig
import ConfigExtend.implementationDep
import ConfigExtend.loadTesterDependencies

plugins {
    id(Plugins.library)
    id(Plugins.kotlin_android)
    id(Plugins.kotlin_android_extensions)
}

android {
    createBaseLibraryProjectConfig(
            Config.LIB_VERSION_CODE,
            Config.LIB_VERSION_NAME
    )
}

dependencies {
    getDependencies().apply {
        loadTesterDependencies()
        apiDep(Deps.AndroidX.app_compat)
        apiDep(Deps.AndroidX.core_ktx)
        apiDep(Deps.Kotlin.stdlib)
        apiDep(Deps.J3Code.core)
    }
}