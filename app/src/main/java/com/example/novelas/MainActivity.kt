package com.example.novelas

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BibliotecaNovelasApp()
        }
    }
}

@Composable
fun BibliotecaNovelasApp() {
    var listaNovelas = remember { mutableStateListOf<Novela>() }

    var titulo by remember { mutableStateOf(TextFieldValue("")) }
    var autor by remember { mutableStateOf(TextFieldValue("")) }
    var anoPublicacion by remember { mutableStateOf(TextFieldValue("")) }
    var sinopsis by remember { mutableStateOf(TextFieldValue("")) }

    // Estado para controlar qué novela se está viendo en detalles
    var novelaSeleccionada by remember { mutableStateOf<Novela?>(null) }

    // Fondo gris
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.LightGray)
            .padding(16.dp)
    ) {
        Text(
            text = "Biblioteca de Novelas",
            style = MaterialTheme.typography.headlineSmall,
            color = Color.Black
        )

        // Campos para agregar nueva novela
        OutlinedTextField(
            value = titulo,
            onValueChange = { titulo = it },
            label = { Text("Título") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = autor,
            onValueChange = { autor = it },
            label = { Text("Autor") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = anoPublicacion,
            onValueChange = { anoPublicacion = it },
            label = { Text("Año de Publicación") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = sinopsis,
            onValueChange = { sinopsis = it },
            label = { Text("Sinopsis") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Botón para añadir una nueva novela con color azul
        Button(
            onClick = {
                if (titulo.text.isNotEmpty() && autor.text.isNotEmpty() && anoPublicacion.text.isNotEmpty() && sinopsis.text.isNotEmpty()) {
                    val ano = anoPublicacion.text.toIntOrNull()
                    if (ano != null) {
                        listaNovelas.add(Novela(titulo.text, autor.text, ano, sinopsis.text))
                        titulo = TextFieldValue("")
                        autor = TextFieldValue("")
                        anoPublicacion = TextFieldValue("")
                        sinopsis = TextFieldValue("")
                    }
                }
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Blue, // Botón azul
                contentColor = Color.White   // Texto en blanco
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Añadir Novela")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Texto indicativo para hacer clic en las novelas
        Text(
            text = "Haz clic en la novela para obtener más detalles y ver o añadir reseñas",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Lista de novelas
        ListaDeNovelas(
            novelas = listaNovelas,
            onEliminar = { novela ->
                listaNovelas.remove(novela)
            },
            onFavoritoToggle = { novela ->
                val index = listaNovelas.indexOf(novela)
                listaNovelas[index] = novela.copy(esFavorita = !novela.esFavorita)
            },
            onVerDetalles = { novela ->
                novelaSeleccionada = novela
            }
        )

        // Mostrar detalles de la novela seleccionada
        novelaSeleccionada?.let { novela ->
            DetallesDeNovela(novela = novela, onDismiss = { novelaSeleccionada = null })
        }
    }
}

@Composable
fun ListaDeNovelas(
    novelas: List<Novela>,
    onEliminar: (Novela) -> Unit,
    onFavoritoToggle: (Novela) -> Unit,
    onVerDetalles: (Novela) -> Unit
) {
    LazyColumn {
        items(novelas.size) { index ->
            NovelaItem(novelas[index], onEliminar, onFavoritoToggle, onVerDetalles)
        }
    }
}

@Composable
fun NovelaItem(
    novela: Novela,
    onEliminar: (Novela) -> Unit,
    onFavoritoToggle: (Novela) -> Unit,
    onVerDetalles: (Novela) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onVerDetalles(novela) } // Muestra los detalles de la novela
    ) {
        Text(text = novela.titulo, style = MaterialTheme.typography.titleLarge)
        Text(text = "Autor: ${novela.autor}")
        Text(text = "Año: ${novela.anoPublicacion}")
        Text(
            text = if (novela.esFavorita) "⭐ Favorita" else "No Favorita",
            style = MaterialTheme.typography.bodyLarge
        )

        Row {
            Button(onClick = { onEliminar(novela) }) {
                Text("Eliminar")
            }
            Spacer(modifier = Modifier.width(8.dp))
            Button(onClick = { onFavoritoToggle(novela) }) {
                Text(if (novela.esFavorita) "Quitar de Favoritos" else "Marcar como Favorita")
            }
        }
    }
}

@Composable
fun DetallesDeNovela(novela: Novela, onDismiss: () -> Unit) {
    var nuevaReseña by remember { mutableStateOf(TextFieldValue("")) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = "Detalles de ${novela.titulo}")
        },
        text = {
            Column {
                Text("Autor: ${novela.autor}")
                Text("Año de publicación: ${novela.anoPublicacion}")
                Text("Sinopsis: ${novela.sinopsis}")
                Text("Favorita: ${if (novela.esFavorita) "Sí" else "No"}")

                Spacer(modifier = Modifier.height(16.dp))
                Text("Reseñas:")
                for (reseña in novela.reseñas) {
                    Text("- $reseña")
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Campo para agregar una nueva reseña
                OutlinedTextField(
                    value = nuevaReseña,
                    onValueChange = { nuevaReseña = it },
                    label = { Text("Agregar una reseña") }
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (nuevaReseña.text.isNotEmpty()) {
                        novela.reseñas.add(nuevaReseña.text)
                        nuevaReseña = TextFieldValue("") // Limpiar el campo
                    }
                    onDismiss()
                }
            ) {
                Text("Aceptar")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cerrar")
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    BibliotecaNovelasApp()
}
