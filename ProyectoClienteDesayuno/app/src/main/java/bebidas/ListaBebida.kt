package bebidas

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectoclientedesayuno.R
import com.example.proyectoclientedesayuno.databinding.ActivityListaBebidaBinding
import modeloBebida.Bebida
import modeloBebida.DatosBebida

class ListaBebida : AppCompatActivity(), Bebidas.OnBebidaSeleccionadaListener {
    private lateinit var binding: ActivityListaBebidaBinding
    val bebidas = DatosBebida.getBebidas()
    var caloriasSeleccionadas = 0
    var proteinasSeleccionadas = 0
    var nombreImagenSeleccionada: String? = null

    override fun onBebidaSeleccionada(bebida: Bebida) {
        caloriasSeleccionadas = bebida.calorias
        proteinasSeleccionadas = bebida.proteinas
        nombreImagenSeleccionada = bebida.nombreImagen
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityListaBebidaBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //setContentView(R.layout.activity_lista_bebida)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        var recyclerView: RecyclerView = findViewById(R.id.rvBebidas)
        recyclerView.layoutManager = LinearLayoutManager(this)
        val bebidasAdaptacion = Bebidas(bebidas.toMutableList(), this)
        recyclerView.adapter = bebidasAdaptacion

        binding.btAceptar.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Confirmacion de Bebida")
            builder.setMessage("¿Estas seguro de la bebida que has elegido?")
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