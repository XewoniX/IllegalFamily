package com.jakubsapplication.app.modules.homeview.ui


import ItemAdapter
import ItemModel
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.FrameLayout
import android.widget.TextView
import androidx.activity.viewModels
import com.jakubsapplication.app.R
import com.jakubsapplication.app.appcomponents.base.BaseActivity
import com.jakubsapplication.app.databinding.ActivityHomeViewBinding
import com.jakubsapplication.app.modules.chatviewcontainer.ui.ChatViewContainerActivity
import com.jakubsapplication.app.modules.homeview.`data`.viewmodel.HomeViewVM
import com.jakubsapplication.app.modules.mapview.ui.MapViewActivity
import com.jakubsapplication.app.modules.profilesettingview.ui.ProfileSettingViewActivity
import com.jakubsapplication.app.modules.votingview.ui.VotingViewActivity
import kotlin.String
import kotlin.Unit
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore


class HomeViewActivity : BaseActivity<ActivityHomeViewBinding>(R.layout.activity_home_view) {
    private val viewModel: HomeViewVM by viewModels<HomeViewVM>()

    private lateinit var recyclerView: RecyclerView
    private lateinit var itemAdapter: ItemAdapter
    private lateinit var firestore: FirebaseFirestore
    override fun onInitialized(): Unit {
        viewModel.navArguments = intent.extras?.getBundle("bundle")
        binding.homeViewVM = viewModel

        val button = findViewById<FrameLayout>(R.id.buttonDelete)
        button.setOnClickListener {
            showX()
        }
        val username = findViewById<TextView>(R.id.txtWitajUsernam)
        val database: FirebaseDatabase = FirebaseDatabase.getInstance()
        val myRef: DatabaseReference = database.getReference("QRAuth")

        myRef.child("123123").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Pobrane dane z Firebase
                    val value = dataSnapshot.value
                    // Możesz teraz wykorzystać tę wartość, np. wyświetlić ją w TextView
                    username.text = "$value.toString(), witaj w Illegal Family Brodnica!\n" +
                            "Zapoznaj się z ogłoszeniami"
                } else {
                    // Dane nie istnieją
                    username.text = "Witaj w Illegal Family Brodnica!\n" +
                            "Zapoznaj się z ogłoszeniami"
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Obsłuż błąd
                Log.w("Firebase", "Failed to read value.", error.toException())
                username.text = "Witaj w Illegal Family Brodnica!\n" +
                        "Zapoznaj się z ogłoszeniami"
            }
        })




   /*    val user = Firebase.auth.currentUser

        if (user != null) {
            val displayName = user?.displayName
            if (displayName != null) {
                val nameParts = displayName.split(" ")
                val firstName = nameParts[0]
                username.text = "$firstName, witaj w Illegal Family Brodnica!\n" +
                        "Zapoznaj się z ogłoszeniami"
            } else {
                username.text = "Witaj w Illegal Family Brodnica!\n" +
                        "Zapoznaj się z ogłoszeniami"
            }
        }*/
        recyclerView = findViewById(R.id.recyclerView)
        firestore = FirebaseFirestore.getInstance()
        val dataList = ArrayList<ItemModel>()
        itemAdapter = ItemAdapter(dataList)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = itemAdapter
        firestore.collection("Ogloszenia")
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val itemModel = document.toObject(ItemModel::class.java)
                    dataList.add(itemModel)

                }
                itemAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                Toast.makeText(getApplicationContext(), "Blad", Toast.LENGTH_SHORT).show();
            }
    }
    fun showX() {

    }
    override fun setUpClicks(): Unit {
        binding.imageMenu.setOnClickListener {
            val destIntent = ChatViewContainerActivity.getIntent(this, null)
            startActivity(destIntent)
        }
        binding.imageMap.setOnClickListener {
            val destIntent = MapViewActivity.getIntent(this, null)
            startActivity(destIntent)
        }
        binding.imageUser.setOnClickListener {
            val destIntent = ProfileSettingViewActivity.getIntent(this, null)
            startActivity(destIntent)
        }
        binding.frameCheckringligh.setOnClickListener {
            val destIntent = VotingViewActivity.getIntent(this, null)
            startActivity(destIntent)
        }
    }

    companion object {
        const val TAG: String = "HOME_VIEW_ACTIVITY"


        fun getIntent(context: Context, bundle: Bundle?): Intent {
            val destIntent = Intent(context, HomeViewActivity::class.java)
            destIntent.putExtra("bundle", bundle)
            return destIntent
        }
    }
}


