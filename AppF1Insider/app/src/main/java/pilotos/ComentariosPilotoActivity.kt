package pilotos

import android.annotation.SuppressLint
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
import circuitos.ComentarioAdapter
import circuitos.Comentario
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase

class ComentariosPilotoActivity : AppCompatActivity() {

    private lateinit var recyclerViewComentarios: RecyclerView
    private lateinit var editTextNuevoComentario: EditText
    private lateinit var buttonEnviarComentario: Button
    private lateinit var progressBarComentarios: ProgressBar

    private lateinit var comentarioAdapter: ComentarioAdapter
    private val listaDeComentarios = mutableListOf<Comentario>()

    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    private var pilotoId: String? = null
    private var nombrePiloto: String? = null

    companion object {
        const val EXTRA_PILOTO_ID = "extra_piloto_id"
        const val EXTRA_NOMBRE_PILOTO = "extra_nombre_piloto"
        private const val TAG = "ComentariosPilotoAct"
    }

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Asegúrate que el nombre del layout es correcto
        setContentView(R.layout.activity_comentarios_piloto)

        db = Firebase.firestore
        auth = Firebase.auth

        pilotoId = intent.getStringExtra(EXTRA_PILOTO_ID)
        nombrePiloto = intent.getStringExtra(EXTRA_NOMBRE_PILOTO)

        if (pilotoId == null) {
            Log.e(TAG, "No se recibió el ID del piloto.")
            Toast.makeText(this, "Error: ID del piloto no encontrado.", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        supportActionBar?.title = nombrePiloto ?: "Comentarios del Piloto"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Asegúrate que los IDs en el layout activity_comentarios_piloto.xml coinciden
        recyclerViewComentarios = findViewById(R.id.recyclerViewComentariosPiloto)
        editTextNuevoComentario = findViewById(R.id.editTextNuevoComentarioPiloto)
        buttonEnviarComentario = findViewById(R.id.buttonEnviarComentarioPiloto)
        progressBarComentarios = findViewById(R.id.progressBarComentariosPiloto)

        setupRecyclerView()
        cargarComentarios()

        buttonEnviarComentario.setOnClickListener {
            enviarComentario()
        }
    }

    private fun setupRecyclerView() {
        comentarioAdapter = ComentarioAdapter(listaDeComentarios)
        recyclerViewComentarios.layoutManager = LinearLayoutManager(this)
        recyclerViewComentarios.adapter = comentarioAdapter
    }

    private fun cargarComentarios() {
        progressBarComentarios.visibility = View.VISIBLE
        db.collection("pilotos").document(pilotoId!!)
            .collection("comentarios")
            .orderBy("timestamp", Query.Direction.ASCENDING)
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
            return
        }

        if (pilotoId == null) { // Doble check, aunque ya se valida en onCreate
            Toast.makeText(this, "Error: ID del piloto no disponible.", Toast.LENGTH_SHORT).show()
            return
        }

        buttonEnviarComentario.isEnabled = false
        // Considerar un ProgressBar más pequeño cerca del botón si el general está muy arriba
        // progressBarEnvio.visibility = View.VISIBLE

        val nuevoComentario = ComentarioPiloto(
            itemId = pilotoId!!, // Usando el campo genérico itemId
            usuarioEmail = usuarioActual.email ?: "Anónimo",
            texto = textoComentario
            // timestamp se establece por @ServerTimestamp en el modelo
        )

        db.collection("pilotos").document(pilotoId!!)
            .collection("comentarios")
            .add(nuevoComentario)
            .addOnSuccessListener {
                Log.d(TAG, "Comentario añadido con ID: ${it.id}")
                editTextNuevoComentario.text.clear()
                // progressBarEnvio.visibility = View.GONE
                buttonEnviarComentario.isEnabled = true
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error añadiendo comentario", e)
                Toast.makeText(this, "Error al enviar comentario: ${e.message}", Toast.LENGTH_SHORT).show()
                // progressBarEnvio.visibility = View.GONE
                buttonEnviarComentario.isEnabled = true
            }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressedDispatcher.onBackPressed() // Correcto para manejar el botón "Up"
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}