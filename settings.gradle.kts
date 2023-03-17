pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
    buildscript {
        repositories {
            mavenCentral()
            maven {
                url = uri("https://storage.googleapis.com/r8-releases/raw")
            }
        }
        dependencies {
            classpath("com.android.tools:r8:4.0.54")
        }
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
rootProject.name = "DnevnikX"
include(":app")

include(":splash:ui")
include(":splash:component")

include(":main:mvi")
include(":main:mvi-impl")
include(":main:component")

include(":settings:ui")
include(":settings:core")
include(":settings:provider")
include(":settings:provider-datastore")

include(":ui:core")
include(":ui:component")
include(":ui:analytics")

include(":application:ui")
include(":application:component")
include(":application:settings")
include(":application:settings-serializer")
include(":application:mvi")
include(":application:mvi-impl")

include(":diary:mvi")
include(":diary:mvi-impl")
include(":diary:domain")
include(":diary:domain-ktor")
include(":diary:domain-merged")
include(":diary:domain-room")
include(":diary:settings")
include(":diary:settings-serializer")
include(":diary:ui")

include(":diary:component")

include(":schedule:mvi")
include(":schedule:mvi-impl")
include(":schedule:domain")
include(":schedule:domain-ktor")
include(":schedule:ui")
include(":schedule:component")

include(":finalmarks:mvi")
include(":finalmarks:mvi-impl")
include(":finalmarks:domain")
include(":finalmarks:domain-ktor")
include(":finalmarks:domain-merged")
include(":finalmarks:domain-room")
include(":finalmarks:ui")
include(":finalmarks:component")

include(":marks:mvi")
include(":marks:mvi-impl")
include(":marks:domain")
include(":marks:domain-ktor")
include(":marks:domain-merged")
include(":marks:domain-room")
include(":marks:ui")
include(":marks:component")

include(":about:mvi")
include(":about:mvi-impl")
include(":about:domain")
include(":about:domain-builtin")
include(":about:ui")
include(":about:component")

include(":experimentalsettings:mvi")
include(":experimentalsettings:mvi-impl")
include(":experimentalsettings:domain")
include(":experimentalsettings:domain-builtin")
include(":experimentalsettings:ui")
include(":experimentalsettings:component")

include(":periods:domain")
include(":periods:domain-room")
include(":periods:domain-ktor")
include(":periods:domain-merged")
include(":periods:ui")

include(":auth:mvi")
include(":auth:mvi-impl")
include(":auth:core")
include(":auth:domain")
include(":auth:domain-room")
include(":auth:domain-ktor")
include(":auth:ktor")
include(":auth:ui")
include(":auth:component")

include(":account:mvi")

include(":account:mvi-impl")
include(":account:domain")
include(":account:domain-room")
include(":account:domain-ktor")
include(":account:domain-datastore")
include(":account:ui")
include(":account:component")
include(":account-selector:mvi")

include(":account-selector:mvi-impl")
include(":account-selector:ui")
include(":account-selector:component")
include(":account-host:component")

include(":core:common")

include(":core:components")
include(":modal:ui")

include(":modal:component")
include(":picker:ui")
include(":picker:component")

include(":room:auth")

include(":room:main")

include(":koin:main")
include(":koin:domain")
include(":koin:mvi")
include(":koin:settings")

include(":ktor:main")
include(":mvi:main")
