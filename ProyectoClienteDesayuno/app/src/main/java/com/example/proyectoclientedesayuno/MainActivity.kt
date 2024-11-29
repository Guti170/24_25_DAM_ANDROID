package com.example.proyectoclientedesayuno

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.proyectoclientedesayuno.databinding.ActivityMainBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import registrarIniciarUsuario.Home

class MainActivity : AppCompatActivity() {
    private lateinit var firebaseauth : FirebaseAuth
    private lateinit var binding: ActivityMainBinding
    private lateinit var googleSignInClient: GoogleSignInClient

    val TAG = "Miguel Angel"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        firebaseauth = FirebaseAuth.getInstance()
        //setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        //------------------------------ Autenticación con email y password ------------------------------------
        binding.btRegistrar.setOnClickListener {
            val email = binding.edEmail.text.toString()
            val password = binding.edPass.text.toString()
            if (email.isNotEmpty() && password.isNotEmpty()){
                firebaseauth.createUserWithEmailAndPassword(email,password).addOnCompleteListener {
                    if (it.isSuccessful){
                        irHome(it.result?.user?.email?:"")
                    } else {
                        showAlert("Error registrando al usuario. Comprueba el formato del email o que la contraseña sea segura")
                    }
                }.addOnFailureListener{
                    Toast.makeText(this, "La conexión ha fallado", Toast.LENGTH_SHORT).show()
                }
            }
            else {
                showAlert("Rellene los campos")
            }
        }

        binding.btLogin.setOnClickListener {
            val email = binding.edEmail.text.toString()
            val password = binding.edPass.text.toString()

            if (email.isEmpty() || password.isEmpty()) {
                showAlert("Rellene correctamente los campos de email y contraseña")
            } else {
                firebaseauth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            irHome(it.result?.user?.email ?: "")
                            binding.edEmail.text.clear()
                            binding.edPass.text.clear()
                        } else {
                            showAlert()
                        }
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Conexión no establecida", Toast.LENGTH_SHORT).show()
                    }
            }
        }

        //------------------Variables para realizar el Login con Google -------------------
        firebaseauth.signOut()

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.cliente_desayuno))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this,gso)

        binding.btGoogle.setOnClickListener {
            loginEnGoogle()
        }

    }

    //************************************** Funciones auxiliares registro y login con externos **************************************

    private fun showAlert(msg:String = "Se ha producido un error autenticando al usuario"){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Error")
        builder.setMessage(msg)
        builder.setPositiveButton("Aceptar",null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }
    private fun irHome(email:String){
        Log.e(TAG,"Valores: ${email}")
        val homeIntent = Intent(this, Home::class.java).apply {
            putExtra("email",email)
        }
        startActivity(homeIntent)
    }
    //***********************************************************************************************************

    //************************************** Funciones auxiliares registro y login con Google **************************************
    private fun loginEnGoogle(){
        //este método es nuestro.
        val signInClient = googleSignInClient.signInIntent
        launcherVentanaGoogle.launch(signInClient)
        //milauncherVentanaGoogle.launch(signInClient)
    }

    private val launcherVentanaGoogle =  registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result ->
        if (result.resultCode == Activity.RESULT_OK){
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            manejarResultados(task)
        }
    }

    private fun manejarResultados(task: Task<GoogleSignInAccount>) {
        if (task.isSuccessful){
            val account : GoogleSignInAccount? = task.result
            if (account != null){
                actualizarUI(account)
            }
        }
        else {
            Toast.makeText(this,task.exception.toString(), Toast.LENGTH_SHORT).show()
        }
    }

    private fun actualizarUI(account: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        firebaseauth.signInWithCredential(credential).addOnCompleteListener {
            if (it.isSuccessful){
                irHome(account.email.toString())
            }
            else {
                Toast.makeText(this,it.exception.toString(), Toast.LENGTH_SHORT).show()
            }
        }
    }

}