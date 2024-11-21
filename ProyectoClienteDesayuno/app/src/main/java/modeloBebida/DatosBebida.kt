package modeloBebida

object DatosBebida {
    private val bebidas = mutableListOf(
        Bebida("Vaso de Agua", 100, 20, "agua"),
        Bebida("Taza de Cafe", 100, 20, "cafe"),
        Bebida("Vaso de Leche", 100, 20, "leche"),
        Bebida("Vaso de Zumos", 100, 20, "zumo")
    )

    fun getBebidas(): List<Bebida> {
        return bebidas
    }
}