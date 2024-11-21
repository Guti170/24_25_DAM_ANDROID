package modeloBebida

data class Bebida(val nombre: String, val calorias: Int, val proteinas: Int, val nombreImagen: String = nombre.lowercase())
