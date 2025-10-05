plugins {
    alias(libs.plugins.multimod)
}

multimod.fabric(project(":common"))

dependencies {
    // TODO fix this in multimod
    add("modImplementation", libs.fabric.permissions.api)
    add("include", libs.fabric.permissions.api)
}
