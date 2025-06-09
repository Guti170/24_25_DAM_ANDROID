package escuderias

import android.annotation.SuppressLint
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

class Escuderias : Fragment(), EscuderiaAdapter.OnEscuderiaClickListener {

    private lateinit var recyclerViewEscuderias: RecyclerView
    private lateinit var escuderiaAdapter: EscuderiaAdapter
    private val listaEscuderiasOriginal = mutableListOf<Escuderia>()
    private val listaEscuderiasFiltrada = mutableListOf<Escuderia>()
    private lateinit var db: FirebaseFirestore
    private lateinit var progressBar: ProgressBar
    private lateinit var fabAddEscuderia: FloatingActionButton
    private var isAdmin: Boolean = false
    private lateinit var searchViewEscuderias: SearchView // Referencia al SearchView

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

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_escuderias, container, false)
        recyclerViewEscuderias = view.findViewById(R.id.recyclerViewEscuderias)
        progressBar = view.findViewById(R.id.progressBarEscuderias)
        fabAddEscuderia = view.findViewById(R.id.fabAddEscuderia)
        searchViewEscuderias = view.findViewById(R.id.searchViewEscuderias)

        setupRecyclerView()
        setupFab()
        setupSearchView()
        cargarEscuderias()
        return view
    }

    private fun setupRecyclerView() {
        escuderiaAdapter = EscuderiaAdapter(listaEscuderiasFiltrada, this, isAdmin)
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

    private fun setupSearchView() {
        searchViewEscuderias.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                filtrarEscuderias(query)
                searchViewEscuderias.clearFocus()
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filtrarEscuderias(newText)
                return true
            }
        })
        searchViewEscuderias.setOnCloseListener {
            filtrarEscuderias("") // Muestra todas las escuderías
            false
        }
    }

    private fun filtrarEscuderias(query: String?) {
        listaEscuderiasFiltrada.clear()
        if (query.isNullOrEmpty()) {
            listaEscuderiasFiltrada.addAll(listaEscuderiasOriginal)
        } else {
            val searchQuery = query.lowercase(Locale.getDefault()).trim()
            listaEscuderiasOriginal.forEach { escuderia ->
                // Filtrar solo por el nombre de la escudería
                // Asegúrate de que tu modelo Escuderia tenga un campo 'nombre'
                if (escuderia.nombre.lowercase(Locale.getDefault()).contains(searchQuery)) {
                    listaEscuderiasFiltrada.add(escuderia)
                }
            }
        }
        if (isAdded) {
            escuderiaAdapter.notifyDataSetChanged()
            if (listaEscuderiasFiltrada.isEmpty() && !query.isNullOrEmpty()) {
                Toast.makeText(context, "No se encontraron escuderías para '$query'", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun cargarEscuderias() {
        if (!isAdded) return
        progressBar.visibility = View.VISIBLE
        recyclerViewEscuderias.visibility = View.GONE
        searchViewEscuderias.setQuery("", false) // Limpiar búsqueda anterior
        searchViewEscuderias.clearFocus()

        db.collection("escuderia").orderBy("nombre", Query.Direction.ASCENDING).get()
            .addOnSuccessListener { result ->
                if (!isAdded) return@addOnSuccessListener
                listaEscuderiasOriginal.clear() // Limpiar la lista original
                if (result.isEmpty) {
                    if (isAdded) Toast.makeText(context, "No hay escuderías.", Toast.LENGTH_SHORT).show()
                } else {
                    result.forEach { doc ->
                        try {
                            // Asegúrate de que tu modelo Escuderia tenga un constructor que acepte 'id'
                            // o usa .copy(id = doc.id) si es una data class
                            listaEscuderiasOriginal.add(doc.toObject<Escuderia>().copy(id = doc.id))
                        } catch (e: Exception) { Log.e(TAG, "Error convirtiendo escudería: ${doc.id}", e) }
                    }
                }
                // Aplicar el filtro actual (o mostrar todos si el query está vacío)
                filtrarEscuderias(searchViewEscuderias.query.toString())
                progressBar.visibility = View.GONE
                recyclerViewEscuderias.visibility = View.VISIBLE
            }
            .addOnFailureListener { e ->
                if (!isAdded) return@addOnFailureListener
                Log.w(TAG, "Error cargando escuderías.", e)
                if (isAdded) Toast.makeText(context, "Error cargando: ${e.message}", Toast.LENGTH_LONG).show()
                progressBar.visibility = View.GONE
                recyclerViewEscuderias.visibility = View.VISIBLE // Mostrar RecyclerView incluso en error
            }
    }

    override fun onEscuderiaClick(escuderia: Escuderia) {
        if (!isAdded) return
        // Reemplaza DetalleEscuderiaActivity::class.java con tu Activity real
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
            .setPositiveButton("Eliminar") { _, _ -> eliminarEscuderia(escuderia) } // Pasamos el objeto escuderia
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun eliminarEscuderia(escuderia: Escuderia) {
        if (escuderia.id.isEmpty()) {
            if (isAdded) Toast.makeText(context, "ID de escudería no válido.", Toast.LENGTH_SHORT).show()
            return
        }

        if (escuderia.imagen.startsWith("gs://")) {
            Firebase.storage.getReferenceFromUrl(escuderia.imagen).delete()
                .addOnFailureListener { e -> Log.w(TAG, "Error borrando imagen de escudería de Storage: ${escuderia.imagen}", e) }
        }

        db.collection("escuderia").document(escuderia.id).delete()
            .addOnSuccessListener {
                if (!isAdded) return@addOnSuccessListener

                // Eliminar de la lista original
                val indexOriginal = listaEscuderiasOriginal.indexOfFirst { it.id == escuderia.id }
                if (indexOriginal != -1) {
                    listaEscuderiasOriginal.removeAt(indexOriginal)
                }

                // Eliminar de la lista filtrada y notificar al adaptador
                val indexFiltrada = listaEscuderiasFiltrada.indexOfFirst { it.id == escuderia.id }
                if (indexFiltrada != -1) {
                    listaEscuderiasFiltrada.removeAt(indexFiltrada)
                    escuderiaAdapter.notifyItemRemoved(indexFiltrada)
                } else {
                    // Si no estaba en la lista filtrada, volvemos a aplicar el filtro.
                    filtrarEscuderias(searchViewEscuderias.query.toString())
                }

                if (isAdded) Toast.makeText(context, "'${escuderia.nombre}' eliminada.", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                if (!isAdded) return@addOnFailureListener
                Log.w(TAG, "Error eliminando '${escuderia.nombre}' de Firestore.", e)
                if (isAdded) Toast.makeText(context, "Error al eliminar: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}