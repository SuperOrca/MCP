package me.superorca.mcp

import co.aikar.commands.MessageType
import co.aikar.commands.PaperCommandManager
import me.superorca.mcp.commands.*
import me.superorca.mcp.libs.punishments.Punishment
import me.superorca.mcp.listeners.ChatListener
import me.superorca.mcp.listeners.LoginListener
import org.bukkit.ChatColor
import org.bukkit.configuration.InvalidConfigurationException
import org.bukkit.plugin.java.JavaPlugin
import java.io.File
import java.io.IOException
import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException
import java.util.*
import java.util.function.Consumer

// TODO
// silent/anonymous punishments
// configuration
// merge commands (ban/tempban + mute/tempmute)
// ip bans
// main punish command (punish gui/viewer/history)
// sounds
// auto completions
class MCP : JavaPlugin() {
    var punishments: MutableList<Punishment> = ArrayList()
    lateinit var connection: Connection
    var chatMute: Boolean = false
    private lateinit var commandManager: PaperCommandManager

    override fun onEnable() {
        dataFolder.mkdir()
        setupDatabase()
        setupCommands()
        setupListeners()
    }

    override fun onDisable() {
        try {
            punishments.forEach(Consumer { punishment: Punishment -> punishment.write() })
            connection.commit()
            connection.close()
        } catch (e: SQLException) {
            e.printStackTrace()
        }
    }

    private fun setupDatabase() {
        val dbPath = dataFolder.toString() + File.separator
        val dbName = "mcp.db"
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:$dbPath$dbName")
            connection.autoCommit = false
            val statement = connection.createStatement()
            statement.executeUpdate(
                "CREATE TABLE IF NOT EXISTS punishments ("
                        + "id TEXT PRIMARY KEY, "
                        + "type TEXT NOT NULL, "
                        + "target VARCHAR(36) NOT NULL ,"
                        + "staff VARCHAR(36), "
                        + "reason TEXT NOT NULL, "
                        + "date DATE NOT NULL, "
                        + "duration BIGINT NOT NULL, "
                        + "active BOOLEAN NOT NULL"
                        + ")"
            )
            connection.commit()
            val result = statement.executeQuery("SELECT * FROM punishments")
            while (result.next()) punishments.add(Punishment(this, result))
            result.close()
            statement.close()
        } catch (e: SQLException) {
            e.printStackTrace()
        }
    }

    private fun setupCommands() {
        val messages = File(dataFolder.toString() + File.separator + "messages.yml")
        commandManager = PaperCommandManager(this)

        commandManager.setFormat(MessageType.ERROR, 1, ChatColor.RED)
        commandManager.setFormat(MessageType.SYNTAX, 1, ChatColor.RED)

        commandManager.registerCommand(BanCommand(this))
        commandManager.registerCommand(ClearChatCommand(this))
        commandManager.registerCommand(KickCommand(this))
        commandManager.registerCommand(MuteChatCommand(this))
        commandManager.registerCommand(MuteCommand(this))
        commandManager.registerCommand(StaffChatCommand(this))
        commandManager.registerCommand(TempBanCommand(this))
        commandManager.registerCommand(TempMuteCommand(this))
        commandManager.registerCommand(UnbanCommand(this))
        commandManager.registerCommand(UnmuteCommand(this))
        try {
            commandManager.locales.loadYamlLanguageFile(messages, Locale.ENGLISH)
        } catch (ex: IOException) {
            ex.printStackTrace()
        } catch (ex: InvalidConfigurationException) {
            ex.printStackTrace()
        }
    }

    private fun setupListeners() {
        val manager = server.pluginManager
        manager.registerEvents(ChatListener(this), this)
        manager.registerEvents(LoginListener(this), this)
    }
}