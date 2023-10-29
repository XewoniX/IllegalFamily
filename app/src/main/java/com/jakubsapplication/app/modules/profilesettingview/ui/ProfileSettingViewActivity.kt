package com.jakubsapplication.app.modules.profilesettingview.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.viewModels
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.jakubsapplication.app.R
import com.jakubsapplication.app.appcomponents.base.BaseActivity
import com.jakubsapplication.app.databinding.ActivityProfileSettingViewBinding
import com.jakubsapplication.app.modules.chatviewcontainer.ui.ChatViewContainerActivity
import com.jakubsapplication.app.modules.homeview.ui.HomeViewActivity
import com.jakubsapplication.app.modules.mapview.ui.MapViewActivity
import com.jakubsapplication.app.modules.profilesettingview.`data`.viewmodel.ProfileSettingViewVM
import com.jakubsapplication.app.modules.votingview.ui.VotingViewActivity
import kotlin.String
import kotlin.Unit

class ProfileSettingViewActivity :
    BaseActivity<ActivityProfileSettingViewBinding>(R.layout.activity_profile_setting_view) {
    private val viewModel: ProfileSettingViewVM by viewModels<ProfileSettingViewVM>()

    override fun onInitialized(): Unit {
        viewModel.navArguments = intent.extras?.getBundle("bundle")
        binding.profileSettingViewVM = viewModel

        val user = Firebase.auth.currentUser
        val username = findViewById<TextView>(R.id.txtUsername)
        val useravatar = findViewById<ImageView>(R.id.frameStackuser)
        val button = findViewById<Button>(R.id.button_change_username)
        val username_edit = findViewById<TextInputLayout>(R.id.input)

        val db = FirebaseFirestore.getInstance()
        val email = user?.email
        val kolekcjaRef = db.collection("QRAuth")
        val zapytanie = kolekcjaRef.whereEqualTo("adres_email", email)


        button.setOnClickListener {
            username.visibility = View.GONE
            username_edit.visibility = View.VISIBLE
            button.visibility = View.GONE
        }

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
                                    val pole = data["name"]
                                    if (pole != null) {
                                        println(pole)
                                        username.text = "$pole"
                                    }
                                }
                            } else {
                                println("Dokument nie istnieje")
                                username.text = "Nie udalo sie zaladowac nicku"
                            }
                        }
                        .addOnFailureListener { e ->
                            println("Błąd pobierania dokumentu: $e")
                            username.text = "Nie udalo sie zaladowac nicku"
                        }
                    println("ID dokumentu: $idDokumentu")
                }
            }
            .addOnFailureListener { e ->
                println("Błąd podczas wykonywania zapytania: $e")
            }


        if (user != null) {
            val photoUrl = user.photoUrl
            if (photoUrl != null) {
                val avatarUrl = photoUrl.toString()
                Glide.with(this)
                    .load(avatarUrl)
                    .diskCacheStrategy(DiskCacheStrategy.NONE) // Opcjonalnie: Wyłącz pamięć podręczną, jeśli obraz jest dynamiczny
                    .skipMemoryCache(true) // Opcjonalnie: Wyłącz pamięć RAM, jeśli obraz jest dynamiczny
                    .into(useravatar)
            } else {
                println("Awatar użytkownika nie jest dostępny")
            }
        }


    }

    override fun setUpClicks(): Unit {
        binding.frameCheckringligh.setOnClickListener {
            val destIntent = VotingViewActivity.getIntent(this, null)
            startActivity(destIntent)
        }
        binding.imageMap.setOnClickListener {
            val destIntent = MapViewActivity.getIntent(this, null)
            startActivity(destIntent)
        }
        binding.imageMenu.setOnClickListener {
            val destIntent = ChatViewContainerActivity.getIntent(this, null)
            startActivity(destIntent)
        }
        binding.frameBottombar.setOnClickListener {
            val destIntent = HomeViewActivity.getIntent(this, null)
            startActivity(destIntent)
        }
    }

    companion object {
        const val TAG: String = "PROFILE_SETTING_VIEW_ACTIVITY"


        fun getIntent(context: Context, bundle: Bundle?): Intent {
            val destIntent = Intent(context, ProfileSettingViewActivity::class.java)
            destIntent.putExtra("bundle", bundle)
            return destIntent
        }
    }
}
