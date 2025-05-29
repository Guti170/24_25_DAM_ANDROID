package com.example.appf1insider // O tu paquete de activities

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.bumptech.glide.Glide
import com.example.appf1insider.R
import com.example.appf1insider.model.Piloto
import com.google.android.material.floatingactionbutton.FloatingActionButton // Importar FAB
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class DetallePilotoActivity : AppCompatActivity() {

    private lateinit var nombreTextView: TextView
    private lateinit var descripcionTextView: TextView
    private lateinit var estadisticasTextView: TextView
    private lateinit var pilotoImageView: ImageView
    private lateinit var fabEditPiloto: FloatingActionButton // FAB para editar

    private val storage = Firebase.storage
    private var pilotoRecibido: Piloto? = null // Para mantener el piloto actual que se muestra
    private val TAG = "DetallePilotoActivity"

    companion object {
        const val EXTRA_PILOTO = "extra_piloto"
    }

    // ActivityResultLauncher para manejar el resultado de EditPilotoActivity
    private val editPilotoLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == EditPilotoActivity.RESULT_PILOTO_EDITADO) {
                val pilotoActualizado = result.data?.getParcelableExtra<Piloto>(EditPilotoActivity.EXTRA_PILOTO_ACTUALIZADO)
                if (pilotoActualizado != null) {
                    Log.d(TAG, "Piloto actualizado recibido: ${pilotoActualizado.nombre}")
                    pilotoRecibido = pilotoActualizado // Actualizar el piloto local
                    cargarDatosEnUI(pilotoRecibido) // Recargar la UI con los nuevos datos
                    // Notificar al fragmento anterior que los datos han cambiado
                    val resultIntent = Intent()
                    setResult(Activity.RESULT_OK, resultIntent) // Indica que algo cambió
                } else {
                    Log.d(TAG, "EditPilotoActivity finalizó con RESULT_PILOTO_EDITADO pero sin datos actualizados.")
                }
            } else {
                Log.d(TAG, "EditPilotoActivity finalizó con código: ${result.resultCode}")
            }
        }

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detalle_piloto) // Asegúrate que este es el layout correcto

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        nombreTextView = findViewById(R.id.textViewDetalleNombrePiloto)
        descripcionTextView = findViewById(R.id.textViewDetalleDescripcionPiloto)
        estadisticasTextView = findViewById(R.id.textViewDetalleEstadisticasPiloto)
        pilotoImageView = findViewById(R.id.imageViewDetallePiloto)
        fabEditPiloto = findViewById(R.id.fabEditPiloto) // Inicializar el FAB

        pilotoRecibido = intent.getParcelableExtra(EXTRA_PILOTO)

        if (pilotoRecibido != null) {
            cargarDatosEnUI(pilotoRecibido)
        } else {
            Log.e(TAG, "No se recibió el objeto Piloto.")
            Toast.makeText(this, "Error al cargar detalles del piloto", Toast.LENGTH_LONG).show()
            finish()
        }

        fabEditPiloto.setOnClickListener {
            if (pilotoRecibido != null && pilotoRecibido!!.id.isNotEmpty()) {
                val intent = Intent(this, EditPilotoActivity::class.java).apply {
                    putExtra(EditPilotoActivity.EXTRA_PILOTO_EDIT, pilotoRecibido)
                }
                editPilotoLauncher.launch(intent)
            } else {
                Toast.makeText(this, "No se puede editar el piloto (datos incompletos).", Toast.LENGTH_SHORT).show()
                Log.e(TAG, "Intento de editar piloto con datos nulos o ID vacío.")
            }
        }
    }

    private fun cargarDatosEnUI(piloto: Piloto?) {
        if (piloto == null) {
            Log.e(TAG, "Intento de cargar UI con piloto nulo.")
            return
        }

        supportActionBar?.title = piloto.nombre
        nombreTextView.text = piloto.nombre
        descripcionTextView.text = piloto.descripcion
        estadisticasTextView.text = formatEstadisticas(piloto.estadisticas)

        loadImage(piloto.imagen)
    }

    private fun formatEstadisticas(stats: String): String {
        // Puedes mantener tu lógica de formateo aquí o simplemente devolver el string
        return stats
    }

    private fun loadImage(imageUrl: String) {
        if (imageUrl.startsWith("gs://")) {
            val storageRef = storage.getReferenceFromUrl(imageUrl)
            storageRef.downloadUrl.addOnSuccessListener { uri ->
                Glide.with(this)
                    .load(uri)
                    .placeholder(R.drawable.placeholder_image)
                    .error(R.drawable.error_image)
                    .into(pilotoImageView)
            }.addOnFailureListener { exception ->
                Log.e(TAG, "Error al cargar imagen desde Storage: ${exception.message}")
                pilotoImageView.setImageResource(R.drawable.error_image)
            }
        } else if (imageUrl.isNotEmpty()) {
            Glide.with(this)
                .load(imageUrl)
                .placeholder(R.drawable.placeholder_image)
                .error(R.drawable.error_image)
                .into(pilotoImageView)
        } else {
            Log.d(TAG, "URL de imagen vacía.")
            pilotoImageView.setImageResource(R.drawable.placeholder_image)
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