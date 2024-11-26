package modeloBebida

object DatosBebida {
    private val bebidas = mutableListOf(
        Bebida("Vaso de Agua", 0, 0, "agua"),
        Bebida("Taza de Cafe", 5, 1, "cafe"),
        Bebida("Vaso de Leche", 100, 7, "leche"),
        Bebida("Vaso de Zumos", 45, 1, "zumo")
    )

    fun getBebidas(): List<Bebida> {
        return bebidas
    }
}