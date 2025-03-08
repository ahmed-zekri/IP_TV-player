package com.zekri_ahmed.ip_tv_player.data.local

import com.zekri_ahmed.ip_tv_player.domain.model.M3uEntry
import java.io.File
import java.io.InputStream

class M3uParser {

    fun parse(inputStream: InputStream): List<M3uEntry> {
        val entries = mutableListOf<M3uEntry>()
        var currentTitle = ""
        var currentAttributes = mutableMapOf<String, String>()
        var timeShiftable = true

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

                        // Check if timeshift is disabled explicitly
                        timeShiftable = currentAttributes["timeshift_disabled"]?.lowercase() != "true"

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
                                timeShiftable = timeShiftable,
                                attributes = currentAttributes.toMap()
                            )
                        )
                        // Reset for next entry
                        currentTitle = ""
                        currentAttributes = mutableMapOf()
                        timeShiftable = true
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