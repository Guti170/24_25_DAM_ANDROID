package com.example.variasactivitys

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.variasactivitys.databinding.ActivityVentana2Binding
import modelo.Persona

class Ventana2 : AppCompatActivity() {
    lateinit var binding: ActivityVentana2Binding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityVentana2Binding.inflate(layoutInflater)
        setContentView(binding.root)
        //setContentView(R.layout.activity_ventana2)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Con intent y pares clave -valor
//        var nombre = intent.getStringExtra("nombre1")
//        var edad = intent.getStringExtra("edad1")
//        binding.txtCaja2.text = "Hola " + nombre + " tienes " + edad + " años"

        // Recoger un objeto
//        var p = intent.getSerializableExtra("obj") as Persona
//        binding.txtCaja2.text = p.toString()
        // con Bundle
        val bundle = intent.getBundleExtra("objeto")
        val nombre = bundle!!.getString("nombre1")
        val edad = bundle!!.getString("edad1")
        val p = bundle!!.getSerializable("persona")
        binding.txtCaja2.text = "Con Bundle el objeto: " + p.toString()

        binding.btVolver.setOnClickListener {
            finish()
        }

    }
}