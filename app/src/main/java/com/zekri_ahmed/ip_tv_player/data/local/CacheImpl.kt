package com.zekri_ahmed.ip_tv_player.data.local

import android.content.Context
import android.os.storage.StorageManager
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.cache.*
import java.io.File
import java.util.NavigableSet
import java.util.TreeSet
import java.util.concurrent.ConcurrentHashMap

@UnstableApi
class CacheImpl(val context: Context) : Cache {

    private val cacheDir = File(context.cacheDir, "media_cache")
    private val cacheMap = ConcurrentHashMap<String, MutableSet<CacheSpan>>()
    private val metadataMap = ConcurrentHashMap<String, ContentMetadata>()

    init {
        if (!cacheDir.exists()) cacheDir.mkdirs()
    }

    override fun commitFile(file: File, length: Long) {
        val key = file.name
        val position = file.name.substringAfterLast("-").toLongOrNull() ?: 0L // Extract position
        val span = CacheSpan(key, position, length, System.currentTimeMillis(), file)
        cacheMap.getOrPut(key) { TreeSet() }.add(span)
    }

    override fun releaseHoleSpan(holeSpan: CacheSpan) {
        cacheMap[holeSpan.key]?.remove(holeSpan)
    }

    override fun removeResource(key: String) {
        cacheMap.remove(key)
        metadataMap.remove(key)
        File(cacheDir, key).delete()
    }

    override fun removeSpan(span: CacheSpan) {
        cacheMap[span.key]?.remove(span)
    }

    override fun isCached(key: String, position: Long, length: Long): Boolean {
        return cacheMap[key]?.any { it.position <= position && it.position + it.length >= position + length } ?: false
    }

    override fun getCachedLength(key: String, position: Long, length: Long): Long {
        return cacheMap[key]?.firstOrNull { it.position <= position }?.length ?: 0
    }

    override fun getCachedBytes(key: String, position: Long, length: Long): Long {
        return getCachedLength(key, position, length)
    }

    override fun applyContentMetadataMutations(key: String, mutations: ContentMetadataMutations) {
        metadataMap[key] = DefaultContentMetadata().copyWithMutationsApplied(mutations)
    }

    override fun getContentMetadata(key: String): ContentMetadata {
        return metadataMap[key] ?: DefaultContentMetadata()
    }

    override fun getKeys(): MutableSet<String> {
        return cacheMap.keys.toMutableSet()
    }

    override fun getCacheSpace(): Long {
        val storageManager = context.getSystemService(Context.STORAGE_SERVICE) as StorageManager

        // Check if the device is running Android 13 or higher
        return if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            val storageVolume = storageManager.storageVolumes.firstOrNull { volume ->
                volume.directory?.absolutePath?.startsWith(cacheDir.absolutePath) == true
            }

            // Check if storageUuid is null and provide a fallback if necessary
            storageVolume?.storageUuid?.let {
                // Use getAllocatableBytes() if storageUuid is available
                storageManager.getAllocatableBytes(it)
            } ?: cacheDir.usableSpace // Fallback if storageUuid is null
        } else {
            // For Android versions lower than Android 13, use cacheDir.usableSpace
            cacheDir.usableSpace
        }
    }




    override fun startReadWrite(key: String, position: Long, length: Long): CacheSpan {
        val file = File(cacheDir, "$key-$position.cache")
        return CacheSpan(key, position, length, System.currentTimeMillis(), file)
    }

    override fun startReadWriteNonBlocking(key: String, position: Long, length: Long): CacheSpan? {
        return cacheMap[key]?.firstOrNull { it.position <= position }
    }

    override fun startFile(key: String, position: Long, length: Long): File {
        val file = File(cacheDir, "$key-$position.cache")
        file.createNewFile()
        return file
    }

    override fun getUid(): Long {
        return cacheDir.hashCode().toLong()
    }

    override fun release() {
        cacheMap.clear()
        metadataMap.clear()
    }

    override fun addListener(key: String, listener: Cache.Listener): NavigableSet<CacheSpan> {
        return TreeSet(cacheMap[key] ?: emptySet())
    }

    override fun removeListener(key: String, listener: Cache.Listener) {
        // No-op (handled internally)
    }

    override fun getCachedSpans(key: String): NavigableSet<CacheSpan> {
        return TreeSet(cacheMap[key] ?: emptySet())
    }
}
