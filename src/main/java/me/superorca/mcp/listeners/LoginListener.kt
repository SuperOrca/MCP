package me.superorca.mcp.listeners

import me.superorca.mcp.MCP
import me.superorca.mcp.libs.models.Listener
import me.superorca.mcp.libs.punishments.Punishment
import me.superorca.mcp.libs.punishments.PunishmentType
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.player.PlayerLoginEvent

class LoginListener(plugin: MCP) : Listener(plugin) {
    @EventHandler(priority = EventPriority.HIGH)
    fun onLogin(e: PlayerLoginEvent) {
        val player = e.player
        val optional = plugin.punishments.stream()
            .filter { punishment: Punishment -> punishment.target!!.uniqueId == player.uniqueId }
            .filter { obj: Punishment -> obj.active }
            .filter { punishment: Punishment -> punishment.type == PunishmentType.BAN }
            .findFirst()
        optional.ifPresent { punishment: Punishment ->
            if (punishment.isPerm || punishment.expires > System.currentTimeMillis()) {
                e.disallow(PlayerLoginEvent.Result.KICK_BANNED, punishment.message!!)
            } else {
                punishment.active = false
            }
        }
    }
}