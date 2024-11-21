package bebidas

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectoclientedesayuno.R
import com.example.proyectoclientedesayuno.databinding.ActivityListaBebidaBinding
import modeloBebida.DatosBebida

class ListaBebida : AppCompatActivity() {
    private lateinit var binding: ActivityListaBebidaBinding
    val bebidas = DatosBebida.getBebidas()
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
        val bebidasAdaptacion = Bebidas(bebidas.toMutableList())
        recyclerView.adapter = bebidasAdaptacion

        binding.btAceptar.setOnClickListener {
            finish()
        }

    }
}