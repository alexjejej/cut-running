package com.raywenderlich.android.runtracking

import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.commit
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.raywenderlich.android.runtracking.databinding.FragmentLoginBinding
import com.raywenderlich.android.rwandroidtutorial.models.Session

/**
 * A simple [Fragment] subclass.
 * Use the [LoginFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class LoginFragment : Fragment() {
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private lateinit var layout: LinearLayout
    private lateinit var auth: FirebaseAuth
    private lateinit var signInRequest: BeginSignInRequest
    private lateinit var oneTapClient: SignInClient
    private var prefs: SharedPreferences? = null

    private val REQUEST_ONE_TAP = 2 // Puede ser cualquier valor entero unico para el Fragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        Log.d("LoginFragment", "Inicio de sesion desde fragment login")
        setup()
        session()
        initialiceGoogleutentication()
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        @JvmStatic
        fun newInstance() = LoginFragment()
    }

    override fun onStart() { // Se invoca cada vez que se vuelva a mostrar el fragment en pantalla
        super.onStart()
        // Mostramos de nuevo el layout en caso de que hagamos un logout y se regrese a este fragment
        layout.visibility = View.VISIBLE
    }

    /** Inicializacion de variables y configuraciones iniciales **/
    private fun setup() {
        auth = Firebase.auth
        layout = binding.authLayout


        binding.btnGoogleSignIn.setOnClickListener {
            oneTapClient.beginSignIn(signInRequest)
                .addOnSuccessListener { result ->
                    try {
                        startIntentSenderForResult(
                            result.pendingIntent.intentSender, REQUEST_ONE_TAP, null, 0, 0, 0, null
                        )
                    }
                    catch ( e: IntentSender.SendIntentException ) {
                        Log.e("FirebaseAuth", "Couldn't start One Tap UI: ${e.localizedMessage}")
                    }
                }
                .addOnFailureListener {
                    Log.d("FirebaseAuth", "${it.localizedMessage}")
                }
        }
    }

    /** Guarda los datos de usuario en el archivo de preferencias **/
    private fun saveSharedPreferences(userData: FirebaseUser?) {
        prefs = requireContext().getSharedPreferences( getString(R.string.prefs_file), Context.MODE_PRIVATE )
        with( prefs!!.edit() ) {
            putString( getString(R.string.prefs_user_name), userData!!.displayName )
            putString( getString(R.string.prefs_email), userData!!.email )
            putString( getString(R.string.prefs_user_photo), userData!!.photoUrl.toString() )
            commit()
        }
        session()
    }

    /** Comprobacion de si existe una sesion activa **/
    private fun session() {
        Session.readPrefs( requireActivity() )
        if ( Session.activeSession ) {
//            layout.visibility = View.INVISIBLE // Hace invisible el layout
            showFragment(
                NavBarFragment(),
                getString(R.string.NavBarFragment)
            )
        }
    }

    /** Navegacion a la vista de home **/
    private fun showFragment( fragment: Fragment, tag: String ) {
        parentFragmentManager.commit {
            setReorderingAllowed(true)
            replace(R.id.main_container_fragment, fragment)
            addToBackStack(tag)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_ONE_TAP -> {
                try {
                    val credential = oneTapClient.getSignInCredentialFromIntent(data)
                    val idToken = credential.googleIdToken
                    when {
                        idToken != null -> {
                            // Got an ID token from Google. Use it to authenticate
                            // with Firebase.
                            val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
                            auth.signInWithCredential(firebaseCredential)
                                .addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        // Sign in success, update UI with the signed-in user's information
                                        // TODO: Set currente user un firebase aut property
                                        saveSharedPreferences( auth.currentUser )
                                        Log.d("FirebaseAuth", "Got ID token.")
                                        Log.d("FirebaseAuth", "${auth.currentUser?.photoUrl}")
                                    }
                                    else {
                                        Log.w("FirebaseAuth", "${task.exception.toString()}")
                                    }
                                }
                        }
                        else -> {
                            // Shouldn't happen.
                            Log.d("FirebaseAuth", "No ID token!")
                        }
                    }
                }
                catch (e: ApiException) {
                    Log.d("FirebaseAuth", "${e.message.toString()}")
                }
            }
        }
    }

    /** Inicializacion de propiedades para utilizar el oneTapClient de inicio de session
     * con Google **/
    private fun initialiceGoogleutentication() {
        oneTapClient = Identity.getSignInClient( requireActivity() )
        signInRequest = BeginSignInRequest.builder()
            .setPasswordRequestOptions(
                BeginSignInRequest.PasswordRequestOptions.builder()
                    .setSupported(true)
                    .build())
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    // Your server's client ID, not your Android client ID.
                    .setServerClientId(
                        getString(R.string.default_web_client_id)
//                        _stringResourcesProvider.getString() TODO: Delete
                    )
                    // Only show accounts previously used to sign in.
                    .setFilterByAuthorizedAccounts(true)
                    .build())
            // Automatically sign in when exactly one credential is retrieved.
            .setAutoSelectEnabled(true)
            .build()
    }
}