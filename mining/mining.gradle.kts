version = "1.8.0"

project.extra["PluginName"] = "Sluwe Mining"
project.extra["PluginDescription"] = "Diggy, diggy hole"

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
