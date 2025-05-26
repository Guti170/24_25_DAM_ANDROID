package com.example.appf1insider // O el paquete donde tengas tus fragmentos

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
import com.example.appf1insider.adapter.PilotoAdapter // CAMBIO: Adaptador de Pilotos
import com.example.appf1insider.model.Piloto // CAMBIO: Modelo de Piloto
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase

class Pilotos : Fragment() { // CAMBIO: Nombre de la clase

    private lateinit var recyclerView: RecyclerView
    private lateinit var pilotoAdapter: PilotoAdapter // CAMBIO: Variable para el adaptador de pilotos
    private lateinit var progressBar: ProgressBar
    private lateinit var db: FirebaseFirestore
    private val TAG = "PilotosFragment" // CAMBIO (Opcional): Tag para logs

    // Lista para almacenar los pilotos recuperados
    private val listaDePilotos = mutableListOf<Piloto>() // CAMBIO: Lista de Pilotos

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        db = Firebase.firestore
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflar el layout para este fragmento
        val view = inflater.inflate(R.layout.fragment_pilotos, container, false) // CAMBIO: Layout del fragmento de pilotos

        // Inicializar Vistas (asegúrate que los IDs coinciden en fragment_pilotos.xml)
        recyclerView = view.findViewById(R.id.recyclerViewPilotos) // CAMBIO: ID del RecyclerView
        progressBar = view.findViewById(R.id.progressBarPilotos)   // CAMBIO: ID del ProgressBar

        // Configurar RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(context)
        pilotoAdapter = PilotoAdapter(listaDePilotos) // CAMBIO: Usar PilotoAdapter
        recyclerView.adapter = pilotoAdapter

        // Cargar los datos si la lista está vacía
        if (listaDePilotos.isEmpty()) {
            fetchPilotosData() // CAMBIO: Llamar a la función para obtener datos de pilotos
        } else {
            progressBar.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE
        }

        return view
    }

    private fun fetchPilotosData() { // CAMBIO: Nombre de la función
        progressBar.visibility = View.VISIBLE
        recyclerView.visibility = View.GONE

        db.collection("piloto") // CAMBIO: Nombre de la colección en Firestore
            .get()
            .addOnSuccessListener { documents ->
                if (documents.isEmpty) {
                    Log.d(TAG, "No se encontraron documentos de pilotos.")
                    Toast.makeText(context, "No hay pilotos para mostrar", Toast.LENGTH_SHORT).show()
                    progressBar.visibility = View.GONE
                    recyclerView.visibility = View.GONE
                    return@addOnSuccessListener
                }

                val tempList = mutableListOf<Piloto>() // CAMBIO: Lista temporal de Pilotos
                for (document in documents) {
                    val piloto = document.toObject<Piloto>() // CAMBIO: Convertir a objeto Piloto
                    tempList.add(piloto)
                    // CAMBIO: Log con los campos del piloto (asegúrate que los nombres de campo en Piloto.kt son correctos)
                    Log.d(TAG, "Piloto cargado: ${piloto.nombre}, Imagen: ${piloto.imagen}, Desc: ${piloto.descripcion}, Estadisticas: ${piloto.estadisticas}")
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

    override fun onDestroyView() {
        super.onDestroyView()
    }

    companion object {
        @JvmStatic
        fun newInstance() = Pilotos() // CAMBIO: Constructor de Pilotos
    }
}