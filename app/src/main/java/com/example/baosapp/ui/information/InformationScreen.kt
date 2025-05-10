// app/src/main/java/com/example/baosapp/ui/information/InformationScreen.kt
package com.example.baosapp.ui.information

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.baosapp.data.model.toilet.Toilet

@Composable
fun InformationScreen(
    toilet: Toilet?,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        if (toilet == null) {
            Text("No se ha seleccionado ningún baño.", style = MaterialTheme.typography.bodyMedium)
        } else {
            Text("Información del baño", style = MaterialTheme.typography.titleLarge)
            Text("• Nombre: ${toilet.name}",      style = MaterialTheme.typography.bodyMedium)
            Text("• Accesible: ${if (toilet.accesible) "Sí" else "No"}", style = MaterialTheme.typography.bodyMedium)
            Text("• Público:   ${if (toilet.publico)   "Sí" else "No"}", style = MaterialTheme.typography.bodyMedium)
            Text("• Mixto:     ${if (toilet.mixto)     "Sí" else "No"}", style = MaterialTheme.typography.bodyMedium)
            Text("• Cambio bebés: ${if (toilet.cambioBebes) "Sí" else "No"}", style = MaterialTheme.typography.bodyMedium)
        }
    }
}
