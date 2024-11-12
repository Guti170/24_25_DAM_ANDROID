package conexion

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

class BaseDeDatosSQL(context: Context, name: String, factory: SQLiteDatabase.CursorFactory?, version: Int) : SQLiteOpenHelper(context, name, factory, version) {

    override fun onCreate(db: SQLiteDatabase) {
        Log.e("ACSCO", "Paso por OnCreate del AdminSQLLite")

        // Crea la tabla alumnos con ID autoincrementable
        db.execSQL("create table alumnos(" +
                "id integer primary key autoincrement," +
                "contAlumnos integer," +
                "nombre text," +
                "sistemaOperativo text," +
                "especialidad text," +
                "horas integer)")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        Log.e("ACSCO", "Paso por OnUpgrade del AdminSQLLite")

        // Aquí puedes manejar las actualizaciones de la base de datos
        // Por ejemplo, si necesitas agregar una nueva columna, puedes hacerlo aquí
    }
}