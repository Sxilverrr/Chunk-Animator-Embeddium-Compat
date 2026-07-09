plugins {
    id("dev.kikugie.stonecutter")
    id("dev.architectury.loom") version "1.9-SNAPSHOT" apply false
    id("net.darkhax.curseforgegradle") version "1.1.28" apply false
}
stonecutter active "1.20.1" /* [SC] DO NOT EDIT */

stonecutter registerChiseled tasks.register("chiseledBuild", stonecutter.chiseled) {
    group = "project"
    ofTask("buildAndCollect")
}

stonecutter registerChiseled tasks.register("chiseledPublish", stonecutter.chiseled) {
    group = "project"
    ofTask("publishCurseForge")
}
