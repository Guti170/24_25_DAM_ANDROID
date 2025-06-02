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
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
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

        auth = Firebase.auth
        db = Firebase.firestore

        emailEditText = findViewById(R.id.emailEditText)
        passwordEditText = findViewById(R.id.passwordEditText)
        loginButton = findViewById(R.id.loginButton)
        registerButton = findViewById(R.id.registerButton)
        googleSignInButton = findViewById(R.id.googleSignInButton)

        configureGoogleSignIn()

        loginButton.setOnClickListener { signInWithEmailPassword() }
        registerButton.setOnClickListener { registerWithEmailPassword() }
        googleSignInButton.setOnClickListener { signInWithGoogle() }

        googleSignInLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
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
        auth.currentUser?.let { checkIfAdminAndNavigate(it) }
    }

    private fun configureGoogleSignIn() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)
    }

    private fun signInWithEmailPassword() {
        val email = emailEditText.text.toString().trim()
        val password = passwordEditText.text.toString().trim()
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Introduce correo y contraseña.", Toast.LENGTH_SHORT).show()
            return
        }
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                task.result?.user?.let { checkIfAdminAndNavigate(it) }
            } else {
                Log.w(TAG, "signInWithEmail:failure", task.exception)
                Toast.makeText(baseContext, "Error: ${task.exception?.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun registerWithEmailPassword() {
        val email = emailEditText.text.toString().trim()
        val password = passwordEditText.text.toString().trim()
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Introduce correo y contraseña para registrarte.", Toast.LENGTH_SHORT).show()
            return
        }
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(baseContext, "Registro exitoso. Inicia sesión.", Toast.LENGTH_SHORT).show()
            } else {
                Log.w(TAG, "createUserWithEmail:failure", task.exception)
                Toast.makeText(baseContext, "Error al registrar: ${task.exception?.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun signInWithGoogle() {
        googleSignInLauncher.launch(googleSignInClient.signInIntent)
    }

    private fun handleGoogleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)
            firebaseAuthWithGoogle(account.idToken!!)
        } catch (e: ApiException) {
            Log.w(TAG, "Google sign in failed", e)
            Toast.makeText(this, "Error Google: ${e.message} (${e.statusCode})", Toast.LENGTH_LONG).show()
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                task.result?.user?.let { checkIfAdminAndNavigate(it) }
            } else {
                Log.w(TAG, "signInWithCredential_Google:failure", task.exception)
                Toast.makeText(baseContext, "Error Google Auth: ${task.exception?.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun checkIfAdminAndNavigate(user: FirebaseUser) {
        val adminDocIds = listOf("1", "2") // IDs de los documentos de admin
        var isAdmin = false
        var checksCompleted = 0

        for (docId in adminDocIds) {
            db.collection("admin").document(docId).get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        val codigoAdmin = document.getString("codigo")
                        if (codigoAdmin == user.uid) {
                            isAdmin = true
                        }
                    }
                    checksCompleted++
                    if (checksCompleted == adminDocIds.size) {
                        navigateToMenuPrincipal(user, isAdmin)
                    }
                }
                .addOnFailureListener { exception ->
                    Log.w(TAG, "Error al obtener documento admin $docId", exception)
                    checksCompleted++
                    if (checksCompleted == adminDocIds.size) {
                        navigateToMenuPrincipal(user, isAdmin) // Navegar incluso si hay error, con isAdmin=false
                    }
                }
        }
    }

    private fun navigateToMenuPrincipal(user: FirebaseUser, isAdmin: Boolean) {
        Log.d(TAG, "Navigating to MenuPrincipal. User: ${user.email}, IsAdmin: $isAdmin")
        val intent = Intent(this, MenuPrincipal::class.java).apply {
            putExtra("USER_EMAIL", user.email)
            putExtra("USER_DISPLAY_NAME", user.displayName)
            putExtra("IS_ADMIN", isAdmin) // Pasar el estado de administrador
        }
        startActivity(intent)
        finish()
    }
}