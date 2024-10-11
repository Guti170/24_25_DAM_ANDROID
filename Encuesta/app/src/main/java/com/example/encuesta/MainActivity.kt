package com.example.encuesta

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.encuesta.databinding.ActivityMainBinding

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

        binding.btValidar.setOnClickListener {
            if (binding.edNombre.text.isEmpty()) {
                Toast.makeText(this, "El nombre no puede estar vacío", Toast.LENGTH_SHORT).show()
            } else if (binding.rgSistema.checkedRadioButtonId == -1) {
                Toast.makeText(this, "Debe seleccionar un sistema operativo", Toast.LENGTH_SHORT).show()
            } else if (binding.cbDAM.isChecked == false && binding.cbDAW.isChecked == false && binding.cbASIR.isChecked == false) {
                Toast.makeText(this, "Debe seleccionar una especialidad", Toast.LENGTH_SHORT).show()
            } else if (binding.sbHoras.progress == 0) {
                Toast.makeText(this, "Debe seleccionar una cantidad de horas", Toast.LENGTH_SHORT).show()
            }
        }
        binding.btReiniciar.setOnClickListener {
            binding.edNombre.text.clear()
            binding.swAnonimo.isChecked = false
            binding.rgSistema.clearCheck()
            binding.cbDAM.isChecked = false
            binding.cbDAW.isChecked = false
            binding.cbASIR.isChecked = false
            binding.sbHoras.progress = 0
        }
        binding.btCuantas.setOnClickListener {

        }
        binding.btResumen.setOnClickListener {

        }
    }
}