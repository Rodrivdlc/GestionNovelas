package com.example.novelas

data class Novela(
    val titulo: String = "",
    val autor: String = "",
    val anoPublicacion: Int = 0,
    val sinopsis: String = "",
    val reseñas: MutableList<String> = mutableListOf(),
    var esFavorita: Boolean = false
)

