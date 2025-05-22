package com.example.baosapp.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Divider
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.outlined.Star
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.baosapp.data.model.toilet.Toilet

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ToiletItem(
    toilet: Toilet,
    isFavorite: Boolean,
    onToggleFavorite: (Toilet) -> Unit,
    onLocateClick: (Toilet) -> Unit,
    onReviewClick: (Toilet) -> Unit,
    onInfoClick: (Toilet) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded }
            .padding(8.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(toilet.name, style = MaterialTheme.typography.titleMedium)
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { onToggleFavorite(toilet) }) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Filled.Star else Icons.Outlined.Star,
                        contentDescription = if (isFavorite) "Quitar de favoritos" else "Añadir a favoritos",
                        tint = if (isFavorite) Color(0xFFADD8E6) else Color.Yellow
                    )
                }
                IconButton(onClick = { onLocateClick(toilet) }) {
                    Icon(
                        imageVector = Icons.Filled.Place,
                        contentDescription = "Localizar baño"
                    )
                }
                Text(
                    text = "${"%.1f".format(toilet.avgRating)} ★",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        if (expanded) {
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = { onReviewClick(toilet) }) {
                    Text("Reseña")
                }
                Button(onClick = { onInfoClick(toilet) }) {
                    Text("Información")
                }
            }
        }

        Divider()
    }
}
