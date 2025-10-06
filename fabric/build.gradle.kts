plugins {
    alias(libs.plugins.multimod)
}

multimod.fabric(project(":common"))

repositories {
    maven {
        name = "TerraformersMC"
        url = uri("https://maven.terraformersmc.com/")
    }
}

dependencies {
    add("modImplementation", libs.fabric.permissions.api)
    add("include", libs.fabric.permissions.api)
    add("modImplementation", libs.fabric.modmenu)
}
