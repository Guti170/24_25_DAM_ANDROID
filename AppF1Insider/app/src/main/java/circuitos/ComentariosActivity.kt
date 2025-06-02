package circuitos // O tu paquete

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.appf1insider.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase

class ComentariosActivity : AppCompatActivity() {

    private lateinit var recyclerViewComentarios: RecyclerView
    private lateinit var editTextNuevoComentario: EditText
    private lateinit var buttonEnviarComentario: Button
    private lateinit var progressBarComentarios: ProgressBar

    private lateinit var comentarioAdapter: ComentarioAdapter
    private val listaDeComentarios = mutableListOf<Comentario>()

    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    private var circuitoId: String? = null
    private var nombreCircuito: String? = null // Opcional, para el título

    companion object {
        const val EXTRA_CIRCUITO_ID = "extra_circuito_id"
        const val EXTRA_NOMBRE_CIRCUITO = "extra_nombre_circuito" // Opcional
        private const val TAG = "ComentariosActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_comentarios) // Crearemos este layout

        db = Firebase.firestore
        auth = Firebase.auth

        circuitoId = intent.getStringExtra(EXTRA_CIRCUITO_ID)
        nombreCircuito = intent.getStringExtra(EXTRA_NOMBRE_CIRCUITO)

        if (circuitoId == null) {
            Log.e(TAG, "No se recibió el ID del circuito.")
            Toast.makeText(this, "Error: ID del circuito no encontrado.", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        supportActionBar?.title = nombreCircuito ?: "Comentarios"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        recyclerViewComentarios = findViewById(R.id.recyclerViewComentarios)
        editTextNuevoComentario = findViewById(R.id.editTextNuevoComentario)
        buttonEnviarComentario = findViewById(R.id.buttonEnviarComentario)
        progressBarComentarios = findViewById(R.id.progressBarComentarios)

        setupRecyclerView()
        cargarComentarios()

        buttonEnviarComentario.setOnClickListener {
            enviarComentario()
        }
    }

    private fun setupRecyclerView() {
        comentarioAdapter = ComentarioAdapter(listaDeComentarios) // Crearemos este adaptador
        recyclerViewComentarios.layoutManager = LinearLayoutManager(this).apply {
            // reverseLayout = true // Si quieres los más nuevos arriba y el input abajo
            // stackFromEnd = true
        }
        recyclerViewComentarios.adapter = comentarioAdapter
    }

    private fun cargarComentarios() {
        progressBarComentarios.visibility = View.VISIBLE
        db.collection("circuitos").document(circuitoId!!)
            .collection("comentarios")
            .orderBy("timestamp", Query.Direction.ASCENDING) // Los más antiguos primero
            .addSnapshotListener { snapshots, e ->
                progressBarComentarios.visibility = View.GONE
                if (e != null) {
                    Log.w(TAG, "Error escuchando comentarios.", e)
                    Toast.makeText(this, "Error al cargar comentarios: ${e.message}", Toast.LENGTH_SHORT).show()
                    return@addSnapshotListener
                }

                val tempList = mutableListOf<Comentario>()
                for (doc in snapshots!!) {
                    try {
                        val comentario = doc.toObject<Comentario>().copy(id = doc.id)
                        tempList.add(comentario)
                    } catch (ex: Exception) {
                        Log.e(TAG, "Error convirtiendo comentario", ex)
                    }
                }
                listaDeComentarios.clear()
                listaDeComentarios.addAll(tempList)
                comentarioAdapter.notifyDataSetChanged()
                // Scroll al último comentario añadido si la lista no está vacía
                if (listaDeComentarios.isNotEmpty()) {
                    recyclerViewComentarios.smoothScrollToPosition(listaDeComentarios.size - 1)
                }
            }
    }

    private fun enviarComentario() {
        val textoComentario = editTextNuevoComentario.text.toString().trim()
        val usuarioActual = auth.currentUser

        if (textoComentario.isEmpty()) {
            editTextNuevoComentario.error = "El comentario no puede estar vacío"
            return
        }

        if (usuarioActual == null) {
            Toast.makeText(this, "Debes iniciar sesión para comentar.", Toast.LENGTH_SHORT).show()
            // Opcional: Redirigir a la pantalla de login
            // startActivity(Intent(this, LoginActivity::class.java))
            return
        }

        if (circuitoId == null) {
            Toast.makeText(this, "Error: ID del circuito no disponible.", Toast.LENGTH_SHORT).show()
            return
        }

        buttonEnviarComentario.isEnabled = false
        // Podrías mostrar un ProgressBar más pequeño aquí si el general está muy lejos
        // progressBarEnvioComentario.visibility = View.VISIBLE

        val nuevoComentario = Comentario(
            circuitoId = circuitoId!!,
            usuarioEmail = usuarioActual.email ?: "Anónimo", // Usar email o un placeholder
            texto = textoComentario
            // El timestamp se añadirá por el servidor con @ServerTimestamp
        )

        db.collection("circuitos").document(circuitoId!!)
            .collection("comentarios")
            .add(nuevoComentario)
            .addOnSuccessListener {
                Log.d(TAG, "Comentario añadido con ID: ${it.id}")
                editTextNuevoComentario.text.clear()
                // El SnapshotListener en cargarComentarios() actualizará la UI automáticamente.
                // No es necesario llamar a notifyDataSetChanged() aquí directamente si usas SnapshotListener.
                // progressBarEnvioComentario.visibility = View.GONE
                buttonEnviarComentario.isEnabled = true
                // Opcional: Ocultar teclado
                // val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                // imm.hideSoftInputFromWindow(editTextNuevoComentario.windowToken, 0)
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error añadiendo comentario", e)
                Toast.makeText(this, "Error al enviar comentario: ${e.message}", Toast.LENGTH_SHORT).show()
                // progressBarEnvioComentario.visibility = View.GONE
                buttonEnviarComentario.isEnabled = true
            }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressedDispatcher.onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}