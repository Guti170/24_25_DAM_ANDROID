package com.example.simongame

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.simongame.databinding.ActivityMainBinding
import java.util.Random

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var botones: Array<Button>
    private var secuencia: MutableList<Int> = mutableListOf()
    private var usuarioSecuencia: MutableList<Int> = mutableListOf()
    private var nivel = 1
    private var UsuarioInputEnabled = false
    private val handler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        botones = arrayOf(
            findViewById(R.id.btRojo),
            findViewById(R.id.btVerde),
            findViewById(R.id.btAzul),
            findViewById(R.id.btAmarillo)
        )

        findViewById<Button>(R.id.btIniciar).setOnClickListener {
            inicioJuego()
        }
    }

    private fun inicioJuego() {
        secuencia.clear()
        usuarioSecuencia.clear()
        nivel = 1
        UsuarioInputEnabled = false
        generarSecuencia()
        iniciarSecuencia()
    }

    private fun generarSecuencia() {
        val random = Random()
        secuencia.add(random.nextInt(4))
    }

    private fun iniciarSecuencia() {
        UsuarioInputEnabled = false
        var delay = 500L

        for (colorIndex in secuencia) {
            handler.postDelayed({
                lucesBotones(colorIndex)
                }, delay)
                delay += 500L
            }

            handler.postDelayed({
                UsuarioInputEnabled = true
            }, delay)
    }

    private fun lucesBotones(colorIndex: Int) {
        val button = botones[colorIndex]

        val originalColor = when (colorIndex) {
            0 -> ContextCompat.getColorStateList(this, R.color.rojoOscuro)
            1 -> ContextCompat.getColorStateList(this, R.color.verdeOscuro)
            2 -> ContextCompat.getColorStateList(this, R.color.azulOscuro)
            3 -> ContextCompat.getColorStateList(this, R.color.amarilloOscuro)
            else -> button.backgroundTintList
        }

        val luzColor = when (colorIndex) {
            0 -> ContextCompat.getColorStateList(this, R.color.rojo)
            1 -> ContextCompat.getColorStateList(this, R.color.verde)
            2 -> ContextCompat.getColorStateList(this, R.color.azul)
            3 -> ContextCompat.getColorStateList(this, R.color.amarillo)
            else -> originalColor
        }

        button.backgroundTintList = luzColor

        handler.postDelayed({
            button.backgroundTintList = originalColor
        }, 300L)
    }

    fun BotonClick(view: View) {
        if (!UsuarioInputEnabled) return

        val buttonIndex = when (view.id) {
            R.id.btRojo -> 0
            R.id.btVerde -> 1
            R.id.btAzul -> 2
            R.id.btAmarillo -> 3
            else -> -1
        }

        if (buttonIndex != -1) {
            usuarioSecuencia.add(buttonIndex)
            lucesBotones(buttonIndex)
            chequeoUsuarioClick()
        }
    }

    private fun chequeoUsuarioClick() {
        if (usuarioSecuencia == secuencia.subList(0, usuarioSecuencia.size)) {
            if (usuarioSecuencia.size == secuencia.size) {
                nivel++
                usuarioSecuencia.clear()
                Toast.makeText(this, "Nivel $nivel", Toast.LENGTH_SHORT).show()
                handler.postDelayed({
                    generarSecuencia()
                    iniciarSecuencia()
                    }, 1000L)
                }
            } else {
                Toast.makeText(this, "Fin del juego", Toast.LENGTH_SHORT).show()
                inicioJuego()
            }
    }
}