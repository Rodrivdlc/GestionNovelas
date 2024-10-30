package com.example.novelas

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.content.ContentValues
import android.database.Cursor

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "UserDatabase.db"
        const val TABLE_USERS = "users"
        const val COLUMN_USERNAME = "username"
        const val COLUMN_PASSWORD = "password"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val CREATE_USERS_TABLE = ("CREATE TABLE $TABLE_USERS ("
                + "$COLUMN_USERNAME TEXT PRIMARY KEY, "
                + "$COLUMN_PASSWORD TEXT)")
        db.execSQL(CREATE_USERS_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_USERS")
        onCreate(db)
    }

    fun addUser(username: String, password: String): Boolean {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(COLUMN_USERNAME, username)
        values.put(COLUMN_PASSWORD, password)
        val result = db.insert(TABLE_USERS, null, values)
        db.close()
        return result != -1L
    }

    fun checkUser(username: String, password: String): Boolean {
        val db = this.readableDatabase
        val cursor: Cursor = db.rawQuery(
            "SELECT * FROM $TABLE_USERS WHERE $COLUMN_USERNAME = ? AND $COLUMN_PASSWORD = ?",
            arrayOf(username, password)
        )
        val exists = cursor.moveToFirst()
        cursor.close()
        db.close()
        return exists
    }
}
