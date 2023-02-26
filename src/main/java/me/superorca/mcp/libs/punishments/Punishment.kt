package me.superorca.mcp.libs.punishments

import me.superorca.mcp.MCP
import me.superorca.mcp.libs.formatDuration
import me.superorca.mcp.libs.generateId
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import java.sql.ResultSet
import java.sql.SQLException
import java.util.*

class Punishment {
    private val plugin: MCP
    private var id: String? = null
    private var date: Long = 0
    var target: OfflinePlayer? = null
    private var staff: OfflinePlayer? = null
    private var reason: String? = null
    var type: PunishmentType? = null
    private var duration: Long = 0
    var active = false
    var expires: Long = 0

    constructor(
        plugin: MCP,
        target: OfflinePlayer?,
        staff: CommandSender?,
        type: PunishmentType,
        duration: Long,
        reason: String?
    ) {
        this.plugin = plugin
        id = generateId(plugin)
        this.target = target
        this.staff = if (staff is OfflinePlayer) staff else null
        this.reason = reason
        date = System.currentTimeMillis()
        this.type = type
        this.duration = duration
        active = type != PunishmentType.KICK
        expires = date + duration
    }

    constructor(plugin: MCP, result: ResultSet) {
        this.plugin = plugin
        try {
            id = result.getString("id")
            target = Bukkit.getOfflinePlayer(UUID.fromString(result.getString("target")))
            val staffName = result.getString("staff")
            staff = if (staffName == null) null else Bukkit.getOfflinePlayer(UUID.fromString(staffName))
            reason = result.getString("reason")
            date = result.getLong("date")
            type = PunishmentType.valueOf(result.getString("type"))
            duration = result.getLong("duration")
            active = result.getBoolean("active")
            expires = date + duration
        } catch (e: SQLException) {
            e.printStackTrace()
        }
    }

    val isPerm: Boolean
        get() = duration == -1L

    fun write() {
        try {
            plugin.connection.prepareStatement("INSERT OR REPLACE INTO punishments (id, type, target, staff, reason, date, duration, active) VALUES (?, ?, ?, ?, ?, ?, ?, ?)")
                .use { statement ->
                    statement.setString(1, id)
                    statement.setString(2, type.toString())
                    statement.setString(3, target!!.uniqueId.toString())
                    statement.setString(4, if (staff == null) null else staff!!.uniqueId.toString())
                    statement.setString(5, reason)
                    statement.setLong(6, date)
                    statement.setLong(7, duration)
                    statement.setBoolean(8, active)
                    statement.executeUpdate()
                }
        } catch (e: SQLException) {
            e.printStackTrace()
        }
    }

