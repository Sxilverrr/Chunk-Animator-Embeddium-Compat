plugins {
    id("dev.architectury.loom")
}

val minecraftVersion = stonecutter.current.version
val modId = property("mod_id") as String
val modVersion = property("mod_version") as String
val forgeVersion = property("forge_version") as String
val embeddiumVersion = property("embeddium_version") as String
val props = listOf(
    "mod_id", "mod_name", "mod_version", "mod_license", "mod_authors", "mod_description",
    "minecraft_version_range", "forge_version_range", "loader_version_range", "pack_format",
).associateWith { property(it) as String }

version = "$modVersion-$minecraftVersion"
group = property("mod_group_id") as String
base.archivesName.set("$modId-forge")

val javaVersion = if (stonecutter.eval(minecraftVersion, ">=1.18")) JavaVersion.VERSION_17 else JavaVersion.VERSION_1_8
val sodiumBand = when {
    stonecutter.eval(minecraftVersion, ">=1.20") -> "sodium05"
    stonecutter.eval(minecraftVersion, ">=1.18") -> "sodium04"
    else -> "sodium02"
}

sourceSets.main {
    java.srcDir("src/$sodiumBand/java")
    resources.srcDir("src/$sodiumBand/resources")
}

repositories {
    maven("https://maven.minecraftforge.net")
    maven("https://api.modrinth.com/maven") { content { includeGroup("maven.modrinth") } }
    mavenCentral()
}

dependencies {
    minecraft("com.mojang:minecraft:$minecraftVersion")
    mappings(loom.officialMojangMappings())
    "forge"("net.minecraftforge:forge:$minecraftVersion-$forgeVersion")
    compileOnly("maven.modrinth:embeddium:$embeddiumVersion")
    findProperty("chunkanimator_version")?.let {
        modLocalRuntime("maven.modrinth:embeddium:$embeddiumVersion")
        modLocalRuntime("maven.modrinth:chunkanimator:$it")
    }
}

loom.forge.mixinConfig("$modId.mixins.json")

java {
    sourceCompatibility = javaVersion
    targetCompatibility = javaVersion
}

tasks.processResources {
    inputs.properties(props)
    filesMatching(listOf("META-INF/mods.toml", "pack.mcmeta")) {
        expand(props)
    }
}

tasks.register<Copy>("buildAndCollect") {
    group = "project"
    from(tasks.remapJar.get().archiveFile)
    into(rootProject.layout.buildDirectory.file("libs/$modVersion"))
    dependsOn("build")
}
