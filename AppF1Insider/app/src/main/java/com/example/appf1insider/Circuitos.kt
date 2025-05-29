package com.example.appf1insider // O el paquete donde tengas tus fragmentos

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog // Importar AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.appf1insider.adapter.CircuitoAdapter
import com.example.appf1insider.model.Circuito
import com.example.appf1insider.DetalleCircuitoActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage // Para borrar imágenes/videos de Storage si es necesario

class Circuitos : Fragment(), CircuitoAdapter.OnItemClickListener {

    private lateinit var recyclerView: RecyclerView
    private lateinit var circuitoAdapter: CircuitoAdapter
    private lateinit var progressBar: ProgressBar
    private lateinit var fabAddCircuito: FloatingActionButton
    private lateinit var db: FirebaseFirestore
    private val storage = Firebase.storage // Referencia a Firebase Storage
    private val TAG = "CircuitosFragment"

    private val listaDeCircuitos = mutableListOf<Circuito>()

    private val addCircuitoLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                Log.d(TAG, "Circuito añadido, recargando datos...")
                fetchCircuitosData()
            } else {
                Log.d(
                    TAG,
                    "AddCircuitoActivity finalizó sin RESULT_OK (código: ${result.resultCode})"
                )
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        db = Firebase.firestore
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_circuitos, container, false)

        recyclerView = view.findViewById(R.id.recyclerViewCircuitos)
        progressBar = view.findViewById(R.id.progressBarCircuitos)
        fabAddCircuito = view.findViewById(R.id.fabAddCircuito) // Inicializar el FAB

        setupRecyclerView()

        fabAddCircuito.setOnClickListener {
            Log.d(TAG, "FAB para añadir circuito presionado.")
            val intent = Intent(activity, AddCircuitoActivity::class.java)
            addCircuitoLauncher.launch(intent)
        }

        if (listaDeCircuitos.isEmpty()) {
            fetchCircuitosData()
        } else {
            progressBar.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE
        }
        return view
    }

    private fun setupRecyclerView() {
        recyclerView.layoutManager = LinearLayoutManager(context)
        circuitoAdapter = CircuitoAdapter(listaDeCircuitos, this)
        recyclerView.adapter = circuitoAdapter
    }

    private fun fetchCircuitosData() {
        progressBar.visibility = View.VISIBLE
        recyclerView.visibility = View.GONE

        db.collection("circuito")
            .orderBy("nombre")
            .get()
            .addOnSuccessListener { documents ->
                if (documents.isEmpty) {
                    Log.d(TAG, "No se encontraron documentos de circuitos.")
                    Toast.makeText(
                        context,
                        "No hay circuitos para mostrar. ¡Añade uno!",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    val tempList = mutableListOf<Circuito>()
                    for (document in documents) {
                        try {
                            val circuito = document.toObject<Circuito>().copy(id = document.id)
                            tempList.add(circuito)
                        } catch (e: Exception) {
                            Log.e(TAG, "Error al convertir documento a Circuito: ${document.id}", e)
                        }
                    }
                    listaDeCircuitos.clear()
                    listaDeCircuitos.addAll(tempList)
                    circuitoAdapter.notifyDataSetChanged()
                }
                progressBar.visibility = View.GONE
                recyclerView.visibility = View.VISIBLE
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error obteniendo documentos de circuitos: ", exception)
                Toast.makeText(
                    context,
                    "Error al cargar los circuitos: ${exception.message}",
                    Toast.LENGTH_LONG
                ).show()
                progressBar.visibility = View.GONE
            }
    }

    override fun onCircuitoClick(circuito: Circuito) {
        Log.d(TAG, "Circuito clickeado: ${circuito.nombre}, ID: ${circuito.id}")
        val intent = Intent(activity, DetalleCircuitoActivity::class.java).apply {
            putExtra(DetalleCircuitoActivity.EXTRA_CIRCUITO, circuito)
        }
        startActivity(intent)
    }

    // Implementación del nuevo método para el Long Click
    override fun onCircuitoLongClick(circuito: Circuito, position: Int) {
        Log.d(TAG, "Circuito long click: ${circuito.nombre}, ID: ${circuito.id}")
        AlertDialog.Builder(requireContext())
            .setTitle("Confirmar Borrado")
            .setMessage("¿Estás seguro de que quieres borrar el circuito '${circuito.nombre}'?")
            .setPositiveButton("Borrar") { dialog, _ ->
                borrarCircuito(circuito, position)
                dialog.dismiss()
            }
            .setNegativeButton("Cancelar") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun borrarCircuito(circuito: Circuito, position: Int) {
        if (circuito.id.isEmpty()) {
            Toast.makeText(context, "Error: ID del circuito no válido.", Toast.LENGTH_SHORT).show()
            return
        }

        // Paso 1: Borrar el documento de Firestore
        db.collection("circuito").document(circuito.id)
            .delete()
            .addOnSuccessListener {
                Log.d(TAG, "Circuito '${circuito.nombre}' borrado de Firestore.")
                Toast.makeText(
                    context,
                    "Circuito '${circuito.nombre}' borrado.",
                    Toast.LENGTH_SHORT
                ).show()

                // Paso 2: (Opcional pero recomendado) Borrar archivos asociados de Firebase Storage
                // Si la URL de la imagen es una URL de Firebase Storage (gs://...)
                if (circuito.imagen.startsWith("gs://")) {
                    val imagenRef = storage.getReferenceFromUrl(circuito.imagen)
                    imagenRef.delete().addOnSuccessListener {
                        Log.d(TAG, "Imagen ${circuito.imagen} borrada de Storage.")
                    }.addOnFailureListener { e ->
                        Log.w(TAG, "Error al borrar imagen ${circuito.imagen} de Storage.", e)
                    }
                }
                // Haz lo mismo para el video si también lo almacenas en Firebase Storage
                if (circuito.video.startsWith("gs://")) {
                    val videoRef = storage.getReferenceFromUrl(circuito.video)
                    videoRef.delete().addOnSuccessListener {
                        Log.d(TAG, "Video ${circuito.video} borrado de Storage.")
                    }.addOnFailureListener { e ->
                        Log.w(TAG, "Error al borrar video ${circuito.video} de Storage.", e)
                    }
                }

                // Paso 3: Actualizar la UI eliminando el ítem del adaptador
                // Es importante hacerlo DESPUÉS de confirmar el borrado en Firestore
                // para mantener la consistencia.
                // listaDeCircuitos.removeAt(position) // Ya no es necesario si el adaptador tiene su propio método
                circuitoAdapter.removeItem(position)


            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error al borrar circuito '${circuito.nombre}' de Firestore.", e)
                Toast.makeText(context, "Error al borrar circuito: ${e.message}", Toast.LENGTH_LONG)
                    .show()
            }
    }

    companion object {
        @JvmStatic
        fun newInstance() = Circuitos()
    }
}