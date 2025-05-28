package com.example.appf1insider

import android.annotation.SuppressLint
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
import com.example.appf1insider.adapter.EscuderiaAdapter
import com.example.appf1insider.model.Escuderia
// Asegúrate que la importación de DetalleEscuderiaActivity es correcta
import com.example.appf1insider.DetalleEscuderiaActivity // O la ruta correcta a tu Activity
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject // Importante para la conversión
import com.google.firebase.ktx.Firebase

class Escuderias : Fragment(), EscuderiaAdapter.OnEscuderiaClickListener { // Implementa la interfaz

    private lateinit var recyclerView: RecyclerView
    private lateinit var escuderiaAdapter: EscuderiaAdapter
    private lateinit var progressBar: ProgressBar
    private lateinit var db: FirebaseFirestore
    private val TAG = "EscuderiasFragment"

    private val listaDeEscuderias = mutableListOf<Escuderia>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        db = Firebase.firestore
        // Considera usar un ViewModel para una mejor gestión del estado.
    }

    @SuppressLint("MissingInflatedId") // Revisa si este SuppressLint es realmente necesario para progressBarEscuderias o recyclerViewEscuderias
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_escuderias, container, false)

        recyclerView = view.findViewById(R.id.recyclerViewEscuderias)
        progressBar = view.findViewById(R.id.progressBarEscuderias)

        setupRecyclerView()

        // Cargar datos solo si la lista está vacía para evitar recargas innecesarias
        // al volver al fragmento (si no usas ViewModel).
        if (listaDeEscuderias.isEmpty()) {
            fetchEscuderiasData()
        } else {
            // Si ya hay datos (por ejemplo, al volver de la pantalla de detalle),
            // simplemente los mostramos.
            // pilotoAdapter.updateData(listaDePilotos) // Asegúrate que el adapter tenga los datos correctos
            progressBar.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE
        }
        return view
    }

    private fun setupRecyclerView() {
        recyclerView.layoutManager = LinearLayoutManager(context)
        // Pasa 'this' (el Fragment que implementa OnEscuderiaClickListener) al adaptador
        escuderiaAdapter = EscuderiaAdapter(listaDeEscuderias, this)
        recyclerView.adapter = escuderiaAdapter
    }

    private fun fetchEscuderiasData() {
        progressBar.visibility = View.VISIBLE
        recyclerView.visibility = View.GONE

        db.collection("escuderia") // Nombre de tu colección de escuderías en Firestore
            .get()
            .addOnSuccessListener { documents ->
                if (documents.isEmpty) {
                    Log.d(TAG, "No se encontraron documentos de escuderías.")
                    Toast.makeText(context, "No hay escuderías para mostrar", Toast.LENGTH_SHORT).show()
                } else {
                    val tempList = mutableListOf<Escuderia>()
                    for (document in documents) {
                        try {
                            // Convertir el documento de Firestore al objeto Escuderia
                            // y asignar el ID del documento al campo 'id' del objeto.
                            val escuderia = document.toObject<Escuderia>().copy(id = document.id)
                            tempList.add(escuderia)
                            Log.d(TAG, "Escudería cargada: ${escuderia.nombre}, ID: ${escuderia.id}, Imagen: ${escuderia.imagen}")
                        } catch (e: Exception) {
                            Log.e(TAG, "Error al convertir documento a Escuderia: ${document.id}", e)
                        }
                    }
                    // Actualizar la lista original y notificar al adaptador
                    listaDeEscuderias.clear()
                    listaDeEscuderias.addAll(tempList)
                    escuderiaAdapter.notifyDataSetChanged() // O usar DiffUtil para mejor rendimiento
                }
                progressBar.visibility = View.GONE
                recyclerView.visibility = View.VISIBLE
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error obteniendo documentos de escuderías: ", exception)
                Toast.makeText(context, "Error al cargar las escuderías: ${exception.message}", Toast.LENGTH_LONG).show()
                progressBar.visibility = View.GONE
                // Considera mostrar un mensaje de error o un botón de reintentar
            }
    }

    // Implementación del método de la interfaz OnEscuderiaClickListener
    override fun onEscuderiaClick(escuderia: Escuderia) {
        Log.d(TAG, "Escudería clickeada: ${escuderia.nombre}, ID: ${escuderia.id}")
        val intent = Intent(activity, DetalleEscuderiaActivity::class.java).apply {
            // Pasar el objeto Escuderia completo a la Activity de detalles
            // DetalleEscuderiaActivity.EXTRA_ESCUDERIA es la constante que definimos en esa Activity
            putExtra(DetalleEscuderiaActivity.EXTRA_ESCUDERIA, escuderia)
        }
        startActivity(intent)
    }

    // No es estrictamente necesario si no pasas argumentos al crear el fragmento,
    // pero es una buena práctica.
    companion object {
        @JvmStatic
        fun newInstance() = Escuderias()
    }
}