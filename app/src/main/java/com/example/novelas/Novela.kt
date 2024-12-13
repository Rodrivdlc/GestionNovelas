package com.example.novelas

data class Novela(
    val titulo: String = "",
    val autor: String = "",
    val anoPublicacion: Int = 0,
    val sinopsis: String = "",
    var esFavorita: Boolean = false,
    val reseñas: MutableList<String> = mutableListOf(),
    val ubicacion: String = ""
)
