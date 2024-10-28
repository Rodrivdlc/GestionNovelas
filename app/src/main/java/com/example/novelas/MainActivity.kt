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
import com.example.novelas.ui.theme.Feedback1Theme


class MainActivity : ComponentActivity() {
    private val databaseUrl = "https://feedback2-c4a03-default-rtdb.europe-west1.firebasedatabase.app/"
    private val database = Firebase.database(databaseUrl)
    private val novelasRef = database.getReference("novelas")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Cargar la preferencia de tema desde SharedPreferences
        val sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val isDarkTheme = sharedPreferences.getBoolean("dark_theme", false)

        setContent {
            Feedback1Theme(darkTheme = isDarkTheme) {  // Aplicar el tema guardado
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
    fun ListaDeNovelas(
        novelas: List<Novela>,
        onEliminar: (Novela) -> Unit,
        onFavoritoToggle: (Novela) -> Unit,
        onVerDetalles: (Novela) -> Unit
    ) {
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

        // Estado para controlar qué novela se está viendo en detalles
        var novelaSeleccionada by remember { mutableStateOf<Novela?>(null) }

        // Leer datos desde Firebase
        LaunchedEffect(Unit) {
            novelasRef.get().addOnSuccessListener { snapshot ->
                val novelasList = snapshot.children.mapNotNull { it.getValue(Novela::class.java) }
                listaNovelas = novelasList
            }
        }

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

            // Mostrar detalles de la novela seleccionada
            novelaSeleccionada?.let { novela ->
                DetallesDeNovela(novela = novela, onDismiss = { novelaSeleccionada = null })
            }
        }
    }
    // Pantalla de login integrada
    @Composable
    fun LoginScreen(
        onLoginSuccess: () -> Unit,
        onRegisterClick: () -> Unit
    ) {
        var username by remember { mutableStateOf(TextFieldValue("")) }
        var password by remember { mutableStateOf(TextFieldValue("")) }
        var isDarkTheme by remember { mutableStateOf(false) }
        val context = LocalContext.current

        // Cargar preferencia del tema
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
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.onBackground)
                )

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Contraseña") },
                    modifier = Modifier.fillMaxWidth(),
                    visualTransformation = PasswordVisualTransformation(),
                    textStyle = MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.onBackground)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        val sharedPreferences = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
                        val savedUsername = sharedPreferences.getString("username", null)
                        val savedPassword = sharedPreferences.getString("password", null)

                        if (username.text == savedUsername && password.text == savedPassword) {
                            onLoginSuccess() // Navegar a la pantalla principal
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
                            // Guardar el tema en SharedPreferences
                            val sharedPreferences = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
                            sharedPreferences.edit().putBoolean("dark_theme", isDarkTheme).apply()
                        }
                    )
                }
            }
        }
    }


    // Pantalla de registro integrada
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
                    onRegisterSuccess() // Navegar de regreso a la pantalla de login
                    Toast.makeText(context, "Registro exitoso", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Registrar")
            }
        }
    }
}
