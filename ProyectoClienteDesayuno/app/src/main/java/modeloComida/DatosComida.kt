package modeloComida

import com.google.firebase.storage.FirebaseStorage

object DatosComida {
    private val comidas = mutableListOf(
        Comida("Tostadas", 102, 3, "tostadas"),
        Comida("Galletas", 100, 7, "galletas"),
        Comida("Frutas", 4, 2, "fruta"),
        Comida("Bollos", 300, 3, "bollos")
    )

    fun getComidas(): List<Comida> {
        return comidas
    }
}