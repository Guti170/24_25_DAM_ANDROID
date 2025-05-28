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
import com.example.appf1insider.adapter.CircuitoAdapter
import com.example.appf1insider.model.Circuito
// Asegúrate de que la importación de DetalleCircuitoActivity es correcta
import com.example.appf1insider.DetalleCircuitoActivity // O la ruta correcta a tu Activity
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject // Importante para la conversión
import com.google.firebase.ktx.Firebase

class Circuitos : Fragment(), CircuitoAdapter.OnItemClickListener { // Implementa la interfaz

    private lateinit var recyclerView: RecyclerView
    private lateinit var circuitoAdapter: CircuitoAdapter
    private lateinit var progressBar: ProgressBar
    private lateinit var db: FirebaseFirestore
    private val TAG = "CircuitosFragment"

    // Usar una lista mutable que el adaptador pueda observar directamente
    private val listaDeCircuitos = mutableListOf<Circuito>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        db = Firebase.firestore
        // Considera usar un ViewModel para manejar los datos y la lógica de fetch
        // para sobrevivir a cambios de configuración y separar responsabilidades.
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_circuitos, container, false)

        recyclerView = view.findViewById(R.id.recyclerViewCircuitos)
        progressBar = view.findViewById(R.id.progressBarCircuitos)

        setupRecyclerView()

        // Solo cargar datos si la lista está vacía (ej. primera creación o si no usas ViewModel)
        if (listaDeCircuitos.isEmpty()) {
            fetchCircuitosData()
        } else {
            // Si los datos ya existen (ej. retenidos por el fragmento o ViewModel),
            // simplemente asegúrate de que la UI esté visible.
            progressBar.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE
            // El adaptador ya tiene los datos, no es necesario notificar cambios aquí
            // a menos que los datos hayan cambiado mientras el fragmento no estaba visible.
        }
        return view
    }

    private fun setupRecyclerView() {
        recyclerView.layoutManager = LinearLayoutManager(context)
        // Pasa 'this' (el Fragment que implementa OnItemClickListener) al adaptador
        circuitoAdapter = CircuitoAdapter(listaDeCircuitos, this)
        recyclerView.adapter = circuitoAdapter
    }

    private fun fetchCircuitosData() {
        progressBar.visibility = View.VISIBLE
        recyclerView.visibility = View.GONE // Ocultar RecyclerView mientras se carga

        db.collection("circuito") // Asegúrate que "circuito" es el nombre exacto de tu colección
            .get()
            .addOnSuccessListener { documents ->
                if (documents.isEmpty) {
                    Log.d(TAG, "No se encontraron documentos de circuitos.")
                    Toast.makeText(context, "No hay circuitos para mostrar", Toast.LENGTH_SHORT).show()
                } else {
                    val tempList = mutableListOf<Circuito>()
                    for (document in documents) {
                        try {
                            // Convertir el documento de Firestore al objeto Circuito
                            // El ID del documento se puede asignar si es necesario y no está en los campos
                            val circuito = document.toObject<Circuito>().copy(id = document.id)
                            tempList.add(circuito)
                            Log.d(TAG, "Circuito cargado: ${circuito.nombre}, ID: ${circuito.id}, Imagen: ${circuito.imagen}, Video: ${circuito.video}")
                        } catch (e: Exception) {
                            Log.e(TAG, "Error al convertir documento a Circuito: ${document.id}", e)
                            // Considera cómo manejar este caso, quizás omitir el ítem o mostrar un error parcial.
                        }
                    }
                    // Actualizar la lista original y notificar al adaptador
                    listaDeCircuitos.clear()
                    listaDeCircuitos.addAll(tempList)
                    circuitoAdapter.notifyDataSetChanged() // O usar DiffUtil para mejor rendimiento
                }
                progressBar.visibility = View.GONE
                recyclerView.visibility = View.VISIBLE // Mostrar RecyclerView con los datos
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error obteniendo documentos de circuitos: ", exception)
                Toast.makeText(context, "Error al cargar los circuitos: ${exception.message}", Toast.LENGTH_LONG).show()
                progressBar.visibility = View.GONE
                // Podrías mostrar un mensaje de error o un botón de reintentar aquí
            }
    }

    // Implementación del método de la interfaz OnItemClickListener
    override fun onCircuitoClick(circuito: Circuito) {
        Log.d(TAG, "Circuito clickeado: ${circuito.nombre}, ID: ${circuito.id}")
        val intent = Intent(activity, DetalleCircuitoActivity::class.java).apply {
            // Pasar el objeto Circuito completo a la Activity de detalles
            // DetalleCircuitoActivity.EXTRA_CIRCUITO es la constante que definimos en esa Activity
            putExtra(DetalleCircuitoActivity.EXTRA_CIRCUITO, circuito)
        }
        startActivity(intent)
    }

    // Opcional: Si quieres recargar los datos cuando el fragmento vuelve a ser visible
    // override fun onResume() {
    //     super.onResume()
    //     // Podrías añadir lógica aquí para refrescar datos si es necesario,
    //     // pero cuidado con recargas excesivas.
    //     // if (listaDeCircuitos.isEmpty()) { // O alguna otra condición
    //     //    fetchCircuitosData()
    //     // }
    // }

    // No es necesario un companion object newInstance() si no pasas argumentos al fragmento
    // al crearlo, pero es una buena práctica si planeas hacerlo en el futuro.
    companion object {
        @JvmStatic
        fun newInstance() = Circuitos()
    }
}