package com.example.appf1insider.model

import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class ComentarioPiloto(
    var id: String = "", // ID del comentario, generado por Firestore
    val itemId: String = "", // ID del circuito O DEL PILOTO al que pertenece el comentario
    val usuarioEmail: String = "", // Email del usuario que comenta
    val texto: String = "",
    @ServerTimestamp
    val timestamp: Date? = null // Para ordenar por fecha
) {
    // Constructor sin argumentos requerido por Firestore
    constructor() : this("", "", "", "", null)
}