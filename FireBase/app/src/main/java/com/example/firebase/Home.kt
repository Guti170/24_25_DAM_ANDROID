package com.example.firebase

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.firebase.databinding.ActivityHomeBinding
import com.google.android.gms.auth.api.identity.Identity
import com.google.firebase.auth.FirebaseAuth

class Home : AppCompatActivity() {
    lateinit var binding: ActivityHomeBinding
    private lateinit var firebaseauth : FirebaseAuth
    val TAG = "Miguel Angel"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //setContentView(R.layout.activity_home)
        // Para la autenticación, de cualquier tipo.
        firebaseauth = FirebaseAuth.getInstance()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        //Recuperamos los datos del login.
        binding.txtEmail.text = intent.getStringExtra("email").toString()
        binding.txtProveedor.text = intent.getStringExtra("provider").toString()
        binding.txtNombre.text = intent.getStringExtra("nombre").toString()

        binding.btVolver.setOnClickListener {
            // Log.e(TAG, firebaseauth.currentUser.toString())
            firebaseauth.signOut()
            finish()
        }

        binding.btCerrarSesion.setOnClickListener {
            Log.e(TAG, firebaseauth.currentUser.toString())

            //Con las siguientes líneas cierras sesión y el usuario tiene que volver a logearse en la ventana anterior.
            firebaseauth.signOut()
            val signInClient = Identity.getSignInClient(this)
            signInClient.signOut()
            Log.e(TAG,"Cerrada sesión completamente")
            finish()
            // Si descomentas el siguiente bloque, olvidas al usuario, limpiando cualquier referencia persistente, es decir lo elimina de Firebase
            //comprobadlo en Firebase, como ha desaparecido.

            /*firebaseauth.currentUser?.delete()?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    firebaseauth.signOut()
                    Log.e(TAG,"Cerrada sesión completamente")
                } else {
                    Log.e(TAG,"Hubo algún error al cerrar la sesión")
                }
            }
            */
        }

    }
}