package registrarIniciarUsuario

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.proyectoclientedesayuno.databinding.FragmentEditUserBinding
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.ByteArrayOutputStream
import java.util.UUID

class EditUserFragment : Fragment() {
    private var _binding: FragmentEditUserBinding? = null
    private val binding get() = _binding!!

    private val REQUEST_CODE_PERMISSIONS = 10
    private val REQUEST_CODE_SELECT_IMAGE = 1
    private val REQUEST_CODE_TAKE_PICTURE = 2
    private var imageUri: Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditUserBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requestPermissions()

        binding.saveButton.setOnClickListener {
            if (imageUri != null) {
                uploadImageToFirebaseStorage(imageUri!!)
            } else {
                saveDataToFirestore(null)
            }
        }

        binding.selectImageButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, REQUEST_CODE_SELECT_IMAGE)
        }

        binding.takePictureButton.setOnClickListener {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(intent, REQUEST_CODE_TAKE_PICTURE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_CODE_SELECT_IMAGE -> {
                    imageUri = data?.data
                    binding.userImagen.setImageURI(imageUri)
                }

                REQUEST_CODE_TAKE_PICTURE -> {
                    val imageBitmap = data?.extras?.get("data") as Bitmap
                    binding.userImagen.setImageBitmap(imageBitmap)
                    imageUri = getImageUri(imageBitmap)
                }
            }
        }
    }

    private fun getImageUri(inImage: Bitmap): Uri? {
        val bytes = ByteArrayOutputStream()
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path = MediaStore.Images.Media.insertImage(
            requireContext().contentResolver,
            inImage,
            "Title",
            null
        )
        return Uri.parse(path)
    }

    private fun uploadImageToFirebaseStorage(fileUri: Uri) {
        val storageRef: StorageReference = FirebaseStorage.getInstance().reference
        val imageName = UUID.randomUUID().toString()
        val imageRef: StorageReference = storageRef.child("images/$imageName")

        imageRef.putFile(fileUri)
            .addOnSuccessListener { taskSnapshot ->
                imageRef.downloadUrl.addOnSuccessListener { uri ->
                    val imageUrl = uri.toString()
                    saveDataToFirestore(imageUrl)
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), "Error al subir la imagen: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun saveDataToFirestore(imageUrl: String?) {
        val menu = hashMapOf(
            "imagen" to imageUrl,
            "altura" to binding.userAltura.text.toString(),
            "peso" to binding.userPeso.text.toString(),
            "fechaCreacion" to FieldValue.serverTimestamp()
        )

        val db = FirebaseFirestore.getInstance()
        db.collection("informacion")
            .add(menu)
            .addOnSuccessListener { documentReference ->
                Toast.makeText(
                    requireContext(),
                    "Usuario guardado correctamente",
                    Toast.LENGTH_SHORT
                ).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(
                    requireContext(),
                    "Error al guardar el usuario: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    private fun requestPermissions() {
        val permissionsToRequest = mutableListOf<String>()

        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            permissionsToRequest.add(Manifest.permission.CAMERA)
        }

        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            permissionsToRequest.add(Manifest.permission.READ_EXTERNAL_STORAGE)
        }

        if (permissionsToRequest.isNotEmpty()) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                permissionsToRequest.toTypedArray(),
                REQUEST_CODE_PERMISSIONS
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permiso concedido
                Toast.makeText(requireContext(), "Permisos concedidos", Toast.LENGTH_SHORT).show()
            } else {
                // Permiso denegado
                Toast.makeText(requireContext(), "Permisos denegados", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}