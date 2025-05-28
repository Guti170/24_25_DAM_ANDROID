package com.example.appf1insider // O el paquete donde tengas tus fragmentos

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.appf1insider.adapter.PilotoAdapter
import com.example.appf1insider.model.Piloto
// Asegúrate que la importación de DetallePilotoActivity es correcta
import com.example.appf1insider.DetallePilotoActivity // O la ruta correcta a tu Activity
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject // Importante para la conversión
import com.google.firebase.ktx.Firebase

class Pilotos : Fragment(), PilotoAdapter.OnPilotoClickListener { // Implementa la interfaz

    private lateinit var recyclerView: RecyclerView
    private lateinit var pilotoAdapter: PilotoAdapter
    private lateinit var progressBar: ProgressBar
    private lateinit var db: FirebaseFirestore
    private val TAG = "PilotosFragment"

    private val listaDePilotos = mutableListOf<Piloto>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        db = Firebase.firestore
        // Considera usar un ViewModel aquí también para una mejor gestión del estado y ciclo de vida.
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_pilotos, container, false)

        recyclerView = view.findViewById(R.id.recyclerViewPilotos)
        progressBar = view.findViewById(R.id.progressBarPilotos)

        setupRecyclerView()

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
        // Pasa 'this' (el Fragment que implementa OnPilotoClickListener) al adaptador
        pilotoAdapter = PilotoAdapter(listaDePilotos, this)
        recyclerView.adapter = pilotoAdapter
    }

    private fun fetchPilotosData() {
        progressBar.visibility = View.VISIBLE
        recyclerView.visibility = View.GONE // Ocultar RecyclerView mientras se carga

        db.collection("piloto") // Nombre de tu colección de pilotos en Firestore
            .get()
            .addOnSuccessListener { documents ->
                if (documents.isEmpty) {
                    Log.d(TAG, "No se encontraron documentos de pilotos.")
                    Toast.makeText(context, "No hay pilotos para mostrar", Toast.LENGTH_SHORT).show()
                } else {
                    val tempList = mutableListOf<Piloto>()
                    for (document in documents) {
                        try {
                            // Convertir el documento de Firestore al objeto Piloto
                            // Asignar el ID del documento al campo 'id' del objeto Piloto
                            val piloto = document.toObject<Piloto>().copy(id = document.id)
                            tempList.add(piloto)
                            Log.d(TAG, "Piloto cargado: ${piloto.nombre}, ID: ${piloto.id}, Imagen: ${piloto.imagen}")
                        } catch (e: Exception) {
                            Log.e(TAG, "Error al convertir documento a Piloto: ${document.id}", e)
                        }
                    }
                    // Actualizar la lista original y notificar al adaptador
                    listaDePilotos.clear()
                    listaDePilotos.addAll(tempList)
                    pilotoAdapter.notifyDataSetChanged() // O usar DiffUtil
                }
                progressBar.visibility = View.GONE
                recyclerView.visibility = View.VISIBLE // Mostrar RecyclerView con los datos
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error obteniendo documentos de pilotos: ", exception)
                Toast.makeText(context, "Error al cargar los pilotos: ${exception.message}", Toast.LENGTH_LONG).show()
                progressBar.visibility = View.GONE
                // Considera mostrar un mensaje de error o un botón de reintentar
            }
    }

    // Implementación del método de la interfaz OnPilotoClickListener
    override fun onPilotoClick(piloto: Piloto) {
        Log.d(TAG, "Piloto clickeado: ${piloto.nombre}, ID: ${piloto.id}")
        val intent = Intent(activity, DetallePilotoActivity::class.java).apply {
            // Pasar el objeto Piloto completo a la Activity de detalles
            // DetallePilotoActivity.EXTRA_PILOTO es la constante que definimos en esa Activity
            putExtra(DetallePilotoActivity.EXTRA_PILOTO, piloto)
        }
        startActivity(intent)
    }

    // No es necesario un companion object newInstance() si no pasas argumentos al fragmento
    // al crearlo, pero es una buena práctica si planeas hacerlo en el futuro.
    companion object {
        @JvmStatic
        fun newInstance() = Pilotos()
    }
}