package com.example.appf1insider

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import calendario.Calendario
import circuitos.Circuitos
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import escuderias.Escuderias
import pilotos.Pilotos

class MenuPrincipal : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var topAppBar: MaterialToolbar
    private lateinit var bottomNavigationView: BottomNavigationView

    private var userEmail: String? = null
    private var userDisplayName: String? = null
    private var isAdminUser: Boolean = false // NUEVO: Para almacenar el estado de admin

    companion object {
        private const val TAG = "MenuPrincipal"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_menu_principal)

        topAppBar = findViewById(R.id.topAppBar)
        bottomNavigationView = findViewById(R.id.bottom_navigation)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        auth = Firebase.auth

        // Recuperar datos del Intent
        userEmail = intent.getStringExtra("USER_EMAIL")
        userDisplayName = intent.getStringExtra("USER_DISPLAY_NAME")
        isAdminUser = intent.getBooleanExtra("IS_ADMIN", false) // Recuperar el estado de admin
        Log.d(TAG, "Usuario: Email='${userEmail}', Nombre='${userDisplayName}', EsAdmin='${isAdminUser}'")


        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)

        topAppBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.action_logout -> {
                    signOut()
                    true
                }
                else -> false
            }
        }
        if (topAppBar.menu.size() == 0) {
            topAppBar.inflateMenu(R.menu.top_app_bar_menu)
        }

        bottomNavigationView.setOnItemSelectedListener { item ->
            var selectedFragment: Fragment? = null
            val args = Bundle().apply { // Crear Bundle para pasar argumentos a los fragmentos
                putBoolean("IS_ADMIN_USER", isAdminUser)
                // Puedes pasar más datos si los fragmentos los necesitan
                putString("USER_EMAIL", userEmail)
            }

            when (item.itemId) {
                R.id.nav_opcion1 -> selectedFragment = Circuitos().apply { arguments = args }
                R.id.nav_opcion2 -> selectedFragment = Pilotos().apply { arguments = args }
                R.id.nav_opcion3 -> selectedFragment = Escuderias().apply { arguments = args }
                R.id.nav_opcion4 -> selectedFragment = Calendario().apply { arguments = args } // Asumiendo que Calendario también podría necesitarlo
                R.id.nav_opcion5 -> {
                    selectedFragment = Perfil.newInstance(
                        email = userEmail,
                        displayName = userDisplayName
                    ).apply {
                        // Si Perfil también necesita saber si es admin:
                        val perfilArgs = arguments ?: Bundle()
                        perfilArgs.putBoolean("IS_ADMIN_USER", isAdminUser)
                        arguments = perfilArgs
                    }
                }
            }
            if (selectedFragment != null) {
                loadFragment(selectedFragment)
            }
            true
        }

        if (savedInstanceState == null) {
            bottomNavigationView.selectedItemId = R.id.nav_opcion1
        }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

    private fun signOut() {
        Log.d(TAG, "signOut: Iniciando proceso de cierre de sesión")
        auth.signOut()
        googleSignInClient.signOut().addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                Log.d(TAG, "Google signOut exitoso.")
            } else {
                Log.w(TAG, "Google signOut falló.", task.exception)
            }
            navigateToLoginScreen()
        }
    }

    private fun navigateToLoginScreen() {
        Log.d(TAG, "Navegando a MainActivity (Pantalla de Login).")
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}