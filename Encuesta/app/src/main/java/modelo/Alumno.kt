package modelo

import java.io.Serializable

data class Alumno(var contAlumnos: Int, var nombre: String, var sistemaOperativo: String, var especialidad: String, var horas: Int) : Serializable {
    override fun toString(): String {
        return "Encuesta $contAlumnos: $nombre, $sistemaOperativo, $especialidad, $horas\n"
    }
}
