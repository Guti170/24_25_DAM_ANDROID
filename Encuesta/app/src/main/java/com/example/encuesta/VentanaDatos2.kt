package com.example.encuesta

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.encuesta.databinding.ActivityVentanaDatos2Binding
import modelo.Alumno

class VentanaDatos2 : AppCompatActivity() {
    lateinit var binding: ActivityVentanaDatos2Binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityVentanaDatos2Binding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val alumno = intent.getSerializableExtra("alumno") as Alumno
        binding.tvDatosNombre.text = alumno.nombre
        binding.tvDatosSistema.text = alumno.sistemaOperativo
        binding.tvDatosEspecialidad.text = alumno.especialidad
        binding.tvContador.text = alumno.horas.toString()

        val intent = Intent()
        intent.putExtra("borrar_datos", true)
        setResult(Activity.RESULT_OK, intent)

        binding.btVolver.setOnClickListener {
            finish()
        }
    }
}