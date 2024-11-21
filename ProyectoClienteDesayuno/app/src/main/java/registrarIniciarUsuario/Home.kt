package registrarIniciarUsuario

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import bebidas.ListaBebida
import com.example.proyectoclientedesayuno.R
import com.example.proyectoclientedesayuno.databinding.ActivityHomeBinding
import com.google.android.gms.auth.api.identity.Identity
import com.google.firebase.auth.FirebaseAuth
import comidas.ListaComida

class Home : AppCompatActivity() {
    private var SECOND_ACTIVITY_REQUEST_CODE = 0

    lateinit var binding: ActivityHomeBinding
    private lateinit var firebaseauth : FirebaseAuth
    val TAG = "Miguel Angel"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // Para la autenticación, de cualquier tipo.
        firebaseauth = FirebaseAuth.getInstance()
        //setContentView(R.layout.activity_home)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.ivBebida.setOnClickListener {
            var inte : Intent = Intent(this, ListaBebida::class.java)
            startActivity(inte)
        }

        binding.ivComida.setOnClickListener {
            var inte : Intent = Intent(this, ListaComida::class.java)
            startActivity(inte)
        }

        //Recuperamos los datos del login.
        binding.txtEmail.text = intent.getStringExtra("email").toString()

        binding.btVolver.setOnClickListener {
            firebaseauth.signOut()
            finish()
        }

        binding.btCerrarSesion.setOnClickListener {
            Log.e(TAG, firebaseauth.currentUser.toString())

            //Con las siguientes líneas cierras sesión y el usuario tiene que volver a logearse en la ventana anterior.
            firebaseauth.signOut()
            val signInClient = Identity.getSignInClient(this)
            signInClient.signOut()
            Log.e(TAG, "Cerrada sesión completamente")
            finish()
        }
    }
}