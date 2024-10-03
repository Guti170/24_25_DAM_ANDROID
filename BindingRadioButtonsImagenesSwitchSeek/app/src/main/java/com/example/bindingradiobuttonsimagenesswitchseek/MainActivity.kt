package com.example.bindingradiobuttonsimagenesswitchseek

import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.bindingradiobuttonsimagenesswitchseek.databinding.ActivityMainBinding
import modelo.PedidoPizzeria

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val miTag = "Miguel Angel"
        var currentImage = R.drawable.ic_comida_foreground
        var imagen1 = R.drawable.ic_comida_foreground
        var imagen2 = R.drawable.ic_pizza

        binding.btAceptar.setOnClickListener {
            if(binding.stLicencia.isChecked){

            } else {
                Toast.makeText(this, "Debes aceptar la licencia", Toast.LENGTH_SHORT).show()
            }
        }

        val order = PedidoPizzeria(binding.ptNombre.text.toString(),
            binding.cbBacon.isChecked,
            binding.cbCebolla.isChecked,
            binding.cbQueso.isChecked,
            binding.rbFino.isChecked,
            binding.rbGordo.isChecked)
        Log.d(miTag, order.toString())

        binding.btBorrar.setOnClickListener {
            binding.ptNombre.text.clear()
            binding.cbBacon.isChecked = false
            binding.cbCebolla.isChecked = false
            binding.cbQueso.isChecked = false
            binding.stLicencia.isChecked = false
            binding.rbFino.isChecked = false
            binding.rbGordo.isChecked = false
        }
        binding.ibCambiarImagen.setOnClickListener {
            if (currentImage == imagen1) {
                binding.ivImagen.setImageResource(imagen2)
                currentImage = imagen2
            } else {
                binding.ivImagen.setImageResource(imagen1)
                currentImage = imagen1
            }
        }
        binding.ivImagen.setOnClickListener {
            binding.ivImagen.setImageResource(R.drawable.pizza)
        }

    }
}