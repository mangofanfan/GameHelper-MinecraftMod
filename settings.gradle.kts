pluginManagement {
    repositories {
        maven {
            name = "fanfan"
            url = uri("https://maven.fanfan.moe/repository/maven-public/")
        }
        maven("https://maven.fabricmc.net/") {
            name = "Fabric"
        }
        gradlePluginPortal()
    }
}
