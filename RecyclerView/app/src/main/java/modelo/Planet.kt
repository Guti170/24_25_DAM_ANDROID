package modelo

data class Planet (val name: String, val sizeKM: Int, val distanceAU: Double, val imageName: String = name.lowercase())
