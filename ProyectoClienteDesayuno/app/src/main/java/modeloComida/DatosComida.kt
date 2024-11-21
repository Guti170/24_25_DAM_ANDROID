package modeloComida

object DatosComida {
    private val comidas = mutableListOf(
        Comida("Tostadas", 100, 20, "tostadas"),
        Comida("Galletas", 100, 20, "galletas"),
        Comida("Frutas", 100, 20, "fruta"),
        Comida("Bollos", 100, 20, "bollos")
    )

    fun getComidas(): List<Comida> {
        return comidas
    }
}