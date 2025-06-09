package circuitos

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
import androidx.appcompat.widget.SearchView
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
import java.util.Locale

class Circuitos : Fragment(), CircuitoAdapter.OnItemClickListener {

    private lateinit var recyclerViewCircuitos: RecyclerView
    private lateinit var circuitoAdapter: CircuitoAdapter
    private val listaCircuitosOriginal = mutableListOf<Circuito>()
    private val listaCircuitosFiltrada = mutableListOf<Circuito>()
    private lateinit var db: FirebaseFirestore
    private lateinit var progressBar: ProgressBar
    private lateinit var fabAddCircuito: FloatingActionButton
    private var isAdmin: Boolean = false
    private lateinit var searchViewCircuitos: SearchView

    companion object {
        private const val TAG = "CircuitosFragment"
    }

    private val addCircuitoLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) cargarCircuitos()
        }

    private val detalleCircuitoLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) cargarCircuitos()
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
        val view = inflater.inflate(R.layout.fragment_circuitos, container, false)
        recyclerViewCircuitos = view.findViewById(R.id.recyclerViewCircuitos)
        progressBar = view.findViewById(R.id.progressBarCircuitos)
        fabAddCircuito = view.findViewById(R.id.fabAddCircuito)
        searchViewCircuitos = view.findViewById(R.id.searchViewCircuitos)

        setupRecyclerView()
        setupFab()
        setupSearchView()
        cargarCircuitos()
        return view
    }

    private fun setupRecyclerView() {
        circuitoAdapter = CircuitoAdapter(listaCircuitosFiltrada, this, isAdmin)
        recyclerViewCircuitos.layoutManager = LinearLayoutManager(context)
        recyclerViewCircuitos.adapter = circuitoAdapter
    }

    private fun setupFab() {
        fabAddCircuito.visibility = if (isAdmin) View.VISIBLE else View.GONE
        if (isAdmin) {
            fabAddCircuito.setOnClickListener {
                addCircuitoLauncher.launch(Intent(activity, AddCircuitoActivity::class.java))
            }
        }
    }

    private fun setupSearchView() {
        searchViewCircuitos.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                filtrarCircuitos(query)
                searchViewCircuitos.clearFocus()
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filtrarCircuitos(newText)
                return true
            }
        })
        searchViewCircuitos.setOnCloseListener {
            filtrarCircuitos("")
            false
        }
    }

    private fun filtrarCircuitos(query: String?) {
        listaCircuitosFiltrada.clear()
        if (query.isNullOrEmpty()) {
            listaCircuitosFiltrada.addAll(listaCircuitosOriginal)
        } else {
            val searchQuery = query.lowercase(Locale.getDefault()).trim()
            listaCircuitosOriginal.forEach { circuito ->
                if (circuito.nombre.lowercase(Locale.getDefault()).contains(searchQuery)) {
                    listaCircuitosFiltrada.add(circuito)
                }
            }
        }
        if (isAdded) {
            circuitoAdapter.notifyDataSetChanged()
            if (listaCircuitosFiltrada.isEmpty() && !query.isNullOrEmpty()) {
                Toast.makeText(context, "No se encontraron circuitos para '$query'", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun cargarCircuitos() {
        if (!isAdded) return
        progressBar.visibility = View.VISIBLE
        recyclerViewCircuitos.visibility = View.GONE
        searchViewCircuitos.setQuery("", false)
        searchViewCircuitos.clearFocus()

        db.collection("circuito").orderBy("nombre", Query.Direction.ASCENDING).get()
            .addOnSuccessListener { result ->
                if (!isAdded) return@addOnSuccessListener
                listaCircuitosOriginal.clear()
                if (result.isEmpty) {
                    if (isAdded) Toast.makeText(context, "No hay circuitos.", Toast.LENGTH_SHORT).show()
                } else {
                    result.forEach { doc ->
                        try {
                            listaCircuitosOriginal.add(doc.toObject<Circuito>().copy(id = doc.id))
                        } catch (e: Exception) { Log.e(TAG, "Error al convertir: ${doc.id}", e) }
                    }
                }
                filtrarCircuitos(searchViewCircuitos.query.toString())
                progressBar.visibility = View.GONE
                recyclerViewCircuitos.visibility = View.VISIBLE
            }
            .addOnFailureListener { e ->
                if (!isAdded) return@addOnFailureListener
                Log.w(TAG, "Error al cargar circuitos.", e)
                if (isAdded) Toast.makeText(context, "Error cargando: ${e.message}", Toast.LENGTH_LONG).show()
                progressBar.visibility = View.GONE
                recyclerViewCircuitos.visibility = View.VISIBLE
            }
    }

    override fun onCircuitoClick(circuito: Circuito) {
        if (!isAdded) return
        val intent = Intent(activity, DetalleCircuitoActivity::class.java).apply {
            putExtra(DetalleCircuitoActivity.EXTRA_CIRCUITO, circuito)
            putExtra("IS_ADMIN_USER", isAdmin)
        }
        detalleCircuitoLauncher.launch(intent)
    }

    override fun onCircuitoLongClick(circuito: Circuito, position: Int) {
        if (!isAdmin || !isAdded) return

        AlertDialog.Builder(requireContext())
            .setTitle("Eliminar Circuito")
            .setMessage("¿Eliminar '${circuito.nombre}'?")
            .setPositiveButton("Eliminar") { _, _ -> eliminarCircuito(circuito) }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun eliminarCircuito(circuito: Circuito) {
        if (circuito.id.isEmpty()) {
            if (isAdded) Toast.makeText(context, "ID de circuito no válido.", Toast.LENGTH_SHORT).show()
            return
        }

        if (circuito.imagen.startsWith("gs://")) {
            Firebase.storage.getReferenceFromUrl(circuito.imagen).delete()
                .addOnFailureListener { e -> Log.w(TAG, "Error borrando imagen de Storage: ${circuito.imagen}", e) }
        }
        if (circuito.video.startsWith("gs://")) {
            Firebase.storage.getReferenceFromUrl(circuito.video).delete()
                .addOnFailureListener { e -> Log.w(TAG, "Error borrando video de Storage: ${circuito.video}", e) }
        }

        db.collection("circuito").document(circuito.id).delete()
            .addOnSuccessListener {
                if (!isAdded) return@addOnSuccessListener
                // Eliminar de ambas listas
                val indexOriginal = listaCircuitosOriginal.indexOfFirst { it.id == circuito.id }
                if (indexOriginal != -1) {
                    listaCircuitosOriginal.removeAt(indexOriginal)
                }
                val indexFiltrada = listaCircuitosFiltrada.indexOfFirst { it.id == circuito.id }
                if (indexFiltrada != -1) {
                    listaCircuitosFiltrada.removeAt(indexFiltrada)
                    circuitoAdapter.notifyItemRemoved(indexFiltrada)
                    // Opcional: notificar un rango si la posición podría no ser la misma
                    // circuitoAdapter.notifyDataSetChanged()
                } else {
                    // Si no estaba en la lista filtrada (raro, pero posible si el filtro cambió justo antes)
                    // simplemente actualizamos la lista filtrada desde la original.
                    filtrarCircuitos(searchViewCircuitos.query.toString())
                }

                if (isAdded) Toast.makeText(context, "'${circuito.nombre}' eliminado.", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                if (!isAdded) return@addOnFailureListener
                Log.w(TAG, "Error eliminando '${circuito.nombre}' de Firestore.", e)
                if (isAdded) Toast.makeText(context, "Error al eliminar: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}