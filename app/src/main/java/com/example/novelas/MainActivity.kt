package com.example.novelas

import android.content.Context
import android.os.Bundle
import android.widget.Toast
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.novelas.ui.theme.Feedback1Theme
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class MainActivity : ComponentActivity() {
    private val databaseUrl = "https://feedback2-c4a03-default-rtdb.europe-west1.firebasedatabase.app/"
    private val novelasRef = Firebase.database(databaseUrl).getReference("novelas")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Cargar la preferencia de tema desde SharedPreferences
        val sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val isDarkTheme = sharedPreferences.getBoolean("dark_theme", false)

        setContent {
            Feedback1Theme(darkTheme = isDarkTheme) {
                val navController = rememberNavController()
                NavHost(navController, startDestination = "login") {
                    composable("login") {
                        LoginScreen(
                            onLoginSuccess = { navController.navigate("main") },
                            onRegisterClick = { navController.navigate("register") }
                        )
                    }
                    composable("register") {
                        RegisterScreen(onRegisterSuccess = { navController.popBackStack() })
                    }
                    composable("main") {
                        BibliotecaNovelasApp()
                    }
                }
            }
        }
    }
    @Composable
    fun DetallesDeNovela(novela: Novela, onDismiss: () -> Unit) {
        var nuevaReseña by remember { mutableStateOf(TextFieldValue("")) }
        var isFavorita by remember { mutableStateOf(novela.esFavorita) } // Estado para el botón de favorito

        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text(text = novela.titulo) },
            text = {
                Column {
                    Text(text = "Autor: ${novela.autor}")
                    Text(text = "Año de Publicación: ${novela.anoPublicacion}")
                    Text(text = "Sinopsis: ${novela.sinopsis}")

                    // Botón para alternar el estado de favorito
                    Button(
                        onClick = {
                            isFavorita = !isFavorita
                            novela.esFavorita = isFavorita // Actualiza el estado de favorito en la novela
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(if (isFavorita) "Quitar de Favoritos" else "Añadir a Favoritos")
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Text(text = "Reseñas:")
                    novela.reseñas.forEach { reseña ->
                        Text(text = reseña, style = MaterialTheme.typography.bodyMedium)
                    }
                    Spacer(modifier = Modifier.height(16.dp))

                    // Campo de texto para agregar una nueva reseña
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

        // Cargar datos desde Firebase
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

            // Botón para añadir una novela
            Button(
                onClick = {
                    if (titulo.text.isNotEmpty() && autor.text.isNotEmpty() && anoPublicacion.text.isNotEmpty() && sinopsis.text.isNotEmpty()) {
                        val ano = anoPublicacion.text.toIntOrNull()
                        if (ano != null) {
                            val nuevaNovela = Novela(titulo.text, autor.text, ano, sinopsis.text)
                            novelasRef.push().setValue(nuevaNovela)
                            listaNovelas = listaNovelas + nuevaNovela
                            titulo = TextFieldValue("")
                            autor = TextFieldValue("")
                            anoPublicacion = TextFieldValue("")
                            sinopsis = TextFieldValue("")
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Añadir Novela")
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Lista de novelas
            LazyColumn {
                items(listaNovelas) { novela ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                            .clickable { novelaSeleccionada = novela }
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = novela.titulo, style = MaterialTheme.typography.bodyLarge)
                            Text(text = "Autor: ${novela.autor}", style = MaterialTheme.typography.bodyMedium)
                            Text(text = "Año: ${novela.anoPublicacion}", style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            }

            // Mostrar los detalles de la novela seleccionada
            novelaSeleccionada?.let { novela ->
                DetallesDeNovela(novela = novela, onDismiss = { novelaSeleccionada = null })
            }
        }
    }


    @Composable
    fun LoginScreen(onLoginSuccess: () -> Unit, onRegisterClick: () -> Unit) {
        var username by remember { mutableStateOf(TextFieldValue("")) }
        var password by remember { mutableStateOf(TextFieldValue("")) }
        val context = LocalContext.current
        val dbHelper = DatabaseHelper(context)

        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "Iniciar Sesión", style = MaterialTheme.typography.headlineSmall)

            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Usuario") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Contraseña") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    if (dbHelper.checkUser(username.text, password.text)) {
                        onLoginSuccess()
                    } else {
                        Toast.makeText(context, "Usuario o contraseña incorrectos", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Iniciar Sesión")
            }

            Spacer(modifier = Modifier.height(8.dp))

            TextButton(onClick = onRegisterClick) {
                Text("Registrarse")
            }
        }
    }

    @Composable
    fun RegisterScreen(onRegisterSuccess: () -> Unit) {
        var username by remember { mutableStateOf(TextFieldValue("")) }
        var password by remember { mutableStateOf(TextFieldValue("")) }
        val context = LocalContext.current
        val dbHelper = DatabaseHelper(context)

        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "Registro", style = MaterialTheme.typography.headlineSmall)

            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Usuario") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Contraseña") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    if (dbHelper.addUser(username.text, password.text)) {
                        Toast.makeText(context, "Registro exitoso", Toast.LENGTH_SHORT).show()
                        onRegisterSuccess()
                    } else {
                        Toast.makeText(context, "Error al registrar", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Registrar")
            }
        }
    }
}
