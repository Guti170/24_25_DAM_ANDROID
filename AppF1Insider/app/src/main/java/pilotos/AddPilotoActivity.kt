package pilotos // O tu paquete de activities

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

class AddPilotoActivity : AppCompatActivity() {

    private lateinit var editTextNombrePiloto: EditText
    private lateinit var editTextImagenUrlPiloto: EditText
    private lateinit var editTextEstadisticasPiloto: EditText
    private lateinit var editTextDescripcionPiloto: EditText
    private lateinit var buttonGuardarPiloto: Button
    private lateinit var progressBarAddPiloto: ProgressBar

    private lateinit var db: FirebaseFirestore
    private val TAG = "AddPilotoActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_piloto) // Crearemos este layout a continuación

        supportActionBar?.title = "Añadir Nuevo Piloto"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        db = Firebase.firestore

        editTextNombrePiloto = findViewById(R.id.editTextNombrePiloto)
        editTextImagenUrlPiloto = findViewById(R.id.editTextImagenUrlPiloto)
        editTextEstadisticasPiloto = findViewById(R.id.editTextEstadisticasPiloto)
        editTextDescripcionPiloto = findViewById(R.id.editTextDescripcionPiloto)
        buttonGuardarPiloto = findViewById(R.id.buttonGuardarPiloto)
        progressBarAddPiloto = findViewById(R.id.progressBarAddPiloto)

        buttonGuardarPiloto.setOnClickListener {
            guardarPiloto()
        }
    }

    private fun guardarPiloto() {
        val nombre = editTextNombrePiloto.text.toString().trim()
        val imagenUrl = editTextImagenUrlPiloto.text.toString().trim()
        val estadisticas = editTextEstadisticasPiloto.text.toString().trim()
        val descripcion = editTextDescripcionPiloto.text.toString().trim()

        if (nombre.isEmpty() || descripcion.isEmpty() || imagenUrl.isEmpty() || estadisticas.isEmpty()) {
            Toast.makeText(this, "Todos los campos son obligatorios", Toast.LENGTH_SHORT).show()
            return
        }

        progressBarAddPiloto.isVisible = true
        buttonGuardarPiloto.isEnabled = false

        val nuevoPilotoId = db.collection("piloto").document().id
        val nuevoPiloto = Piloto(
            id = nuevoPilotoId,
            nombre = nombre,
            imagen = imagenUrl,
            estadisticas = estadisticas,
            descripcion = descripcion
        )

        db.collection("piloto").document(nuevoPilotoId)
            .set(nuevoPiloto)
            .addOnSuccessListener {
                Log.d(TAG, "Piloto añadido con ID: $nuevoPilotoId")
                Toast.makeText(this, "Piloto añadido correctamente", Toast.LENGTH_SHORT).show()
                progressBarAddPiloto.isVisible = false
                setResult(Activity.RESULT_OK)
                finish()
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error al añadir piloto", e)
                Toast.makeText(this, "Error al añadir piloto: ${e.message}", Toast.LENGTH_LONG).show()
                progressBarAddPiloto.isVisible = false
                buttonGuardarPiloto.isEnabled = true
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