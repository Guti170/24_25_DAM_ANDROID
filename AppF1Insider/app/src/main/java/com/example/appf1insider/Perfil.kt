package com.example.appf1insider

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

private const val ARG_USER_EMAIL = "user_email"

class Perfil : Fragment() {
    private var userEmail: String? = null
    private lateinit var auth: FirebaseAuth

    private lateinit var textViewUserEmail: TextView
    private lateinit var textViewAppInfo: TextView
    private lateinit var buttonPrivacyPolicy: Button
    private lateinit var buttonLogout: Button

    companion object {
        private const val TAG = "PerfilFragment"

        @JvmStatic
        fun newInstance(email: String?, displayName: String?) = // Solo email
            Perfil().apply {
                arguments = Bundle().apply {
                    putString(ARG_USER_EMAIL, email)
                }
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            userEmail = it.getString(ARG_USER_EMAIL)
            // Lógica para displayName y photoUrl eliminada
        }
        auth = Firebase.auth
    }

    @SuppressLint("StringFormatInvalid")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_perfil, container, false)

        // Inicialización de las vistas que existen en el XML
        textViewUserEmail = view.findViewById(R.id.textViewUserEmail)

        textViewAppInfo = view.findViewById(R.id.textViewAppInfo)
        buttonPrivacyPolicy = view.findViewById(R.id.buttonPrivacyPolicy)
        buttonLogout = view.findViewById(R.id.buttonLogout)

        // Lógica para cargar imagen de perfil eliminada

        // Mostrar el email del usuario
        textViewUserEmail.text = userEmail ?: getString(R.string.email_not_available)

        // Lógica para mostrar nombre de usuario eliminada

        // Información de la aplicación
        try {
            val versionName = requireActivity().packageManager
                .getPackageInfo(requireActivity().packageName, 0).versionName
            textViewAppInfo.text = getString(R.string.app_info_placeholder, versionName)
        } catch (e: Exception) {
            Log.e(TAG, "Error al obtener la versión de la app", e)
            textViewAppInfo.text = getString(R.string.app_info_fallback)
        }

        buttonPrivacyPolicy.setOnClickListener {
            showPrivacyPolicyDialog()
        }

        buttonLogout.setOnClickListener {
            logoutUser()
        }

        return view
    }

    private fun showPrivacyPolicyDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.privacy_policy_title))
            .setMessage(getString(R.string.privacy_policy_text))
            .setPositiveButton(getString(R.string.privacy_policy_button_ok)) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun logoutUser() {
        Log.d(TAG, "Iniciando proceso de cierre de sesión.")
        auth.signOut()
        Log.d(TAG, "Cierre de sesión de Firebase Auth completado.")

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        val googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)

        googleSignInClient.signOut().addOnCompleteListener(requireActivity()) { task ->
            if (task.isSuccessful) {
                Log.d(TAG, "Cierre de sesión de Google Sign-In completado.")
            } else {
                Log.w(TAG, "Fallo al cerrar sesión de Google Sign-In.", task.exception)
            }
            navigateToLoginScreen()
        }
    }

    private fun navigateToLoginScreen() {
        Log.d(TAG, "Navegando a MainActivity.")
        val intent = Intent(activity, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        activity?.finish()
    }
}