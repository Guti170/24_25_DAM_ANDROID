package escuderias

import android.os.Parcelable
import kotlinx.parcelize.Parcelize // Asegúrate de tener esta importación

@Parcelize // Anotación clave
data class Escuderia(
    val id: String = "",
    val nombre: String = "",
    val imagen: String = "", // URL gs:// o HTTPS
    val estadisticas: String = "",
    val descripcion: String = ""
) : Parcelable { // Implementa Parcelable
    // Constructor vacío requerido por Firestore y Parcelize
    constructor() : this("", "", "", "", "")
}