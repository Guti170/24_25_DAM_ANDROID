package com.example.appf1insider

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
// import android.widget.TextView // Descomenta si vas a usar un TextView para el email
// import android.widget.Toast // Descomenta si necesitas mostrar Toasts
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MenuPrincipal : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient // Para cerrar sesión de Google
    private lateinit var logoutButton: Button
    // private lateinit var userEmailTextView: TextView // Ejemplo, descomenta si lo usas

    companion object {
        private const val TAG = "MenuPrincipal"
    }

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge() // Mantenido de tu código original
        setContentView(R.layout.activity_menu_principal) // Asegúrate que este es tu layout

        // Aplicar WindowInsets (mantenido de tu código original)
        // Asegúrate que R.id.main existe en tu layout activity_menu_principal.xml
        // y es el View raíz o el contenedor principal.
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Inicialización de Firebase Auth
        auth = Firebase.auth

        // Configurar GoogleSignInClient para poder hacer signOut de Google también
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id)) // Asegúrate que esto existe y es accesible
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)

        // Inicializar Vistas (asegúrate que logoutButton existe en tu layout)
        logoutButton = findViewById(R.id.logoutButton)
        // userEmailTextView = findViewById(R.id.userEmailTextView) // Ejemplo, descomenta si lo usas

        // Recuperar y mostrar el email (opcional)
        // val userEmail = intent.getStringExtra("USER_EMAIL")
        // if (userEmail != null) {
        //     userEmailTextView.text = "Bienvenido, $userEmail"
        //     Log.d(TAG, "User email received: $userEmail")
        // } else {
        //     userEmailTextView.text = "Bienvenido"
        //     Log.d(TAG, "No user email received in Intent.")
        // }

        // Configurar OnClickListener para el botón de logout
        logoutButton.setOnClickListener {
            signOut()
        }
    }

    private fun signOut() {
        Log.d(TAG, "signOut: Initiating sign out process")

        // 1. Cerrar sesión en Firebase
        auth.signOut()
        Log.d(TAG, "Firebase signOut successful.")

        // 2. Cerrar sesión en Google Sign-In
        googleSignInClient.signOut().addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                Log.d(TAG, "Google signOut successful.")
            } else {
                Log.w(TAG, "Google signOut failed.", task.exception)
            }
            // Continuar a la pantalla de login independientemente del resultado de Google signOut
            navigateToLoginScreen()
        }
    }

    private fun navigateToLoginScreen() {
        Log.d(TAG, "Navigating to MainActivity (Login Screen).")
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish() // Cierra MenuPrincipal
    }
}