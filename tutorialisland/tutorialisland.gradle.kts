version = "1.0.0"

project.extra["PluginName"] = "Sluwe Tutorial Island"
project.extra["PluginDescription"] = "We already know how to play the game"

dependencies {
    compileOnly(project(":autodialog"))
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
                                nameToId("Sluwe Auto Dialog")
                            ).joinToString(),
                    "Plugin-License" to project.extra["PluginLicense"]
                )
            )
        }
    }
}
