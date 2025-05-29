package com.example.appf1insider

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
import com.example.appf1insider.adapter.EscuderiaAdapter
import com.example.appf1insider.model.Escuderia
import com.example.appf1insider.DetalleEscuderiaActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage // Para borrar imágenes de Storage si es necesario

class Escuderias : Fragment(), EscuderiaAdapter.OnEscuderiaClickListener { // La interfaz ya está implementada

    private lateinit var recyclerView: RecyclerView
    private lateinit var escuderiaAdapter: EscuderiaAdapter
    private lateinit var progressBar: ProgressBar
    private lateinit var fabAddEscuderia: FloatingActionButton
    private lateinit var db: FirebaseFirestore
    private val storage = Firebase.storage // Referencia a Firebase Storage
    private val TAG = "EscuderiasFragment"

    private val listaDeEscuderias = mutableListOf<Escuderia>()

    private val addEscuderiaLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            Log.d(TAG, "Escudería añadida, recargando datos...")
            fetchEscuderiasData()
        } else {
            Log.d(TAG, "AddEscuderiaActivity finalizó sin RESULT_OK (código: ${result.resultCode})")
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
        val view = inflater.inflate(R.layout.fragment_escuderias, container, false) // Completar aquí

        recyclerView = view.findViewById(R.id.recyclerViewEscuderias)
        progressBar = view.findViewById(R.id.progressBarEscuderias)
        fabAddEscuderia = view.findViewById(R.id.fabAddEscuderia)

        setupRecyclerView()

        fabAddEscuderia.setOnClickListener {
            Log.d(TAG, "FAB para añadir escudería presionado.")
            val intent = Intent(activity, AddEscuderiaActivity::class.java)
            addEscuderiaLauncher.launch(intent)
        }

        if (listaDeEscuderias.isEmpty()) {
            fetchEscuderiasData()
        } else {
            progressBar.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE
        }
        return view
    }

    private fun setupRecyclerView() {
        recyclerView.layoutManager = LinearLayoutManager(context)
        escuderiaAdapter = EscuderiaAdapter(listaDeEscuderias, this)
        recyclerView.adapter = escuderiaAdapter
    }

    private fun fetchEscuderiasData() {
        progressBar.visibility = View.VISIBLE
        recyclerView.visibility = View.GONE

        db.collection("escuderia")
            .orderBy("nombre")
            .get()
            .addOnSuccessListener { documents ->
                if (documents.isEmpty) {
                    Log.d(TAG, "No se encontraron documentos de escuderías.")
                    Toast.makeText(context, "No hay escuderías para mostrar. ¡Añade una!", Toast.LENGTH_SHORT).show()
                } else {
                    val tempList = mutableListOf<Escuderia>()
                    for (document in documents) {
                        try {
                            val escuderia = document.toObject<Escuderia>().copy(id = document.id)
                            tempList.add(escuderia)
                        } catch (e: Exception) {
                            Log.e(TAG, "Error al convertir documento a Escuderia: ${document.id}", e)
                        }
                    }
                    listaDeEscuderias.clear()
                    listaDeEscuderias.addAll(tempList)
                    escuderiaAdapter.notifyDataSetChanged() // O escuderiaAdapter.updateData(listaDeEscuderias)
                }
                progressBar.visibility = View.GONE
                recyclerView.visibility = View.VISIBLE
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error obteniendo documentos de escuderías: ", exception)
                Toast.makeText(context, "Error al cargar las escuderías: ${exception.message}", Toast.LENGTH_LONG).show()
                progressBar.visibility = View.GONE
            }
    }

    override fun onEscuderiaClick(escuderia: Escuderia) {
        Log.d(TAG, "Escudería clickeada: ${escuderia.nombre}, ID: ${escuderia.id}")
        val intent = Intent(activity, DetalleEscuderiaActivity::class.java).apply {
            putExtra(DetalleEscuderiaActivity.EXTRA_ESCUDERIA, escuderia)
        }
        startActivity(intent)
    }

    // Implementación del nuevo método para el Long Click
    override fun onEscuderiaLongClick(escuderia: Escuderia, position: Int) {
        Log.d(TAG, "Escudería long click: ${escuderia.nombre}, ID: ${escuderia.id}")
        AlertDialog.Builder(requireContext())
            .setTitle("Confirmar Borrado")
            .setMessage("¿Estás seguro de que quieres borrar la escudería '${escuderia.nombre}'?")
            .setPositiveButton("Borrar") { dialog, _ ->
                borrarEscuderia(escuderia, position)
                dialog.dismiss()
            }
            .setNegativeButton("Cancelar") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun borrarEscuderia(escuderia: Escuderia, position: Int) {
        if (escuderia.id.isEmpty()) {
            Toast.makeText(context, "Error: ID de la escudería no válido.", Toast.LENGTH_SHORT).show()
            return
        }

        // Paso 1: Borrar el documento de Firestore
        db.collection("escuderia").document(escuderia.id)
            .delete()
            .addOnSuccessListener {
                Log.d(TAG, "Escudería '${escuderia.nombre}' borrada de Firestore.")
                Toast.makeText(context, "Escudería '${escuderia.nombre}' borrada.", Toast.LENGTH_SHORT).show()

                // Paso 2: (Opcional pero recomendado) Borrar imagen asociada de Firebase Storage
                if (escuderia.imagen.startsWith("gs://")) {
                    val imagenRef = storage.getReferenceFromUrl(escuderia.imagen)
                    imagenRef.delete().addOnSuccessListener {
                        Log.d(TAG, "Imagen ${escuderia.imagen} borrada de Storage.")
                    }.addOnFailureListener { e ->
                        Log.w(TAG, "Error al borrar imagen ${escuderia.imagen} de Storage.", e)
                    }
                }
                // (No hay video para escuderías en tu modelo actual)

                // Paso 3: Actualizar la UI eliminando el ítem del adaptador
                escuderiaAdapter.removeItem(position)

            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error al borrar escudería '${escuderia.nombre}' de Firestore.", e)
                Toast.makeText(context, "Error al borrar escudería: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }

    companion object {
        @JvmStatic
        fun newInstance() = Escuderias()
    }
}