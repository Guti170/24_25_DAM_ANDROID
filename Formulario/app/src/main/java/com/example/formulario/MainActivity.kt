package com.example.formulario

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        //val mitag = "Miguel Angel"
        val ptNombre = findViewById<EditText>(R.id.ptNombre)
        val ptApellido1 = findViewById<EditText>(R.id.ptApellido1)
        val ptApellido2 = findViewById<EditText>(R.id.ptApellido2)
        val phTelefono = findViewById<EditText>(R.id.phTelefono)
        val dtFechas = findViewById<EditText>(R.id.dtFechas)
        val emEmail = findViewById<EditText>(R.id.emEmail)
        val psContrasenia1 = findViewById<EditText>(R.id.psContrasenia1)
        val psContrasenia2 = findViewById<EditText>(R.id.psContrasenia2)
        val btValidar = findViewById<Button>(R.id.btValidar)
        val btLimpiar = findViewById<Button>(R.id.btLimpiar)

        btValidar.setOnClickListener {
            val numeroTelefono = phTelefono.text.toString()
            if (numeroTelefono.length != 9) {
                Toast.makeText(this, "El número de teléfono debe tener 9 dígitos", Toast.LENGTH_SHORT).show()
                phTelefono.setBackgroundColor(ContextCompat.getColor(this, R.color.errorColor))
            }
            else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(emEmail.text.toString()).matches()) {
                Toast.makeText(this, "El email no es válido", Toast.LENGTH_SHORT).show()
                emEmail.setBackgroundColor(ContextCompat.getColor(this, R.color.errorColor))
            }
            else if (psContrasenia1.text.toString() != psContrasenia2.text.toString()) {
                Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show()
                psContrasenia1.setBackgroundColor(ContextCompat.getColor(this, R.color.errorColor))
                psContrasenia2.setBackgroundColor(ContextCompat.getColor(this, R.color.errorColor))
            }
            else {
                Toast.makeText(this, "Formulario enviado correctamente", Toast.LENGTH_SHORT).show()
            }
        }
        btLimpiar.setOnClickListener {
            ptNombre.text.clear()
            ptApellido1.text.clear()
            ptApellido2.text.clear()
            phTelefono.text.clear()
            phTelefono.setBackgroundColor(ContextCompat.getColor(this, R.color.AzulClaro))
            dtFechas.text.clear()
            emEmail.text.clear()
            emEmail.setBackgroundColor(ContextCompat.getColor(this, R.color.AzulClaro))
            psContrasenia1.text.clear()
            psContrasenia2.text.clear()
            psContrasenia1.setBackgroundColor(ContextCompat.getColor(this, R.color.AzulClaro))
            psContrasenia2.setBackgroundColor(ContextCompat.getColor(this, R.color.AzulClaro))

        }
    }
}