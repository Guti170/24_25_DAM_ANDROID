package com.example.variasactivitys

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.variasactivitys.databinding.ActivityMainBinding
import modelo.Persona

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
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

        binding.btPasar.setOnClickListener {
            // Con intent y pares clave -valor
            var inte : Intent = Intent(this, Ventana2::class.java)
//            inte.putExtra("nombre1", binding.etNombre.text.toString())
//            inte.putExtra("edad1", binding.etEdad.text.toString())

            // Pasar un objeto a otra ventana
//            var p = Persona(binding.etNombre.text.toString(), binding.etEdad.text.toString().toInt())
//            inte.putExtra("obj", p)

            var p = Persona(binding.etNombre.text.toString(), binding.etEdad.text.toString().toInt())
            // ahora con Bundle
            val bundle = Bundle()
            bundle.putString("nombre1", binding.etNombre.text.toString())
            bundle.putString("edad1", binding.etEdad.text.toString())
            bundle.putSerializable("persona", p)
            inte.putExtra("objeto", bundle)

            startActivity(inte)
        }
    }
}