package com.example.appf1insider.model // O tu paquete de modelos

data class Circuito(
    val id: String = "", // Para almacenar el ID del documento si es necesario
    val nombre: String = "",
    val imagen: String = "",
    val descripcion: String = "", // Nuevo campo
    val video: String = ""     // Nuevo campo
    // Puedes añadir más campos si los tienes
) {
    // Constructor vacío requerido por Firestore para deserialización automática
    constructor() : this("", "", "", "", "") // Actualiza el constructor vacío
}