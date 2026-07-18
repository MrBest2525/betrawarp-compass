plugins {
    id("net.fabricmc.fabric-loom")
    `maven-publish`
}

version = providers.gradleProperty("mod_version").get()
group = providers.gradleProperty("maven_group").get()

repositories {
    // Add repositories to retrieve artifacts from in here.
    // You should only use this when depending on other mods because
    // Loom adds the essential maven repositories to download Minecraft and libraries from automatically.
    // See https://docs.gradle.org/current/userguide/declaring_repositories.html
    // for more information about repositories.

    // Polymer
    maven {
        url = uri("https://maven.nucleoid.xyz")
    }
}

loom {
    splitEnvironmentSourceSets()

    mods {
        register("betrawarp-compass") {
            sourceSet(sourceSets.main.get())
            sourceSet(sourceSets.getByName("client"))
        }
    }
}

fabricApi {
    configureDataGeneration() {
        client = true
    }
}

dependencies {
    // To change the versions see the gradle.properties file
    minecraft("com.mojang:minecraft:${providers.gradleProperty("minecraft_version").get()}")
    implementation("net.fabricmc:fabric-loader:${providers.gradleProperty("loader_version").get()}")

    // Fabric API. This is technically optional, but you probably want it anyway.
    implementation("net.fabricmc.fabric-api:fabric-api:${providers.gradleProperty("fabric_api_version").get()}")

    // Polymer
    compileOnly("eu.pb4:polymer-core:${providers.gradleProperty("polymer_core_version").get()}")
    compileOnly("eu.pb4:polymer-resource-pack:${providers.gradleProperty("polymer_resource_pack_version").get()}")
    compileOnly("eu.pb4:polymer-networking:${providers.gradleProperty("polymer_networking_version").get()}")
}

tasks.processResources {
    val version = version
    val loader_version = providers.gradleProperty("loader_version").get()
    val minecraft_version = providers.gradleProperty("minecraft_version").get()

    filteringCharset = "UTF-8"

    inputs.property("version", version)
    inputs.property("loader_version", loader_version)
    inputs.property("minecraft_version", minecraft_version)

    filesMatching("fabric.mod.json") {
        expand(
            "version" to version,
            "loader_version" to loader_version,
            "minecraft_version" to minecraft_version
        )
    }
}

tasks.withType<JavaCompile>().configureEach {
    options.release = 25
}

java {
    // Loom will automatically attach sourcesJar to a RemapSourcesJar task and to the "build" task
    // if it is present.
    // If you remove this line, sources will not be generated.
    withSourcesJar()

    sourceCompatibility = JavaVersion.VERSION_25
    targetCompatibility = JavaVersion.VERSION_25
}

tasks.jar {
    val projectName = project.name
    inputs.property("projectName", projectName)

    from("LICENSE") {
        rename { "${it}_$projectName" }
    }
}

// configure the maven publication
publishing {
    publications {
        register<MavenPublication>("mavenJava") {
            from(components["java"])
        }
    }

    // See https://docs.gradle.org/current/userguide/publishing_maven.html for information on how to set up publishing.
    repositories {
        // Add repositories to publish to here.
        // Notice: This block does NOT have the same function as the block in the top level.
        // The repositories here will be used for publishing your artifact, not for
        // retrieving dependencies.
    }
}
