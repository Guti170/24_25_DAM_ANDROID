package modelo

object Almacen {
    var listaAlumnos = ArrayList<Alumno>()
    fun addAlumno(alumno: Alumno) {
        listaAlumnos.add(alumno)
    }
    fun getAlumno(): ArrayList<Alumno> {
        return listaAlumnos
    }
}