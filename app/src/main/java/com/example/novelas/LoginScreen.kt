package com.example.novelas

import androidx.compose.foundation.background
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
fun LoginScreen(
    dbHelper: DatabaseHelper,
    preferencesManager: PreferencesManager,
    onLoginExitoso: () -> Unit,
    onRegistrarClick: () -> Unit
) {
    var nombre by remember { mutableStateOf(TextFieldValue("")) }
    var contrasena by remember { mutableStateOf(TextFieldValue("")) }
    var mensaje by remember { mutableStateOf("") }
    var backgroundColor by remember { mutableStateOf(preferencesManager.getBackgroundColor()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .padding(16.dp)
    ) {
        Text(
            text = "Iniciar Sesión",
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
            if (dbHelper.autenticarUsuario(nombre.text, contrasena.text)) {
                mensaje = "Inicio de sesión exitoso"
                onLoginExitoso()
            } else {
                mensaje = "Nombre de usuario o contraseña incorrectos"
            }
        }, modifier = Modifier.fillMaxWidth()) {
            Text("Iniciar Sesión")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(text = mensaje, color = Color.Red, style = MaterialTheme.typography.bodyMedium)

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(onClick = onRegistrarClick) {
            Text("¿No tienes una cuenta? Regístrate")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            val newColor = if (backgroundColor == Color.White) Color.LightGray else Color.White
            backgroundColor = newColor
            preferencesManager.setBackgroundColor(newColor)
        }) {
            Text("Cambiar color de fondo")
        }
    }
}

