package com.example.novelas

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.content.ContentValues

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "novelasApp.db"
        private const val DATABASE_VERSION = 1
        private const val TABLE_USERS = "usuarios"
        private const val COLUMN_USERNAME = "nombre"
        private const val COLUMN_PASSWORD = "contrasena"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTable = "CREATE TABLE $TABLE_USERS (" +
                "$COLUMN_USERNAME TEXT PRIMARY KEY," +
                "$COLUMN_PASSWORD TEXT)"
        db.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_USERS")
        onCreate(db)
    }

    fun registrarUsuario(nombre: String, contrasena: String): Boolean {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_USERNAME, nombre)
            put(COLUMN_PASSWORD, contrasena)
        }
        val result = db.insert(TABLE_USERS, null, values)
        db.close() // Cerrar la base de datos
        return result != -1L
    }

    fun autenticarUsuario(nombre: String, contrasena: String): Boolean {
        val db = readableDatabase
        val cursor = db.query(
            TABLE_USERS,
            arrayOf(COLUMN_USERNAME),
            "$COLUMN_USERNAME = ? AND $COLUMN_PASSWORD = ?",
            arrayOf(nombre, contrasena),
            null, null, null
        )
        val autenticado = cursor.count > 0
        cursor.close() // Cerrar el cursor
        db.close() // Cerrar la base de datos
        return autenticado
    }
}
