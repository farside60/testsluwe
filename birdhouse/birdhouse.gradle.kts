version = "1.3.12"

project.extra["PluginName"] = "Sluwe Bird House"
project.extra["PluginDescription"] = "Mass bird slaughter"

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