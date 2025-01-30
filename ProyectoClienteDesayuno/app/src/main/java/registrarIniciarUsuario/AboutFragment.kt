package registrarIniciarUsuario

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.proyectoclientedesayuno.databinding.FragmentAboutBinding

class AboutFragment : Fragment() {
    private var _binding: FragmentAboutBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAboutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Aquí puedes agregar lógica para mostrar la información de la aplicación y los autores
        binding.textViewAuthors.text = "Autores: \nMiguel Angel Gutierrez Estevez \nJavier Jimenez Perez"
        binding.textViewAppInfo.text = "Información de la aplicación: \nVersión 1.0 \nLa aplicacion consiste en la creacion de un desayuno saludable" +
                " donde el usuario podra introducir una varidad de bebidas, comidas y complementos para crear ese ansiado desayuno, ademas cuenta con un" +
                " sistema de poder editar el perfil y tambien con un soporte en caso de que necesite el usuario contactar con nosotros. Gracias por utilizar" +
                " nuestra aplicacion."
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}