import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.jakubsapplication.app.R

data class Message(
    val senderEmail: String,
    val messageContent: String,
    val timestamp: Long
)
var pole = ""
class MessageAdapter(private val messages: List<Message>) : RecyclerView.Adapter<MessageAdapter.MessageViewHolder>() {

    class MessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val senderEmailTextView: TextView = itemView.findViewById(R.id.senderEmailTextView)
        val messageContentTextView: TextView = itemView.findViewById(R.id.messageContentTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_message, parent, false)
        return MessageViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {


        val db = FirebaseFirestore.getInstance()
        val kolekcjaRef = db.collection("QRAuth")
        val user = Firebase.auth.currentUser
        val email = user?.email
        val zapytanie = kolekcjaRef.whereEqualTo("adres_email", email)
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

        val message = messages[position]
        holder.messageContentTextView.text = message.messageContent
    }
    override fun getItemCount(): Int {
        return messages.size
    }
}