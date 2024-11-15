package com.example.novelas

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.fragment.app.Fragment
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier

class NovelaDetailsFragment : Fragment() {

    private var novela: Novela? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return ComposeView(requireContext()).apply {
            setContent {
                novela?.let {
                    DetallesDeNovela(novela = it, onDismiss = { closeFragment() })
                }
            }
        }
    }

    fun setNovela(novela: Novela) {
        this.novela = novela
    }

    private fun closeFragment() {
        parentFragmentManager.popBackStack()
    }

    @Composable
    fun DetallesDeNovela(novela: Novela, onDismiss: () -> Unit) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "Título: ${novela.titulo}", style = MaterialTheme.typography.headlineSmall)
            Text(text = "Autor: ${novela.autor}", style = MaterialTheme.typography.bodyLarge)
            Text(text = "Año de Publicación: ${novela.anoPublicacion}", style = MaterialTheme.typography.bodyLarge)
            Text(text = "Sinopsis: ${novela.sinopsis}", style = MaterialTheme.typography.bodyLarge)
            Text(text = "Favorita: ${if (novela.esFavorita) "Sí" else "No"}", style = MaterialTheme.typography.bodyLarge)

            Spacer(modifier = Modifier.height(16.dp))

            Text(text = "Reseñas:", style = MaterialTheme.typography.headlineSmall)
            novela.reseñas.forEach { reseña ->
                Text(text = reseña, style = MaterialTheme.typography.bodyLarge)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = onDismiss) {
                Text("Cerrar")
            }
        }
    }
}