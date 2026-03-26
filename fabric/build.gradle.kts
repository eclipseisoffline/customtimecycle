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

multimod.modPublishing {
    modrinth {
        // Fabric API
        requires {
            slug = "P7dR8mSH"
        }
    }
}


multimod.fabric(project(":common"))
