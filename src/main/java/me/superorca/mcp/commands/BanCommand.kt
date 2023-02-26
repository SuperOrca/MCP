package me.superorca.mcp.commands

import co.aikar.commands.annotation.CommandAlias
import co.aikar.commands.annotation.CommandCompletion
import co.aikar.commands.annotation.CommandPermission
import co.aikar.commands.annotation.Default
import me.superorca.mcp.MCP
import me.superorca.mcp.libs.models.Command
import me.superorca.mcp.libs.punishments.Punishment
import me.superorca.mcp.libs.punishments.PunishmentType
import org.bukkit.OfflinePlayer
import org.bukkit.command.CommandSender

@CommandAlias("ban")
@CommandPermission("mcp.ban")
class BanCommand(plugin: MCP) : Command(plugin) {
    @Default
    @CommandCompletion("@players @nothing")
    fun execute(sender: CommandSender?, target: OfflinePlayer?, reason: String?) {
        val punishment = Punishment(
            plugin,
            target,
            sender, PunishmentType.BAN,
            -1L,
            reason
        )
        punishment.execute()
    }
}