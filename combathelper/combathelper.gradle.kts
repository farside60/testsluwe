version = "1.10.1"

project.extra["PluginName"] = "Sluwe Combat Helper"
project.extra["PluginDescription"] = "Various utilities to make combat easier"

dependencies {
    compileOnly(project(":alchemicalhydra"))
    compileOnly(project(":cerberus"))
    compileOnly(project(":demonicgorilla"))
    compileOnly(project(":gauntletextended"))
    compileOnly(project(":grotesqueguardians"))
    compileOnly(project(":zulrah"))
}

tasks {
    jar {
        manifest {
            attributes(
                    mapOf(
                            "Plugin-Version" to project.version,
                            "Plugin-Id" to nameToId(project.extra["PluginName"] as String),
                            "Plugin-Provider" to project.extra["PluginProvider"],
                            "Plugin-Description" to project.extra["PluginDescription"],
                            "Plugin-Dependencies" to
                                    arrayOf(
                                            nameToId("Sluwe Utils"),
                                            nameToId("Sluwe Alchemical Hydra"),
                                            nameToId("Sluwe Cerberus"),
                                            nameToId("Sluwe Demonic Gorillas"),
                                            nameToId("Sluwe Gauntlet Extended"),
                                            nameToId("Sluwe Grotesque Guardians"),
                                            nameToId("Sluwe Zulrah")
                                    ).joinToString(),
                            "Plugin-License" to project.extra["PluginLicense"]
                    )
            )
        }
    }
}