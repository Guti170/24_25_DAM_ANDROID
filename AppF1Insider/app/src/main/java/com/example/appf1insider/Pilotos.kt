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
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.appf1insider.adapter.PilotoAdapter
import com.example.appf1insider.model.Piloto
import com.example.appf1insider.DetallePilotoActivity // Asegúrate que la importación es correcta
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class Pilotos : Fragment(), PilotoAdapter.OnPilotoClickListener {

    private lateinit var recyclerView: RecyclerView
    private lateinit var pilotoAdapter: PilotoAdapter
    private lateinit var progressBar: ProgressBar
    private lateinit var fabAddPiloto: FloatingActionButton
    private lateinit var db: FirebaseFirestore
    private val storage = Firebase.storage
    private val TAG = "PilotosFragment"

    private val listaDePilotos = mutableListOf<Piloto>()

    // Launcher para AddPilotoActivity
    private val addPilotoLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            Log.d(TAG, "Piloto añadido, recargando datos...")
            fetchPilotosData()
        } else {
            Log.d(TAG, "AddPilotoActivity finalizó sin RESULT_OK (código: ${result.resultCode})")
        }
    }

    // Launcher para DetallePilotoActivity (NUEVO)
    private val detallePilotoLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            // Si DetallePilotoActivity indica que hubo cambios (porque EditPilotoActivity los hizo),
            // recargamos los datos para reflejar cualquier edición.
            Log.d(TAG, "DetallePilotoActivity indicó cambios (posible edición), recargando pilotos...")
            fetchPilotosData()
        } else {
            Log.d(TAG, "DetallePilotoActivity finalizó sin RESULT_OK (código: ${result.resultCode})")
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
        val view = inflater.inflate(R.layout.fragment_pilotos, container, false)

        recyclerView = view.findViewById(R.id.recyclerViewPilotos)
        progressBar = view.findViewById(R.id.progressBarPilotos)
        fabAddPiloto = view.findViewById(R.id.fabAddPiloto)

        setupRecyclerView()

        fabAddPiloto.setOnClickListener {
            Log.d(TAG, "FAB para añadir piloto presionado.")
            val intent = Intent(activity, AddPilotoActivity::class.java)
            addPilotoLauncher.launch(intent)
        }

        if (listaDePilotos.isEmpty()) {
            fetchPilotosData()
        } else {
            progressBar.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE
        }
        return view
    }

    private fun setupRecyclerView() {
        recyclerView.layoutManager = LinearLayoutManager(context)
        pilotoAdapter = PilotoAdapter(listaDePilotos, this)
        recyclerView.adapter = pilotoAdapter
    }

    private fun fetchPilotosData() {
        progressBar.visibility = View.VISIBLE
        recyclerView.visibility = View.GONE

        db.collection("piloto")
            .orderBy("nombre")
            .get()
            .addOnSuccessListener { documents ->
                if (documents.isEmpty) {
                    Log.d(TAG, "No se encontraron documentos de pilotos.")
                    // Toast.makeText(context, "No hay pilotos para mostrar. ¡Añade uno!", Toast.LENGTH_SHORT).show()
                }
                val tempList = mutableListOf<Piloto>()
                for (document in documents) {
                    try {
                        val piloto = document.toObject<Piloto>().copy(id = document.id)
                        tempList.add(piloto)
                    } catch (e: Exception) {
                        Log.e(TAG, "Error al convertir documento a Piloto: ${document.id}", e)
                    }
                }
                listaDePilotos.clear()
                listaDePilotos.addAll(tempList)
                pilotoAdapter.notifyDataSetChanged()
                progressBar.visibility = View.GONE
                recyclerView.visibility = View.VISIBLE
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error obteniendo documentos de pilotos: ", exception)
                Toast.makeText(context, "Error al cargar los pilotos: ${exception.message}", Toast.LENGTH_LONG).show()
                progressBar.visibility = View.GONE
            }
    }

    // Modificado para usar el nuevo launcher
    override fun onPilotoClick(piloto: Piloto) {
        Log.d(TAG, "Piloto clickeado: ${piloto.nombre}, ID: ${piloto.id}")
        val intent = Intent(activity, DetallePilotoActivity::class.java).apply {
            putExtra(DetallePilotoActivity.EXTRA_PILOTO, piloto)
        }
        detallePilotoLauncher.launch(intent) // Usar el launcher para DetallePilotoActivity
    }

    override fun onPilotoLongClick(piloto: Piloto, position: Int) {
        Log.d(TAG, "Piloto long click: ${piloto.nombre}, ID: ${piloto.id}")
        AlertDialog.Builder(requireContext())
            .setTitle("Confirmar Borrado")
            .setMessage("¿Estás seguro de que quieres borrar al piloto '${piloto.nombre}'?")
            .setPositiveButton("Borrar") { dialog, _ ->
                borrarPiloto(piloto, position)
                dialog.dismiss()
            }
            .setNegativeButton("Cancelar") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun borrarPiloto(piloto: Piloto, position: Int) {
        if (piloto.id.isEmpty()) {
            Toast.makeText(context, "Error: ID del piloto no válido.", Toast.LENGTH_SHORT).show()
            return
        }

        db.collection("piloto").document(piloto.id)
            .delete()
            .addOnSuccessListener {
                Log.d(TAG, "Piloto '${piloto.nombre}' borrado de Firestore.")
                Toast.makeText(context, "Piloto '${piloto.nombre}' borrado.", Toast.LENGTH_SHORT).show()

                if (piloto.imagen.startsWith("gs://")) {
                    val imagenRef = storage.getReferenceFromUrl(piloto.imagen)
                    imagenRef.delete().addOnSuccessListener {
                        Log.d(TAG, "Imagen ${piloto.imagen} borrada de Storage.")
                    }.addOnFailureListener { e ->
                        Log.w(TAG, "Error al borrar imagen ${piloto.imagen} de Storage.", e)
                    }
                }
                pilotoAdapter.removeItem(position)
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error al borrar piloto '${piloto.nombre}' de Firestore.", e)
                Toast.makeText(context, "Error al borrar piloto: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }

    companion object {
        @JvmStatic
        fun newInstance() = Pilotos()
    }
}