package escuderias

import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class ComentarioEscuderia(
    var id: String = "", // ID del comentario, generado por Firestore
    val itemId: String = "", // ID del circuito, PILOTO O ESCUDERÍA al que pertenece el comentario
    val usuarioEmail: String = "", // Email del usuario que comenta
    val texto: String = "",
    @ServerTimestamp
    val timestamp: Date? = null // Para ordenar por fecha
) {
    // Constructor sin argumentos requerido por Firestore
    constructor() : this("", "", "", "", null)
}