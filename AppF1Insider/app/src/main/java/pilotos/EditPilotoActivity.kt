package pilotos

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

class EditPilotoActivity : AppCompatActivity() {

    private lateinit var editTextNombrePiloto: EditText
    private lateinit var editTextImagenUrlPiloto: EditText
    private lateinit var editTextEstadisticasPiloto: EditText // Campo para estadísticas
    private lateinit var editTextDescripcionPiloto: EditText
    private lateinit var buttonGuardarCambiosPiloto: Button
    private lateinit var progressBarEditPiloto: ProgressBar

    private lateinit var db: FirebaseFirestore
    private var pilotoRecibido: Piloto? = null // Para almacenar el piloto que se está editando
    private var pilotoId: String? = null // Para almacenar el ID del documento
    private val TAG = "EditPilotoActivity"

    companion object {
        const val EXTRA_PILOTO_EDIT = "extra_piloto_para_editar"
        const val RESULT_PILOTO_EDITADO = 2002 // Código de resultado personalizado
        const val EXTRA_PILOTO_ACTUALIZADO = "extra_piloto_actualizado"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_piloto) // Crearemos este layout a continuación

        supportActionBar?.title = "Editar Piloto"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        db = Firebase.firestore

        // Asumir que los IDs son los mismos que en activity_add_piloto.xml
        // o una copia con los mismos IDs.
        editTextNombrePiloto = findViewById(R.id.editTextNombrePiloto)
        editTextImagenUrlPiloto = findViewById(R.id.editTextImagenUrlPiloto)
        editTextEstadisticasPiloto = findViewById(R.id.editTextEstadisticasPiloto) // Asegúrate que este ID existe en tu layout
        editTextDescripcionPiloto = findViewById(R.id.editTextDescripcionPiloto)
        buttonGuardarCambiosPiloto = findViewById(R.id.buttonGuardarPiloto) // Reutilizando el botón
        progressBarEditPiloto = findViewById(R.id.progressBarAddPiloto) // Reutilizando la ProgressBar

        buttonGuardarCambiosPiloto.text = "Guardar Cambios"

        pilotoRecibido = intent.getParcelableExtra(EXTRA_PILOTO_EDIT)

        if (pilotoRecibido == null || pilotoRecibido?.id == null || pilotoRecibido!!.id.isEmpty()) {
            Toast.makeText(this, "Error: No se pudo cargar el piloto para editar.", Toast.LENGTH_LONG).show()
            Log.e(TAG, "Piloto nulo o ID de piloto no válido recibido.")
            finish()
            return
        }

        pilotoId = pilotoRecibido!!.id
        cargarDatosDelPiloto()

        buttonGuardarCambiosPiloto.setOnClickListener {
            if (validarCampos()) {
                actualizarPilotoEnFirestore()
            }
        }
    }

    private fun cargarDatosDelPiloto() {
        pilotoRecibido?.let {
            editTextNombrePiloto.setText(it.nombre)
            editTextImagenUrlPiloto.setText(it.imagen)
            editTextEstadisticasPiloto.setText(it.estadisticas)
            editTextDescripcionPiloto.setText(it.descripcion)
        }
    }

    private fun validarCampos(): Boolean {
        if (editTextNombrePiloto.text.toString().trim().isEmpty()) {
            editTextNombrePiloto.error = "El nombre es obligatorio"
            return false
        }
        if (editTextEstadisticasPiloto.text.toString().trim().isEmpty()) {
            editTextEstadisticasPiloto.error = "Las estadísticas son obligatorias"
            return false
        }
        // Puedes añadir más validaciones
        return true
    }

    private fun actualizarPilotoEnFirestore() {
        progressBarEditPiloto.visibility = View.VISIBLE
        buttonGuardarCambiosPiloto.isEnabled = false

        val nombreActualizado = editTextNombrePiloto.text.toString().trim()
        val imagenUrlActualizada = editTextImagenUrlPiloto.text.toString().trim()
        val estadisticasActualizadas = editTextEstadisticasPiloto.text.toString().trim()
        val descripcionActualizada = editTextDescripcionPiloto.text.toString().trim()

        val pilotoActualizadoMap = mapOf(
            "nombre" to nombreActualizado,
            "imagen" to imagenUrlActualizada,
            "estadisticas" to estadisticasActualizadas,
            "descripcion" to descripcionActualizada
        )

        if (pilotoId == null) {
            Log.e(TAG, "El ID del piloto es nulo, no se puede actualizar.")
            Toast.makeText(this, "Error al guardar: ID del piloto no encontrado.", Toast.LENGTH_LONG).show()
            progressBarEditPiloto.visibility = View.GONE
            buttonGuardarCambiosPiloto.isEnabled = true
            return
        }

        db.collection("piloto").document(pilotoId!!)
            .update(pilotoActualizadoMap)
            .addOnSuccessListener {
                Log.d(TAG, "Piloto actualizado correctamente en Firestore con ID: $pilotoId")
                Toast.makeText(this, "Piloto actualizado", Toast.LENGTH_SHORT).show()
                progressBarEditPiloto.visibility = View.GONE

                val pilotoParaDevolver = Piloto(
                    id = pilotoId!!,
                    nombre = nombreActualizado,
                    imagen = imagenUrlActualizada,
                    estadisticas = estadisticasActualizadas,
                    descripcion = descripcionActualizada
                )

                val resultIntent = Intent()
                resultIntent.putExtra(EXTRA_PILOTO_ACTUALIZADO, pilotoParaDevolver)
                setResult(RESULT_PILOTO_EDITADO, resultIntent)
                finish()
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error al actualizar el piloto en Firestore", e)
                Toast.makeText(this, "Error al actualizar: ${e.message}", Toast.LENGTH_LONG).show()
                progressBarEditPiloto.visibility = View.GONE
                buttonGuardarCambiosPiloto.isEnabled = true
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