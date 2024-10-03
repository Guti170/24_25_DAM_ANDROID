package com.example.calculadora

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.calculadora.databinding.ActivityMainBinding

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

        binding.tvResto.visibility = View.INVISIBLE
        binding.nbResto.visibility = View.INVISIBLE

        binding.btSumar.setOnClickListener {
            binding.tvResto.visibility = View.INVISIBLE
            binding.nbResto.visibility = View.INVISIBLE
            if (binding.nbNumero1.text.toString().isEmpty() || binding.nbNumero2.text.toString().isEmpty()) {
                Toast.makeText(this, "Tienes que tener los dos numeros introducidos!!", Toast.LENGTH_SHORT).show()
            } else {
                val total = binding.nbNumero1.text.toString().toInt() + binding.nbNumero2.text.toString().toInt()
                binding.nbTotal.setText(total.toString())
            }
        }
        binding.btRestar.setOnClickListener {
            binding.tvResto.visibility = View.INVISIBLE
            binding.nbResto.visibility = View.INVISIBLE
            if (binding.nbNumero1.text.toString().isEmpty() || binding.nbNumero2.text.toString().isEmpty()) {
                Toast.makeText(this, "Tienes que tener los dos numeros introducidos!!", Toast.LENGTH_SHORT).show()
            } else {
                val total = binding.nbNumero1.text.toString().toInt() - binding.nbNumero2.text.toString().toInt()
                binding.nbTotal.setText(total.toString())
            }
        }
        binding.btMultiplicar.setOnClickListener {
            binding.tvResto.visibility = View.INVISIBLE
            binding.nbResto.visibility = View.INVISIBLE
            if (binding.nbNumero1.text.toString().isEmpty() || binding.nbNumero2.text.toString().isEmpty()) {
                Toast.makeText(this, "Tienes que tener los dos numeros introducidos!!", Toast.LENGTH_SHORT).show()
            } else {
                val total = binding.nbNumero1.text.toString().toInt() * binding.nbNumero2.text.toString().toInt()
                binding.nbTotal.setText(total.toString())
            }
        }
        binding.btDividir.setOnClickListener {
            binding.tvResto.visibility = View.VISIBLE
            binding.nbResto.visibility = View.VISIBLE
            if (binding.nbNumero1.text.toString().isEmpty() || binding.nbNumero2.text.toString().isEmpty()) {
                Toast.makeText(this, "Tienes que tener los dos numeros introducidos!!", Toast.LENGTH_SHORT).show()
            } else if (binding.nbNumero2.text.toString().toInt() <= 0) {
                Toast.makeText(this, "No se puede dividir entre 0 y numeros negativos!!", Toast.LENGTH_SHORT).show()
            } else {
                val total = binding.nbNumero1.text.toString().toInt() / binding.nbNumero2.text.toString().toInt()
                binding.nbTotal.setText(total.toString())
                val resto = binding.nbNumero1.text.toString().toInt() % binding.nbNumero2.text.toString().toInt()
                binding.nbResto.setText(resto.toString())
            }
        }
    }
}