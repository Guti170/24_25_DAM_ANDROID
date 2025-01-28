package registrarIniciarUsuario

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import com.example.proyectoclientedesayuno.R
import com.example.proyectoclientedesayuno.databinding.FragmentEditUserBinding
import com.google.firebase.firestore.FirebaseFirestore

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [EditUserFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class EditUserFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var imageView: ImageView
    private lateinit var editTextAltura: EditText
    private lateinit var editTextPeso: EditText

    lateinit var binding: FragmentEditUserBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragmentEditUserBinding.inflate(layoutInflater)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_edit_user, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        imageView = view.findViewById(R.id.user_imagen)
        editTextAltura = view.findViewById(R.id.user_altura)
        editTextPeso = view.findViewById(R.id.user_peso)

        binding.saveButton.setOnClickListener {
            val menu = hashMapOf(
                "imagen" to imageView,
                "altura" to editTextAltura,
                "peso" to editTextPeso
            )

            val db = FirebaseFirestore.getInstance()
            db.collection("informacion")
                .add(menu)
                .addOnSuccessListener { documentReference ->
                    Toast.makeText(requireContext(), "Usuario guardado correctamente con ID: ${documentReference.id}", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(requireContext(), "Error al guardar el usuario: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }

        binding.selectImageButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, REQUEST_CODE_SELECT_IMAGE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE_SELECT_IMAGE && resultCode == Activity.RESULT_OK && data != null) {
            val selectedImageUri = data.data
            imageView.setImageURI(selectedImageUri)
        }
    }

    companion object {
        private const val REQUEST_CODE_SELECT_IMAGE = 1
    }
}