package com.example.bindingradiobuttonsimagenesswitchseek

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.bindingradiobuttonsimagenesswitchseek.databinding.ActivityMainBinding

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

        binding.btAceptar.setOnClickListener {
            Log.i(miTag,"Hola ${binding.ptNombre.text}")
            Toast.makeText(this, "Hola ${binding.ptNombre.text}", Toast.LENGTH_LONG).show()
        }
        binding.btBorrar.setOnClickListener {
            binding.ptNombre.text.clear()
        }

    }
}