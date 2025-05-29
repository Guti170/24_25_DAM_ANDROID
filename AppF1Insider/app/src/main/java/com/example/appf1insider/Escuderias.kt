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
import androidx.activity.result.contract.ActivityResultContracts // Importante para el nuevo manejo de resultados
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.appf1insider.adapter.EscuderiaAdapter
import com.example.appf1insider.model.Escuderia
// Asegúrate de que la importación de DetalleEscuderiaActivity es correcta
import com.example.appf1insider.DetalleEscuderiaActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton // Importar FAB
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject // Importante para la conversión
import com.google.firebase.ktx.Firebase

class Escuderias : Fragment(), EscuderiaAdapter.OnEscuderiaClickListener {

    private lateinit var recyclerView: RecyclerView
    private lateinit var escuderiaAdapter: EscuderiaAdapter
    private lateinit var progressBar: ProgressBar
    private lateinit var fabAddEscuderia: FloatingActionButton // Variable para el FAB
    private lateinit var db: FirebaseFirestore
    private val TAG = "EscuderiasFragment" // Completar el TAG

    private val listaDeEscuderias = mutableListOf<Escuderia>()

    // Nuevo ActivityResultLauncher para manejar el resultado de AddEscuderiaActivity
    private val addEscuderiaLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            // La escudería se añadió correctamente, recargar la lista
            Log.d(TAG, "Escudería añadida, recargando datos...")
            fetchEscuderiasData() // Vuelve a cargar los datos para mostrar la nueva escudería
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
        val view = inflater.inflate(R.layout.fragment_escuderias, container, false)

        recyclerView = view.findViewById(R.id.recyclerViewEscuderias)
        progressBar = view.findViewById(R.id.progressBarEscuderias)
        fabAddEscuderia = view.findViewById(R.id.fabAddEscuderia) // Inicializar el FAB

        setupRecyclerView()

        fabAddEscuderia.setOnClickListener {
            Log.d(TAG, "FAB para añadir escudería presionado.")
            val intent = Intent(activity, AddEscuderiaActivity::class.java)
            addEscuderiaLauncher.launch(intent) // Usar el launcher para iniciar la activity
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

        db.collection("escuderia") // Nombre de tu colección de escuderías
            .orderBy("nombre") // Opcional: ordenar por nombre o algún otro campo
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
                    escuderiaAdapter.notifyDataSetChanged()
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

    companion object {
        @JvmStatic
        fun newInstance() = Escuderias()
    }
}