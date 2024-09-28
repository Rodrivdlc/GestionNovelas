package com.example.feedback1

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
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
    var listaNovelas by remember { mutableStateOf(mutableListOf<Novela>()) }
    var titulo by remember { mutableStateOf(TextFieldValue("")) }
    var autor by remember { mutableStateOf(TextFieldValue("")) }
    var anoPublicacion by remember { mutableStateOf(TextFieldValue("")) }
    var sinopsis by remember { mutableStateOf(TextFieldValue("")) }

    Column(modifier = Modifier.padding(16.dp)) {
        Text(text = "Biblioteca de Novelas", style = MaterialTheme.typography.h5)

        // Input fields to add a new novel
        BasicTextField(value = titulo, onValueChange = { titulo = it }, modifier = Modifier.fillMaxWidth(), decorationBox = { innerTextField -> TextFieldDefaults.TextFieldDecorationBox(innerTextField = innerTextField, label = { Text("Título") }) })
        BasicTextField(value = autor, onValueChange = { autor = it }, modifier = Modifier.fillMaxWidth(), decorationBox = { innerTextField -> TextFieldDefaults.TextFieldDecorationBox(innerTextField = innerTextField, label = { Text("Autor") }) })
        BasicTextField(value = anoPublicacion, onValueChange = { anoPublicacion = it }, modifier = Modifier.fillMaxWidth(), decorationBox = { innerTextField -> TextFieldDefaults.TextFieldDecorationBox(innerTextField = innerTextField, label = { Text("Año de Publicación") }) })
        BasicTextField(value = sinopsis, onValueChange = { sinopsis = it }, modifier = Modifier.fillMaxWidth(), decorationBox = { innerTextField -> TextFieldDefaults.TextFieldDecorationBox(innerTextField = innerTextField, label = { Text("Sinopsis") }) })

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            if (titulo.text.isNotEmpty() && autor.text.isNotEmpty() && anoPublicacion.text.isNotEmpty() && sinopsis.text.isNotEmpty()) {
                listaNovelas.add(Novela(titulo.text, autor.text, anoPublicacion.text.toInt(), sinopsis.text))
                titulo = TextFieldValue("")
                autor = TextFieldValue("")
                anoPublicacion = TextFieldValue("")
                sinopsis = TextFieldValue("")
            }
        }) {
            Text("Añadir Novela")
        }

        Spacer(modifier = Modifier.height(16.dp))

        ListaDeNovelas(listaNovelas, onEliminar = { novela ->
            listaNovelas.remove(novela)
        }, onFavoritoToggle = { novela ->
            novela.esFavorita = !novela.esFavorita
        })
    }
}

@Composable
fun ListaDeNovelas(novelas: List<Novela>, onEliminar: (Novela) -> Unit, onFavoritoToggle: (Novela) -> Unit) {
    LazyColumn {
        items(novelas.size) { index ->
            NovelaItem(novelas[index], onEliminar, onFavoritoToggle)
        }
    }
}

@Composable
fun NovelaItem(novela: Novela, onEliminar: (Novela) -> Unit, onFavoritoToggle: (Novela) -> Unit) {
    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(8.dp)
        .clickable { onFavoritoToggle(novela) }) {
        Text(text = novela.titulo, style = MaterialTheme.typography.h6)
        Text(text = "Autor: ${novela.autor}")
        Text(text = "Año: ${novela.anoPublicacion}")
        Text(text = if (novela.esFavorita) "⭐ Favorita" else "No Favorita", style = MaterialTheme.typography.body1)

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

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    BibliotecaNovelasApp()
}
