import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.cut.android.running.R
import com.cut.android.running.models.Achievement

class LogrosAdapter_delete(private var logros: List<Achievement>, private val onClick: (Achievement) -> Unit) :
    RecyclerView.Adapter<LogrosAdapter_delete.LogroViewHolder>() {

    class LogroViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val logroName: TextView = view.findViewById(R.id.logroname)
        val logroDescripcion: TextView = view.findViewById(R.id.logrodescripcion)
        val logroProgreso: TextView = view.findViewById(R.id.logroprogreso)
        val logroStatus: TextView = view.findViewById(R.id.logrostatus)
        val logroPhoto: ImageView = view.findViewById(R.id.imlogro)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LogroViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_listalogros, parent, false)
        return LogroViewHolder(view)
    }

    override fun onBindViewHolder(holder: LogroViewHolder, position: Int) {
        val logro = logros[position]
        holder.logroName.text = logro.name
        holder.logroDescripcion.text = logro.description
        var pasos = logro.steps
        holder.logroProgreso.text = "Pasos para obtener el logro: $pasos"
        var id = logro.id.toString()
        holder.logroStatus.text = "Id del logro: $id"
        Glide.with(holder.itemView.context)
            .load(logro.photo)
            .into(holder.logroPhoto)
        holder.itemView.setOnClickListener {
            onClick(logro)
        }
    }

    fun updateLogros(nuevosLogros: List<Achievement>) {
        logros = nuevosLogros
        notifyDataSetChanged() // Notifica al RecyclerView que los datos han cambiado
    }

    override fun getItemCount() = logros.size
}
