package com.zekri_ahmed.ip_tv_player.domain.model

data class M3uEntry(
    val title: String,
    val path: String,
    val duration: Long = -1, // in milliseconds, -1 if unknown
    val timeShiftable: Boolean = true, // Default to true to allow time shifting for most streams
    val attributes: Map<String, String> = emptyMap(),
    val tvgName: String?,
    val tvgId: String?,
    val groupTitle: String?,
    val thumbnailUrl: String?
)