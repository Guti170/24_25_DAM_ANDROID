package registrarIniciarUsuario

import android.app.Activity
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
import complementos.ListaComplementos

class Home : AppCompatActivity() {
    private var SECOND_ACTIVITY_REQUEST_CODE = 0
    companion object {
        private const val CODIGO_SOLICITUD_BEBIDA = 1
        private const val CODIGO_SOLICITUD_COMIDA = 2
        private const val CODIGO_SOLICITUD_COMPLEMENTO = 3
    }
    private var totalEnergetico = 0

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
            val intent = Intent(this, ListaBebida::class.java)
            startActivityForResult(intent, CODIGO_SOLICITUD_BEBIDA)
        }

        binding.ivComida.setOnClickListener {
            val intent = Intent(this, ListaComida::class.java)
            startActivityForResult(intent, CODIGO_SOLICITUD_COMIDA)
        }

        binding.ivComplemento.setOnClickListener {
            val intent = Intent(this, ListaComplementos::class.java)
            startActivityForResult(intent, CODIGO_SOLICITUD_COMPLEMENTO)
        }

        //Recuperamos los datos del login.
        binding.txtEmail.text = intent.getStringExtra("email").toString()

        /*binding.btVolver.setOnClickListener {
            firebaseauth.signOut()
            finish()
        }*/

        binding.btCerrarSesion.setOnClickListener {
            Log.e(TAG, firebaseauth.currentUser.toString())

            //Con las siguientes líneas cierras sesión y el usuario tiene que volver a logearse en la ventana anterior.
            firebaseauth.signOut()
            val signInClient = Identity.getSignInClient(this)
            signInClient.signOut()
            Log.e(TAG, "Cerrada sesión completamente")
            finish()
        }

        binding.btConfirmar.setOnClickListener {
            totalEnergetico += binding.txvResultado1.text.toString().toInt()
            totalEnergetico += binding.txvResultado2.text.toString().toInt()
            totalEnergetico += binding.txvResultado3.text.toString().toInt()
            binding.txvResultadoTotal.text = totalEnergetico.toString()
            totalEnergetico = 0
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            val calorias = data?.getIntExtra("calorias", 0) ?: 0
            val proteinas = data?.getIntExtra("proteinas", 0) ?: 0
            val nombreImagen = data?.getStringExtra("nombreImagen")

            when (requestCode) {
                CODIGO_SOLICITUD_BEBIDA -> {
                    binding.txvResultado1.text = (calorias + proteinas).toString()
                    if (nombreImagen != null) {
                        val imageResourceId = resources.getIdentifier(nombreImagen, "drawable", packageName)
                        binding.ivBebida.setImageResource(imageResourceId)
                    }
                }
                CODIGO_SOLICITUD_COMIDA -> {
                    binding.txvResultado2.text = (calorias + proteinas).toString()
                    if (nombreImagen != null) {
                        val imageResourceId = resources.getIdentifier(nombreImagen, "drawable", packageName)
                        binding.ivComida.setImageResource(imageResourceId)
                    }
                }
                CODIGO_SOLICITUD_COMPLEMENTO -> {
                    binding.txvResultado3.text = (calorias + proteinas).toString()
                    if (nombreImagen != null) {
                        val imageResourceId = resources.getIdentifier(nombreImagen, "drawable", packageName)
                        binding.ivComplemento.setImageResource(imageResourceId)
                    }
                }
            }
        }
    }
}