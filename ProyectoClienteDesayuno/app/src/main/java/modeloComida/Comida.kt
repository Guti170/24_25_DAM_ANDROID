package modeloComida

data class Comida(
    val nombre: String,
    val calorias: Int,
    val proteinas: Int,
    val nombreImagen: String = nombre.lowercase(),
)
