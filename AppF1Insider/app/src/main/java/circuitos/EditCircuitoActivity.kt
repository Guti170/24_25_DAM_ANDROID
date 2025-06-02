package circuitos

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.example.appf1insider.R

class EditCircuitoActivity : AppCompatActivity() {

    private lateinit var editTextNombreCircuito: EditText
    private lateinit var editTextImagenUrlCircuito: EditText
    private lateinit var editTextVideoUrlCircuito: EditText
    private lateinit var editTextDescripcionCircuito: EditText
    private lateinit var buttonGuardarCambiosCircuito: Button
    private lateinit var progressBarEditCircuito: ProgressBar

    private lateinit var db: FirebaseFirestore
    private var circuitoActual: Circuito? = null // Para almacenar el circuito que se está editando
    private var circuitoId: String? = null // Para almacenar el ID del documento
    private val TAG = "EditCircuitoActivity"

    companion object {
        const val EXTRA_CIRCUITO_EDIT = "extra_circuito_para_editar"
        const val RESULT_CIRCUITO_EDITADO = 2001 // Código de resultado personalizado
        const val EXTRA_CIRCUITO_ACTUALIZADO = "extra_circuito_actualizado"
    }

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_circuito) // Crearemos este layout a continuación

        supportActionBar?.title = "Editar Circuito"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        db = Firebase.firestore

        // Usaremos los mismos IDs que en AddCircuitoActivity para reutilizar el layout
        // o puedes crear IDs específicos si copiaste y modificaste el layout.
        // Aquí asumiré que los IDs son los mismos que en activity_add_circuito.xml
        // Si creaste activity_edit_circuito.xml con IDs diferentes, ajústalos.
        editTextNombreCircuito = findViewById(R.id.editTextNombreCircuito)
        editTextImagenUrlCircuito = findViewById(R.id.editTextImagenUrlCircuito)
        editTextVideoUrlCircuito = findViewById(R.id.editTextVideoUrlCircuito)
        editTextDescripcionCircuito = findViewById(R.id.editTextDescripcionCircuito)
        buttonGuardarCambiosCircuito = findViewById(R.id.buttonGuardarCircuito) // Reutilizando el botón
        progressBarEditCircuito = findViewById(R.id.progressBarAddCircuito) // Reutilizando la ProgressBar

        // Cambiar el texto del botón
        buttonGuardarCambiosCircuito.text = "Guardar Cambios"

        // Recuperar el circuito pasado desde DetalleCircuitoActivity
        circuitoActual = intent.getParcelableExtra(EXTRA_CIRCUITO_EDIT)

        if (circuitoActual == null || circuitoActual?.id == null || circuitoActual!!.id.isEmpty()) {
            Toast.makeText(this, "Error: No se pudo cargar el circuito para editar.", Toast.LENGTH_LONG).show()
            Log.e(TAG, "Circuito nulo o ID de circuito no válido recibido.")
            finish() // Cierra la actividad si no hay datos válidos
            return
        }

        circuitoId = circuitoActual!!.id // Guardar el ID
        cargarDatosDelCircuito()

        buttonGuardarCambiosCircuito.setOnClickListener {
            if (validarCampos()) {
                actualizarCircuitoEnFirestore()
            }
        }
    }

    private fun cargarDatosDelCircuito() {
        circuitoActual?.let {
            editTextNombreCircuito.setText(it.nombre)
            editTextImagenUrlCircuito.setText(it.imagen)
            editTextVideoUrlCircuito.setText(it.video)
            editTextDescripcionCircuito.setText(it.descripcion)
        }
    }

    private fun validarCampos(): Boolean {
        if (editTextNombreCircuito.text.toString().trim().isEmpty()) {
            editTextNombreCircuito.error = "El nombre es obligatorio"
            return false
        }
        // Puedes añadir más validaciones si es necesario (ej. formato de URL)
        return true
    }

    private fun actualizarCircuitoEnFirestore() {
        progressBarEditCircuito.visibility = View.VISIBLE
        buttonGuardarCambiosCircuito.isEnabled = false

        val nombreActualizado = editTextNombreCircuito.text.toString().trim()
        val imagenUrlActualizada = editTextImagenUrlCircuito.text.toString().trim()
        val videoUrlActualizada = editTextVideoUrlCircuito.text.toString().trim()
        val descripcionActualizada = editTextDescripcionCircuito.text.toString().trim()

        // Crear un mapa con los campos a actualizar
        // Es importante actualizar solo los campos que pueden cambiar.
        // No actualices el 'id' aquí.
        val circuitoActualizadoMap = mapOf(
            "nombre" to nombreActualizado,
            "imagen" to imagenUrlActualizada,
            "video" to videoUrlActualizada,
            "descripcion" to descripcionActualizada
            // No incluyas 'id' aquí, ya que no debe cambiar.
        )

        if (circuitoId == null) {
            Log.e(TAG, "El ID del circuito es nulo, no se puede actualizar.")
            Toast.makeText(this, "Error al guardar: ID del circuito no encontrado.", Toast.LENGTH_LONG).show()
            progressBarEditCircuito.visibility = View.GONE
            buttonGuardarCambiosCircuito.isEnabled = true
            return
        }

        db.collection("circuito").document(circuitoId!!) // Usar el ID guardado
            .update(circuitoActualizadoMap)
            .addOnSuccessListener {
                Log.d(TAG, "Circuito actualizado correctamente en Firestore con ID: $circuitoId")
                Toast.makeText(this, "Circuito actualizado", Toast.LENGTH_SHORT).show()
                progressBarEditCircuito.visibility = View.GONE

                // Crear el objeto Circuito actualizado para devolverlo
                val circuitoParaDevolver = Circuito(
                    id = circuitoId!!, // Mantener el ID original
                    nombre = nombreActualizado,
                    imagen = imagenUrlActualizada,
                    video = videoUrlActualizada,
                    descripcion = descripcionActualizada
                )

                val resultIntent = Intent()
                resultIntent.putExtra(EXTRA_CIRCUITO_ACTUALIZADO, circuitoParaDevolver)
                setResult(RESULT_CIRCUITO_EDITADO, resultIntent) // Usar el código de resultado personalizado
                finish() // Cierra la actividad de edición
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error al actualizar el circuito en Firestore", e)
                Toast.makeText(this, "Error al actualizar: ${e.message}", Toast.LENGTH_LONG).show()
                progressBarEditCircuito.visibility = View.GONE
                buttonGuardarCambiosCircuito.isEnabled = true
            }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Manejar el clic en el botón de "atrás" de la ActionBar
        if (item.itemId == android.R.id.home) {
            onBackPressedDispatcher.onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}