import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.jakubsapplication.app.R


data class ItemModel(
    val opis: String = ""
)
class ItemAdapter(private val dataList: List<ItemModel>) : RecyclerView.Adapter<ItemAdapter.ItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.activity_ogloszenie, parent, false)
        println("dupa")
        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = dataList[position]
        holder.opisTextView.text = item.opis
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val opisTextView: TextView = itemView.findViewById(R.id.txtDescription)
    }
}
