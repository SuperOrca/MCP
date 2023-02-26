package me.superorca.mcp.commands

import co.aikar.commands.annotation.CommandAlias
import co.aikar.commands.annotation.CommandCompletion
import co.aikar.commands.annotation.CommandPermission
import co.aikar.commands.annotation.Default
import me.superorca.mcp.MCP
import me.superorca.mcp.libs.convertToDuration
import me.superorca.mcp.libs.models.Command
import me.superorca.mcp.libs.punishments.Punishment
import me.superorca.mcp.libs.punishments.PunishmentType
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.OfflinePlayer
import org.bukkit.command.CommandSender

@CommandAlias("tempmute")
@CommandPermission("mcp.tempmute")
class TempMuteCommand(plugin: MCP) : Command(plugin) {
    @Default
    @CommandCompletion("@players @nothing @nothing")
    fun execute(sender: CommandSender, target: OfflinePlayer?, duration: String?, reason: String?) {
        val span: Long = convertToDuration(duration)
        if (span == -1L) {
            sender.sendMessage(Component.text("Error: Invalid duration.", NamedTextColor.RED))
            return
        }
        val punishment = Punishment(
            plugin,
            target,
            sender, PunishmentType.MUTE,
            span,
            reason
        )
        punishment.execute()
    }
}