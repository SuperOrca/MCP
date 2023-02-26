package me.superorca.mcp.commands

import co.aikar.commands.annotation.CommandAlias
import co.aikar.commands.annotation.CommandCompletion
import co.aikar.commands.annotation.CommandPermission
import co.aikar.commands.annotation.Default
import me.superorca.mcp.MCP
import me.superorca.mcp.libs.models.Command
import me.superorca.mcp.libs.punishments.Punishment
import me.superorca.mcp.libs.punishments.PunishmentType
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

@CommandAlias("unban")
@CommandPermission("mcp.unban")
class UnbanCommand(plugin: MCP) : Command(plugin) {
    @Default
    @CommandCompletion("@players")
    fun execute(sender: CommandSender, target: OfflinePlayer) {
        val staffName = (sender as? Player)?.name ?: "CONSOLE"
        val optional = plugin.punishments.stream()
            .filter { punishment: Punishment -> punishment.target!!.uniqueId == target.uniqueId }
            .filter { obj: Punishment -> obj.active }
            .filter { punishment: Punishment -> punishment.type == PunishmentType.BAN }
            .findFirst()
        optional.ifPresentOrElse({ punishment: Punishment ->
            punishment.active = false
            Bukkit.broadcast(
                Component.text(target.name!!, NamedTextColor.RED)
                    .append(Component.text(" was unbanned by ", NamedTextColor.GRAY))
                    .append(Component.text(staffName, NamedTextColor.RED))
                    .append(Component.text(".", NamedTextColor.GRAY))
            )
        }) { sender.sendMessage(Component.text("Player is not banned.", NamedTextColor.RED)) }
    }
}