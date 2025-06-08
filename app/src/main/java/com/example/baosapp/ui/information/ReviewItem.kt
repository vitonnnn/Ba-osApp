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
import androidx.compose.ui.text.font.FontWeight
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
            // Mostrar el nombre de usuario en primera línea (por ejemplo, en negrita o con estilo)
            Text(
                text = uiModel.username,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
            )

            // Línea con estrellas, limpieza y olor
            Text(
                text = "★ ${uiModel.valoracion}   🧼 ${uiModel.limpieza}   👃 ${uiModel.olor}",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = 4.dp)
            )

            // Comentario
            Text(
                text = uiModel.comment,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 4.dp)
            )

            // Fecha/hora: si es null, mostramos “–”

        }
    }
}

