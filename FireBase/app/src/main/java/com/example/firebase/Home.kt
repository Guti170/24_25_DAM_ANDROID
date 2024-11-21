package com.example.firebase

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.firebase.databinding.ActivityHomeBinding
import com.google.android.gms.auth.api.identity.Identity
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.firestore

class Home : AppCompatActivity() {
    /**************************DATOS DE PRUEBA ******************************************************************/
    var miArray:ArrayList<User> = ArrayList()  //Este será el arrayList que se usará para el adapter del RecyclerView o de la ListView.
    //Valores fake.
    val nombres = listOf("Ragnar","Ivar","Lagertha","Floki")
    val apellidos = listOf("Lothbrok","Sin huesos","Piel de Hierro","Semi diosa")
    val edades = listOf(18, 23, 45, 67, 34, 47, 41)

    lateinit var binding: ActivityHomeBinding
    private lateinit var firebaseauth : FirebaseAuth
    val TAG = "Miguel Angel"
    val db = Firebase.firestore

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

        binding.btGuardar.setOnClickListener {
            //Se guardarán en modo HashMap (clave, valor).
            var user = hashMapOf(
                "provider" to binding.txtProveedor.text,
                "email" to binding.txtEmail.text.toString(),
                "name" to binding.edNombre.text.toString(),
                "age" to binding.edEdad.text.toString(),
                "roles" to arrayListOf(1, 2, 3),
                "timestamp" to FieldValue.serverTimestamp()
            )

            // Si no existe el documento lo crea, si existe lo remplaza.
            db.collection("users")
                .document(user.get("email").toString()) //Será la clave del documento.
                .set(user).addOnSuccessListener {
                    Toast.makeText(this, "Almacenado", Toast.LENGTH_SHORT).show()
                }.addOnFailureListener{
                    Toast.makeText(this, "Ha ocurrido un error", Toast.LENGTH_SHORT).show()
                }


            //Otra forma
            //Si no existe el documento lo crea, si existe añade otro. Las id serán asignadas automáticamente.
//            db.collection("users")
//                .add(user)
//                .addOnSuccessListener {
//                    Toast.makeText(this, "Almacenado", Toast.LENGTH_SHORT).show()
//                    Log.e(TAG, "Documento añadido con ID: ${it.id}")
//                }
//                .addOnFailureListener { e ->
//                    Log.w(TAG, "Error añadiendo documento", e.cause)
//                }
            // this.crearUnoAutomaticoConIndiceAleatorio()
//            this.crearUnoAutomaticoConIndiceNoAleatorio()
//            this.crearCamposArray()

        }

    }
}