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
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.appf1insider.adapter.EscuderiaAdapter
import com.example.appf1insider.model.Escuderia
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class Escuderias : Fragment(), EscuderiaAdapter.OnEscuderiaClickListener {

    private lateinit var recyclerView: RecyclerView
    private lateinit var escuderiaAdapter: EscuderiaAdapter
    private lateinit var progressBar: ProgressBar
    private lateinit var fabAddEscuderia: FloatingActionButton
    private lateinit var db: FirebaseFirestore
    private val storage = Firebase.storage
    private val TAG = "EscuderiasFragment"

    private val listaDeEscuderias = mutableListOf<Escuderia>()

    private val addEscuderiaLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            fetchEscuderiasData()
        }
    }

    private val detalleEscuderiaLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            fetchEscuderiasData()
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
        fabAddEscuderia = view.findViewById(R.id.fabAddEscuderia)

        setupRecyclerView()

        fabAddEscuderia.setOnClickListener {
            val intent = Intent(activity, AddEscuderiaActivity::class.java)
            addEscuderiaLauncher.launch(intent)
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

        db.collection("escuderia")
            .orderBy("nombre")
            .get()
            .addOnSuccessListener { documents ->
                val tempList = mutableListOf<Escuderia>()
                for (document in documents) {
                    try {
                        val escuderia = document.toObject<Escuderia>().copy(id = document.id)
                        tempList.add(escuderia)
                    } catch (e: Exception) {
                        Log.e(TAG, "Error converting document to Escuderia: ${document.id}", e)
                    }
                }
                listaDeEscuderias.clear()
                listaDeEscuderias.addAll(tempList)
                escuderiaAdapter.notifyDataSetChanged()
                progressBar.visibility = View.GONE
                recyclerView.visibility = View.VISIBLE
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting escuderias documents: ", exception)
                Toast.makeText(context, "Error loading escuderias: ${exception.message}", Toast.LENGTH_LONG).show()
                progressBar.visibility = View.GONE
            }
    }

    override fun onEscuderiaClick(escuderia: Escuderia) {
        val intent = Intent(activity, DetalleEscuderiaActivity::class.java).apply {
            putExtra(DetalleEscuderiaActivity.EXTRA_ESCUDERIA, escuderia)
        }
        detalleEscuderiaLauncher.launch(intent)
    }

    override fun onEscuderiaLongClick(escuderia: Escuderia, position: Int) {
        AlertDialog.Builder(requireContext())
            .setTitle("Confirm Delete")
            .setMessage("Are you sure you want to delete '${escuderia.nombre}'?")
            .setPositiveButton("Delete") { dialog, _ ->
                borrarEscuderia(escuderia, position)
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun borrarEscuderia(escuderia: Escuderia, position: Int) {
        if (escuderia.id.isEmpty()) {
            Toast.makeText(context, "Error: Invalid escuderia ID.", Toast.LENGTH_SHORT).show()
            return
        }

        db.collection("escuderia").document(escuderia.id)
            .delete()
            .addOnSuccessListener {
                Log.d(TAG, "Escuderia '${escuderia.nombre}' deleted from Firestore.")
                Toast.makeText(context, "Escuderia '${escuderia.nombre}' deleted.", Toast.LENGTH_SHORT).show()

                if (escuderia.imagen.startsWith("gs://")) {
                    val imagenRef = storage.getReferenceFromUrl(escuderia.imagen)
                    imagenRef.delete().addOnSuccessListener {
                        Log.d(TAG, "Image ${escuderia.imagen} deleted from Storage.")
                    }.addOnFailureListener { e ->
                        Log.w(TAG, "Error deleting image ${escuderia.imagen} from Storage.", e)
                    }
                }
                // Example for another potential file, like 'logoCocheUrl'
                // if (escuderia.logoCocheUrl.isNotBlank() && escuderia.logoCocheUrl.startsWith("gs://")) {
                //     val logoCocheRef = storage.getReferenceFromUrl(escuderia.logoCocheUrl)
                //     logoCocheRef.delete().addOnSuccessListener {
                //         Log.d(TAG, "Car logo ${escuderia.logoCocheUrl} deleted from Storage.")
                //     }.addOnFailureListener { e ->
                //         Log.w(TAG, "Error deleting car logo ${escuderia.logoCocheUrl} from Storage.", e)
                //     }
                // }

                escuderiaAdapter.removeItem(position) // Make sure your adapter has this method
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error deleting escuderia '${escuderia.nombre}' from Firestore.", e)
                Toast.makeText(context, "Error deleting escuderia: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }

    companion object {
        // Puedes usar newInstance si necesitas pasar argumentos al crear el fragmento,
        // pero para este caso no es estrictamente necesario.
         fun newInstance(): Escuderias {
             return Escuderias()
         }
    }
}