package pilotos

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize // Anotación clave
data class Piloto(
    val id: String = "",
    val nombre: String = "",
    val imagen: String = "",
    val estadisticas: String = "",
    val descripcion: String = ""
) : Parcelable { // Implementa Parcelable
    // Constructor vacío requerido por Firestore y Parcelize
    constructor() : this("", "", "", "", "")
}