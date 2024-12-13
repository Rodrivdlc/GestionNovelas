package com.example.novelas

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Place
import java.util.concurrent.TimeUnit
import androidx.work.WorkManager
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkRequest
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class MainActivity : ComponentActivity() {
    private var listaNovelas: List<Novela> = emptyList()


    private lateinit var dbHelper: DatabaseHelper
    private lateinit var preferencesManager: PreferencesManager
    private lateinit var valueEventListener: ValueEventListener

    private val databaseUrl = "https://feedback2-c4a03-default-rtdb.europe-west1.firebasedatabase.app/"
    private val database = Firebase.database(databaseUrl)
    private val novelasRef = database.getReference("novelas")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dbHelper = DatabaseHelper(this)
        preferencesManager = PreferencesManager(this)

        // Activar persistencia local de Firebase
        Firebase.database.setPersistenceEnabled(true)

        // Configurar sincronización periódica
        val syncWorkRequest = PeriodicWorkRequestBuilder<SyncWork>(1, TimeUnit.HOURS).build()
        WorkManager.getInstance(applicationContext).enqueue(syncWorkRequest)

        // Configurar el ValueEventListener para escuchar cambios en la base de datos
        valueEventListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // Actualizar la lista de novelas
                listaNovelas = snapshot.children.mapNotNull { it.getValue(Novela::class.java) }
            }

            override fun onCancelled(error: DatabaseError) {
                // Manejo de errores en la consulta de Firebase
            }
        }
        novelasRef.addValueEventListener(valueEventListener)

        setContent {
            var isLoggedIn by remember { mutableStateOf(false) }
            var showRegister by remember { mutableStateOf(false) }

            if (isLoggedIn) {
                BibliotecaNovelasApp()
            } else {
                if (showRegister) {
                    RegistroScreen(dbHelper) {
                        showRegister = false
                    }
                } else {
                    LoginScreen(dbHelper, preferencesManager, onLoginExitoso = { isLoggedIn = true }) {
                        showRegister = true
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        novelasRef.removeEventListener(valueEventListener) // Detener listeners activos
    }


    @Composable
    fun ListaDeNovelas(
        novelas: List<Novela>,
        onEliminar: (Novela) -> Unit,
        onFavoritoToggle: (Novela) -> Unit,
        onVerDetalles: (Novela) -> Unit
    ) {
        // Variable para manejar la novela seleccionada para mostrar el mapa
        var novelaSeleccionadaParaMapa by remember { mutableStateOf<Novela?>(null) }

        // Mostrar el mapa junto con la ubicación si hay una novela seleccionada
        if (novelaSeleccionadaParaMapa != null) {
            VerMapa(
                ubicacion = novelaSeleccionadaParaMapa!!.ubicacion, // Pasar la ubicación de la novela seleccionada
                onDismiss = { novelaSeleccionadaParaMapa = null }
            )
        }

        LazyColumn {
            items(novelas) { novela ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .clickable { onVerDetalles(novela) }
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = novela.titulo, style = MaterialTheme.typography.bodyLarge)
                        Text(text = "Autor: ${novela.autor}", style = MaterialTheme.typography.bodyMedium)
                        Text(text = "Año: ${novela.anoPublicacion}", style = MaterialTheme.typography.bodyMedium)
                        Text(text = "Ubicación: ${novela.ubicacion}", style = MaterialTheme.typography.bodyMedium)
                    }
                    IconButton(onClick = { onFavoritoToggle(novela) }) {
                        Icon(
                            imageVector = if (novela.esFavorita) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                            contentDescription = "Toggle Favorite"
                        )
                    }
                    IconButton(onClick = { onEliminar(novela) }) {
                        Icon(imageVector = Icons.Filled.Delete, contentDescription = "Eliminar novela")
                    }
                    IconButton(onClick = { novelaSeleccionadaParaMapa = novela }) {
                        Icon(imageVector = Icons.Default.Place, contentDescription = "Ver en mapa")
                    }
                }
            }
        }
    }

    @Composable
    fun DetallesDeNovela(novela: Novela, onDismiss: () -> Unit) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text(text = novela.titulo) },
            text = {
                Column {
                    Text(text = "Autor: ${novela.autor}")
                    Text(text = "Año de Publicación: ${novela.anoPublicacion}")
                    Text(text = "Sinopsis: ${novela.sinopsis}")
                    Text(text = "Favorita: ${if (novela.esFavorita) "Sí" else "No"}")
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(text = "Reseñas:")
                    novela.reseñas.forEach { reseña ->
                        Text(text = reseña, style = MaterialTheme.typography.bodyMedium)
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    var nuevaReseña by remember { mutableStateOf(TextFieldValue("")) }
                    OutlinedTextField(
                        value = nuevaReseña,
                        onValueChange = { nuevaReseña = it },
                        label = { Text("Agregar una reseña") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Button(
                        onClick = {
                            if (nuevaReseña.text.isNotEmpty()) {
                                novela.reseñas.add(nuevaReseña.text)
                                nuevaReseña = TextFieldValue("")
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Aceptar")
                    }
                }
            },
            confirmButton = {
                Button(onClick = onDismiss) {
                    Text("Cerrar")
                }
            }
        )
    }

    @Composable
    fun BibliotecaNovelasApp() {
        var listaNovelas by remember { mutableStateOf(listOf<Novela>()) }
        var titulo by remember { mutableStateOf(TextFieldValue("")) }
        var autor by remember { mutableStateOf(TextFieldValue("")) }
        var anoPublicacion by remember { mutableStateOf(TextFieldValue("")) }
        var sinopsis by remember { mutableStateOf(TextFieldValue("")) }
        var novelaSeleccionada by remember { mutableStateOf<Novela?>(null) }
        var ubicacion by remember { mutableStateOf(TextFieldValue("")) }


        LaunchedEffect(Unit) {
            novelasRef.get().addOnSuccessListener { snapshot ->
                val novelasList = snapshot.children.mapNotNull { it.getValue(Novela::class.java) }
                listaNovelas = novelasList
            }
        }

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

            OutlinedTextField(
                value = ubicacion,
                onValueChange = { ubicacion = it },
                label = { Text("Ubicación") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    if (titulo.text.isNotEmpty() && autor.text.isNotEmpty() && anoPublicacion.text.isNotEmpty() && sinopsis.text.isNotEmpty() && ubicacion.text.isNotEmpty()) {
                        val ano = anoPublicacion.text.toIntOrNull()
                        if (ano != null) {
                            val nuevaNovela = Novela(
                                titulo = titulo.text,
                                autor = autor.text,
                                anoPublicacion = ano,
                                sinopsis = sinopsis.text,
                                ubicacion = ubicacion.text // Añadimos la ubicación
                            )
                            novelasRef.push().setValue(nuevaNovela)
                            listaNovelas = listaNovelas + nuevaNovela
                            titulo = TextFieldValue("")
                            autor = TextFieldValue("")
                            anoPublicacion = TextFieldValue("")
                            sinopsis = TextFieldValue("")
                            ubicacion = TextFieldValue("")
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Blue,
                    contentColor = Color.White
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Añadir Novela")
            }


            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Haz clic en la novela para obtener más detalles y ver o añadir reseñas",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Black,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            ListaDeNovelas(
                novelas = listaNovelas,
                onEliminar = { novela ->
                    novelasRef.child(novela.titulo).removeValue()
                    listaNovelas = listaNovelas - novela
                },
                onFavoritoToggle = { novela ->
                    val index = listaNovelas.indexOf(novela)
                    val updatedNovela = novela.copy(esFavorita = !novela.esFavorita)
                    listaNovelas = listaNovelas.toMutableList().apply { set(index, updatedNovela) }
                    novelasRef.child(novela.titulo).setValue(updatedNovela)
                },
                onVerDetalles = { novela ->
                    novelaSeleccionada = novela
                }
            )

            novelaSeleccionada?.let { novela ->
                DetallesDeNovela(novela = novela, onDismiss = { novelaSeleccionada = null })
            }
        }
    }
}