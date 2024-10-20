package com.example.encuesta

import adaptador.EncuestaAdaptador
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.encuesta.databinding.ActivityVentanaDatosListaBinding
import modelo.Almacen
import modelo.Alumno

class VentanaDatosLista : AppCompatActivity() {
    private lateinit var binding: ActivityVentanaDatosListaBinding
    val alumnos = Almacen.getAlumnos()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityVentanaDatosListaBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val intent = Intent()
        intent.putExtra("borrar_datos", true)
        setResult(Activity.RESULT_OK, intent)

        var recyclerView: RecyclerView = findViewById(R.id.listaAlumnos)
        recyclerView.layoutManager = LinearLayoutManager(this)
        val planetAdapter = EncuestaAdaptador(alumnos.toMutableList() as ArrayList<Alumno>)
        recyclerView.adapter = planetAdapter

        binding.btVolverAlumnos.setOnClickListener {
            finish()
        }

    }
}