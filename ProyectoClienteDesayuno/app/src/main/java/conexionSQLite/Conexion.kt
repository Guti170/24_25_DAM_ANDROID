package conexionSQLite

import android.content.ContentValues
import androidx.appcompat.app.AppCompatActivity
import listaMenus.Menus

object Conexion {
    private  var DATABASE_NAME = "menus.db3"
    private  var DATABASE_VERSION = 1


    fun cambiarBD(nombreBD:String){
        this.DATABASE_NAME = nombreBD
    }

    fun addMenu(contexto: AppCompatActivity, m: Menus):Long{
        val admin = AdminSQLIteConexion(contexto, this.DATABASE_NAME, null, DATABASE_VERSION)
        val bd = admin.writableDatabase
        val registro = ContentValues()
        registro.put("correo", m.correo)
        registro.put("nombreBebida", m.nombreBebida)
        registro.put("nombreComida", m.nombreComida)
        registro.put("nombreComplemento", m.nombreComplemento)
        registro.put("valorEnergeticoTotal", m.valorEnergeticoTotal.toString())
        val codigo = bd.insert("menus", null, registro)
        bd.close()
        return codigo
    }

}