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
import com.example.appf1insider.adapter.CircuitoAdapter
import com.example.appf1insider.model.Circuito
// Asegúrate de que la importación de DetalleCircuitoActivity es correcta
import com.example.appf1insider.DetalleCircuitoActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton // Importar FAB
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject // Importante para la conversión
import com.google.firebase.ktx.Firebase

class Circuitos : Fragment(), CircuitoAdapter.OnItemClickListener {

    private lateinit var recyclerView: RecyclerView
    private lateinit var circuitoAdapter: CircuitoAdapter
    private lateinit var progressBar: ProgressBar
    private lateinit var fabAddCircuito: FloatingActionButton // Variable para el FAB
    private lateinit var db: FirebaseFirestore
    private val TAG = "CircuitosFragment"

    private val listaDeCircuitos = mutableListOf<Circuito>()

    // Nuevo ActivityResultLauncher para manejar el resultado de AddCircuitoActivity
    private val addCircuitoLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            // El circuito se añadió correctamente, recargar la lista
            Log.d(TAG, "Circuito añadido, recargando datos...")
            fetchCircuitosData() // Vuelve a cargar los datos para mostrar el nuevo circuito
        } else {
            Log.d(TAG, "AddCircuitoActivity finalizó sin RESULT_OK (código: ${result.resultCode})")
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
            addCircuitoLauncher.launch(intent) // Usar el launcher para iniciar la activity
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
            .orderBy("nombre") // Opcional: ordenar por nombre o algún otro campo
            .get()
            .addOnSuccessListener { documents ->
                if (documents.isEmpty) {
                    Log.d(TAG, "No se encontraron documentos de circuitos.")
                    Toast.makeText(context, "No hay circuitos para mostrar. ¡Añade uno!", Toast.LENGTH_SHORT).show()
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
                Toast.makeText(context, "Error al cargar los circuitos: ${exception.message}", Toast.LENGTH_LONG).show()
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

    companion object {
        @JvmStatic
        fun newInstance() = Circuitos()
    }
}