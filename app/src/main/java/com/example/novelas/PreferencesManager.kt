package com.example.novelas

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.ui.graphics.Color

class PreferencesManager(context: Context) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("settings", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_BACKGROUND_COLOR = "background_color"
    }

    // Función para obtener el color guardado
    fun getBackgroundColor(): Color {
        val color = sharedPreferences.getInt(KEY_BACKGROUND_COLOR, Color.White.hashCode())
        return Color(color)
    }

    // Función para guardar el color de fondo
    fun setBackgroundColor(color: Color) {
        sharedPreferences.edit().putInt(KEY_BACKGROUND_COLOR, color.hashCode()).apply()
    }
}
