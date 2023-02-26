package me.superorca.mcp.commands

import co.aikar.commands.annotation.CommandAlias
import co.aikar.commands.annotation.CommandPermission
import co.aikar.commands.annotation.Default
import me.superorca.mcp.MCP
import me.superorca.mcp.libs.models.Command
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

@CommandAlias("clearchat")
@CommandPermission("mcp.clearchat")
class ClearChatCommand(plugin: MCP) : Command(plugin) {
    @Default
    fun execute(sender: CommandSender) {
        val senderName = (sender as? Player)?.name ?: "CONSOLE"
        Bukkit.getOnlinePlayers().forEach { p: Player ->
            p.sendMessage(
                Component.text("\n".repeat(250))
                    .append(Component.text("Chat was cleared by ", NamedTextColor.GRAY))
                    .append(Component.text(senderName, NamedTextColor.RED))
                    .append(Component.text(".", NamedTextColor.GRAY))
            )
        }
    }
}