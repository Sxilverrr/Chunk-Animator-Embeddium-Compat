plugins {
    id("dev.architectury.loom")
}

val minecraftVersion: String = stonecutter.current.version
val modId = property("mod_id") as String
val modVersion = property("mod_version") as String
val modName = property("mod_name") as String
val modLicense = property("mod_license") as String
val modAuthors = property("mod_authors") as String
val modDescription = property("mod_description") as String
val modGroup = property("mod_group_id") as String
val forgeVersion = property("forge_version") as String
val minecraftVersionRange = property("minecraft_version_range") as String
val forgeVersionRange = property("forge_version_range") as String
val loaderVersionRange = property("loader_version_range") as String
val packFormat = property("pack_format") as String
val compatDeps = (findProperty("compat_deps") as String?)
    ?.split(",")?.map { it.trim() }?.filter { it.isNotEmpty() } ?: emptyList()

version = minecraftVersion
group = modGroup
base {
    archivesName.set(modId)
}

val javaVersion = if (stonecutter.eval(minecraftVersion, ">=1.18"))
    JavaVersion.VERSION_17 else JavaVersion.VERSION_1_8

val sodiumBand = when {
    stonecutter.eval(minecraftVersion, ">=1.20") -> "sodium05"
    stonecutter.eval(minecraftVersion, ">=1.18") -> "sodium04"
    else -> "sodium02"
}

sourceSets {
    named("main") {
        java.srcDir("src/$sodiumBand/java")
        resources.srcDir("src/$sodiumBand/resources")
    }
}

repositories {
    maven("https://maven.minecraftforge.net")
    maven("https://api.modrinth.com/maven") { content { includeGroup("maven.modrinth") } }
    maven("https://www.cursemaven.com") { content { includeGroup("curse.maven") } }
    mavenCentral()
}

dependencies {
    minecraft("com.mojang:minecraft:$minecraftVersion")
    mappings(loom.officialMojangMappings())
    "forge"("net.minecraftforge:forge:$minecraftVersion-$forgeVersion")

    compatDeps.forEach { compileOnly(it) }
}

loom {
    forge {
        mixinConfig("$modId.mixins.json")
    }
}

java {
    withSourcesJar()
    sourceCompatibility = javaVersion
    targetCompatibility = javaVersion
}

tasks.processResources {
    val props = mapOf(
        "mod_id" to modId,
        "mod_name" to modName,
        "mod_version" to modVersion,
        "mod_license" to modLicense,
        "mod_authors" to modAuthors,
        "mod_description" to modDescription,
        "minecraft_version_range" to minecraftVersionRange,
        "forge_version_range" to forgeVersionRange,
        "loader_version_range" to loaderVersionRange,
        "pack_format" to packFormat,
    )
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
