package com.example.recyclerview

import adaptador.PlanetAdapter
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.recyclerview.databinding.ActivityMainBinding
import modelo.PlanetData

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    val planets = PlanetData.getPlanets()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        //val botonDetalle: Button = findViewById(R.id.btDetalle)
        var recyclerView: RecyclerView = findViewById(R.id.rvPlanetas)
        recyclerView.layoutManager = LinearLayoutManager(this)
        val planetAdapter = PlanetAdapter(planets.toMutableList())
        recyclerView.adapter = planetAdapter

    }
}