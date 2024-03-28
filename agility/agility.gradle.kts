version = "1.0.10"

project.extra["PluginName"] = "Sluwe Agility"
project.extra["PluginDescription"] = "Hippity hoppity, jumps on your property"

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
                                nameToId("Sluwe Utils")
                            ).joinToString(),
                    "Plugin-License" to project.extra["PluginLicense"]
                )
            )
        }
    }
}