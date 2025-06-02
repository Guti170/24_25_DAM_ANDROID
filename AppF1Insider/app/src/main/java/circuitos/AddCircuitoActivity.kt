package circuitos

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

class AddCircuitoActivity : AppCompatActivity() {

    private lateinit var editTextNombre: EditText
    private lateinit var editTextDescripcion: EditText
    private lateinit var editTextImagenUrl: EditText
    private lateinit var editTextVideoUrl: EditText
    private lateinit var buttonGuardarCircuito: Button
    private lateinit var progressBarAddCircuito: ProgressBar

    private lateinit var db: FirebaseFirestore
    private val TAG = "AddCircuitoActivity"

    companion object {
        const val REQUEST_CODE_ADD_CIRCUITO = 101 // Código para identificar el resultado
    }

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_circuito) // Crearemos este layout a continuación

        supportActionBar?.title = "Añadir Nuevo Circuito"
        supportActionBar?.setDisplayHomeAsUpEnabled(true) // Botón de atrás

        db = Firebase.firestore

        editTextNombre = findViewById(R.id.editTextNombreCircuito)
        editTextDescripcion = findViewById(R.id.editTextDescripcionCircuito)
        editTextImagenUrl = findViewById(R.id.editTextImagenUrlCircuito)
        editTextVideoUrl = findViewById(R.id.editTextVideoUrlCircuito)
        buttonGuardarCircuito = findViewById(R.id.buttonGuardarCircuito)
        progressBarAddCircuito = findViewById(R.id.progressBarAddCircuito)

        buttonGuardarCircuito.setOnClickListener {
            guardarCircuito()
        }
    }

    private fun guardarCircuito() {
        val nombre = editTextNombre.text.toString().trim()
        val descripcion = editTextDescripcion.text.toString().trim()
        val imagenUrl = editTextImagenUrl.text.toString().trim() // Puede ser gs:// o https://
        val videoUrl = editTextVideoUrl.text.toString().trim()   // URL de YouTube, por ejemplo

        if (nombre.isEmpty() || descripcion.isEmpty()) {
            Toast.makeText(this, "Nombre y descripción son obligatorios", Toast.LENGTH_SHORT).show()
            return
        }

        // Mostrar ProgressBar y deshabilitar botón
        progressBarAddCircuito.isVisible = true
        buttonGuardarCircuito.isEnabled = false

        // Firestore generará un ID único para el nuevo documento si no especificas uno.
        // Lo obtenemos primero para poder guardarlo en nuestro objeto Circuito.
        val nuevoCircuitoId = db.collection("circuito").document().id
        val nuevoCircuito = Circuito(
            id = nuevoCircuitoId, // Usamos el ID generado por Firestore
            nombre = nombre,
            descripcion = descripcion,
            imagen = imagenUrl,
            video = videoUrl
        )

        db.collection("circuito").document(nuevoCircuitoId) // Especificamos el ID para el nuevo documento
            .set(nuevoCircuito)
            .addOnSuccessListener {
                Log.d(TAG, "Circuito añadido con ID: $nuevoCircuitoId")
                Toast.makeText(this, "Circuito añadido correctamente", Toast.LENGTH_SHORT).show()
                progressBarAddCircuito.isVisible = false
                // Indicar que la operación fue exitosa y cerrar la activity
                setResult(Activity.RESULT_OK) // Para que el fragmento sepa que debe recargar
                finish()
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error al añadir circuito", e)
                Toast.makeText(this, "Error al añadir circuito: ${e.message}", Toast.LENGTH_LONG).show()
                progressBarAddCircuito.isVisible = false
                buttonGuardarCircuito.isEnabled = true // Habilitar el botón de nuevo en caso de error
            }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Manejar el clic en el botón de atrás de la ActionBar
        if (item.itemId == android.R.id.home) {
            onBackPressedDispatcher.onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}