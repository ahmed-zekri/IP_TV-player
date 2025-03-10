package com.zekri_ahmed.ip_tv_player.presentation.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.zekri_ahmed.ip_tv_player.domain.model.M3uEntry

@Composable
@Preview(showBackground = true)
fun ChannelItem(
    entry: M3uEntry = M3uEntry(
        title = "Title",
        path = "URL",
        duration = 120000,
        timeShiftable = true,
        attributes = mapOf("group-title" to "News"),
        thumbnailUrl = "https://i.imgur.com/NbesiPn.png", // Example thumbnail URL
        tvgName = "La 1", // Example tvg-name
        tvgId = "La1.es", // Example tvg-id
        groupTitle = "Spain" // Example group-title
    ),
    isPlaying: Boolean = false,
    onClick: () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Thumbnail
            if (entry.thumbnailUrl != null) {
                AsyncImage(
                    model = entry.thumbnailUrl,
                    contentDescription = "Channel Thumbnail",
                    modifier = Modifier
                        .size(64.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Fit,

                )
            } else {
                // If no thumbnail URL is provided, show a placeholder icon
                Icon(
                    imageVector = Icons.Default.Tv,
                    contentDescription = "Channel Icon",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .size(64.dp)
                        .padding(8.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Channel Info
            Column(
                modifier = Modifier.weight(1f)
            ) {
                // Channel Title
                Text(
                    text = entry.title,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                // TVG Name and ID
                if (entry.tvgName != null || entry.tvgId != null) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (entry.tvgName != null) {
                            Text(
                                text = entry.tvgName,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.secondary
                            )
                        }
                        if (entry.tvgId != null) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "(${entry.tvgId})",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.secondary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Group Title and Duration
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Schedule,
                        contentDescription = "Duration",
                        tint = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (entry.duration > 0) "${entry.duration / 60000} min" else "Unknown",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.secondary
                    )

                    Spacer(modifier = Modifier.width(16.dp))

                    Text(
                        text = entry.groupTitle ?: "No Category",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }

            // Play Icon (if the channel is playing)
            if (isPlaying) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = "Now Playing",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}