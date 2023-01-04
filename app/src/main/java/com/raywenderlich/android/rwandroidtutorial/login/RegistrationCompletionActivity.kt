package com.raywenderlich.android.rwandroidtutorial.login

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.raywenderlich.android.runtracking.R
import com.raywenderlich.android.rwandroidtutorial.models.User

class RegistrationCompletionActivity : AppCompatActivity() {
    private val db = FirebaseFirestore.getInstance() // Referencia a la DB Cloud Firestore definida en Firebase
    lateinit var txtvwEmail: TextView
    lateinit var txtvwPass: TextView
    lateinit var btnLogout: Button

    lateinit var txtSemester: TextInputEditText
    lateinit var txtCareer: TextInputEditText
    lateinit var txtCU: TextInputEditText
    lateinit var btnSave: Button
    lateinit var btnSkip: Button
    // lateinit var btnGet: Button
    // lateinit var btnDelete: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration_completion)

        // Recuperacion de los paramaetros enviados desde el login
        var bundle = intent.extras
        var email = bundle?.getString("email")
        var provider = bundle?.getString("provider").toString()

        // Setup
        setup( email ?: "", provider ?:"" )

        /** Guardado de los datos del usuario que se ha autenticado a nivel de sesion de la app
         * para que si el usuario ya se encuentra autenticado no nos pida iniciar sesion
         * de nuevo en el login**/
        val prefs: SharedPreferences = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE) // Se lee el name el file desde el archivo strings.xml
        with( prefs.edit() ) {
            // Es preferible esitar las preferencias dentro de un bloque "with"
            putString("email", email)
            putString("provider", provider)
            apply()
        }
    }

    private fun setup( email: String, provider: String ) {
        txtvwEmail  = findViewById(R.id.txtvwEmail)
        txtvwPass   = findViewById(R.id.txtvwProvider)
        btnLogout   = findViewById(R.id.btnLogout)

        txtCareer   = findViewById(R.id.txtCareer)
        txtSemester = findViewById(R.id.txtSemester)
        txtCU       = findViewById(R.id.txtCU)
        btnSave     = findViewById(R.id.btnSave)
        btnSkip     = findViewById(R.id.btnSkip)
        // btnGet      = findViewById(R.id.btnGet)
        // btnDelete   = findViewById(R.id.btnDelete)

        txtvwEmail.text = email
        txtvwPass.text = provider

        // Boton de logout
        btnLogout.setOnClickListener{
            // Borrado de datos
            val prefs: SharedPreferences = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE)
            with( prefs.edit() ) {
                clear() // Borra todas las preferencias que se encuentran guardadas en el archivo
                apply()
            }

            FirebaseAuth.getInstance().signOut()
            onBackPressed() // Nos devuelve a la pantalla anterior, en este caso el login (AuthActivity)
        }

        /** Eventos de mnajeo de la DB Cloud Firestore */
        btnSave.setOnClickListener {
            // Definicion de la coleccion "users"
            // En "document()" indicamos la key del usuario
            db.collection("users").document(email)
                .set(
                    // TODO: Agrevar validacion de campos vacios
                    User(
                        provider = provider,
                        semester = txtSemester.text.toString().toInt(),
                        career = txtCareer.text.toString(),
                        cu = txtCU.text.toString(),
                        enable = true,
                        completeInformation = true
                    )
                )
                .addOnSuccessListener {
                    Log.d("Registro exitoso", "Datos del usuario agregados correctamente")
                    this.showHome()
                }
                .addOnFailureListener {
                    Log.w("Registro fallido", "No se ha logrado realizar el registro de los datos")
                }
        }

        btnSkip.setOnClickListener { this.showHome() }

        /** btnGet.setOnClickListener {
            db.collection("users").document(email)
                .get()
                .addOnSuccessListener {
                    txtCU.setText(it.get("cu").toString())
                    txtSemester.setText(it.get("semester").toString())
                    txtCareer.setText(it.get("career").toString())
                    Log.d("Recuperacion exitosa", "Datos del usuario recuperados correctamente")
                }
                .addOnFailureListener {
                    Log.w("Recuperacion fallida", "No se ha logrado recuperar los datos")
                }
        } **/

        /** btnDelete.setOnClickListener {
            db.collection("users").document(email)
                .set(
                    hashMapOf(
                        "enable" to false
                    )
                )
                .addOnSuccessListener {
                    Log.d("Eliminacion exitosa", "Eliminacion de datos del usuario recuperados correctamente")
                }
                .addOnFailureListener {
                    Log.w("Eliminacion fallida", "No se ha logrado eliminar los datos")
                }
        } **/
    }

    /** Muestra la pantalla de Home (HomeActivity) **/
    private fun showHome() {
        val homeIntent = Intent(this, HomeActivity::class.java)
        startActivity(homeIntent)
    }

}