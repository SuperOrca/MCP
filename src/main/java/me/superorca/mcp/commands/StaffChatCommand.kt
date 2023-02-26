package me.superorca.mcp.commands

import co.aikar.commands.annotation.*
import me.superorca.mcp.MCP
import me.superorca.mcp.libs.models.Command
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.metadata.FixedMetadataValue

@CommandAlias("staffchat|sc")
@CommandPermission("mcp.staffchat")
class StaffChatCommand(plugin: MCP) : Command(plugin) {
    @Default
    @CommandCompletion("@players @nothing")
    fun execute(player: Player, @Optional message: String?) {
        if (message == null) {
            val toggle = !player.hasMetadata("staffchat")
            if (toggle) {
                player.setMetadata("staffchat", FixedMetadataValue(plugin, true))
                player.sendMessage(
                    Component.text("Staff chat has been ", NamedTextColor.GRAY)
                        .append(Component.text("enabled", NamedTextColor.GREEN))
                        .append(Component.text(".", NamedTextColor.GRAY))
                )
            } else {
                player.removeMetadata("staffchat", plugin)
                player.sendMessage(
                    Component.text("Staff chat has been ", NamedTextColor.GRAY)
                        .append(Component.text("disabled", NamedTextColor.RED))
                        .append(Component.text(".", NamedTextColor.GRAY))
                )
            }
        } else {
            Bukkit.getOnlinePlayers().stream().filter { p: Player -> p.hasPermission("mcp.staffchat") }
                .forEach { p: Player ->
                    p.sendMessage(
                        Component.text()
                            .append(Component.text("SC ", NamedTextColor.RED, TextDecoration.BOLD))
                            .append(Component.text("➡ ", NamedTextColor.DARK_GRAY))
                            .append(Component.text(player.name + ": ", NamedTextColor.GRAY))
                            .append(Component.text(message, NamedTextColor.WHITE))
                            .build()
                    )
                }
        }
    }
}