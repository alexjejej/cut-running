import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.android.material.button.MaterialButton
import com.raywenderlich.android.runtracking.R
import com.raywenderlich.android.rwandroidtutorial.provider.resources.Presets
import com.raywenderlich.android.rwandroidtutorial.usecases.home.HomeFragment
import com.raywenderlich.android.rwandroidtutorial.usecases.logros.LogrosFragment
import nl.dionsegijn.konfetti.xml.KonfettiView
import java.lang.Thread.sleep
import kotlin.random.Random

class LogroConseguidoFragment : Fragment() {

    private lateinit var viewKonfetti: KonfettiView
    private var nombreLogro: String? = null
    private var pasosLogro: Int? = null
    private lateinit var imageButton: ImageButton
    private lateinit var layoutImagen: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            nombreLogro = it.getString(ARG_NOMBRE_LOGRO)
            pasosLogro = it.getInt(ARG_PASOS_LOGRO)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_logro_conseguido, container, false)
        initializeUI(view)
        return view
    }

    private fun initializeUI(view: View) {
        // Configuración del mensaje de felicitación
        val tvFelicidades = view.findViewById<TextView>(R.id.tvFelicidades)
        tvFelicidades.text = "Conseguiste el logro $nombreLogro por haber caminado más de $pasosLogro pasos. ¡Sigue así!"

        // Configuración de la animación de confeti
        viewKonfetti = view.findViewById(R.id.konfettiView)
        imageButton = view.findViewById(R.id.imgbtnConfeti)
        layoutImagen = view.findViewById(R.id.LayoutImagen)

        startRandomConfettiAnimation()

        setupButtonListeners(view)
    }

    private fun setupButtonListeners(view: View) {
        view.findViewById<MaterialButton>(R.id.btnVolverInicio).setOnClickListener {
            navigateToFragment(HomeFragment())
        }

        view.findViewById<MaterialButton>(R.id.btnVerLogros).setOnClickListener {
            navigateToFragment(LogrosFragment())

        }

        imageButton.setOnClickListener {
            setRandomMovement()
            startRandomConfettiAnimation()
        }



    }

    private fun setRandomMovement() {

        // Calcula nuevas coordenadas aleatorias dentro del LinearLayout
        val x = Random.nextInt(0, layoutImagen.width - imageButton.width)
        val y = Random.nextInt(0, layoutImagen.height - imageButton.height)

        // Crea y aplica la animación
        imageButton.animate()
            .x(x.toFloat())
            .y(y.toFloat())
            .setDuration(500)
            .start()


    }



    private fun startRandomConfettiAnimation() {
        val randomParty = when ((1..4).random()) {
            1 -> Presets.festive()
            2 -> Presets.explode()
            3 -> Presets.parade()
            4 -> Presets.rain()
            else -> emptyList()
        }

        viewKonfetti.start(randomParty)
    }

    companion object {
        private const val ARG_NOMBRE_LOGRO = "nombreLogro"
        private const val ARG_PASOS_LOGRO = "pasosLogro"

        fun newInstance(nombreLogro: String, pasosLogro: Int) = LogroConseguidoFragment().apply {
            arguments = Bundle().apply {
                putString(ARG_NOMBRE_LOGRO, nombreLogro)
                putInt(ARG_PASOS_LOGRO, pasosLogro)
            }
        }
    }

    private fun navigateToFragment(fragment: Fragment) {
        val transaction = parentFragmentManager.beginTransaction()
        transaction.replace(R.id.home_container_fragment, fragment)
        transaction.addToBackStack(null)  // Permite volver al fragmento anterior con el botón de retroceso
        transaction.commit()
    }
}
