import android.graphics.Typeface
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.cut.android.running.R
import com.cut.android.running.provider.DatosUsuario
import com.cut.android.running.provider.resources.Presets
import com.cut.android.running.usecases.home.HomeActivity
import com.cut.android.running.usecases.home.HomeFragment
import com.cut.android.running.usecases.logros.LogrosFragment
import nl.dionsegijn.konfetti.xml.KonfettiView
import kotlin.random.Random

class LogroConseguidoFragment : Fragment() {

    private lateinit var viewKonfetti: KonfettiView
    private var nombreLogro: String? = null
    private var pasosLogro: Int? = null
    private lateinit var imageButton: ImageButton
    private lateinit var layoutImagen: LinearLayout
    private lateinit var txtNombreFelicidades: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            nombreLogro = it.getString(ARG_NOMBRE_LOGRO,"Error en el logro")
            pasosLogro = it.getInt(ARG_PASOS_LOGRO,0)
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
        val nombreuser = DatosUsuario.getUserName(requireContext())
        // Configuración del mensaje de felicitación
        txtNombreFelicidades = view.findViewById(R.id.txtNombreFelicidades)
        txtNombreFelicidades.text = "¡Felicidades $nombreuser!"
        val tvFelicidades = view.findViewById<TextView>(R.id.tvFelicidades)
        val textoCompleto = "Conseguiste el logro $nombreLogro por haber caminado más de $pasosLogro pasos. ¡Sigue así!"

        // Crea un SpannableString a partir del texto completo
        val spannableString = SpannableString(textoCompleto)

        // Encuentra el índice de inicio y fin para el nombre del logro y los pasos
        val inicioLogro = textoCompleto.indexOf(nombreLogro!!)
        val finLogro = inicioLogro + nombreLogro!!.length
        val inicioPasos = textoCompleto.indexOf(pasosLogro.toString())
        val finPasos = inicioPasos + pasosLogro.toString().length

        // Aplica estilo negrita al nombre del logro
        spannableString.setSpan(StyleSpan(Typeface.BOLD), inicioLogro, finLogro, Spannable.SPAN_INCLUSIVE_INCLUSIVE)

        // Aplica estilo cursiva a los pasos
        spannableString.setSpan(StyleSpan(Typeface.ITALIC), inicioPasos, finPasos, Spannable.SPAN_INCLUSIVE_INCLUSIVE)

        // Establece el SpannableString en el TextView
        tvFelicidades.text = spannableString


        // Configuración de la animación de confeti
        viewKonfetti = view.findViewById(R.id.konfettiView)
        imageButton = view.findViewById(R.id.imgbtnConfeti)
        layoutImagen = view.findViewById(R.id.LayoutImagen)

        startRandomConfettiAnimation()

        setupButtonListeners(view)
    }

    private fun setupButtonListeners(view: View) {
        view.findViewById<Button>(R.id.btnCerrar).setOnClickListener {
            activity?.finishAffinity()
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
