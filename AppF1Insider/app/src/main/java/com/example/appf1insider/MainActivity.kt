package com.example.appf1insider

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var googleSignInLauncher: ActivityResultLauncher<Intent>

    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var loginButton: Button
    private lateinit var registerButton: Button
    private lateinit var googleSignInButton: Button

    companion object {
        private const val TAG = "MainActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Inicializar Firebase Auth
        auth = Firebase.auth

        // Inicializar Vistas
        emailEditText = findViewById(R.id.emailEditText)
        passwordEditText = findViewById(R.id.passwordEditText)
        loginButton = findViewById(R.id.loginButton)
        registerButton = findViewById(R.id.registerButton)
        googleSignInButton = findViewById(R.id.googleSignInButton)

        // Configurar Google Sign In
        configureGoogleSignIn()

        // Configurar Listeners de los Botones
        loginButton.setOnClickListener {
            signInWithEmailPassword()
        }

        registerButton.setOnClickListener {
            registerWithEmailPassword()
        }

        googleSignInButton.setOnClickListener {
            signInWithGoogle()
        }

        // ActivityResultLauncher para el inicio de sesión con Google
        googleSignInLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == RESULT_OK) {
                val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                handleGoogleSignInResult(task)
            } else {
                Toast.makeText(this, "Inicio de sesión con Google cancelado", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        // Comprobar si el usuario ya ha iniciado sesión (opcional, para saltar esta pantalla)
        val currentUser = auth.currentUser
        if (currentUser != null) {
            navigateToMenuPrincipal()
        }
    }

    private fun configureGoogleSignIn() {
        // Configurar Google Sign In
        // Reemplaza R.string.default_web_client_id con tu ID de cliente web de Google
        // Este ID se encuentra en tu archivo google-services.json o en Firebase Console -> Project Settings
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id)) // Asegúrate de tener este string
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)
    }

    private fun signInWithEmailPassword() {
        val email = emailEditText.text.toString().trim()
        val password = passwordEditText.text.toString().trim()

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Por favor, introduce correo y contraseña.", Toast.LENGTH_SHORT).show()
            return
        }

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "signInWithEmail:success")
                    navigateToMenuPrincipal()
                } else {
                    Log.w(TAG, "signInWithEmail:failure", task.exception)
                    Toast.makeText(baseContext, "Error al iniciar sesión: ${task.exception?.message}",
                        Toast.LENGTH_LONG).show()
                }
            }
    }

    private fun registerWithEmailPassword() {
        val email = emailEditText.text.toString().trim()
        val password = passwordEditText.text.toString().trim()

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Por favor, introduce correo y contraseña para registrarte.", Toast.LENGTH_SHORT).show()
            return
        }
        // Puedes añadir validación de contraseña aquí (longitud, etc.)

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "createUserWithEmail:success")
                    Toast.makeText(baseContext, "Registro exitoso. Ahora puedes iniciar sesión.",
                        Toast.LENGTH_SHORT).show()
                    // Opcionalmente, iniciar sesión directamente o pedir al usuario que inicie sesión
                    // navigateToMenuPrincipal() // Si quieres iniciar sesión automáticamente tras el registro
                } else {
                    Log.w(TAG, "createUserWithEmail:failure", task.exception)
                    Toast.makeText(baseContext, "Error al registrarse: ${task.exception?.message}",
                        Toast.LENGTH_LONG).show()
                }
            }
    }

    private fun signInWithGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        googleSignInLauncher.launch(signInIntent)
    }

    private fun handleGoogleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)
            Log.d(TAG, "firebaseAuthWithGoogle:" + account.id)
            firebaseAuthWithGoogle(account.idToken!!)
        } catch (e: ApiException) {
            Log.w(TAG, "Google sign in failed", e)
            Toast.makeText(this, "Error de inicio de sesión con Google: ${e.message} (${e.statusCode})", Toast.LENGTH_LONG).show()
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "signInWithCredential_Google:success")
                    navigateToMenuPrincipal() // Llamada a la nueva función
                }
                else {
                    Log.w(TAG, "signInWithCredential_Google:failure", task.exception)
                    Toast.makeText(baseContext, "Error al autenticar con Google: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                }
            }
    }

    /**
     * Navega a la Activity MenuPrincipal.
     * Cierra la MainActivity actual para que el usuario no pueda volver a ella
     * con el botón "Atrás" después de iniciar sesión.
     */
    private fun navigateToMenuPrincipal() {
        Log.d(TAG, "Navigating to MenuPrincipal Activity.")
        // Asegúrate de que 'MenuPrincipal' es el nombre correcto de tu Activity
        // y que está declarada en tu AndroidManifest.xml.
        val intent = Intent(this, MenuPrincipal::class.java)

        // Opcional: Si necesitas pasar datos a MenuPrincipal, puedes hacerlo aquí.
        // Por ejemplo, el email del usuario actual:
        val currentUser = auth.currentUser
        currentUser?.email?.let { email ->
            intent.putExtra("USER_EMAIL", email)
        }

        startActivity(intent)
        finish() // Cierra MainActivity
    }

} // Cierre de la clase MainActivity