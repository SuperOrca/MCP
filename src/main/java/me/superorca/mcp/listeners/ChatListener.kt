package me.superorca.mcp.listeners

import io.papermc.paper.event.player.AsyncChatEvent
import me.superorca.mcp.MCP
import me.superorca.mcp.libs.models.Listener
import me.superorca.mcp.libs.punishments.Punishment
import me.superorca.mcp.libs.punishments.PunishmentType
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority

class ChatListener(plugin: MCP) : Listener(plugin) {
    @EventHandler(priority = EventPriority.HIGH)
    fun onChat(e: AsyncChatEvent) {
        val player = e.player
        if (player.hasMetadata("staffchat")) {
            e.isCancelled = true
            Bukkit.getOnlinePlayers().stream().filter { p: Player -> p.hasPermission("mcp.staffchat") }
                .forEach { p: Player ->
                    p.sendMessage(
                        Component.text()
                            .append(Component.text("SC ", NamedTextColor.RED, TextDecoration.BOLD))
                            .append(Component.text("➡ ", NamedTextColor.DARK_GRAY))
                            .append(Component.text(player.name + ": ", NamedTextColor.GRAY))
                            .append(e.message())
                            .build()
                    )
                }
        }
        if (plugin.chatMute && !player.hasPermission("mcp.bypass")) {
            e.isCancelled = true
            player.sendMessage(Component.text("You cannot talk while chat is muted.", NamedTextColor.RED))
        }
        val optional = plugin.punishments.stream()
            .filter { punishment: Punishment -> punishment.target!!.uniqueId == player.uniqueId }
            .filter { punishment: Punishment -> punishment.active }
            .filter { punishment: Punishment -> punishment.type == PunishmentType.MUTE }
            .findFirst()
        optional.ifPresent { punishment: Punishment ->
            if (punishment.isPerm || punishment.expires > System.currentTimeMillis()) {
                e.isCancelled = true
                player.sendMessage(punishment.message!!)
            } else {
                punishment.active = false
            }
        }
    }
}