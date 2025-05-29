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
import androidx.activity.result.contract.ActivityResultContracts // Importante para el nuevo manejo de resultados
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.appf1insider.adapter.PilotoAdapter
import com.example.appf1insider.model.Piloto
// Asegúrate de que la importación de DetallePilotoActivity es correcta
import com.example.appf1insider.DetallePilotoActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton // Importar FAB
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject // Importante para la conversión
import com.google.firebase.ktx.Firebase

class Pilotos : Fragment(), PilotoAdapter.OnPilotoClickListener {

    private lateinit var recyclerView: RecyclerView
    private lateinit var pilotoAdapter: PilotoAdapter
    private lateinit var progressBar: ProgressBar
    private lateinit var fabAddPiloto: FloatingActionButton // Variable para el FAB
    private lateinit var db: FirebaseFirestore
    private val TAG = "PilotosFragment"

    private val listaDePilotos = mutableListOf<Piloto>()

    // Nuevo ActivityResultLauncher para manejar el resultado de AddPilotoActivity
    private val addPilotoLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            // El piloto se añadió correctamente, recargar la lista
            Log.d(TAG, "Piloto añadido, recargando datos...")
            fetchPilotosData() // Vuelve a cargar los datos para mostrar el nuevo piloto
        } else {
            Log.d(TAG, "AddPilotoActivity finalizó sin RESULT_OK (código: ${result.resultCode})")
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
        fabAddPiloto = view.findViewById(R.id.fabAddPiloto) // Inicializar el FAB

        setupRecyclerView()

        fabAddPiloto.setOnClickListener {
            Log.d(TAG, "FAB para añadir piloto presionado.")
            val intent = Intent(activity, AddPilotoActivity::class.java)
            addPilotoLauncher.launch(intent) // Usar el launcher para iniciar la activity
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

        db.collection("piloto") // Nombre de tu colección de pilotos
            .orderBy("nombre") // Opcional: ordenar por nombre o algún otro campo
            .get()
            .addOnSuccessListener { documents ->
                if (documents.isEmpty) {
                    Log.d(TAG, "No se encontraron documentos de pilotos.")
                    Toast.makeText(context, "No hay pilotos para mostrar. ¡Añade uno!", Toast.LENGTH_SHORT).show()
                } else {
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
                }
                progressBar.visibility = View.GONE
                recyclerView.visibility = View.VISIBLE
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error obteniendo documentos de pilotos: ", exception)
                Toast.makeText(context, "Error al cargar los pilotos: ${exception.message}", Toast.LENGTH_LONG).show()
                progressBar.visibility = View.GONE
            }
    }

    override fun onPilotoClick(piloto: Piloto) {
        Log.d(TAG, "Piloto clickeado: ${piloto.nombre}, ID: ${piloto.id}")
        val intent = Intent(activity, DetallePilotoActivity::class.java).apply {
            putExtra(DetallePilotoActivity.EXTRA_PILOTO, piloto)
        }
        startActivity(intent)
    }

    companion object {
        @JvmStatic
        fun newInstance() = Pilotos()
    }
}