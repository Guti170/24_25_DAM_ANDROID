package com.example.appf1insider

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MenuPrincipal : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var topAppBar: MaterialToolbar
    private lateinit var bottomNavigationView: BottomNavigationView

    // Variables para almacenar la información del usuario
    private var userEmail: String? = null
    private var userDisplayName: String? = null

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

        // Recuperar el email y nombre del Intent
        userEmail = intent.getStringExtra("USER_EMAIL")
        userDisplayName = intent.getStringExtra("USER_DISPLAY_NAME")
        Log.d(TAG, "Usuario recibido: Email='${userEmail}', Nombre='${userDisplayName}'")


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
        // Asegúrate de que el menú se infla solo una vez o que no cause problemas si se llama múltiples veces
        // Si topAppBar.inflateMenu ya está en el XML o se infla de otra manera, esta línea podría ser redundante
        // o necesitar una condición para no inflarlo repetidamente.
        // Por ahora, lo dejamos como estaba en tu código original.
        if (topAppBar.menu.size() == 0) { // Solo inflar si no tiene menú aún
            topAppBar.inflateMenu(R.menu.top_app_bar_menu)
        }


        bottomNavigationView.setOnItemSelectedListener { item ->
            var selectedFragment: Fragment? = null
            when (item.itemId) {
                R.id.nav_opcion1 -> selectedFragment = Circuitos() // Asumo que Circuitos es tu fragmento
                R.id.nav_opcion2 -> selectedFragment = Pilotos()
                R.id.nav_opcion3 -> selectedFragment = Escuderias()
                R.id.nav_opcion4 -> selectedFragment = Calendario()
                R.id.nav_opcion5 -> {
                    // Crear instancia de Perfil y pasar argumentos
                    selectedFragment = Perfil.newInstance(
                        email = userEmail,
                        displayName = userDisplayName
                    )
                }
            }
            if (selectedFragment != null) {
                loadFragment(selectedFragment)
            }
            true
        }

        if (savedInstanceState == null) {
            // Cargar el fragmento inicial. Asegúrate que R.id.nav_opcion1 existe en tu menú.
            // Y que Circuitos() es el fragmento que quieres cargar por defecto.
            bottomNavigationView.selectedItemId = R.id.nav_opcion1
            // loadFragment(Circuitos()) // Esto se manejará por el listener si selectedItemId se establece
        }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment) // Asegúrate que R.id.fragment_container es el ID de tu FrameLayout o FragmentContainerView
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