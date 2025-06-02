package escuderias // O tu paquete de activities

import android.annotation.SuppressLint
import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.core.view.isVisible
import com.example.appf1insider.R
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class AddEscuderiaActivity : AppCompatActivity() {

    private lateinit var editTextNombreEscuderia: EditText
    private lateinit var editTextImagenUrlEscuderia: EditText
    private lateinit var editTextEstadisticasEscuderia: EditText
    private lateinit var editTextDescripcionEscuderia: EditText
    private lateinit var buttonGuardarEscuderia: Button
    private lateinit var progressBarAddEscuderia: ProgressBar

    private lateinit var db: FirebaseFirestore
    private val TAG = "AddEscuderiaActivity"

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_escuderia) // Crearemos este layout a continuación

        supportActionBar?.title = "Añadir Nueva Escudería"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        db = Firebase.firestore

        editTextNombreEscuderia = findViewById(R.id.editTextNombreEscuderia)
        editTextImagenUrlEscuderia = findViewById(R.id.editTextImagenUrlEscuderia)
        editTextEstadisticasEscuderia = findViewById(R.id.editTextEstadisticasEscuderia)
        editTextDescripcionEscuderia = findViewById(R.id.editTextDescripcionEscuderia)
        buttonGuardarEscuderia = findViewById(R.id.buttonGuardarEscuderia)
        progressBarAddEscuderia = findViewById(R.id.progressBarAddEscuderia)

        buttonGuardarEscuderia.setOnClickListener {
            guardarEscuderia()
        }
    }

    private fun guardarEscuderia() {
        val nombre = editTextNombreEscuderia.text.toString().trim()
        val imagenUrl = editTextImagenUrlEscuderia.text.toString().trim()
        val estadisticas = editTextEstadisticasEscuderia.text.toString().trim()
        val descripcion = editTextDescripcionEscuderia.text.toString().trim()

        if (nombre.isEmpty() || descripcion.isEmpty() || imagenUrl.isEmpty() || estadisticas.isEmpty()) {
            Toast.makeText(this, "Todos los campos son obligatorios", Toast.LENGTH_SHORT).show()
            return
        }

        progressBarAddEscuderia.isVisible = true
        buttonGuardarEscuderia.isEnabled = false

        val nuevaEscuderiaId = db.collection("escuderia").document().id // Colección "escuderia"
        val nuevaEscuderia = Escuderia(
            id = nuevaEscuderiaId,
            nombre = nombre,
            imagen = imagenUrl,
            estadisticas = estadisticas,
            descripcion = descripcion
        )

        db.collection("escuderia").document(nuevaEscuderiaId) // Colección "escuderia"
            .set(nuevaEscuderia)
            .addOnSuccessListener {
                Log.d(TAG, "Escudería añadida con ID: $nuevaEscuderiaId")
                Toast.makeText(this, "Escudería añadida correctamente", Toast.LENGTH_SHORT).show()
                progressBarAddEscuderia.isVisible = false
                setResult(Activity.RESULT_OK)
                finish()
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error al añadir escudería", e)
                Toast.makeText(this, "Error al añadir escudería: ${e.message}", Toast.LENGTH_LONG).show()
                progressBarAddEscuderia.isVisible = false
                buttonGuardarEscuderia.isEnabled = true
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