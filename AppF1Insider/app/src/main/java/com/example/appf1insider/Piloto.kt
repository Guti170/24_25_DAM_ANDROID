package com.example.appf1insider.model

data class Piloto(
    val id: String = "", // Para el ID del documento de Firestore, si es necesario
    val nombre: String = "",
    val imagen: String = "", // Asumo que será una URL gs:// o HTTPS
    val estadisticas: String, // Ejemplo: "Victorias" to "10", "Poles" to "5"
    // O podría ser una subcolección o un String JSON
    val descripcion: String = ""
) {
    // Constructor vacío requerido por Firestore
    constructor() : this("", "", "", "", "")
}