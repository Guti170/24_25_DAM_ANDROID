package modelo

object Almacen {
    var listaAlumnos = ArrayList<Alumno>()
    fun addAlumnos(alumno: Alumno) {
        listaAlumnos.add(alumno)
    }
    fun getAlumnos(): ArrayList<Alumno> {
        return listaAlumnos
    }
}