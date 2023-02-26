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

@CommandAlias("mutechat")
@CommandPermission("mcp.mutechat")
class MuteChatCommand(plugin: MCP) : Command(plugin) {
    @Default
    fun execute(sender: CommandSender?) {
        val toggle = !plugin.chatMute
        if (toggle) {
            Bukkit.broadcast(Component.text("Chat was unmuted.", NamedTextColor.GRAY))
        } else {
            Bukkit.broadcast(Component.text("Chat was muted.", NamedTextColor.GRAY))
        }
    }
}