package com.example.encuesta

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.SeekBar
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.encuesta.databinding.ActivityMainBinding
import modelo.Almacen
import modelo.Alumno

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    var CODIGO_SOLICITUD = 0
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

        var miTag = "Miguel Angel"
        var nombre = ""
        var especialidad = ""
        var sistema = ""
        var contAlumnos = 0
        var datos = true

        binding.btValidar.setOnClickListener {
            especialidad = ""

            if (binding.edNombre.text.isEmpty() && binding.swAnonimo.isChecked == false) {
                Toast.makeText(this, "El nombre no puede estar vacío", Toast.LENGTH_SHORT).show()
                datos = false
            } else if (binding.rgSistema.checkedRadioButtonId == -1) {
                Toast.makeText(this, "Debe seleccionar un sistema operativo", Toast.LENGTH_SHORT).show()
                datos = false
            } else if (binding.cbDAM.isChecked == false && binding.cbDAW.isChecked == false && binding.cbASIR.isChecked == false) {
                Toast.makeText(this, "Debe seleccionar una especialidad", Toast.LENGTH_SHORT).show()
                datos = false
            } else if (binding.sbHoras.progress == 0) {
                Toast.makeText(this, "Debe seleccionar una cantidad de horas", Toast.LENGTH_SHORT).show()
                datos = false
            } else {
                datos = true
            }

            if (binding.swAnonimo.isChecked) {
                nombre = "Anónimo"
            } else {
                nombre = binding.edNombre.text.toString()
            }

            if (binding.cbDAM.isChecked) {
                especialidad += binding.cbDAM.text.toString() + " "
            }
            if (binding.cbDAW.isChecked) {
                especialidad += binding.cbDAW.text.toString() + " "
            }
            if (binding.cbASIR.isChecked) {
                especialidad += binding.cbASIR.text.toString() + " "
            }

            if (binding.rgSistema.checkedRadioButtonId == R.id.rbMac) {
                sistema = binding.rbMac.text.toString()
            } else if (binding.rgSistema.checkedRadioButtonId == R.id.rbWindows) {
                sistema = binding.rbWindows.text.toString()
            } else if (binding.rgSistema.checkedRadioButtonId == R.id.rbLinux) {
                sistema = binding.rbLinux.text.toString()
            }

            if (datos) {
                contAlumnos++

                var alumno = Alumno(contAlumnos, nombre, sistema, especialidad, binding.sbHoras.progress)
                Almacen.addAlumnos(alumno)
                Toast.makeText(this, "Alumno añadido", Toast.LENGTH_SHORT).show()

                val intent = Intent(this, VentanaDatos2::class.java)
                intent.putExtra("alumno", alumno)
                startActivityForResult(intent, CODIGO_SOLICITUD)
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
            Toast.makeText(this, "Ha realizado $contAlumnos encuestas", Toast.LENGTH_SHORT).show()
        }
        binding.btResumen.setOnClickListener {
            binding.tvDatos.text = Almacen.getAlumnos().toString()
        }

        binding.sbHoras.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                Log.i(miTag, "Progress: $progress")
                binding.tvContador.text = progress.toString()
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                Log.i(miTag, "Start tracking ${binding.sbHoras.progress}")
            }
            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                Log.i(miTag, "Stop tracking ${binding.sbHoras.progress}")
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CODIGO_SOLICITUD && resultCode == Activity.RESULT_OK) {
            if (data?.getBooleanExtra("borrar_datos", false) == true) {
                binding.edNombre.text.clear()
                binding.swAnonimo.isChecked = false
                binding.rgSistema.clearCheck()
                binding.cbDAM.isChecked = false
                binding.cbDAW.isChecked = false
                binding.cbASIR.isChecked = false
                binding.sbHoras.progress = 0
            }
        }
    }
}