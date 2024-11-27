package complementos

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import bebidas.Bebidas
import com.example.proyectoclientedesayuno.R
import com.example.proyectoclientedesayuno.databinding.ActivityListaComplementosBinding
import comidas.Comidas
import modeloBebida.Bebida
import modeloBebida.DatosBebida
import modeloComida.Comida
import modeloComida.DatosComida

class ListaComplementos : AppCompatActivity(), Bebidas.OnBebidaSeleccionadaListener, Comidas.OnComidaSeleccionadaListener {
    private lateinit var binding: ActivityListaComplementosBinding
    val bebidas = DatosBebida.getBebidas()
    val comidas = DatosComida.getComidas()
    var caloriasSeleccionadas = 0
    var proteinasSeleccionadas = 0
    var nombreImagenSeleccionada: String? = null

    override fun onBebidaSeleccionada(bebida: Bebida) {
        caloriasSeleccionadas = bebida.calorias
        proteinasSeleccionadas = bebida.proteinas
        nombreImagenSeleccionada = bebida.nombreImagen
    }

    override fun onComidaSeleccionada(comida: Comida) {
        caloriasSeleccionadas = comida.calorias
        proteinasSeleccionadas = comida.proteinas
        nombreImagenSeleccionada = comida.nombreImagen
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityListaComplementosBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.rbComida.setOnClickListener {
            binding.rvComidas.visibility = View.VISIBLE
            if (binding.rvComidas.visibility == View.VISIBLE) {
                binding.rvBebidas.visibility = View.INVISIBLE
            }
            var recyclerView: RecyclerView = findViewById(R.id.rvComidas)
            recyclerView.layoutManager = LinearLayoutManager(this)
            val comidasAdaptacion = Comidas(comidas.toMutableList(), this)
            recyclerView.adapter = comidasAdaptacion
        }

        binding.rbBebida.setOnClickListener {
            binding.rvBebidas.visibility = View.VISIBLE
            if (binding.rvBebidas.visibility == View.VISIBLE) {
                binding.rvComidas.visibility = View.INVISIBLE
            }
            var recyclerView: RecyclerView = findViewById(R.id.rvBebidas)
            recyclerView.layoutManager = LinearLayoutManager(this)
            val bebidasAdaptacion = Bebidas(bebidas.toMutableList(), this)
            recyclerView.adapter = bebidasAdaptacion
        }

        binding.btAceptar.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Confirmacion de Complemento")
            builder.setMessage("¿Estas seguro del complemento que has elegido?")
            builder.setPositiveButton("Sí") { dialog, which ->
                val intent = Intent()
                intent.putExtra("calorias", caloriasSeleccionadas)
                intent.putExtra("proteinas", proteinasSeleccionadas)
                intent.putExtra("nombreImagen", nombreImagenSeleccionada)
                setResult(Activity.RESULT_OK, intent)
                finish()
            }
            builder.setNegativeButton("No") { dialog, which ->

            }
            builder.show()
        }

    }
}