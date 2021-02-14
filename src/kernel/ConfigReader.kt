package kernel

import com.natpryce.konfig.*
import java.io.File

/**
 * Created by flore on 26/02/2017.
 */
object ConfigReader {
    lateinit var data: Configuration

    init {
        this.reload()
    }

    fun reload() {
        this.data = EnvironmentVariables() overriding ConfigurationProperties.fromFile(File("config.properties"))
    }

    object server : PropertyGroup() {
        val id by intType
        val key by stringType
        val host by stringType
        val port by intType
    }

    object exchange : PropertyGroup() {
        val host by stringType
        val port by intType
    }

    object database : PropertyGroup() {
        object login : PropertyGroup() {
            val host by stringType
            val port by intType
            val user by stringType
            val pass by stringType
            val name by stringType
        }

        object game : PropertyGroup() {
            val host by stringType
            val port by intType
            val user by stringType
            val pass by stringType
            val name by stringType
        }
    }

    object rate : PropertyGroup() {
        val xp by doubleType
        val job by intType
        val farm by intType
        val honor by intType
        val kamas by intType
    }

    object mode : PropertyGroup() {
        val halloween by booleanType
        val christmas by booleanType
        val heroic by booleanType
    }

    object options : PropertyGroup() {
        object start : PropertyGroup() {
            val message by stringType
            val map by intType
            val cell by intType
            val kamas by longType
            val level by intType
        }
        object event : PropertyGroup() {
            val active by booleanType
            val timePerEvent by intType
        }

        val autoReboot by booleanType
        val encryptPacket by booleanType
        val deathMatch by booleanType
        val teamMatch by booleanType
        val allZaap by booleanType
        val allEmote by booleanType
        val subscription by booleanType
    }
}