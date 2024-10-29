package com.example.novelas

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "novelas")
data class NovelaEntity(
    @PrimaryKey val titulo: String,
    val autor: String,
    val anoPublicacion: Int,
    val sinopsis: String,
    var esFavorita: Boolean = false,
    val reseñas: List<String> = listOf() // Convierte `MutableList` a `List`
)
