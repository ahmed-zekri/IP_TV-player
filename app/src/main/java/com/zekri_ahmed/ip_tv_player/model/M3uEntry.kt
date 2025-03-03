// app/src/main/java/com/example/m3uplayer/model/M3uPlaylist.kt

package com.zekri_ahmed.ip_tv_player.model

import java.io.File
import java.io.InputStream

data class M3uEntry(
    val title: String,
    val path: String,
    val duration: Long = -1, // in milliseconds, -1 if unknown
    val attributes: Map<String, String> = emptyMap()
)

class M3uParser {
    private fun parse(inputStream: InputStream): List<M3uEntry> {
        val entries = mutableListOf<M3uEntry>()
        var currentTitle = ""
        var currentAttributes = mutableMapOf<String, String>()

        inputStream.bufferedReader().useLines { lines ->
            lines.forEach { line ->
                when {
                    line.startsWith("#EXTINF:") -> {
                        // Parse duration and title
                        val durationStr = line.substringAfter("#EXTINF:").substringBefore(",")
                        val duration = try {
                            (durationStr.toFloat() * 1000).toLong()
                        } catch (e: Exception) {
                            -1L
                        }

                        currentTitle = line.substringAfter(",", "")

                        // Extract additional attributes if present
                        val attrRegex = """(\w+)="([^"]*)"""""
                        attrRegex.toRegex().findAll(line).forEach {
                            val (key, value) = it.destructured
                            currentAttributes[key] = value
                        }

                        // Set duration as an attribute
                        if (duration > 0) {
                            currentAttributes["duration"] = duration.toString()
                        }
                    }
                    line.startsWith("#") -> {
                        // Skip other comments/directives
                    }
                    line.isNotBlank() -> {
                        // This is a media URL
                        entries.add(
                            M3uEntry(
                                title = currentTitle,
                                path = line.trim(),
                                duration = currentAttributes["duration"]?.toLongOrNull() ?: -1,
                                attributes = currentAttributes.toMap()
                            )
                        )
                        // Reset for next entry
                        currentTitle = ""
                        currentAttributes = mutableMapOf()
                    }
                }
            }
        }

        return entries
    }

    fun parseFile(file: File): List<M3uEntry> {
        return file.inputStream().use { parse(it) }
    }
}