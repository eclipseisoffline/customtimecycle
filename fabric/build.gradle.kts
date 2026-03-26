plugins {
    alias(libs.plugins.multimod)
}

repositories {
    maven {
        name = "TerraformersMC"
        url = uri("https://maven.terraformersmc.com/")
    }
}

dependencies {
    implementation(libs.fabric.modmenu)
}

multimod.fabric(project(":common"))
