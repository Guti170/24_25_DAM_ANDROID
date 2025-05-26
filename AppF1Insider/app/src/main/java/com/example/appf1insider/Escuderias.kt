package com.example.appf1insider

import android.annotation.SuppressLint
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
import com.example.appf1insider.adapter.EscuderiaAdapter // CAMBIO: Adaptador de Escuderias
import com.example.appf1insider.model.Escuderia // CAMBIO: Modelo de Escuderia
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase

class Escuderias : Fragment() { // Nombre de la clase ya es Escuderias

    private lateinit var recyclerView: RecyclerView
    private lateinit var escuderiaAdapter: EscuderiaAdapter // CAMBIO: Variable para el adaptador de escuderias
    private lateinit var progressBar: ProgressBar
    private lateinit var db: FirebaseFirestore
    private val TAG = "EscuderiasFragment" // CAMBIO (Opcional): Tag para logs

    // Lista para almacenar las escuderías recuperadas
    private val listaDeEscuderias = mutableListOf<Escuderia>() // CAMBIO: Lista de Escuderias

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        db = Firebase.firestore
        // Eliminamos la lógica de ARG_PARAM1 y ARG_PARAM2 ya que no se usan para este listado
    }

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflar el layout para este fragmento
        val view = inflater.inflate(R.layout.fragment_escuderias, container, false) // CAMBIO: Layout del fragmento de escuderias

        // Inicializar Vistas (asegúrate que los IDs coinciden en fragment_escuderias.xml)
        recyclerView = view.findViewById(R.id.recyclerViewEscuderias) // CAMBIO: ID del RecyclerView
        progressBar = view.findViewById(R.id.progressBarEscuderias)   // CAMBIO: ID del ProgressBar

        // Configurar RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(context)
        escuderiaAdapter = EscuderiaAdapter(listaDeEscuderias) // CAMBIO: Usar EscuderiaAdapter
        recyclerView.adapter = escuderiaAdapter

        // Cargar los datos si la lista está vacía
        if (listaDeEscuderias.isEmpty()) {
            fetchEscuderiasData() // CAMBIO: Llamar a la función para obtener datos de escuderias
        } else {
            progressBar.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE
        }

        return view
    }

    private fun fetchEscuderiasData() { // CAMBIO: Nombre de la función
        progressBar.visibility = View.VISIBLE
        recyclerView.visibility = View.GONE

        db.collection("escuderia") // CAMBIO: Nombre de la colección en Firestore
            .get()
            .addOnSuccessListener { documents ->
                if (documents.isEmpty) {
                    Log.d(TAG, "No se encontraron documentos de escuderías.")
                    Toast.makeText(context, "No hay escuderías para mostrar", Toast.LENGTH_SHORT).show()
                    progressBar.visibility = View.GONE
                    recyclerView.visibility = View.GONE
                    return@addOnSuccessListener
                }

                val tempList = mutableListOf<Escuderia>() // CAMBIO: Lista temporal de Escuderias
                for (document in documents) {
                    val escuderia = document.toObject<Escuderia>() // CAMBIO: Convertir a objeto Escuderia
                    tempList.add(escuderia)
                    // CAMBIO: Log con los campos de la escuderia
                    Log.d(TAG, "Escudería cargada: ${escuderia.nombre}, Imagen: ${escuderia.imagen}, Desc: ${escuderia.descripcion}, Estadisticas: ${escuderia.estadisticas}")
                }

                listaDeEscuderias.clear()
                listaDeEscuderias.addAll(tempList)
                escuderiaAdapter.notifyDataSetChanged()

                progressBar.visibility = View.GONE
                recyclerView.visibility = View.VISIBLE
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error obteniendo documentos de escuderías: ", exception)
                Toast.makeText(context, "Error al cargar las escuderías: ${exception.message}", Toast.LENGTH_LONG).show()
                progressBar.visibility = View.GONE
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment.
         *
         * @return A new instance of fragment Escuderias.
         */
        @JvmStatic
        fun newInstance() = Escuderias() // Simplificado, ya no usa parámetros
    }
}