import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.jakubsapplication.app.R
import com.jakubsapplication.app.modules.chatviewcontainer.ui.ChatViewContainerActivity

data class Message(
    val senderEmail: String,
    val messageContent: String,
    val timestamp: Long
)
class MessageAdapter(private val messages: MutableList<ChatViewContainerActivity.Message>) :
    RecyclerView.Adapter<MessageAdapter.MessageViewHolder>() {

    class MessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val senderEmailTextView: TextView = itemView.findViewById(R.id.senderEmailTextView)
        val messageContentTextView: TextView = itemView.findViewById(R.id.messageContentTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_message, parent, false)
        return MessageViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        val message = messages[position]
        holder.senderEmailTextView.text = message.senderEmail
        holder.messageContentTextView.text = message.messageContent
    }

    override fun getItemCount(): Int {
        return messages.size
    }
}