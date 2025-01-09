package com.example.fragmentos

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.fragmentos.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
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

        binding.btF1.setOnClickListener {
            val mFragmentManager = supportFragmentManager
            val fragmentTransaction = mFragmentManager.beginTransaction()
            val fragment1 = FragmentoA()

            val mBundle = Bundle()
            mBundle.putString("variable1",binding.edCaja.text.toString())
            fragment1.arguments = mBundle
            fragmentTransaction.replace(R.id.miFragmento, fragment1).commit()
        }

        binding.btF2.setOnClickListener {
            val fragmentoB = FragmentoB()

            val fragmentTransaction =supportFragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.miFragmento, fragmentoB)
            fragmentTransaction.commit()
        }
    }
}