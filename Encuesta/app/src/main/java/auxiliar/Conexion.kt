package auxiliar

import conexion.BaseDeDatosSQL
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import modelo.Alumno

object Conexion {
    private var DATABASE_VERSION = 1
    private var DATABASE_NAME = "alumnos.db3"
    private var TABLA_ALUMNOS = "alumnos"


    fun cambiarBD(nombreBD:String){
        this.DATABASE_NAME = nombreBD
    }

    fun addAlumno(contexto: Context, alumno: Alumno): Long {
        val admin = BaseDeDatosSQL(contexto, DATABASE_NAME, null, DATABASE_VERSION)
        val bd = admin.writableDatabase
        val registro = ContentValues()
        registro.put("contAlumnos", alumno.contAlumnos)
        registro.put("nombre", alumno.nombre)
        registro.put("sistemaOperativo", alumno.sistemaOperativo)
        registro.put("especialidad", alumno.especialidad)
        registro.put("horas", alumno.horas)
        val codigo = bd.insert(TABLA_ALUMNOS, null, registro)
        bd.close()
        if (codigo == -1L) {
            Toast.makeText(contexto, "Error al insertar alumno", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(contexto, "Alumno insertado", Toast.LENGTH_SHORT).show()
        }
        return codigo
    }

    fun delAlumno(contexto: AppCompatActivity, id: Int): Int {
        val admin = BaseDeDatosSQL(contexto, this.DATABASE_NAME, null, DATABASE_VERSION)
        val bd = admin.writableDatabase
        val cant = bd.delete("alumnos", "id=?", arrayOf(id.toString())) // Delete by ID
        bd.close()
        return cant
    }

    fun modAlumno(contexto: AppCompatActivity, id: Int, alumno: Alumno): Int {
        val admin = BaseDeDatosSQL(contexto, this.DATABASE_NAME, null, DATABASE_VERSION)
        val bd = admin.writableDatabase
        val registro = ContentValues()
        registro.put("contAlumnos", alumno.contAlumnos)
        registro.put("nombre", alumno.nombre)
        registro.put("sistemaOperativo", alumno.sistemaOperativo)
        registro.put("especialidad", alumno.especialidad)
        registro.put("horas", alumno.horas)
        val cant = bd.update("alumnos", registro, "id=?", arrayOf(id.toString())) // Update by ID
        bd.close()
        return cant
    }

    fun buscarAlumno(contexto: AppCompatActivity, id: Int): Alumno? {
        var alumno: Alumno? = null
        val admin = BaseDeDatosSQL(contexto, this.DATABASE_NAME, null, DATABASE_VERSION)
        val bd = admin.readableDatabase
        val fila = bd.rawQuery(
            "SELECT contAlumnos, nombre, sistemaOperativo, especialidad, horas FROM alumnos WHERE id=?",
            arrayOf(id.toString())
        )
        if (fila.moveToFirst()) {
            alumno = Alumno(
                fila.getInt(0), // contAlumnos
                fila.getString(1), // nombre
                fila.getString(2), // sistemaOperativo
                fila.getString(3), // especialidad
                fila.getInt(4) // horas
            )
        }
        bd.close()
        return alumno
    }

    fun obtenerAlumnos(contexto: Context): ArrayList<Alumno> {
        val admin = BaseDeDatosSQL(contexto, DATABASE_NAME, null, DATABASE_VERSION)
        val bd = admin.readableDatabase
        val alumnos = ArrayList<Alumno>()
        val cursor: Cursor = bd.rawQuery("SELECT * FROM $TABLA_ALUMNOS", null)
        if (cursor.moveToFirst()) {
            do {
                val contAlumnos = cursor.getInt(cursor.getColumnIndexOrThrow("contAlumnos"))
                val nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre"))
                val sistemaOperativo = cursor.getString(cursor.getColumnIndexOrThrow("sistemaOperativo"))
                val especialidad = cursor.getString(cursor.getColumnIndexOrThrow("especialidad"))
                val horas = cursor.getInt(cursor.getColumnIndexOrThrow("horas"))
                alumnos.add(Alumno(contAlumnos, nombre, sistemaOperativo, especialidad, horas))
            } while (cursor.moveToNext())
        }
        cursor.close()
        bd.close()
        return alumnos
    }

}