package modelo

import java.io.Serializable

data class Alumno(var nombre: String, var sistemaOperativo: String, var especialidad: String, var horas: Int) : Serializable
