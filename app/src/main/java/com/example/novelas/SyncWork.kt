package com.example.novelas

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.ListenableWorker.Result
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.tasks.await

class SyncWork(appContext: Context, workerParams: WorkerParameters) :
    CoroutineWorker(appContext, workerParams) {

    private val database = Firebase.database("https://feedback2-c4a03-default-rtdb.europe-west1.firebasedatabase.app/")
    private val novelasRef = database.getReference("novelas")

    override suspend fun doWork(): Result {
        return try {
            val snapshot = withContext(Dispatchers.IO) {
                novelasRef.get().await()
            }
            processData(snapshot)
            Result.success()
        } catch (e: Exception) {
            Result.failure()
        }
    }

    private fun processData(snapshot: DataSnapshot) {
        // Procesar los datos sincronizados
        snapshot.children.forEach { child ->
            val novela = child.getValue(Novela::class.java)
            // Realizar acciones con los datos si es necesario
        }
    }
}