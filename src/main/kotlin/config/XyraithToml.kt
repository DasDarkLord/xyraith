package config

import cc.ekblad.toml.decode
import cc.ekblad.toml.tomlMapper
import kotlin.io.path.Path

data class Config(
    val server: Settings
) {
    data class Settings(
        val port: Int,
        val motd: String,
        val host: String,
    )
}

fun parseToml(): Config {
    val mapper = tomlMapper { }
    return try {
        val tomlFile = Path("./xyraith.toml")
        mapper.decode<Config>(tomlFile)
    } catch(e: java.nio.file.NoSuchFileException) {
        println("NOTE: No `xyraith.toml` found. Defaulting to default config settings for server running.")
        Config(
            Config.Settings(
                25565,
                "",
                "0.0.0.0"
            )
        )
    }
}