package escuderias

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem // Asegúrate que este import está
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

class ComentariosEscuderiaActivity : AppCompatActivity() {

    private lateinit var recyclerViewComentarios: RecyclerView
    private lateinit var editTextNuevoComentario: EditText
    private lateinit var buttonEnviarComentario: Button
    private lateinit var progressBarComentarios: ProgressBar

    private lateinit var comentarioAdapter: ComentarioAdapter
    private val listaDeComentarios = mutableListOf<Comentario>()

    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    private var escuderiaId: String? = null
    private var nombreEscuderia: String? = null

    companion object {
        const val EXTRA_ESCUDERIA_ID = "extra_escuderia_id"
        const val EXTRA_NOMBRE_ESCUDERIA = "extra_nombre_escuderia"
        private const val TAG = "ComentariosEscuderiaAct"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Asegúrate que el nombre del layout es correcto
        setContentView(R.layout.activity_comentarios_escuderia)

        db = Firebase.firestore
        auth = Firebase.auth

        escuderiaId = intent.getStringExtra(EXTRA_ESCUDERIA_ID)
        nombreEscuderia = intent.getStringExtra(EXTRA_NOMBRE_ESCUDERIA)

        if (escuderiaId == null) {
            Log.e(TAG, "No se recibió el ID de la escudería.")
            Toast.makeText(this, "Error: ID de la escudería no encontrado.", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        supportActionBar?.title = nombreEscuderia ?: "Comentarios de la Escudería"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Asegúrate que los IDs en el layout activity_comentarios_escuderia.xml coinciden
        recyclerViewComentarios = findViewById(R.id.recyclerViewComentariosEscuderia)
        editTextNuevoComentario = findViewById(R.id.editTextNuevoComentarioEscuderia)
        buttonEnviarComentario = findViewById(R.id.buttonEnviarComentarioEscuderia)
        progressBarComentarios = findViewById(R.id.progressBarComentariosEscuderia)

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
        db.collection("escuderias").document(escuderiaId!!)
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

        if (escuderiaId == null) { // Doble check
            Toast.makeText(this, "Error: ID de la escudería no disponible.", Toast.LENGTH_SHORT).show()
            return
        }

        buttonEnviarComentario.isEnabled = false

        val nuevoComentario = ComentarioEscuderia(
            itemId = escuderiaId!!,
            usuarioEmail = usuarioActual.email ?: "Anónimo",
            texto = textoComentario
        )

        db.collection("escuderias").document(escuderiaId!!)
            .collection("comentarios")
            .add(nuevoComentario)
            .addOnSuccessListener {
                Log.d(TAG, "Comentario añadido con ID: ${it.id}")
                editTextNuevoComentario.text.clear()
                buttonEnviarComentario.isEnabled = true
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error añadiendo comentario", e)
                Toast.makeText(this, "Error al enviar comentario: ${e.message}", Toast.LENGTH_SHORT).show()
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