package com.example.novelas

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [NovelaEntity::class], version = 1)
abstract class NovelaDatabase : RoomDatabase() {
    abstract fun novelaDao(): NovelaDao
}

