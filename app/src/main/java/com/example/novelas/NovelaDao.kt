package com.example.novelas

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface NovelaDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNovela(novela: NovelaEntity)

    @Query("SELECT * FROM novelas")
    suspend fun getAllNovelas(): List<NovelaEntity>

    @Query("DELETE FROM novelas WHERE titulo = :titulo")
    suspend fun deleteNovela(titulo: String)
}
