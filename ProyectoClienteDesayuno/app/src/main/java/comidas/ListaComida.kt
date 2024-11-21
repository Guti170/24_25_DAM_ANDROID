package comidas

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectoclientedesayuno.R
import com.example.proyectoclientedesayuno.databinding.ActivityListaComidaBinding
import modeloComida.DatosComida

class ListaComida : AppCompatActivity() {
    private lateinit var binding: ActivityListaComidaBinding
    val comidas = DatosComida.getComidas()
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
        val comidasAdaptacion = Comidas(comidas.toMutableList())
        recyclerView.adapter = comidasAdaptacion

        binding.btAceptar.setOnClickListener {
            finish()
        }
    }
}