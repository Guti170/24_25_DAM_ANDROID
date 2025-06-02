package calendario

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
import com.example.appf1insider.R
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase

class Calendario : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var calendarioAdapter: CalendarioAdapter // CAMBIO
    private lateinit var progressBar: ProgressBar
    private lateinit var db: FirebaseFirestore
    private val TAG = "CalendarioFragment" // CAMBIO (Opcional)

    private val listaDeEventos = mutableListOf<EventoCalendario>() // CAMBIO

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        db = Firebase.firestore
        // Eliminamos la lógica de ARG_PARAM1 y ARG_PARAM2
    }

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_calendario, container, false) // CAMBIO

        recyclerView = view.findViewById(R.id.recyclerViewCalendario) // CAMBIO
        progressBar = view.findViewById(R.id.progressBarCalendario)   // CAMBIO

        recyclerView.layoutManager = LinearLayoutManager(context)
        calendarioAdapter = CalendarioAdapter(listaDeEventos) // CAMBIO
        recyclerView.adapter = calendarioAdapter

        if (listaDeEventos.isEmpty()) {
            fetchCalendarioData() // CAMBIO
        } else {
            progressBar.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE
        }

        return view
    }

    private fun fetchCalendarioData() { // CAMBIO
        progressBar.visibility = View.VISIBLE
        recyclerView.visibility = View.GONE

        // CAMBIO: Nombre de la colección en Firestore.
        // Si tienes un campo para ordenar (ej. 'fechaInicio' de tipo Timestamp o 'orden' de tipo Number), úsalo.
        // Ejemplo: .orderBy("fechaInicio", Query.Direction.ASCENDING)
        db.collection("calendario") // Asegúrate que "calendario" es el nombre correcto
            // Si quieres ordenar, por ejemplo por un campo "orden" o una fecha:
            // .orderBy("orden", Query.Direction.ASCENDING) // o "fechaTimestamp"
            .get()
            .addOnSuccessListener { documents ->
                if (documents.isEmpty) {
                    Log.d(TAG, "No se encontraron eventos en el calendario.")
                    Toast.makeText(context, "No hay eventos para mostrar", Toast.LENGTH_SHORT).show()
                    progressBar.visibility = View.GONE
                    recyclerView.visibility = View.GONE
                    return@addOnSuccessListener
                }

                val tempList = mutableListOf<EventoCalendario>() // CAMBIO
                for (document in documents) {
                    val evento = document.toObject<EventoCalendario>() // CAMBIO
                    tempList.add(evento)
                    // CAMBIO: Log con los campos del evento
                    Log.d(TAG, "Evento cargado: ${evento.nombre}, Imagen: ${evento.imagen}, Horario: ${evento.horario}, Temporada: ${evento.temporada}")
                }

                listaDeEventos.clear()
                listaDeEventos.addAll(tempList)
                calendarioAdapter.notifyDataSetChanged()

                progressBar.visibility = View.GONE
                recyclerView.visibility = View.VISIBLE
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error obteniendo documentos del calendario: ", exception)
                Toast.makeText(context, "Error al cargar el calendario: ${exception.message}", Toast.LENGTH_LONG).show()
                progressBar.visibility = View.GONE
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }

    companion object {
        @JvmStatic
        fun newInstance() = Calendario() // Simplificado
    }
}