package registrarIniciarUsuario

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import bebidas.ListaBebida
import com.example.proyectoclientedesayuno.R
import com.example.proyectoclientedesayuno.databinding.ActivityHomeBinding
import com.google.android.gms.auth.api.identity.Identity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import comidas.ListaComida
import complementos.ListaComplementos
import conexionSQLite.Conexion
import listaMenus.Menus
import kotlin.text.clear

class Home : AppCompatActivity() {
    companion object {
        private const val CODIGO_SOLICITUD_BEBIDA = 1
        private const val CODIGO_SOLICITUD_COMIDA = 2
        private const val CODIGO_SOLICITUD_COMPLEMENTO = 3
    }
    private var totalEnergetico = 0
    private var nombreComida: String? = null
    private var nombreBebida: String? = null
    private var nombreComplemento: String? = null
    private val nombresBebidas = mutableListOf<String>()
    private val nombresComidas = mutableListOf<String>()
    private val nombresComplementos = mutableListOf<String>()


    lateinit var binding: ActivityHomeBinding
    private lateinit var firebaseauth : FirebaseAuth
    val TAG = "Miguel Angel : Javier Jimenez"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        firebaseauth = FirebaseAuth.getInstance()
        //setContentView(R.layout.activity_home)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Configura el Toolbar
        val toolbar = binding.toolbar2
        setSupportActionBar(toolbar)
        // Establece un título personalizado
        /*supportActionBar?.title = "Home"
        // Agregar un ícono como logo
        supportActionBar?.setLogo(R.drawable.ic_icono_desayuno)
        supportActionBar?.setDisplayUseLogoEnabled(true)
        // Habilita la flecha de retroceso
        supportActionBar?.setDisplayHomeAsUpEnabled(true)*/

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

        binding.txtEmail.text = intent.getStringExtra("email").toString()

        /*binding.btCerrarSesion.setOnClickListener {
            Log.e(TAG, firebaseauth.currentUser.toString())

            firebaseauth.signOut()
            val signInClient = Identity.getSignInClient(this)
            signInClient.signOut()
            Log.e(TAG, "Cerrada sesión completamente")
            finish()
        }*/

        binding.btConfirmar.setOnClickListener {
            var totalEnergetico = 0
            totalEnergetico += binding.txvResultado1.text.toString().toIntOrNull() ?: 0
            totalEnergetico += binding.txvResultado2.text.toString().toIntOrNull() ?: 0
            totalEnergetico += binding.txvResultado3.text.toString().toIntOrNull() ?: 0
            binding.txvResultadoTotal.text = totalEnergetico.toString()

            val email = binding.txtEmail.text.toString()
            val menu = hashMapOf(
                "email" to email,
                "bebidas" to nombresBebidas,
                "comidas" to nombresComidas,
                "complementos" to nombresComplementos,
                "totalEnergetico" to totalEnergetico,
                "fechaCreacion" to FieldValue.serverTimestamp()
            )

            val db = FirebaseFirestore.getInstance()
            db.collection("menus")
                .document(email)
                .set(menu)
                .addOnSuccessListener {
                    Toast.makeText(this, "Menu guardado correctamente", Toast.LENGTH_SHORT).show()
                    nombresBebidas.clear()
                    nombresComidas.clear()
                    nombresComplementos.clear()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Error al insertar el menú en Firebase: ${e.message}", Toast.LENGTH_SHORT).show()
                    Log.e(TAG, "Error al insertar el menú en Firebase: ${e.message}")
                }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            val calorias = data?.getIntExtra("calorias", 0) ?: 0
            val proteinas = data?.getIntExtra("proteinas", 0) ?: 0
            val nombreImagen = data?.getStringExtra("nombreImagen")
            val nombreBebida = data?.getStringExtra("nombreBebida")
            val nombreComida = data?.getStringExtra("nombreComida")
            val nombreComplemento = data?.getStringExtra("nombreComplemento")

            when (requestCode) {
                CODIGO_SOLICITUD_BEBIDA -> {
                    binding.txvResultado1.text = (calorias + proteinas).toString()
                    if (nombreImagen != null) {
                        val imageResourceId = resources.getIdentifier(nombreImagen, "drawable", packageName)
                        binding.ivBebida.setImageResource(imageResourceId)
                        if (nombreBebida != null) {
                            nombresBebidas.clear()
                            nombresBebidas.add(nombreBebida)
                        }
                    }
                }
                CODIGO_SOLICITUD_COMIDA -> {
                    binding.txvResultado2.text = (calorias + proteinas).toString()
                    if (nombreImagen != null) {
                        val imageResourceId = resources.getIdentifier(nombreImagen, "drawable", packageName)
                        binding.ivComida.setImageResource(imageResourceId)
                        if (nombreComida != null) {
                            nombresComidas.clear()
                            nombresComidas.add(nombreComida)
                        }
                    }
                }
                CODIGO_SOLICITUD_COMPLEMENTO -> {
                    binding.txvResultado3.text = (calorias + proteinas).toString()
                    if (nombreImagen != null) {
                        val imageResourceId = resources.getIdentifier(nombreImagen, "drawable", packageName)
                        binding.ivComplemento.setImageResource(imageResourceId)
                        if (nombreComplemento != null) {
                            nombresComplementos.clear()
                            nombresComplementos.add(nombreComplemento)
                        }
                    }
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_home, menu) // Reemplaza R.menu.menu con el ID de tu archivo de menú
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_camera -> {
                // Acción para "Cerrar Sesión"
                Log.e(TAG, firebaseauth.currentUser.toString())

                firebaseauth.signOut()
                val signInClient = Identity.getSignInClient(this)
                signInClient.signOut()
                Log.e(TAG, "Cerrada sesión completamente")
                finish()
                true
            }
            R.id.action_help -> {
                // Acción para "Editar Usuario"
                // Aquí puedes iniciar la Activity para editar el usuario
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}