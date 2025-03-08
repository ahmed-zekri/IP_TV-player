package com.zekri_ahmed.ip_tv_player.data.local

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import com.zekri_ahmed.ip_tv_player.domain.model.M3uEntry
import java.io.File
import java.io.FileOutputStream

class M3uLocalDataSource(private val context: Context) {

    private val m3uParser = M3uParser() // Initialize M3uParser

    fun loadPlaylist(uri: Uri): List<M3uEntry> {
        val file = copyFileToLocalStorage(uri)
        return m3uParser.parseFile(file) // Use M3uParser to parse the file
    }

    private fun copyFileToLocalStorage(uri: Uri): File {
        val inputStream = context.contentResolver.openInputStream(uri)
        val fileName = getFileName(context, uri)
        val file = File(context.filesDir, fileName)

        inputStream?.use { input ->
            FileOutputStream(file).use { output ->
                input.copyTo(output)
            }
        }

        return file
    }

    private fun getFileName(context: Context, uri: Uri): String {
        var name: String? = null
        context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (nameIndex != -1 && cursor.moveToFirst()) {
                name = cursor.getString(nameIndex)
            }
        }
        return name ?: "playlist.m3u"
    }
}