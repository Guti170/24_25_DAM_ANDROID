package com.example.juegodados

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.juegodados.databinding.ActivityMainBinding
import java.util.Random
import kotlin.random.nextInt

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

        binding.tvCampeon1.visibility = View.INVISIBLE
        binding.tvCampeon2.visibility = View.INVISIBLE
        binding.btReiniciar.visibility = View.INVISIBLE
        binding.btReiniciarTotal.visibility = View.INVISIBLE

        val imagenes = listOf(
            R.drawable.ic_dado_1,
            R.drawable.ic_dado_2,
            R.drawable.ic_dado_3,
            R.drawable.ic_dado_4,
            R.drawable.ic_dado_5,
            R.drawable.ic_dado_6
        )
        var ganadas1 = 0
        var ganadas2 = 0
        var puntos1 = 0
        var puntos2 = 0
        var tiradas1 = 0
        var tiradas2 = 0

        fun mostrarGanador() {
            if (puntos1 > puntos2){
                ganadas1++
                binding.tvCampeon1.visibility = View.VISIBLE
                binding.tvGPuntos1.text = ganadas1.toString()
                binding.btReiniciar.visibility = View.VISIBLE
                binding.btReiniciarTotal.visibility = View.VISIBLE
            } else if (puntos2 > puntos1){
                ganadas2++
                binding.tvCampeon2.visibility = View.VISIBLE
                binding.tvGPuntos2.text = ganadas2.toString()
                binding.btReiniciar.visibility = View.VISIBLE
                binding.btReiniciarTotal.visibility = View.VISIBLE
            } else {
                Toast.makeText(this, "Empate", Toast.LENGTH_SHORT).show()
                binding.btReiniciar.visibility = View.VISIBLE
                binding.btReiniciarTotal.visibility = View.VISIBLE
            }
        }

        binding.btJugador1.setOnClickListener {
            val random = Random()
            val imagenAleatoria = imagenes[random.nextInt(imagenes.size)]
            binding.ivDado1.setImageResource(imagenAleatoria)
            tiradas1++
            if (tiradas1 == tiradas2) {
                mostrarGanador()
            }
            if (tiradas1 <= 5){
                if (imagenAleatoria == R.drawable.ic_dado_1) {
                    puntos1++
                    binding.tvPtPuntos1.text = puntos1.toString()
                    binding.tvTPuntos1.text = tiradas1.toString()
                } else if (imagenAleatoria == R.drawable.ic_dado_2) {
                    puntos1 += 2
                    binding.tvPtPuntos1.text = puntos1.toString()
                    binding.tvTPuntos1.text = tiradas1.toString()
                } else if (imagenAleatoria == R.drawable.ic_dado_3) {
                    puntos1 += 3
                    binding.tvPtPuntos1.text = puntos1.toString()
                    binding.tvTPuntos1.text = tiradas1.toString()
                } else if (imagenAleatoria == R.drawable.ic_dado_4) {
                    puntos1 += 4
                    binding.tvPtPuntos1.text = puntos1.toString()
                    binding.tvTPuntos1.text = tiradas1.toString()
                } else if (imagenAleatoria == R.drawable.ic_dado_5) {
                    puntos1 += 5
                    binding.tvPtPuntos1.text = puntos1.toString()
                    binding.tvTPuntos1.text = tiradas1.toString()
                } else if (imagenAleatoria == R.drawable.ic_dado_6) {
                    puntos1 += 6
                    binding.tvPtPuntos1.text = puntos1.toString()
                    binding.tvTPuntos1.text = tiradas1.toString()
                }
            } else {
                Toast.makeText(this, "Jugador 1 no puede realizar mas tiradas", Toast.LENGTH_SHORT).show()
                binding.btJugador1.isEnabled = false
            }
        }

        binding.btJugador2.setOnClickListener {
            val random = Random()
            val imagenAleatoria = imagenes[random.nextInt(imagenes.size)]
            binding.ivDado2.setImageResource(imagenAleatoria)
            tiradas2++
            if (tiradas1 == tiradas2) {
                mostrarGanador()
            }
            if (tiradas2 <= 5) {
                if (imagenAleatoria == R.drawable.ic_dado_1) {
                    puntos2++
                    binding.tvPtPuntos2.text = puntos2.toString()
                    binding.tvTPuntos2.text = tiradas2.toString()
                } else if (imagenAleatoria == R.drawable.ic_dado_2) {
                    puntos2 += 2
                    binding.tvPtPuntos2.text = puntos2.toString()
                    binding.tvTPuntos2.text = tiradas2.toString()
                } else if (imagenAleatoria == R.drawable.ic_dado_3) {
                    puntos2 += 3
                    binding.tvPtPuntos2.text = puntos2.toString()
                    binding.tvTPuntos2.text = tiradas2.toString()
                } else if (imagenAleatoria == R.drawable.ic_dado_4) {
                    puntos2 += 4
                    binding.tvPtPuntos2.text = puntos2.toString()
                    binding.tvTPuntos2.text = tiradas2.toString()
                } else if (imagenAleatoria == R.drawable.ic_dado_5) {
                    puntos2 += 5
                    binding.tvPtPuntos2.text = puntos2.toString()
                    binding.tvTPuntos2.text = tiradas2.toString()
                } else if (imagenAleatoria == R.drawable.ic_dado_6) {
                    puntos2 += 6
                    binding.tvPtPuntos2.text = puntos2.toString()
                    binding.tvTPuntos2.text = tiradas2.toString()
                }
            } else {
                Toast.makeText(this, "Jugador 2 no puede realizar mas tiradas", Toast.LENGTH_SHORT)
                    .show()
                binding.btJugador2.isEnabled = false
            }
        }

        binding.btReiniciar.setOnClickListener {
            binding.tvGPuntos1.text = ganadas1.toString()
            binding.tvGPuntos2.text = ganadas2.toString()
            puntos1 = 0
            puntos2 = 0
            binding.tvPtPuntos1.text = puntos1.toString()
            binding.tvPtPuntos2.text = puntos2.toString()
            tiradas1 = 0
            tiradas2 = 0
            binding.tvTPuntos1.text = tiradas1.toString()
            binding.tvTPuntos2.text = tiradas2.toString()
            binding.btJugador1.isEnabled = true
            binding.btJugador2.isEnabled = true
            binding.ivDado1.setImageResource(R.drawable.ic_dado_aleatorio)
            binding.ivDado2.setImageResource(R.drawable.ic_dado_aleatorio)
            binding.tvCampeon1.visibility = View.INVISIBLE
            binding.tvCampeon2.visibility = View.INVISIBLE
            binding.btReiniciar.visibility = View.INVISIBLE
            binding.btReiniciarTotal.visibility = View.INVISIBLE
        }

        binding.btReiniciarTotal.setOnClickListener {
            ganadas1 = 0
            ganadas2 = 0
            binding.tvGPuntos1.text = ganadas1.toString()
            binding.tvGPuntos2.text = ganadas2.toString()
            puntos1 = 0
            puntos2 = 0
            binding.tvPtPuntos1.text = puntos1.toString()
            binding.tvPtPuntos2.text = puntos2.toString()
            tiradas1 = 0
            tiradas2 = 0
            binding.tvTPuntos1.text = tiradas1.toString()
            binding.tvTPuntos2.text = tiradas2.toString()
            binding.btJugador1.isEnabled = true
            binding.btJugador2.isEnabled = true
            binding.ivDado1.setImageResource(R.drawable.ic_dado_aleatorio)
            binding.ivDado2.setImageResource(R.drawable.ic_dado_aleatorio)
            binding.tvCampeon1.visibility = View.INVISIBLE
            binding.tvCampeon2.visibility = View.INVISIBLE
            binding.btReiniciar.visibility = View.INVISIBLE
            binding.btReiniciarTotal.visibility = View.INVISIBLE
        }

    }
}