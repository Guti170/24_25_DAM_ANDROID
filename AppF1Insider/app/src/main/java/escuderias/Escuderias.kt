package escuderias

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

class Escuderias : Fragment(), EscuderiaAdapter.OnEscuderiaClickListener {

    private lateinit var recyclerViewEscuderias: RecyclerView
    private lateinit var escuderiaAdapter: EscuderiaAdapter
    private val listaEscuderias = mutableListOf<Escuderia>()
    private lateinit var db: FirebaseFirestore
    private lateinit var progressBar: ProgressBar
    private lateinit var fabAddEscuderia: FloatingActionButton
    private var isAdmin: Boolean = false

    companion object {
        private const val TAG = "EscuderiasFragment"
    }

    private val addEscuderiaLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) cargarEscuderias()
        }

    private val detalleEscuderiaLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) cargarEscuderias()
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
        val view = inflater.inflate(R.layout.fragment_escuderias, container, false)
        recyclerViewEscuderias = view.findViewById(R.id.recyclerViewEscuderias)
        progressBar = view.findViewById(R.id.progressBarEscuderias)
        fabAddEscuderia = view.findViewById(R.id.fabAddEscuderia)

        setupRecyclerView()
        setupFab()
        cargarEscuderias()
        return view
    }

    private fun setupRecyclerView() {
        escuderiaAdapter = EscuderiaAdapter(listaEscuderias, this, isAdmin)
        recyclerViewEscuderias.layoutManager = LinearLayoutManager(context)
        recyclerViewEscuderias.adapter = escuderiaAdapter
    }

    private fun setupFab() {
        fabAddEscuderia.visibility = if (isAdmin) View.VISIBLE else View.GONE
        if (isAdmin) {
            fabAddEscuderia.setOnClickListener {
                addEscuderiaLauncher.launch(Intent(activity, AddEscuderiaActivity::class.java))
            }
        }
    }

    private fun cargarEscuderias() {
        if (!isAdded) return
        progressBar.visibility = View.VISIBLE
        recyclerViewEscuderias.visibility = View.GONE

        db.collection("escuderia").orderBy("nombre", Query.Direction.ASCENDING).get()
            .addOnSuccessListener { result ->
                if (!isAdded) return@addOnSuccessListener
                listaEscuderias.clear()
                if (result.isEmpty) {
                    if (isAdded) Toast.makeText(context, "No hay escuderías.", Toast.LENGTH_SHORT).show()
                } else {
                    result.forEach { doc ->
                        try {
                            listaEscuderias.add(doc.toObject<Escuderia>().copy(id = doc.id))
                        } catch (e: Exception) { Log.e(TAG, "Error convirtiendo escudería: ${doc.id}", e) }
                    }
                }
                escuderiaAdapter.notifyDataSetChanged()
                progressBar.visibility = View.GONE
                recyclerViewEscuderias.visibility = View.VISIBLE
            }
            .addOnFailureListener { e ->
                if (!isAdded) return@addOnFailureListener
                Log.w(TAG, "Error cargando escuderías.", e)
                if (isAdded) Toast.makeText(context, "Error cargando: ${e.message}", Toast.LENGTH_LONG).show()
                progressBar.visibility = View.GONE
            }
    }

    override fun onEscuderiaClick(escuderia: Escuderia) {
        if (!isAdded) return
        val intent = Intent(activity, DetalleEscuderiaActivity::class.java).apply {
            putExtra(DetalleEscuderiaActivity.EXTRA_ESCUDERIA, escuderia)
            putExtra("IS_ADMIN_USER", isAdmin)
        }
        detalleEscuderiaLauncher.launch(intent)
    }

    override fun onEscuderiaLongClick(escuderia: Escuderia, position: Int) {
        if (!isAdmin || !isAdded) return

        AlertDialog.Builder(requireContext())
            .setTitle("Eliminar Escudería")
            .setMessage("¿Eliminar '${escuderia.nombre}'?")
            .setPositiveButton("Eliminar") { _, _ -> eliminarEscuderia(escuderia, position) }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun eliminarEscuderia(escuderia: Escuderia, position: Int) {
        if (escuderia.id.isEmpty()) {
            if (isAdded) Toast.makeText(context, "ID no válido.", Toast.LENGTH_SHORT).show()
            return
        }

        if (escuderia.imagen.startsWith("gs://")) {
            Firebase.storage.getReferenceFromUrl(escuderia.imagen).delete()
                .addOnFailureListener { e -> Log.w(TAG, "Error borrando imagen de escudería.", e) }
        }


        db.collection("escuderia").document(escuderia.id).delete()
            .addOnSuccessListener {
                if (!isAdded) return@addOnSuccessListener
                val index = listaEscuderias.indexOfFirst { it.id == escuderia.id }
                if (index != -1) {
                    listaEscuderias.removeAt(index)
                    escuderiaAdapter.notifyItemRemoved(index)
                } else {
                    cargarEscuderias()
                }
                if (isAdded) Toast.makeText(context, "'${escuderia.nombre}' eliminada.", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                if (!isAdded) return@addOnFailureListener
                Log.w(TAG, "Error eliminando '${escuderia.nombre}'.", e)
                if (isAdded) Toast.makeText(context, "Error al eliminar: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}