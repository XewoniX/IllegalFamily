import android.R.attr.bottom
import android.R.attr.left
import android.R.attr.right
import android.R.attr.top
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.jakubsapplication.app.R
import com.jakubsapplication.app.modules.chatviewcontainer.ui.ChatViewContainerActivity


data class Message(
    val senderEmail: String,
    val messageContent: String,
    val timestamp: Long
)
var pole = ""
val drawableResId = R.drawable.rectangle_gradient_s_grey_400_e_cyan_a202_radius_20
var VIEW_TYPE_EMAIL_EXAMPLE = 0
class MessageAdapter(private val messages: MutableList<ChatViewContainerActivity.Message>) :
    RecyclerView.Adapter<MessageAdapter.MessageViewHolder>() {

    class MessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val senderEmailTextView: TextView = itemView.findViewById(R.id.senderEmailTextView)
        val messageContentTextView: TextView = itemView.findViewById(R.id.messageContentTextView)
    }

    override fun getItemViewType(position: Int): Int {
        val user = Firebase.auth.currentUser
        val email = user?.email
        val message = messages[position]
        if (email == message.senderEmail) {
            VIEW_TYPE_EMAIL_EXAMPLE = 0
            return VIEW_TYPE_EMAIL_EXAMPLE
        } else {
            VIEW_TYPE_EMAIL_EXAMPLE = 1
            return VIEW_TYPE_EMAIL_EXAMPLE
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val itemView: View
        if (VIEW_TYPE_EMAIL_EXAMPLE == 1) {
            itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_message_moja, parent, false)
        } else {
            itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_message_kogos, parent, false)
        }
        return MessageViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        val message = messages[position]
        val db = FirebaseFirestore.getInstance()
        val kolekcjaRef = db.collection("QRAuth")
        val zapytanie = kolekcjaRef.whereEqualTo("adres_email", message.senderEmail)
        zapytanie.get()
            .addOnSuccessListener { querySnapshot ->
                for (document in querySnapshot.documents) {
                    val idDokumentu = document.id
                    val docRef = db.collection("QRAuth").document(idDokumentu)
                    docRef.get()
                        .addOnSuccessListener { documentSnapshot ->
                            if (documentSnapshot.exists()) {

                                val data = documentSnapshot.data
                                if (data != null) {
                                    pole = data["name"].toString()

                                    if (pole != null) {
                                        println(pole)
                                        holder.senderEmailTextView.text = "$pole"
                                    }
                                }
                            } else {
                                println("Dokument nie istnieje")
                            }
                        }
                        .addOnFailureListener { e ->
                            println("Błąd pobierania dokumentu: $e")
                        }
                    println("ID dokumentu: $idDokumentu")
                }
            }
            .addOnFailureListener { e ->
                println("Błąd podczas wykonywania zapytania: $e")
            }
        holder.messageContentTextView.text = message.messageContent



    /*    if(email == message.senderEmail)
        {
            holder.senderEmailTextView.visibility = View.GONE
            holder.messageFrame.setBackgroundResource(drawableResId)
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            params.setMargins(0, 10, 100, 0,)
            holder.messageFrame.setLayoutParams(params)
        }*/
    }
    override fun getItemCount(): Int {
        return messages.size
    }
}