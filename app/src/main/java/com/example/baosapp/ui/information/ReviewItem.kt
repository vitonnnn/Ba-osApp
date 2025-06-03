// app/src/main/java/com/example/baosapp/ui/information/ReviewItem.kt
package com.example.baosapp.ui.information

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ReviewItem(uiModel: ReviewUiModel) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = MaterialTheme.shapes.medium
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            // Línea con estrellas, limpieza y olor
            Text(
                text = "★ ${uiModel.valoracion}   🧼 ${uiModel.limpieza}   👃 ${uiModel.olor}",
                style = MaterialTheme.typography.bodySmall
            )

            // Comentario
            Text(
                text = uiModel.comment,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 4.dp)
            )

            // Fecha/hora: si es null, mostramos “–” o cadena vacía
            val fechaText = uiModel.createdAt?.replace('T', ' ') ?: "–"
            Text(
                text = fechaText,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}
