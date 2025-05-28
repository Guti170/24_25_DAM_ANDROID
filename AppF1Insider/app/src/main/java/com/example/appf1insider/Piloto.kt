package com.example.appf1insider.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize // Asegúrate de tener esta importación

@Parcelize // Anotación clave
data class Piloto(
    val id: String = "",
    val nombre: String = "",
    val imagen: String = "",
    val estadisticas: String = "", // Mantendremos esto como String por ahora, podrías parsearlo en la Activity de detalles
    val descripcion: String = ""
) : Parcelable { // Implementa Parcelable
    // Constructor vacío requerido por Firestore y Parcelize
    constructor() : this("", "", "", "", "")
}