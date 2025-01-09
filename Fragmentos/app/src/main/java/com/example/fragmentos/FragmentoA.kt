package com.example.fragmentos

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.fragmentos.databinding.FragmentFragmentoABinding

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [FragmentoA.newInstance] factory method to
 * create an instance of this fragment.
 */
class FragmentoA : Fragment() {
    // TODO: Rename and change types of parameters

    private lateinit var binding: FragmentFragmentoABinding

//    private var param1: String? = null
//    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        arguments?.let {
//            param1 = it.getString(ARG_PARAM1)
//            param2 = it.getString(ARG_PARAM2)
//        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentFragmentoABinding.inflate(inflater, container, false)
        val view = binding.root
        // Gets the data from the passed bundle
        val bundle = arguments
        val message = bundle!!.getString("variable1")

        // Sets the derived data (type String) in the TextView
        binding.txtF1.text = message

        return view
        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_fragmento_a, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //No tiene mucho sentido regargar elmimso Activity como hace este ejemplo, pero sí se podría ir a otro,así tenéis el código.
        //val boton: Button = view.findViewById<Button>(R.id.btnFA)
        binding.btnFA.setOnClickListener(){
//            val texto = requireActivity().findViewById<View>(R.id.edCaja) as EditText
//            texto.setText("Desde el fragment")
            val editProfileIntent = Intent(this.context, MainActivity::class.java)
            editProfileIntent.putExtra("ValorFromIntent","Desde Intent")
            startActivity(editProfileIntent)
            Toast.makeText(this.context,"Pulsado el botón de dentro del F1", Toast.LENGTH_SHORT).show()
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment FragmentoA.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            FragmentoA().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}