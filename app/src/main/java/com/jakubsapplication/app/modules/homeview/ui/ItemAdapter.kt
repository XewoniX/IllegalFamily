import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.jakubsapplication.app.R


data class ItemModel(
    val tytul: String = "",
    val opis: String = "",
    val widok: Int = 0
)
class ItemAdapter(private val dataList: List<ItemModel>) : RecyclerView.Adapter<ItemAdapter.ItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.activity_ogloszenie, parent, false)
        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = dataList[position]
        holder.tytulTextView.text = item.tytul
        holder.opisTextView.text = item.opis
        if(item.widok==0)
            holder.x.visibility = View.GONE
        else if(item.widok==1)
            holder.x.visibility = View.VISIBLE

    }
    override fun getItemCount(): Int {
        return dataList.size
    }

    class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val opisTextView: TextView = itemView.findViewById(R.id.txtDescription2)
        val tytulTextView: TextView = itemView.findViewById(R.id.txtDescription)
        val x: Button = itemView.findViewById(R.id.button)
    }
}
