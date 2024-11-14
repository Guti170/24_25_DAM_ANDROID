package com.example.firebase

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
import com.example.firebase.databinding.ActivityMainBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

class MainActivity : AppCompatActivity() {
    // en esta fariable llevaremos el objeto que nos conecta con los Users de Firebase.
    private lateinit var firebaseauth : FirebaseAuth
    // en esta variable llevamos el binding con el layout.
    private lateinit var binding: ActivityMainBinding
    // esta variable me conecta con google y me permite hacer login con google.
    private lateinit var googleSignInClient: GoogleSignInClient

    val TAG = "Miguel Angel"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // inicializamos el objeto firebaseauth que nos sirve para la autentificación de tipo genérico.
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
                        irHome(it.result?.user?.email?:"",Proveedor.BASIC)  //Esto de los interrogantes es por si está vacío el email, que enviaría una cadena vacía.
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
            if (email.isNotEmpty() && password.isNotEmpty()){
                firebaseauth.signInWithEmailAndPassword(email,password).addOnCompleteListener {
                    if (it.isSuccessful){
                        irHome(it.result?.user?.email?:"",Proveedor.BASIC)  //Esto de los interrogantes es por si está vacío el email.
                    } else {
                        showAlert()
                    }
                }.addOnFailureListener{
                    Toast.makeText(this, "Conexión no establecida", Toast.LENGTH_SHORT).show()
                }
            }
            else {
                showAlert("Rellene los campos")
            }
        }

        //------------------Variables para realizar el Login con Google -------------------
        //se hace un signOut por si había algún login antes.
        firebaseauth.signOut()
        //clearGooglePlayServicesCache()
        //esta variable me conecta con  google y me permite hacer el login.
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.your_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this,gso)

        //Listener del botón para hacer login con Google.
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
    private fun irHome(email:String, provider:Proveedor, nombre:String = "Usuario"){
        Log.e(TAG,"Valores: ${email}, ${provider}, ${nombre}")
        val homeIntent = Intent(this, Home::class.java).apply {
            putExtra("email",email)
            putExtra("provider",provider.name)
            putExtra("nombre",nombre)
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


    //con este launcher, abro la ventana que me lleva a la validacion de Google.
    private val launcherVentanaGoogle =  registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result ->
        //si la ventana va bien, se accede a las propiedades que trae la propia ventana q llamamos y recogemos en result.
        if (result.resultCode == Activity.RESULT_OK){
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            manejarResultados(task)
        }
    }

    //es como una muñeca rusa, vamos desgranando, de la ventana a task y de task a los datos concretos que me da google.
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

    // Está diseñado para manejar las actualizaciones de la interfaz de usuario después de un inicio de sesión exitoso
    // con Google. Recibe un objeto GoogleSignInAccount como entrada, que contiene información sobre el usuario que
    // inició sesión.
    private fun actualizarUI(account: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        //pido un token, y con ese token, si es correcto obtengo la info.
        firebaseauth.signInWithCredential(credential).addOnCompleteListener {
            if (it.isSuccessful){
                //hacer account. y ver otras propiedades interesantes.
                irHome(account.email.toString(),Proveedor.GOOGLE, account.displayName.toString())
            }
            else {
                Toast.makeText(this,it.exception.toString(), Toast.LENGTH_SHORT).show()
            }
        }
    }

}