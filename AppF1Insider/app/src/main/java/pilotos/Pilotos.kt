package pilotos

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
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.appf1insider.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class Pilotos : Fragment(), PilotoAdapter.OnPilotoClickListener {

    private lateinit var recyclerViewPilotos: RecyclerView
    private lateinit var pilotoAdapter: PilotoAdapter
    private val listaPilotos = mutableListOf<Piloto>()
    private lateinit var db: FirebaseFirestore
    private lateinit var progressBar: ProgressBar
    private lateinit var fabAddPiloto: FloatingActionButton
    private var isAdmin: Boolean = false

    companion object {
        private const val TAG = "PilotosFragment"
    }

    private val addPilotoLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) cargarPilotos()
        }

    private val detallePilotoLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) cargarPilotos()
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let { isAdmin = it.getBoolean("IS_ADMIN_USER", false) }
        db = Firebase.firestore
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_pilotos, container, false)
        recyclerViewPilotos = view.findViewById(R.id.recyclerViewPilotos)
        progressBar = view.findViewById(R.id.progressBarPilotos)
        fabAddPiloto = view.findViewById(R.id.fabAddPiloto)

        setupRecyclerView()
        setupFab()
        cargarPilotos()
        return view
    }

    private fun setupRecyclerView() {
        pilotoAdapter = PilotoAdapter(listaPilotos, this, isAdmin) // Pasar isAdmin
        recyclerViewPilotos.layoutManager = LinearLayoutManager(context)
        recyclerViewPilotos.adapter = pilotoAdapter
    }

    private fun setupFab() {
        fabAddPiloto.visibility = if (isAdmin) View.VISIBLE else View.GONE
        if (isAdmin) {
            fabAddPiloto.setOnClickListener {
                addPilotoLauncher.launch(Intent(activity, AddPilotoActivity::class.java))
            }
        }
    }

    private fun cargarPilotos() {
        if (!isAdded) return
        progressBar.visibility = View.VISIBLE
        recyclerViewPilotos.visibility = View.GONE

        db.collection("piloto").orderBy("nombre", Query.Direction.ASCENDING).get()
            .addOnSuccessListener { result ->
                if (!isAdded) return@addOnSuccessListener
                listaPilotos.clear()
                if (result.isEmpty) {
                    if (isAdded) Toast.makeText(context, "No hay pilotos.", Toast.LENGTH_SHORT).show()
                } else {
                    result.forEach { doc ->
                        try {
                            listaPilotos.add(doc.toObject<Piloto>().copy(id = doc.id))
                        } catch (e: Exception) { Log.e(TAG, "Error al convertir piloto: ${doc.id}", e) }
                    }
                }
                pilotoAdapter.notifyDataSetChanged()
                progressBar.visibility = View.GONE
                recyclerViewPilotos.visibility = View.VISIBLE
            }
            .addOnFailureListener { e ->
                if (!isAdded) return@addOnFailureListener
                Log.w(TAG, "Error al cargar pilotos.", e)
                if (isAdded) Toast.makeText(context, "Error cargando: ${e.message}", Toast.LENGTH_LONG).show()
                progressBar.visibility = View.GONE
            }
    }

    override fun onPilotoClick(piloto: Piloto) {
        if (!isAdded) return
        val intent = Intent(activity, DetallePilotoActivity::class.java).apply {
            putExtra(DetallePilotoActivity.EXTRA_PILOTO, piloto)
            putExtra("IS_ADMIN_USER", isAdmin) // Pasar el estado de admin
        }
        detallePilotoLauncher.launch(intent)
    }

    override fun onPilotoLongClick(piloto: Piloto, position: Int) {
        if (!isAdmin || !isAdded) return

        AlertDialog.Builder(requireContext())
            .setTitle("Eliminar Piloto")
            .setMessage("¿Eliminar '${piloto.nombre}'?")
            .setPositiveButton("Eliminar") { _, _ -> eliminarPiloto(piloto, position) }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun eliminarPiloto(piloto: Piloto, position: Int) {
        if (piloto.id.isEmpty()) {
            if (isAdded) Toast.makeText(context, "ID no válido.", Toast.LENGTH_SHORT).show()
            return
        }

        if (piloto.imagen.startsWith("gs://")) {
            Firebase.storage.getReferenceFromUrl(piloto.imagen).delete()
                .addOnFailureListener { e -> Log.w(TAG, "Error borrando imagen de piloto.", e) }
        }

        db.collection("piloto").document(piloto.id).delete()
            .addOnSuccessListener {
                if (!isAdded) return@addOnSuccessListener
                val index = listaPilotos.indexOfFirst { it.id == piloto.id }
                if (index != -1) {
                    listaPilotos.removeAt(index)
                    pilotoAdapter.notifyItemRemoved(index)
                } else {
                    cargarPilotos()
                }
                if (isAdded) Toast.makeText(context, "'${piloto.nombre}' eliminado.", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                if (!isAdded) return@addOnFailureListener
                Log.w(TAG, "Error eliminando '${piloto.nombre}'.", e)
                if (isAdded) Toast.makeText(context, "Error al eliminar: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}