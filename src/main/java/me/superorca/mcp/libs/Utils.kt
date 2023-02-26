package me.superorca.mcp.libs

import me.superorca.mcp.MCP
import org.apache.commons.lang3.time.DurationFormatUtils
import java.sql.SQLException

fun generateId(plugin: MCP): String {
    val charset =
        "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".split("".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
    val id = StringBuilder()
    for (i in 0..5) {
        id.append(charset[(Math.random() * charset.size).toInt()])
    }
    try {
        plugin.connection.prepareStatement("SELECT * FROM punishments WHERE id = ?").use { statement ->
            statement.setString(1, id.toString())
            val result = statement.executeQuery()
            result.close()
            if (result.next()) {
                return generateId(plugin)
            }
        }
    } catch (e: SQLException) {
        e.printStackTrace()
        return id.toString()
    }
    return id.toString()
}

fun convertToDuration(input: String?): Long {
    if (input.isNullOrEmpty()) return -1L
    val parts = input.trim { it <= ' ' }.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
    var duration = 0L
    for (part in parts) {
        val trimmedPart = part.trim { it <= ' ' }
        val trimmed = trimmedPart.substring(0, trimmedPart.length - 1).toLong() * 1000L
        duration += if (trimmedPart.endsWith("s")) {
            trimmed
        } else if (trimmedPart.endsWith("m")) {
            trimmed * 60L
        } else if (trimmedPart.endsWith("h")) {
            trimmed * 60L * 60L
        } else if (trimmedPart.endsWith("d")) {
            trimmed * 60L * 60L * 24L
        } else if (trimmedPart.endsWith("w")) {
            trimmed * 60L * 60L * 24L * 7L
        } else if (trimmedPart.endsWith("y")) {
            trimmed * 60L * 60L * 24L * 365L
        } else {
            return -1L
        }
    }
    return duration
}

fun formatDuration(duration: Long): String {
    return DurationFormatUtils.formatDurationWords(duration, true, false)
}