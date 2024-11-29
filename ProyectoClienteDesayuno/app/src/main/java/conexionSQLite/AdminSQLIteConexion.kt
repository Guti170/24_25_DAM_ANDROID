package conexionSQLite

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

class AdminSQLIteConexion(context: Context, name: String, factory: SQLiteDatabase.CursorFactory?, version: Int) : SQLiteOpenHelper(context, name, factory, version) {

    override fun onCreate(db: SQLiteDatabase) {
        Log.e("ACSCO","Paso por OnCreate del AdminSQLLite")
        db.execSQL("create table menus(correo text primary key, nombreBebida text, nombreComida text, " +
                "nombreComplemento text, valorEnergeticoTotal int)")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        Log.e("ACSCO","Paso por OnUpgrade del AdminSQLLite")
    }
}