package registrarIniciarUsuario

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.proyectoclientedesayuno.R
import com.example.proyectoclientedesayuno.databinding.ActivitySupportBinding

class SupportActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySupportBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySupportBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //setContentView(R.layout.activity_support)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Agregar el SupportFragment al contenedor
        supportFragmentManager.beginTransaction()
            .replace(binding.fragmentContainer.id, SupportFragment())
            .commit()
    }
}