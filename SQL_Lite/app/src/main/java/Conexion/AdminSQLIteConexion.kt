package Conexion

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

class AdminSQLIteConexion(context: Context, name: String, factory: SQLiteDatabase.CursorFactory?, version: Int) : SQLiteOpenHelper(context, name, factory, version) {

    override fun onCreate(db: SQLiteDatabase) {
        Log.e("ACSCO","Paso por OnCreate del AdminSQLLite")
        /*
        Este método se lanza automáticamente cuando se ejecuta SQLite por primera vez (función sobrrescrita ya que es obigatoria)
        Aquí crearíamos las tablas y claves si son más de una.
        Se crean la primera vez si no existe. Pero tener en cuenta que aquí se gaurdarán configuraciones
        y pequeñas cosas, por lo tanto tampoco se metarán grandes sentencias yq que SQLite no está pensado para eso
        Para BBDD más complejas, ya usarmeos servicios externos.
        */
        db.execSQL("create table personas(dni text primary key, nombre text, edad int)")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        Log.e("ACSCO","Paso por OnUpgrade del AdminSQLLite")
        //si la BBDD ya existe, se modificaria con el SQL que aquí pongamos.
        //Si no existe se va al OnCreate, si existe, viene aquí.
        //para venir aquí has tenido que pasar una versión diferente y se detecta automáticamente y pasa por aquí.
        //por ejemplo podríamos borrar una tabla con DROP y luego crearla si interesa que una tabla esté siempre vacía
        //o le hago un truncate y me cargo sus datos directamente. (por ejemplo la típica tabla de registro de bitácora de la sesión)
    }
}