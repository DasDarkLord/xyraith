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
    val tomlFile = Path("./xyraith.toml")
    return mapper.decode<Config>(tomlFile)
}