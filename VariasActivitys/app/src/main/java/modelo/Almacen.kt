package modelo

object Almacen {
    var listaPersonas = ArrayList<Persona>()
    fun addPersona(p: Persona) {
        listaPersonas.add(p)
    }
    fun getPersonas(): ArrayList<Persona> {
        return listaPersonas
    }
}