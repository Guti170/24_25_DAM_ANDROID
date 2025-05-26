package com.example.appf1insider.model

data class Escuderia(
    val id: String = "",
    val nombre: String = "",
    val imagen: String = "", // URL gs:// o HTTPS
    val estadisticas: String = "", // Puede ser un String simple, JSON, o podrías cambiarlo a Map si es más complejo
    val descripcion: String = ""
) {
    // Constructor vacío requerido por Firestore
    constructor() : this("", "", "", "", "")
}