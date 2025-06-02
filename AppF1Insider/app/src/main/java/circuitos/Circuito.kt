package circuitos

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize // Anotación para generar automáticamente el código Parcelable
data class Circuito(
    val id: String = "",
    val nombre: String = "",
    val imagen: String = "",
    val descripcion: String = "",
    val video: String = ""
) : Parcelable { // Implementar Parcelable
    // Constructor vacío requerido por Firestore para deserialización automática
    constructor() : this("", "", "", "", "")
}