package com.example.appf1insider.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize // Importante

@Parcelize // Anotación para generar automáticamente el código Parcelable
data class Circuito(
    val id: String = "",
    val nombre: String = "",
    val imagen: String = "",
    val descripcion: String = "",
    val video: String = ""
    // Puedes añadir más campos si los tienes
) : Parcelable { // Implementar Parcelable
    // Constructor vacío requerido por Firestore para deserialización automática
    // No es necesario modificarlo para Parcelable si usas @Parcelize
    constructor() : this("", "", "", "", "")
}