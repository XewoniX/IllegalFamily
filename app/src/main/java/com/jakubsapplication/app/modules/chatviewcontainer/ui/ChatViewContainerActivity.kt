package com.jakubsapplication.app.modules.chatviewcontainer.ui

import MessageAdapter
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.ScrollView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.jakubsapplication.app.R
import com.jakubsapplication.app.appcomponents.base.BaseActivity
import com.jakubsapplication.app.databinding.ActivityChatViewContainerBinding
import com.jakubsapplication.app.modules.chatviewcontainer.data.viewmodel.ChatViewContainerVM
import com.jakubsapplication.app.modules.homeview.ui.HomeViewActivity
import com.jakubsapplication.app.modules.mapview.ui.MapViewActivity
import com.jakubsapplication.app.modules.profilesettingview.ui.ProfileSettingViewActivity
import com.jakubsapplication.app.modules.votingview.ui.VotingViewActivity

class ChatViewContainerActivity :
    BaseActivity<ActivityChatViewContainerBinding>(R.layout.activity_chat_view_container) {
    private val viewModel: ChatViewContainerVM by viewModels<ChatViewContainerVM>()

    data class Message(
        val senderEmail: String,
        val messageContent: String,
        val timestamp: Long
    )
    private val messages = mutableListOf<Message>()
    private lateinit var adapter: MessageAdapter
    private lateinit var firestore: FirebaseFirestore
    private var messageListener: ListenerRegistration? = null
    override fun onInitialized(): Unit {

        viewModel.navArguments = intent.extras?.getBundle("bundle")
        binding.chatViewContainerVM = viewModel

        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN or
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)


        val recyclerView: RecyclerView = findViewById(R.id.recyclerViewChat)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = MessageAdapter(messages)
        recyclerView.adapter = adapter

        firestore = FirebaseFirestore.getInstance()

        // Rozpocznij nasłuchiwanie na kolekcję "Messages" w Firebase Firestore
        messageListener = firestore.collection("Messages")
            .orderBy("timestamp")
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    // Obsługa błędów
                    return@addSnapshotListener
                }

                messages.clear()
                if (snapshot != null) {
                    for (document in snapshot) {
                        val senderEmail = document.getString("senderEmail") ?: ""
                        val messageContent = document.getString("messageContent") ?: ""
                        val timestamp = document.getTimestamp("timestamp")?.toDate()?.time ?: 0

                        val message = Message(senderEmail, messageContent, timestamp)
                        messages.add(message)
                    }
                }
                adapter.notifyDataSetChanged()
            }
        val layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager

        // Przewinięcie do ostatniego elementu
        layoutManager.scrollToPosition(adapter.itemCount - 1)
    }

    override fun onDestroy() {
        super.onDestroy()
        // Zatrzymaj nasłuchiwanie, aby uniknąć wycieków pamięci
        messageListener?.remove()
    }

    override fun setUpClicks(): Unit {

        binding.editTextChat.setOnClickListener{
            val recyclerView: RecyclerView = findViewById(R.id.recyclerViewChat)
            val layoutManager = LinearLayoutManager(this)
            recyclerView.layoutManager = layoutManager
            // Przewinięcie do ostatniego elementu
            layoutManager.scrollToPosition(adapter.itemCount - 1)
        }
        binding.SendMessage.setOnClickListener {
            val user = Firebase.auth.currentUser
            val email = user?.email
            val chat_message = findViewById<TextInputEditText>(R.id.editTextChat)
            val db = FirebaseFirestore.getInstance()
            if (chat_message.text.toString().length >= 1) {
                val messageData = hashMapOf(
                    "senderEmail" to email,
                    "messageContent" to chat_message.text.toString(),
                    "timestamp" to FieldValue.serverTimestamp()
                )

                db.collection("Messages")
                    .add(messageData)
                    .addOnSuccessListener { documentReference ->
                        // Wiadomość została dodana do Firestore
                    }
                    .addOnFailureListener { e ->
                        // Błąd podczas dodawania wiadomości
                    }
                chat_message.text = null
            } else {
                Toast.makeText(
                    getApplicationContext(),
                    "Nie mozna wyslac pustej wiadomosci.", Toast.LENGTH_SHORT
                ).show();
            }
            val inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(binding.SendMessage.windowToken, 0)
            val recyclerView: RecyclerView = findViewById(R.id.recyclerViewChat)
            val layoutManager = LinearLayoutManager(this)
            recyclerView.layoutManager = layoutManager

            // Przewinięcie do ostatniego elementu
            layoutManager.scrollToPosition(adapter.itemCount - 1)


        }
        binding.imageLineOne.setOnClickListener {
            val destIntent = VotingViewActivity.getIntent(this, null)
            startActivity(destIntent)
        }
        binding.imageUser.setOnClickListener {
            val destIntent = ProfileSettingViewActivity.getIntent(this, null)
            startActivity(destIntent)
        }
        binding.imageHome.setOnClickListener {
            val destIntent = HomeViewActivity.getIntent(this, null)
            startActivity(destIntent)
        }
        binding.imageMap.setOnClickListener {
            val destIntent = MapViewActivity.getIntent(this, null)
            startActivity(destIntent)
        }
    }

    companion object {
        const val TAG: String = "CHAT_VIEW_CONTAINER_ACTIVITY"
        fun getIntent(context: Context, bundle: Bundle?): Intent {
            val destIntent = Intent(context, ChatViewContainerActivity::class.java)
            destIntent.putExtra("bundle", bundle)
            return destIntent
        }
    }
}
