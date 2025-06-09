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

class Pilotos : Fragment(), PilotoAdapter.OnPilotoClickListener {

    private lateinit var recyclerViewPilotos: RecyclerView
    private lateinit var pilotoAdapter: PilotoAdapter
    // Dos listas para el filtro
    private val listaPilotosOriginal = mutableListOf<Piloto>()
    private val listaPilotosFiltrada = mutableListOf<Piloto>()
    private lateinit var db: FirebaseFirestore
    private lateinit var progressBar: ProgressBar
    private lateinit var fabAddPiloto: FloatingActionButton
    private var isAdmin: Boolean = false
    private lateinit var searchViewPilotos: SearchView // Referencia al SearchView

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
        searchViewPilotos = view.findViewById(R.id.searchViewPilotos) // Inicializar SearchView

        setupRecyclerView()
        setupFab()
        setupSearchView() // Configurar el SearchView
        cargarPilotos()
        return view
    }

    private fun setupRecyclerView() {
        // El adaptador ahora usa listaPilotosFiltrada
        pilotoAdapter = PilotoAdapter(listaPilotosFiltrada, this, isAdmin)
        recyclerViewPilotos.layoutManager = LinearLayoutManager(context)
        recyclerViewPilotos.adapter = pilotoAdapter
    }

    private fun setupFab() {
        fabAddPiloto.visibility = if (isAdmin) View.VISIBLE else View.GONE
        if (isAdmin) {
            fabAddPiloto.setOnClickListener {
                // Reemplaza AddPilotoActivity::class.java con tu Activity real
                addPilotoLauncher.launch(Intent(activity, AddPilotoActivity::class.java))
            }
        }
    }

    private fun setupSearchView() {
        searchViewPilotos.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                filtrarPilotos(query)
                searchViewPilotos.clearFocus()
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filtrarPilotos(newText)
                return true
            }
        })
        searchViewPilotos.setOnCloseListener {
            filtrarPilotos("") // Muestra todos los pilotos
            false
        }
    }

    private fun filtrarPilotos(query: String?) {
        listaPilotosFiltrada.clear()
        if (query.isNullOrEmpty()) {
            listaPilotosFiltrada.addAll(listaPilotosOriginal)
        } else {
            val searchQuery = query.lowercase(Locale.getDefault()).trim()
            listaPilotosOriginal.forEach { piloto ->
                // Filtrar solo por el nombre del piloto
                if (piloto.nombre.lowercase(Locale.getDefault()).contains(searchQuery)) {
                    listaPilotosFiltrada.add(piloto)
                }
            }
        }
        if (isAdded) {
            pilotoAdapter.notifyDataSetChanged()
            if (listaPilotosFiltrada.isEmpty() && !query.isNullOrEmpty()) {
                Toast.makeText(context, "No se encontraron pilotos para '$query'", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun cargarPilotos() {
        if (!isAdded) return
        progressBar.visibility = View.VISIBLE
        recyclerViewPilotos.visibility = View.GONE
        searchViewPilotos.setQuery("", false)
        searchViewPilotos.clearFocus()

        db.collection("piloto").orderBy("nombre", Query.Direction.ASCENDING).get()
            .addOnSuccessListener { result ->
                if (!isAdded) return@addOnSuccessListener
                listaPilotosOriginal.clear() // Limpiar la lista original
                if (result.isEmpty) {
                    if (isAdded) Toast.makeText(context, "No hay pilotos.", Toast.LENGTH_SHORT).show()
                } else {
                    result.forEach { doc ->
                        try {
                            // Asegúrate de que tu modelo Piloto tenga un constructor que acepte 'id'
                            // o usa .copy(id = doc.id) si es una data class
                            listaPilotosOriginal.add(doc.toObject<Piloto>().copy(id = doc.id))
                        } catch (e: Exception) { Log.e(TAG, "Error al convertir piloto: ${doc.id}", e) }
                    }
                }
                // Aplicar el filtro actual (o mostrar todos si el query está vacío)
                filtrarPilotos(searchViewPilotos.query.toString())
                progressBar.visibility = View.GONE
                recyclerViewPilotos.visibility = View.VISIBLE
            }
            .addOnFailureListener { e ->
                if (!isAdded) return@addOnFailureListener
                Log.w(TAG, "Error al cargar pilotos.", e)
                if (isAdded) Toast.makeText(context, "Error cargando: ${e.message}", Toast.LENGTH_LONG).show()
                progressBar.visibility = View.GONE
                recyclerViewPilotos.visibility = View.VISIBLE // Mostrar RecyclerView incluso en error
            }
    }

    override fun onPilotoClick(piloto: Piloto) {
        if (!isAdded) return
        // Reemplaza DetallePilotoActivity::class.java con tu Activity real
        val intent = Intent(activity, DetallePilotoActivity::class.java).apply {
            putExtra(DetallePilotoActivity.EXTRA_PILOTO, piloto)
            putExtra("IS_ADMIN_USER", isAdmin)
        }
        detallePilotoLauncher.launch(intent)
    }

    override fun onPilotoLongClick(piloto: Piloto, position: Int) {
        if (!isAdmin || !isAdded) return

        // Usar el nombre del piloto en el mensaje (y apellido si lo tienes y quieres)
        val nombrePilotoParaMensaje = piloto.nombre // O "${piloto.nombre} ${piloto.apellido}"
        AlertDialog.Builder(requireContext())
            .setTitle("Eliminar Piloto")
            .setMessage("¿Eliminar '${nombrePilotoParaMensaje}'?")
            .setPositiveButton("Eliminar") { _, _ -> eliminarPiloto(piloto) } // Pasamos el objeto piloto
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun eliminarPiloto(piloto: Piloto) {
        if (piloto.id.isEmpty()) {
            if (isAdded) Toast.makeText(context, "ID de piloto no válido.", Toast.LENGTH_SHORT).show()
            return
        }

        // Asumiendo que el modelo Piloto tiene un campo 'imagen' y que quieres eliminarla de Storage
        // Si no tiene imagen o no quieres eliminarla, puedes omitir este bloque.
        if (piloto.imagen.startsWith("gs://")) {
            Firebase.storage.getReferenceFromUrl(piloto.imagen).delete()
                .addOnFailureListener { e -> Log.w(TAG, "Error borrando imagen de piloto de Storage: ${piloto.imagen}", e) }
        }
        // Si tienes otros archivos en Storage asociados al piloto (ej. casco_imagen, numero_imagen),
        // deberías añadir lógica similar para eliminarlos aquí.

        db.collection("piloto").document(piloto.id).delete()
            .addOnSuccessListener {
                if (!isAdded) return@addOnSuccessListener

                // Eliminar de la lista original
                val indexOriginal = listaPilotosOriginal.indexOfFirst { it.id == piloto.id }
                if (indexOriginal != -1) {
                    listaPilotosOriginal.removeAt(indexOriginal)
                }

                // Eliminar de la lista filtrada y notificar al adaptador
                val indexFiltrada = listaPilotosFiltrada.indexOfFirst { it.id == piloto.id }
                if (indexFiltrada != -1) {
                    listaPilotosFiltrada.removeAt(indexFiltrada)
                    pilotoAdapter.notifyItemRemoved(indexFiltrada)
                    // Si temes que las posiciones no coincidan después de múltiples operaciones,
                    // podrías usar notifyDataSetChanged(), pero es menos eficiente.
                    // pilotoAdapter.notifyDataSetChanged()
                } else {
                    // Si por alguna razón no estaba en la lista filtrada (ej. el filtro cambió justo antes),
                    // simplemente volvemos a aplicar el filtro para asegurar la consistencia.
                    filtrarPilotos(searchViewPilotos.query.toString())
                }

                val nombrePilotoParaMensaje = piloto.nombre // O "${piloto.nombre} ${piloto.apellido}"
                if (isAdded) Toast.makeText(context, "'${nombrePilotoParaMensaje}' eliminado.", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                if (!isAdded) return@addOnFailureListener
                val nombrePilotoParaMensaje = piloto.nombre // O "${piloto.nombre} ${piloto.apellido}"
                Log.w(TAG, "Error eliminando '${nombrePilotoParaMensaje}' de Firestore.", e)
                if (isAdded) Toast.makeText(context, "Error al eliminar: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}