package com.example.fotosgaleriafirebase

import android.Manifest
import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import com.example.fotosgaleriafirebase.databinding.ActivityMainBinding
import com.google.firebase.storage.FirebaseStorage
import java.io.IOException

class MainActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMainBinding
    private val cameraRequest = 1888
    private lateinit var bitmap : Bitmap
    var uriImagen : Uri? = null
    val TAG  = "ACSCO"

    //------------------------- Funciones de callback para activities results -------------------------
    //Activity para lanzar la galería de imágenes.
    val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        // Callback is invoked after the user selects a media item or closes the
        // photo picker.
        if (uri != null) {
            Log.d(TAG, "Selected URI: $uri")
            this.uriImagen = uri
            binding.img.setImageURI(uri)
            Log.d(TAG, "Cargada")
        } else {
            Log.d(TAG, "No media selected")
        }
    }

    //Segunda activity para lanzar la cámara.
    val openCamera = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            val data = result.data!!
            this.bitmap = data.extras!!.get("data") as Bitmap
            binding.img.setImageBitmap(bitmap)
        }
    }

    //Activity para pedir permisos de cámara.
    val requestCameraPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
        if (isGranted) {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            //si quisieras vídeo. Poner el punto y ver resto de opciones que ofrece, que prueben alguna.
            //val intent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
            openCamera.launch(intent)
        } else {
            Log.e(TAG,"Permiso de cámara no concedido")
        }
    }


    //-----------------------------------------------------------
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_main)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        if (ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED)
//            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), cameraRequest)

        binding.openCamera.setOnClickListener {
            requestCameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }

        binding.saveToGallery.setOnClickListener {
            saveImageToGallery(binding.txtImagen.text.toString())
        }

        binding.openGallery.setOnClickListener{
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            //si quisieras que se vean imágenes y vídeos de la galería.
            // pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageAndVideo))
        }

        binding.uploadToFirebase.setOnClickListener {
            uploadImageToFirebase(binding.txtImagen.text.toString())
        }

    }


    //----------------------- Funciones lanzadas por los botones -------------------------

    /**
     * Guardar imagen en la galería. Se guardará con el nombre que pongamos en la editText.
     */
    private fun saveImageToGallery(filename:String = "my_image.jpg"): Uri? {

        val filepath = Environment.DIRECTORY_PICTURES
        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, filename)
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            put(MediaStore.Images.Media.RELATIVE_PATH, filepath)
        }
        val contentResolver = contentResolver
        val uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

        try {
            val outputStream = contentResolver.openOutputStream(uri!!)
            if (outputStream != null) {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            }
            outputStream?.close()
        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(this, "Se ha producido algún error al almcenar la imagen en la galería.",
                Toast.LENGTH_SHORT).show()
            return null
        }
        Toast.makeText(this, "Imagen almacenada en la galería", Toast.LENGTH_SHORT).show()
        binding.txtImagen.setText("")
        return uri
    }

    //-------------------------------------- Funcion para guardar la foto en FireBase --------------------------------------

    private fun uploadImageToFirebase(filename: String = "my_image.jpg") {
        if (uriImagen == null) {
            Toast.makeText(this, "No se ha seleccionado ninguna imagen", Toast.LENGTH_SHORT).show()
            return
        }

        val storageRef = FirebaseStorage.getInstance().reference.child("images/$filename.jpg") // Ajusta la ruta
        val uploadTask = storageRef.putFile(uriImagen!!)

        uploadTask.addOnSuccessListener {
            // La imagen se ha subido correctamente
            Toast.makeText(this, "Imagen subida a Firebase Storage", Toast.LENGTH_SHORT).show()
            binding.txtImagen.setText("")
        }.addOnFailureListener { exception ->
            // Se ha producido un error al subir la imagen
            Toast.makeText(this, "Error al subir la imagen a Firebase Storage: ${exception.message}", Toast.LENGTH_SHORT).show()
            Log.e(TAG, "Error al subir la imagen a Firebase Storage: ${exception.message}")
        }
    }


//    //------------------------------------- Auxiliares --------------------------------------
//    private fun getBitmap(): Bitmap {
//        return BitmapFactory.decodeFile(file.toString())
//    }
//
//    private fun createPhotoFile() {
//        val dir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!
//        file = File.createTempFile("IMG_${System.currentTimeMillis()}_", ".jpg", dir)
//    }
}