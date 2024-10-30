package com.example.novelas

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp


@Composable
fun RegistroScreen(dbHelper: DatabaseHelper, onRegistroExitoso: () -> Unit) {
    var nombre by remember { mutableStateOf(TextFieldValue("")) }
    var contrasena by remember { mutableStateOf(TextFieldValue("")) }
    var mensaje by remember { mutableStateOf("") }

    Column(modifier = Modifier.padding(16.dp)) {
        Text(
            text = "Registro",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        OutlinedTextField(
            value = nombre,
            onValueChange = { nombre = it },
            label = { Text("Nombre de usuario") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = contrasena,
            onValueChange = { contrasena = it },
            label = { Text("Contraseña") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        Button(onClick = {
            if (dbHelper.registrarUsuario(nombre.text, contrasena.text)) {
                mensaje = "Registro exitoso"
                onRegistroExitoso()  // Navega de vuelta al login
            } else {
                mensaje = "Error: el usuario ya existe"
            }
        },
            modifier = Modifier.fillMaxWidth()) {
            Text("Registrar")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(text = mensaje, color = Color.Red, style = MaterialTheme.typography.bodyMedium)
    }
}


