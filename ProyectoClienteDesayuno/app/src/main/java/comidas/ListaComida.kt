package comidas

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectoclientedesayuno.R
import com.example.proyectoclientedesayuno.databinding.ActivityListaComidaBinding
import modeloComida.Comida
import modeloComida.DatosComida

class ListaComida : AppCompatActivity(), Comidas.OnComidaSeleccionadaListener {
    private lateinit var binding: ActivityListaComidaBinding
    val comidas = DatosComida.getComidas()
    var caloriasSeleccionadas = 0
    var proteinasSeleccionadas = 0
    var nombreImagenSeleccionada: String? = null

    override fun onComidaSeleccionada(comida: Comida) {
        caloriasSeleccionadas = comida.calorias
        proteinasSeleccionadas = comida.proteinas
        nombreImagenSeleccionada = comida.nombreImagen
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityListaComidaBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //setContentView(R.layout.activity_lista_comida)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        var recyclerView: RecyclerView = findViewById(R.id.rvComidas)
        recyclerView.layoutManager = LinearLayoutManager(this)
        val comidasAdaptacion = Comidas(comidas.toMutableList(), this)
        recyclerView.adapter = comidasAdaptacion

        binding.btAceptar.setOnClickListener {
            val intent = Intent()
            intent.putExtra("calorias", caloriasSeleccionadas)
            intent.putExtra("proteinas", proteinasSeleccionadas)
            intent.putExtra("nombreImagen", nombreImagenSeleccionada)
            setResult(Activity.RESULT_OK, intent)
            finish()
            finish()
        }
    }
}