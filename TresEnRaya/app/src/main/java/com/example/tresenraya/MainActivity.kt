package com.example.tresenraya

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.tresenraya.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    var puntuacionJugador1 = 0
    var puntuacionJugador2 = 0
    var ganador = 0
    var contador = 0
    var turnoJugador = 1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.tvTituloTresEnRaya)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.btReiniciar.visibility = View.INVISIBLE

        binding.ivPosicion1.setOnClickListener {
            if (turnoJugador == 1) {
                binding.ivPosicion1.setImageResource(R.drawable.zombie_tres_en_raya)
                turnoJugador = 2
                binding.ivTurnoTER.setImageResource(R.drawable.calavera_tres_en_raya)
            } else {
                binding.ivPosicion1.setImageResource(R.drawable.calavera_tres_en_raya)
                turnoJugador = 1
                binding.ivTurnoTER.setImageResource(R.drawable.zombie_tres_en_raya)
            }
            contador++
            binding.ivPosicion1.isClickable = false
            if (contador == 9) {
                binding.btReiniciar.visibility = View.VISIBLE
                if (ganador == 0) {
                    Toast.makeText(this, "Empate!", Toast.LENGTH_SHORT).show()
                }
            }
            comprobadorVictoria()
        }
        binding.ivPosicion2.setOnClickListener {
            if (turnoJugador == 1) {
                binding.ivPosicion2.setImageResource(R.drawable.zombie_tres_en_raya)
                turnoJugador = 2
                binding.ivTurnoTER.setImageResource(R.drawable.calavera_tres_en_raya)
            } else {
                binding.ivPosicion2.setImageResource(R.drawable.calavera_tres_en_raya)
                turnoJugador = 1
                binding.ivTurnoTER.setImageResource(R.drawable.zombie_tres_en_raya)
            }
            contador++
            binding.ivPosicion2.isClickable = false
            if (contador == 9) {
                binding.btReiniciar.visibility = View.VISIBLE
                if (ganador == 0) {
                    Toast.makeText(this, "Empate!", Toast.LENGTH_SHORT).show()
                }
            }
            comprobadorVictoria()
        }
        binding.ivPosicion3.setOnClickListener {
            if (turnoJugador == 1) {
                binding.ivPosicion3.setImageResource(R.drawable.zombie_tres_en_raya)
                turnoJugador = 2
                binding.ivTurnoTER.setImageResource(R.drawable.calavera_tres_en_raya)
            } else {
                binding.ivPosicion3.setImageResource(R.drawable.calavera_tres_en_raya)
                turnoJugador = 1
                binding.ivTurnoTER.setImageResource(R.drawable.zombie_tres_en_raya)
            }
            contador++
            binding.ivPosicion3.isClickable = false
            if (contador == 9) {
                binding.btReiniciar.visibility = View.VISIBLE
                if (ganador == 0) {
                    Toast.makeText(this, "Empate!", Toast.LENGTH_SHORT).show()
                }
            }
            comprobadorVictoria()
        }
        binding.ivPosicion4.setOnClickListener {
            if (turnoJugador == 1) {
                binding.ivPosicion4.setImageResource(R.drawable.zombie_tres_en_raya)
                turnoJugador = 2
                binding.ivTurnoTER.setImageResource(R.drawable.calavera_tres_en_raya)
            } else {
                binding.ivPosicion4.setImageResource(R.drawable.calavera_tres_en_raya)
                turnoJugador = 1
                binding.ivTurnoTER.setImageResource(R.drawable.zombie_tres_en_raya)
            }
            contador++
            binding.ivPosicion4.isClickable = false
            if (contador == 9) {
                binding.btReiniciar.visibility = View.VISIBLE
                if (ganador == 0) {
                    Toast.makeText(this, "Empate!", Toast.LENGTH_SHORT).show()
                }
            }
            comprobadorVictoria()
        }
        binding.ivPosicion5.setOnClickListener {
            if (turnoJugador == 1) {
                binding.ivPosicion5.setImageResource(R.drawable.zombie_tres_en_raya)
                turnoJugador = 2
                binding.ivTurnoTER.setImageResource(R.drawable.calavera_tres_en_raya)
            } else {
                binding.ivPosicion5.setImageResource(R.drawable.calavera_tres_en_raya)
                turnoJugador = 1
                binding.ivTurnoTER.setImageResource(R.drawable.zombie_tres_en_raya)
            }
            contador++
            binding.ivPosicion5.isClickable = false
            if (contador == 9) {
                binding.btReiniciar.visibility = View.VISIBLE
                if (ganador == 0) {
                    Toast.makeText(this, "Empate!", Toast.LENGTH_SHORT).show()
                }
            }
            comprobadorVictoria()
        }
        binding.ivPosicion6.setOnClickListener {
            if (turnoJugador == 1) {
                binding.ivPosicion6.setImageResource(R.drawable.zombie_tres_en_raya)
                turnoJugador = 2
                binding.ivTurnoTER.setImageResource(R.drawable.calavera_tres_en_raya)
            } else {
                binding.ivPosicion6.setImageResource(R.drawable.calavera_tres_en_raya)
                turnoJugador = 1
                binding.ivTurnoTER.setImageResource(R.drawable.zombie_tres_en_raya)
            }
            contador++
            binding.ivPosicion6.isClickable = false
            if (contador == 9) {
                binding.btReiniciar.visibility = View.VISIBLE
                if (ganador == 0) {
                    Toast.makeText(this, "Empate!", Toast.LENGTH_SHORT).show()
                }
            }
            comprobadorVictoria()
        }
        binding.ivPosicion7.setOnClickListener {
            if (turnoJugador == 1) {
                binding.ivPosicion7.setImageResource(R.drawable.zombie_tres_en_raya)
                turnoJugador = 2
                binding.ivTurnoTER.setImageResource(R.drawable.calavera_tres_en_raya)
            } else {
                binding.ivPosicion7.setImageResource(R.drawable.calavera_tres_en_raya)
                turnoJugador = 1
                binding.ivTurnoTER.setImageResource(R.drawable.zombie_tres_en_raya)
            }
            contador++
            binding.ivPosicion7.isClickable = false
            if (contador == 9) {
                binding.btReiniciar.visibility = View.VISIBLE
                if (ganador == 0) {
                    Toast.makeText(this, "Empate!", Toast.LENGTH_SHORT).show()
                }
            }
            comprobadorVictoria()
        }
        binding.ivPosicion8.setOnClickListener {
            if (turnoJugador == 1) {
                binding.ivPosicion8.setImageResource(R.drawable.zombie_tres_en_raya)
                turnoJugador = 2
                binding.ivTurnoTER.setImageResource(R.drawable.calavera_tres_en_raya)
            } else {
                binding.ivPosicion8.setImageResource(R.drawable.calavera_tres_en_raya)
                turnoJugador = 1
                binding.ivTurnoTER.setImageResource(R.drawable.zombie_tres_en_raya)
            }
            contador++
            binding.ivPosicion8.isClickable = false
            if (contador == 9) {
                binding.btReiniciar.visibility = View.VISIBLE
                if (ganador == 0) {
                    Toast.makeText(this, "Empate!", Toast.LENGTH_SHORT).show()
                }
            }
            comprobadorVictoria()
        }
        binding.ivPosicion9.setOnClickListener {
            if (turnoJugador == 1) {
                binding.ivPosicion9.setImageResource(R.drawable.zombie_tres_en_raya)
                turnoJugador = 2
                binding.ivTurnoTER.setImageResource(R.drawable.calavera_tres_en_raya)
            } else {
                binding.ivPosicion9.setImageResource(R.drawable.calavera_tres_en_raya)
                turnoJugador = 1
                binding.ivTurnoTER.setImageResource(R.drawable.zombie_tres_en_raya)
            }
            contador++
            binding.ivPosicion9.isClickable = false
            if (contador == 9) {
                binding.btReiniciar.visibility = View.VISIBLE
                if (ganador == 0) {
                    Toast.makeText(this, "Empate!", Toast.LENGTH_SHORT).show()
                }
            }
            comprobadorVictoria()
        }

        binding.btReiniciar.setOnClickListener {
            if (turnoJugador == 1) {
                turnoJugador = 1
                binding.ivTurnoTER.setImageResource(R.drawable.zombie_tres_en_raya)
            } else {
                turnoJugador = 2
                binding.ivTurnoTER.setImageResource(R.drawable.calavera_tres_en_raya)
            }
            contador = 0
            binding.ivPosicion1.isClickable = true
            binding.ivPosicion2.isClickable = true
            binding.ivPosicion3.isClickable = true
            binding.ivPosicion4.isClickable = true
            binding.ivPosicion5.isClickable = true
            binding.ivPosicion6.isClickable = true
            binding.ivPosicion7.isClickable = true
            binding.ivPosicion8.isClickable = true
            binding.ivPosicion9.isClickable = true
            binding.ivPosicion1.setImageResource(R.drawable.imagen_blanco)
            binding.ivPosicion2.setImageResource(R.drawable.imagen_blanco)
            binding.ivPosicion3.setImageResource(R.drawable.imagen_blanco)
            binding.ivPosicion4.setImageResource(R.drawable.imagen_blanco)
            binding.ivPosicion5.setImageResource(R.drawable.imagen_blanco)
            binding.ivPosicion6.setImageResource(R.drawable.imagen_blanco)
            binding.ivPosicion7.setImageResource(R.drawable.imagen_blanco)
            binding.ivPosicion8.setImageResource(R.drawable.imagen_blanco)
            binding.ivPosicion9.setImageResource(R.drawable.imagen_blanco)
            binding.btReiniciar.visibility = View.INVISIBLE
        }

    }

    private fun comprobadorVictoria() {
        val combinacionesVictoria = listOf(
            listOf(1, 2, 3), listOf(4, 5, 6), listOf(7, 8, 9),
            listOf(1, 4, 7), listOf(2, 5, 8), listOf(3, 6, 9),
            listOf(1, 5, 9), listOf(3, 5, 7)
        )

        for (combination in combinacionesVictoria) {
            val image1 = findViewById<ImageView>(resources.getIdentifier("ivPosicion${combination[0]}", "id", packageName))
            val image2 = findViewById<ImageView>(resources.getIdentifier("ivPosicion${combination[1]}", "id", packageName))
            val image3 = findViewById<ImageView>(resources.getIdentifier("ivPosicion${combination[2]}", "id", packageName))

            if (image1.drawable != null && image2.drawable != null && image3.drawable != null) {
                if (image1.drawable.constantState == image2.drawable.constantState &&
                    image2.drawable.constantState == image3.drawable.constantState
                ) {
                    if (image1.drawable.constantState == resources.getDrawable(R.drawable.zombie_tres_en_raya)?.constantState) {
                        ganador = 1
                    } else if (image1.drawable.constantState == resources.getDrawable(R.drawable.calavera_tres_en_raya)?.constantState) {
                        ganador = 2
                    }

                    if (ganador == 1) {
                        puntuacionJugador1++
                        binding.tvPuntos1.text = puntuacionJugador1.toString()
                    } else if (ganador == 2){
                        puntuacionJugador2++
                        binding.tvPuntos2.text = puntuacionJugador2.toString()
                    }

                    if (ganador == 1 || ganador == 2) {
                        binding.btReiniciar.visibility = View.VISIBLE
                        for (i in 1..9) {
                            val imageView = findViewById<ImageView>(resources.getIdentifier("ivPosicion$i", "id", packageName))
                            imageView.isClickable = false
                        }

                        if (ganador == 1) {
                            Toast.makeText(this, "Ganador: Zombie", Toast.LENGTH_SHORT).show()
                            ganador = 0
                        } else if (ganador == 2) {
                            Toast.makeText(this, "Ganador: Calavera", Toast.LENGTH_SHORT).show()
                            ganador = 0
                        }
                    }
                    return
                }
            }
        }
    }

}