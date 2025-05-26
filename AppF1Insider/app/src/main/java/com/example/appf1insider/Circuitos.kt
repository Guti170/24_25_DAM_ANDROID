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
import com.example.appf1insider.adapter.CircuitoAdapter // Asegúrate que la ruta del adaptador es correcta
import com.example.appf1insider.model.Circuito // Asegúrate que la ruta del modelo es correcta
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject // Importante para convertir a objeto
import com.google.firebase.ktx.Firebase

class Circuitos : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var circuitoAdapter: CircuitoAdapter
    private lateinit var progressBar: ProgressBar
    private lateinit var db: FirebaseFirestore
    private val TAG = "CircuitosFragment" // Tag para los logs

    // Lista para almacenar los circuitos recuperados
    private val listaDeCircuitos = mutableListOf<Circuito>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Inicializar Firestore
        db = Firebase.firestore
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflar el layout para este fragmento
        val view = inflater.inflate(R.layout.fragment_circuitos, container, false)

        // Inicializar Vistas
        recyclerView = view.findViewById(R.id.recyclerViewCircuitos)
        progressBar = view.findViewById(R.id.progressBarCircuitos)

        // Configurar RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(context)
        circuitoAdapter = CircuitoAdapter(listaDeCircuitos) // Pasar la lista al adaptador
        recyclerView.adapter = circuitoAdapter

        // (Opcional) Añadir un listener de clic al adaptador si quieres navegar a detalles
        // circuitoAdapter.setOnItemClickListener { circuito ->
        //     // Lógica para navegar a una pantalla de detalles del circuito
        //     // Por ejemplo, usando Navigation Component o iniciando una nueva Activity
        //     Log.d(TAG, "Circuito clickeado: ${circuito.nombre}")
        //     // val action = CircuitosFragmentDirections.actionCircuitosFragmentToDetalleCircuitoFragment(circuito.id)
        //     // findNavController().navigate(action)
        // }


        // Cargar los datos si la lista está vacía (evita recargar en cambios de configuración si se maneja estado)
        if (listaDeCircuitos.isEmpty()) {
            fetchCircuitosData()
        } else {
            // Si ya hay datos (ej. por cambio de config y ViewModel), solo actualizar UI
            progressBar.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE
        }


        return view
    }

    private fun fetchCircuitosData() {
        progressBar.visibility = View.VISIBLE
        recyclerView.visibility = View.GONE

        db.collection("circuito")
            // Puedes añadir .orderBy() si necesitas un orden específico, por ejemplo:
            // .orderBy("nombre", Query.Direction.ASCENDING)
            .get()
            .addOnSuccessListener { documents ->
                if (documents.isEmpty) {
                    Log.d(TAG, "No se encontraron documentos de circuitos.")
                    Toast.makeText(context, "No hay circuitos para mostrar", Toast.LENGTH_SHORT).show()
                    progressBar.visibility = View.GONE
                    recyclerView.visibility = View.GONE // Asegurarse que el RV está oculto
                    return@addOnSuccessListener
                }

                val tempList = mutableListOf<Circuito>()
                for (document in documents) {
                    // Convertir el documento de Firestore directamente a un objeto Circuito
                    // Firestore intentará mapear los campos del documento a los de tu clase Circuito.
                    // Asegúrate que los nombres de los campos en Firestore coinciden con los de la clase.
                    val circuito = document.toObject<Circuito>()

                    // Opcional: Si el ID del documento no es un campo en Firestore pero lo quieres en tu objeto
                    // val circuitoConId = circuito.copy(id = document.id)
                    // tempList.add(circuitoConId)
                    // Si 'id' ya es un campo en tu documento Firestore que coincide con el de la clase,
                    // no necesitas .copy(id = document.id)

                    tempList.add(circuito)
                    Log.d(TAG, "Circuito cargado: ${circuito.nombre}, Imagen: ${circuito.imagen}, Desc: ${circuito.descripcion}, Video: ${circuito.video}")
                }

                // Actualizar la lista principal y notificar al adaptador
                listaDeCircuitos.clear()
                listaDeCircuitos.addAll(tempList)
                circuitoAdapter.notifyDataSetChanged() // O usar DiffUtil para mejor rendimiento

                progressBar.visibility = View.GONE
                recyclerView.visibility = View.VISIBLE
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error obteniendo documentos de circuitos: ", exception)
                Toast.makeText(context, "Error al cargar los circuitos: ${exception.message}", Toast.LENGTH_LONG).show()
                progressBar.visibility = View.GONE
                // Podrías mostrar un mensaje de error en la UI aquí también
            }
    }

    // Si necesitas limpiar algo al destruir la vista (ej. listeners de Firestore en tiempo real)
    override fun onDestroyView() {
        super.onDestroyView()
        // Si usaras addSnapshotListener, aquí deberías removerlo.
        // Con .get() no es estrictamente necesario para este caso.
    }

    // El companion object para newInstance generalmente se usa si necesitas pasar argumentos
    // al crear el fragmento. Si no pasas argumentos, puedes simplificarlo o quitarlo.
    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment.
         *
         * @return A new instance of fragment Circuitos.
         */
        @JvmStatic
        fun newInstance() = Circuitos() // Constructor simple si no hay args
    }
}