package com.example.firebase

import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.firebase.databinding.ActivityUsoStorageBinding
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import java.io.File

class UsoStorage : AppCompatActivity() {
    private lateinit var binding: ActivityUsoStorageBinding
    /*
    https://firebase.google.com/docs/storage/android/create-reference
    Crea una referencia para subir, descargar o borrar un archivo, o para obtener o actualizar sus metadatos.
    Se puede decir que una referencia es un indicador que apunta a un archivo en la nube. Las referencias son livianas,
    por lo que puedes crear todas las que necesites. También se pueden reutilizar en varias operaciones.
     */
    var storage = Firebase.storage
    // Crea una referencia con la instancia singleton FirebaseStorage y con una llamada al método reference.
    var storageRef = storage.reference
    val TAG = "ACSCO"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        //setContentView(R.layout.activity_uso_storage)
        binding = ActivityUsoStorageBinding.inflate(layoutInflater)
        setContentView(binding.root) // Create a child reference
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // imagesRef now points to "images"
        var imagesRef: StorageReference? = storageRef.child("paisajes")

        // Child references can also take paths
        // spaceRef now points to "images/space.jpg
        // imagesRef still points to "images"
        var spaceRef = storageRef.child("paisajes/pai1.jpeg")


        // parent allows us to move our reference to a parent node
        // imagesRef now points to 'images'
        imagesRef = spaceRef.parent
        Log.e(TAG, imagesRef!!.path.toString())

        // root allows us to move all the way back to the top of our bucket
        // rootRef now points to the root
        val rootRef = spaceRef.root
        Log.e(TAG, rootRef.path.toString())



        // File path is "images/spock.jpg"
        val path = spaceRef.path
        Log.e(TAG, path.toString())

        // File name is "spock.jpg"
        val name = spaceRef.name
        Log.e(TAG, name.toString())

        //Listar todos.
        val listRef = storage.reference.child("paisajes")
        listRef.listAll()
            .addOnSuccessListener {
                Log.d(TAG,"Items en paisajes")
                for (item in it.items) {
                    Log.e(TAG,item.toString())
                }
            }
            .addOnFailureListener {
                // Uh-oh, an error occurred!
            }

        binding.btCargar.setOnClickListener {
            fileUpload()
        }

        binding.btnDescargar.setOnClickListener {
            fileDownload()
        }
    }

    /**
     * Método que descarga el fichero usando un archivo temporal.
     */
    fun fileDownload() {
        var spaceRef = storageRef.child("paisajes/pai2.jpeg")

        val localfile  = File.createTempFile("tempImage","jpeg")
        spaceRef.getFile(localfile).addOnSuccessListener {
            val bitmap = BitmapFactory.decodeFile(localfile.absolutePath)
            binding.imgImagen.setImageBitmap(bitmap)
        }.addOnFailureListener{
            Toast.makeText(this,"Algo ha fallado en la descarga", Toast.LENGTH_SHORT).show()
        }

    }

    /**
     * Subida de imagen. Seleccionamos de la galería de imágenes (ver ejemplo de cargar Fotos). Si queremos subir de la cámara ver ejemplo Fotos.
     */
    //------------------------- Funciones de callback para activities results -------------------------
    //Activity para lanzar la galería de imágenes.
    val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        // Callback is invoked after the user selects a media item or closes the
        // photo picker.
        if (uri != null) {
            Log.d("ACSCO", "Selected URI: $uri")
            binding.imgImagen.setImageURI(uri)
            Log.d("ACSCO", "Cargada")
            val Folder: StorageReference =
                FirebaseStorage.getInstance().getReference().child("paisajes")
//            val file_name: StorageReference = Folder.child("" + uri!!.lastPathSegment) //<-- Podemos coger el último segmento de toda la ruta como nombre.
            val file_name: StorageReference = Folder.child("Otronombre")  //<-- Podemos poner el nombre que queramos.
            file_name.putFile(uri).addOnSuccessListener { taskSnapshot ->
                file_name.getDownloadUrl().addOnSuccessListener { uri ->
                    Toast.makeText(this,"Imagen ${uri.toString()} subida correctamente", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            Log.d("ACSCO", "No media selected")
        }
    }

    fun fileUpload(){
        pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }
}