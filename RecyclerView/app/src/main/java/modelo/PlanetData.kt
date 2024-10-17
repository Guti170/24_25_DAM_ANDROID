package modelo

object PlanetData {
    private val planets = mutableListOf(
        Planet("Mercurio", 4878, 0.39, "mercurio"),
        Planet("Venus", 12104, 0.72, "venus"),
        Planet("Tierra", 12756, 1.0, "tierra"),
        Planet("Marte", 6792, 1.52, "marte"),
        Planet("Jupiter", 142984, 5.2, "jupiter"),
        Planet("Saturno", 120536, 9.58, "saturno"),
        Planet("Urano", 51118, 19.18, "urano"),
        Planet("Neptuno", 49528, 30.06, "neptuno")
    )

    fun getPlanets(): List<Planet> {
        return planets
    }

}