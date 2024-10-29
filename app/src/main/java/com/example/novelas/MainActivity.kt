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
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.room.Room
import com.example.novelas.ui.theme.Feedback1Theme
import kotlinx.coroutines.launch



class MainActivity : ComponentActivity() {
    private lateinit var database: NovelaDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inicializar la base de datos de Room
        database = Room.databaseBuilder(
            applicationContext,
            NovelaDatabase::class.java, "novela_database"
        ).build()

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
                        BibliotecaNovelasApp(database = database)
                    }
                }
            }
        }
    }

    @Composable
    fun BibliotecaNovelasApp(database: NovelaDatabase) {
        val context = LocalContext.current
        val scope = rememberCoroutineScope()

        var listaNovelas by remember { mutableStateOf(listOf<NovelaEntity>()) }
        var titulo by remember { mutableStateOf(TextFieldValue("")) }
        var autor by remember { mutableStateOf(TextFieldValue("")) }
        var anoPublicacion by remember { mutableStateOf(TextFieldValue("")) }
        var sinopsis by remember { mutableStateOf(TextFieldValue("")) }
        var novelaSeleccionada by remember { mutableStateOf<NovelaEntity?>(null) }

        // Leer datos desde SQLite al iniciar
        LaunchedEffect(Unit) {
            listaNovelas = database.novelaDao().getAllNovelas()
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp)
        ) {
            Text(
                text = "Biblioteca de Novelas",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onBackground
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

            // Botón para añadir nueva novela
            Button(
                onClick = {
                    if (titulo.text.isNotEmpty() && autor.text.isNotEmpty() && anoPublicacion.text.isNotEmpty() && sinopsis.text.isNotEmpty()) {
                        val ano = anoPublicacion.text.toIntOrNull()
                        if (ano != null) {
                            val nuevaNovela = NovelaEntity(
                                titulo = titulo.text,
                                autor = autor.text,
                                anoPublicacion = ano,
                                sinopsis = sinopsis.text,
                                esFavorita = false
                            )

                            // Insertar la novela en la base de datos y actualizar la lista
                            scope.launch {
                                database.novelaDao().insertNovela(nuevaNovela)
                                listaNovelas = database.novelaDao().getAllNovelas() // Actualizar la lista de novelas
                            }

                            // Limpiar campos después de agregar la novela
                            titulo = TextFieldValue("")
                            autor = TextFieldValue("")
                            anoPublicacion = TextFieldValue("")
                            sinopsis = TextFieldValue("")
                        } else {
                            Toast.makeText(context, "Año de publicación inválido", Toast.LENGTH_SHORT).show()
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Añadir Novela")
            }

            Spacer(modifier = Modifier.height(16.dp))

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
                        IconButton(onClick = {
                            scope.launch {
                                database.novelaDao().deleteNovela(novela.titulo)
                                listaNovelas = database.novelaDao().getAllNovelas()
                            }
                        }) {
                            Icon(imageVector = Icons.Filled.Delete, contentDescription = "Eliminar novela")
                        }
                    }
                }
            }

            // Mostrar detalles de la novela seleccionada
            novelaSeleccionada?.let { novela ->
                DetallesDeNovela(novela = novela, onDismiss = { novelaSeleccionada = null })
            }
        }
    }

    @Composable
    fun DetallesDeNovela(novela: NovelaEntity, onDismiss: () -> Unit) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text(text = novela.titulo) },
            text = {
                Column {
                    Text(text = "Autor: ${novela.autor}")
                    Text(text = "Año de Publicación: ${novela.anoPublicacion}")
                    Text(text = "Sinopsis: ${novela.sinopsis}")
                    Text(text = "Favorita: ${if (novela.esFavorita) "Sí" else "No"}")
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
    fun LoginScreen(onLoginSuccess: () -> Unit, onRegisterClick: () -> Unit) {
        var username by remember { mutableStateOf(TextFieldValue("")) }
        var password by remember { mutableStateOf(TextFieldValue("")) }
        var isDarkTheme by remember { mutableStateOf(false) }
        val context = LocalContext.current

        LaunchedEffect(Unit) {
            val sharedPreferences = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
            isDarkTheme = sharedPreferences.getBoolean("dark_theme", false)
        }

        Feedback1Theme(darkTheme = isDarkTheme) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
            ) {
                Text(
                    text = "Iniciar Sesión",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onBackground
                )

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
                    modifier = Modifier.fillMaxWidth(),
                    visualTransformation = PasswordVisualTransformation()
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        val sharedPreferences = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
                        val savedUsername = sharedPreferences.getString("username", null)
                        val savedPassword = sharedPreferences.getString("password", null)

                        if (username.text == savedUsername && password.text == savedPassword) {
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

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Tema oscuro",
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Switch(
                        checked = isDarkTheme,
                        onCheckedChange = { isChecked ->
                            isDarkTheme = isChecked
                            val sharedPreferences = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
                            sharedPreferences.edit().putBoolean("dark_theme", isDarkTheme).apply()
                        }
                    )
                }
            }
        }
    }

    @Composable
    fun RegisterScreen(onRegisterSuccess: () -> Unit) {
        var username by remember { mutableStateOf(TextFieldValue("")) }
        var password by remember { mutableStateOf(TextFieldValue("")) }
        val context = LocalContext.current

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
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = PasswordVisualTransformation()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    val sharedPreferences = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
                    sharedPreferences.edit().apply {
                        putString("username", username.text)
                        putString("password", password.text)
                        apply()
                    }
                    onRegisterSuccess()
                    Toast.makeText(context, "Registro exitoso", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Registrar")
            }
        }
    }
}
