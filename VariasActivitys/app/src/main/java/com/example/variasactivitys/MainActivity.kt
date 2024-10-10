package com.example.variasactivitys

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.variasactivitys.databinding.ActivityMainBinding
import modelo.Almacen
import modelo.Persona

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    private var SECOND_ACTIVITY_REQUEST_CODE = 0

    var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            val returnString = data!!.getStringExtra("valor")
            binding.etNombre.setText(returnString)
        }
    }

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
//            val bundle = Bundle()
//            bundle.putString("nombre1", binding.etNombre.text.toString())
//            bundle.putString("edad1", binding.etEdad.text.toString())
//            bundle.putSerializable("persona", p)
//            inte.putExtra("objeto", bundle)

            // ahora con un objeto
            Almacen.addPersona(p)
            startActivity(inte)
        }

        binding.btReiniciar.setOnClickListener {
            var ine : Intent = intent
            finish()
            startActivity(ine)
        }

        binding.btLlamar1.setOnClickListener {
            var intent : Intent = Intent(this, Ventana2::class.java)
            startActivityForResult(intent, SECOND_ACTIVITY_REQUEST_CODE)
        }

        binding.btLlamar2.setOnClickListener {
            var intent : Intent = Intent(this, Ventana2::class.java)
            resultLauncher.launch(intent)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == SECOND_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                val returnString = data!!.getStringExtra("valor")
                binding.etNombre.setText(returnString)
            }
            else {
                binding.etNombre.setText("No ha devuelto nada")
            }
        }
    }
}