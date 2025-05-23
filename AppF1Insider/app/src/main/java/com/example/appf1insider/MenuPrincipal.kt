package com.example.appf1insider

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment // Importar Fragment
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomnavigation.BottomNavigationView // Importar BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MenuPrincipal : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var topAppBar: MaterialToolbar
    private lateinit var bottomNavigationView: BottomNavigationView // Añadido

    companion object {
        private const val TAG = "MenuPrincipal"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_menu_principal)

        topAppBar = findViewById(R.id.topAppBar)
        bottomNavigationView = findViewById(R.id.bottom_navigation) // Inicializar BottomNavigationView

        // Aplicar WindowInsets al contenedor principal (main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Inicialización de Firebase Auth
        auth = Firebase.auth

        // Configurar GoogleSignInClient
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)

        // Configurar la Toolbar
        topAppBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.action_logout -> {
                    signOut()
                    true
                }
                else -> false
            }
        }
        topAppBar.inflateMenu(R.menu.top_app_bar_menu)

        // Configurar BottomNavigationView
        bottomNavigationView.setOnItemSelectedListener { item ->
            var selectedFragment: Fragment? = null
            when (item.itemId) {
                R.id.nav_opcion1 -> selectedFragment = Circuitos()
                R.id.nav_opcion2 -> selectedFragment = Pilotos()
                R.id.nav_opcion3 -> selectedFragment = Escuderias()
                R.id.nav_opcion4 -> selectedFragment = Calendario()
                R.id.nav_opcion5 -> selectedFragment = Perfil()
            }
            if (selectedFragment != null) {
                loadFragment(selectedFragment)
            }
            true
        }

        // Cargar el fragmento inicial (opcional, pero común)
        if (savedInstanceState == null) { // Solo si no se está recreando la actividad
            bottomNavigationView.selectedItemId = R.id.nav_opcion1 // Selecciona el primer ítem por defecto
            loadFragment(Circuitos()) // Carga el fragmento correspondiente
        }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

    private fun signOut() {
        Log.d(TAG, "signOut: Initiating sign out process")
        auth.signOut()
        Log.d(TAG, "Firebase signOut successful.")
        googleSignInClient.signOut().addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                Log.d(TAG, "Google signOut successful.")
            } else {
                Log.w(TAG, "Google signOut failed.", task.exception)
            }
            navigateToLoginScreen()
        }
    }

    private fun navigateToLoginScreen() {
        Log.d(TAG, "Navigating to MainActivity (Login Screen).")
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}