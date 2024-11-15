package com.example.novelas

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class NovelasListFragment : Fragment() {

    private lateinit var novelasAdapter: NovelasAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d("NovelasListFragment", "onCreateView called")
        val view = inflater.inflate(R.layout.fragment_novelas_list, container, false)
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerViewNovelas)
        recyclerView.layoutManager = LinearLayoutManager(context)
        novelasAdapter = NovelasAdapter(emptyList()) { novela ->
            (activity as MainActivity).showNovelaDetails(novela)
        }
        recyclerView.adapter = novelasAdapter
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Populate with sample data
        val sampleNovelas = listOf(
            Novela("Title 1", "Author 1", 2021, "Synopsis 1"),
            Novela("Title 2", "Author 2", 2022, "Synopsis 2")
        )
        updateNovelas(sampleNovelas)
    }

    fun updateNovelas(novelas: List<Novela>) {
        novelasAdapter.updateNovelas(novelas)
    }
}