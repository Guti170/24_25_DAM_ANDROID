package calendario

data class EventoCalendario(
    val id: String = "",
    val nombre: String = "",      // Ej: "Gran Premio de Bahrein"
    val imagen: String = "",      // URL gs:// o HTTPS de la imagen del evento/circuito
    val horario: String = "",     // Ej: "10-12 MAR", "15:00 CET" o podrías usar Timestamp de Firestore si necesitas más precisión
    val temporada: Int = 0    // Ej: "2023"
    // Podrías añadir más campos como 'circuitoNombre', 'pais', etc. si los necesitas
) {
    // Constructor vacío requerido por Firestore
    constructor() : this("", "", "", "", 0)
}