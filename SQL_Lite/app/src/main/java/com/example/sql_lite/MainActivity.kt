package com.example.sql_lite

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import Modelo.Persona
import Auxiliar.Conexion
import android.util.Log

class MainActivity : AppCompatActivity() {
    lateinit var edDNI: EditText
    lateinit var edNombre: EditText
    lateinit var edEdad: EditText
    lateinit var botonAdd: Button
    lateinit var botonBuscar: Button
    lateinit var botonBorrar: Button
    lateinit var botonEditar: Button
    lateinit var txtListdo: TextView
    //En este proyecto el acceso a los controles y la creación de los eventos está hecha de la primera manera que vimos
    //El motivo es didáctico, para que no lo perdamos de vista porque a veces es útil o más rápido y viene bien que
    //estéis familiarizados, porque en una empresa os podéis encontrar esta manera de hacerlo.
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.e("ACSCO","Paso por OnCreate ")
        setContentView(R.layout.activity_main)
        edDNI = findViewById(R.id.edDNI)
        edDNI.requestFocus()
        edNombre = findViewById<EditText>(R.id.edNombre)
        edEdad = findViewById<EditText>(R.id.edEdad)
        botonAdd = findViewById(R.id.btnAdd)
        botonBuscar = findViewById(R.id.btnBuscar)
        botonBorrar = findViewById(R.id.btnBorrar)
        botonEditar = findViewById(R.id.btnEditar)
        txtListdo = findViewById(R.id.txtListado)
    }

    fun addPersona(view: View) {
        if (edDNI.text.toString().trim().isEmpty() || edNombre.text.toString().trim().isEmpty()
            || edEdad.text.toString().trim().isEmpty()){
            Toast.makeText(this, "Campos en blanco", Toast.LENGTH_SHORT).show()
        }
        else {
            var pers: Persona = Persona(
                edDNI.getText().toString(),
                edNombre.getText().toString(),
                edEdad.getText().toString().toInt()
            )
            var codigo=Conexion.addPersona(this, pers)
            edDNI.setText("")
            edNombre.setText("")
            edEdad.setText("")
            edDNI.requestFocus()
            //la L es por ser un Long lo que trae codigo.
            if(codigo!=-1L) {
                Toast.makeText(this, "Persona insertada", Toast.LENGTH_SHORT).show()
                listarPersonas(view)
            }
            else
                Toast.makeText(this, "Ya existe ese DNI. Persona NO insertada", Toast.LENGTH_SHORT).show()
        }
    }

    fun delPersona(view: View) {
        var cant = Conexion.delPersona(this, edDNI.text.toString())
        edDNI.setText("")
        edNombre.setText("")
        edEdad.setText("")
        if (cant == 1) {
            Toast.makeText(this, "Se borró la persona con ese DNI", Toast.LENGTH_SHORT).show()
            listarPersonas(view)
        }
        else
            Toast.makeText(this, "No existe una persona con ese DNI", Toast.LENGTH_SHORT).show()

    }

    fun modPersona(view: View) {
        if (edDNI.text.toString().trim().isEmpty()|| edNombre.text.toString().trim().isEmpty()
            || edEdad.text.toString().trim().isEmpty()){
            Toast.makeText(this, "Campos en blanco", Toast.LENGTH_SHORT).show()
        }
        else {
            var pers: Persona = Persona(
                edDNI.getText().toString(),
                edNombre.getText().toString(),
                edEdad.getText().toString().toInt()
            )
            var cant = Conexion.modPersona(this, edDNI.text.toString(), pers)
            if (cant == 1)
                Toast.makeText(this, "Se modificaron los datos", Toast.LENGTH_SHORT).show()
            else
                Toast.makeText(this, "No existe una persona con ese DNI", Toast.LENGTH_SHORT).show()
        }
        listarPersonas(view)
    }

    fun buscarPersona(view: View) {
        var p:Persona? = null
        p = Conexion.buscarPersona(this, edDNI.text.toString())
        if (p!=null) {
            edNombre.setText(p.nombre)
            edEdad.setText(p.edad.toString())
        } else {
            Toast.makeText(this, "No existe una persona con ese DNI", Toast.LENGTH_SHORT).show()
        }

    }


    fun listarPersonas(view: View) {
        var listado:ArrayList<Persona> = Conexion.obtenerPersonas(this)

        txtListdo.setText("")

        if (listado.size==0) {
            Toast.makeText(this, "No existen datos en la tabla", Toast.LENGTH_SHORT).show()
        }
        else {
            for(p in listado){
                var cadena = p.dni + ", " + p.nombre + ", " + p.edad.toString() + "\r\n"
                txtListdo.append(cadena)
            }
        }
    }


}