    private val display: Component?
        get() {
            val staffName = if (staff != null) staff!!.name else "CONSOLE"
            when (type) {
                PunishmentType.BAN -> {
                    return if (isPerm) {
                        Component.text(target!!.name!!, NamedTextColor.RED)
                            .append(Component.text(" was banned by ", NamedTextColor.GRAY))
                            .append(Component.text(staffName!!))
                            .append(Component.text(" for ", NamedTextColor.GRAY))
                            .append(Component.text(reason!!, NamedTextColor.RED))
                            .append(Component.text(".", NamedTextColor.GRAY))
                    } else {
                        Component.text(target!!.name!!, NamedTextColor.RED)
                            .append(Component.text(" was banned by ", NamedTextColor.GRAY))
                            .append(Component.text(staffName!!))
                            .append(Component.text(" for ", NamedTextColor.GRAY))
                            .append(Component.text(formatDuration(duration), NamedTextColor.RED))
                            .append(Component.text(" for ", NamedTextColor.GRAY))
                            .append(Component.text(reason!!, NamedTextColor.RED))
                            .append(Component.text(".", NamedTextColor.GRAY))
                    }
                }

                PunishmentType.MUTE -> {
                    return if (isPerm) {
                        Component.text(target!!.name!!, NamedTextColor.RED)
                            .append(Component.text(" was muted by ", NamedTextColor.GRAY))
                            .append(Component.text(staffName!!))
                            .append(Component.text(" for ", NamedTextColor.GRAY))
                            .append(Component.text(reason!!, NamedTextColor.RED))
                            .append(Component.text(".", NamedTextColor.GRAY))
                    } else {
                        Component.text(target!!.name!!, NamedTextColor.RED)
                            .append(Component.text(" was muted by ", NamedTextColor.GRAY))
                            .append(Component.text(staffName!!))
                            .append(Component.text(" for ", NamedTextColor.GRAY))
                            .append(Component.text(formatDuration(duration), NamedTextColor.RED))
                            .append(Component.text(" for ", NamedTextColor.GRAY))
                            .append(Component.text(reason!!, NamedTextColor.RED))
                            .append(Component.text(".", NamedTextColor.GRAY))
                    }
                }

                PunishmentType.KICK -> {
                    return Component.text(target!!.name!!, NamedTextColor.RED)
                        .append(Component.text(" was kicked by ", NamedTextColor.GRAY))
                        .append(Component.text(staffName!!))
                        .append(Component.text(" for ", NamedTextColor.GRAY))
                        .append(Component.text(reason!!, NamedTextColor.RED))
                        .append(Component.text(".", NamedTextColor.GRAY))
                }

                else -> {}
            }
            return null
        }
    val message: Component?
        get() {
            val staffName = if (staff != null) staff!!.name else "CONSOLE"
            when (type) {
                PunishmentType.BAN -> {
                    return if (isPerm) {
                        Component.text()
                            .append(Component.text("BANNED", NamedTextColor.RED, TextDecoration.BOLD))
                            .appendNewline()
                            .appendNewline()
                            .append(Component.text("Staff: ", NamedTextColor.GRAY))
                            .append(Component.text(staffName!!, NamedTextColor.WHITE))
                            .appendNewline()
                            .append(Component.text("Reason: ", NamedTextColor.GRAY))
                            .append(Component.text(reason!!, NamedTextColor.WHITE))
                            .appendNewline()
                            .appendNewline()
                            .append(Component.text(id!!, NamedTextColor.DARK_GRAY))
                            .build()
                    } else {
                        Component.text()
                            .append(Component.text("BANNED", NamedTextColor.RED, TextDecoration.BOLD))
                            .appendNewline()
                            .appendNewline()
                            .append(Component.text("Staff: ", NamedTextColor.GRAY))
                            .append(Component.text(staffName!!, NamedTextColor.WHITE))
                            .appendNewline()
                            .append(Component.text("Reason: ", NamedTextColor.GRAY))
                            .append(Component.text(reason!!, NamedTextColor.WHITE))
                            .appendNewline()
                            .append(Component.text("Expires: ", NamedTextColor.GRAY))
                            .append(
                                Component.text(
                                    formatDuration(expires - System.currentTimeMillis()),
                                    NamedTextColor.WHITE
                                )
                            )
                            .appendNewline()
                            .appendNewline()
                            .append(Component.text(id!!, NamedTextColor.DARK_GRAY))
                            .build()
                    }
                }

                PunishmentType.MUTE -> {
                    return if (isPerm) {
                        Component.text()
                            .append(
                                Component.text(
                                    "-".repeat(50),
                                    NamedTextColor.DARK_GRAY,
                                    TextDecoration.STRIKETHROUGH
                                )
                            )
                            .appendNewline()
                            .append(
                                Component.text("MUTED", NamedTextColor.RED, TextDecoration.BOLD)
                                    .decoration(TextDecoration.STRIKETHROUGH, false)
                            )
                            .appendNewline()
                            .appendNewline()
                            .append(Component.text("Staff: ", NamedTextColor.GRAY))
                            .append(Component.text(staffName!!, NamedTextColor.WHITE))
                            .appendNewline()
                            .append(Component.text("Reason: ", NamedTextColor.GRAY))
                            .append(Component.text(reason!!, NamedTextColor.WHITE))
                            .appendNewline()
                            .appendNewline()
                            .append(Component.text(id!!, NamedTextColor.DARK_GRAY))
                            .appendNewline()
                            .append(
                                Component.text(
                                    "-".repeat(50),
                                    NamedTextColor.DARK_GRAY,
                                    TextDecoration.STRIKETHROUGH
                                )
                            )
                            .build()
                    } else {
                        Component.text()
                            .append(
                                Component.text(
                                    "-".repeat(50),
                                    NamedTextColor.DARK_GRAY,
                                    TextDecoration.STRIKETHROUGH
                                )
                            )
                            .appendNewline()
                            .append(
                                Component.text("MUTED", NamedTextColor.RED, TextDecoration.BOLD)
                                    .decoration(TextDecoration.STRIKETHROUGH, false)
                            )
                            .appendNewline()
                            .appendNewline()
                            .append(Component.text("Staff: ", NamedTextColor.GRAY))
                            .append(Component.text(staffName!!, NamedTextColor.WHITE))
                            .appendNewline()
                            .append(Component.text("Reason: ", NamedTextColor.GRAY))
                            .append(Component.text(reason!!, NamedTextColor.WHITE))
                            .appendNewline()
                            .append(Component.text("Expires: ", NamedTextColor.GRAY))
                            .append(
                                Component.text(
                                    formatDuration(expires - System.currentTimeMillis()),
                                    NamedTextColor.WHITE
                                )
                            )
                            .appendNewline()
                            .appendNewline()
                            .append(Component.text(id!!, NamedTextColor.DARK_GRAY))
                            .appendNewline()
                            .append(
                                Component.text(
                                    "-".repeat(50),
                                    NamedTextColor.DARK_GRAY,
                                    TextDecoration.STRIKETHROUGH
                                )
                            )
                            .build()
                    }
                }

                PunishmentType.KICK -> {
                    return Component.text()
                        .append(Component.text("KICKED", NamedTextColor.RED, TextDecoration.BOLD))
                        .appendNewline()
                        .appendNewline()
                        .append(Component.text("Staff: ", NamedTextColor.GRAY))
                        .append(Component.text(staffName!!, NamedTextColor.WHITE))
                        .appendNewline()
                        .append(Component.text("Reason: ", NamedTextColor.GRAY))
                        .append(Component.text(reason!!, NamedTextColor.WHITE))
                        .appendNewline()
                        .appendNewline()
                        .append(Component.text(id!!, NamedTextColor.DARK_GRAY))
                        .build()
                }

                else -> {}
            }
            return null
        }

    fun execute() {
        Bukkit.broadcast(display!!)
        if (target!!.isOnline) {
            val p = target as Player?
            if (type == PunishmentType.KICK || type == PunishmentType.BAN) p!!.kick(message) else p!!.sendMessage(
                message!!
            )
        }
        plugin.punishments.add(this)
    }
}