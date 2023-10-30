package com.jakubsapplication.app.modules.profilesettingview.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.jakubsapplication.app.R
import com.jakubsapplication.app.appcomponents.base.BaseActivity
import com.jakubsapplication.app.databinding.ActivityProfileSettingViewBinding
import com.jakubsapplication.app.modules.chatviewcontainer.ui.ChatViewContainerActivity
import com.jakubsapplication.app.modules.homeview.ui.HomeViewActivity
import com.jakubsapplication.app.modules.mapview.ui.MapViewActivity
import com.jakubsapplication.app.modules.profilesettingview.data.viewmodel.ProfileSettingViewVM
import com.jakubsapplication.app.modules.votingview.ui.VotingViewActivity

class ProfileSettingViewActivity :
    BaseActivity<ActivityProfileSettingViewBinding>(R.layout.activity_profile_setting_view) {
    private val viewModel: ProfileSettingViewVM by viewModels<ProfileSettingViewVM>()

    var pole = ""
    var pole1 = ""

    override fun onInitialized(): Unit {
        viewModel.navArguments = intent.extras?.getBundle("bundle")
        binding.profileSettingViewVM = viewModel
        val username = findViewById<TextView>(R.id.txtUsername)
        val button = findViewById<Button>(R.id.button_change_username)
        val username_edit = findViewById<TextInputLayout>(R.id.input)
        val button_accept = findViewById<Button>(R.id.button_accept_name)
        val button_Car = findViewById<Button>(R.id.button_change_car)
        val car_info = findViewById<TextView>(R.id.txtCar)
        val editcar = findViewById<TextInputLayout>(R.id.editcar)
        val button_car_edit = findViewById<Button>(R.id.button_accept_car)
        val db = FirebaseFirestore.getInstance()
        val kolekcjaRef = db.collection("QRAuth")
        update()
        button_accept.setOnClickListener {
            val zapytanie = kolekcjaRef.whereEqualTo("name", pole)
            zapytanie.get()
                .addOnSuccessListener { querySnapshot ->
                    for (document in querySnapshot.documents) {
                        val idDokumentu = document.id
                        // Odebrane ID dokumentu znajduje się w zmiennej 'idDokumentu'
                        val docRef = db.collection("QRAuth").document(idDokumentu)
                        //val editText = findViewById<TextInputEditText>(R.id.editText)
                        val editText: TextInputEditText = findViewById(R.id.editText)
                        val maxLength = 20 // Maksymalna liczba znaków
                        editText.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(maxLength))
                        val inputLayout = editText.parent.parent as TextInputLayout
                        editText.addTextChangedListener(object : TextWatcher {
                            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                                if (s?.length ?: 0 > maxLength) {
                                    inputLayout.error = "Przekroczono limit znaków"
                                } else {
                                    inputLayout.error = null
                                }
                            }

                            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                                if (s?.length ?: 0 > maxLength) {
                                    inputLayout.error = "Przekroczono limit znaków"
                                } else {
                                    inputLayout.error = null
                                }
                            }
                            override fun afterTextChanged(s: Editable?) {}
                        })


                        val nowaWartosc = editText.text.toString()

                        docRef.update("name", nowaWartosc)
                            .addOnSuccessListener {
                                // Sukces
                                println("DZIALA")
                            }
                            .addOnFailureListener { e ->
                                // Obsługa błędu
                                println("NIE DZIALA")
                            }
                    }
                }
                .addOnFailureListener { e ->
                    println("Błąd podczas wykonywania zapytania: $e")
                }
            update()
            username.visibility = View.VISIBLE
            username_edit.visibility = View.GONE
            button.visibility = View.VISIBLE
            val inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(button_accept.windowToken, 0)
        }

        button.setOnClickListener {
            username.visibility = View.GONE
            username_edit.visibility = View.VISIBLE
            button.visibility = View.GONE
        }
        button_Car.setOnClickListener {
            car_info.visibility = View.GONE
            editcar.visibility = View.VISIBLE
            button_Car.visibility = View.GONE
        }
        button_car_edit.setOnClickListener {

            val zapytanie = kolekcjaRef.whereEqualTo("car", pole1)
            zapytanie.get()
                .addOnSuccessListener { querySnapshot ->
                    for (document in querySnapshot.documents) {
                        val idDokumentu = document.id
                        // Odebrane ID dokumentu znajduje się w zmiennej 'idDokumentu'
                        val docRef = db.collection("QRAuth").document(idDokumentu)
                        val editText: TextInputEditText = findViewById(R.id.txteditcar)
                        val maxLength = 30 // Maksymalna liczba znaków
                        editText.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(maxLength))
                        val inputLayout = editText.parent.parent as TextInputLayout
                        editText.addTextChangedListener(object : TextWatcher {
                            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                                if (s?.length ?: 0 > maxLength) {
                                    inputLayout.error = "Przekroczono limit znaków"
                                } else {
                                    inputLayout.error = null
                                }
                            }

                            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                                if (s?.length ?: 0 > maxLength) {
                                    inputLayout.error = "Przekroczono limit znaków"
                                } else {
                                    inputLayout.error = null
                                }
                            }
                            override fun afterTextChanged(s: Editable?) {}
                        })

                        val nowaWartoscCar = editText.text.toString()

                        docRef.update("car", nowaWartoscCar)
                            .addOnSuccessListener {
                                // Sukces
                                println("DZIALA")
                            }
                            .addOnFailureListener { e ->
                                // Obsługa błędu
                                println("NIE DZIALA")
                            }
                    }
                }
                .addOnFailureListener { e ->
                    println("Błąd podczas wykonywania zapytania: $e")
                }
            update()
            car_info.visibility = View.VISIBLE
            button_Car.visibility = View.VISIBLE
            editcar.visibility = View.GONE
            val inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(button_car_edit.windowToken, 0)
        }
    }


    fun update(){
        val car_info = findViewById<TextView>(R.id.txtCar)
        val db = FirebaseFirestore.getInstance()
        val kolekcjaRef = db.collection("QRAuth")
        val username = findViewById<TextView>(R.id.txtUsername)
        val useravatar = findViewById<ImageView>(R.id.frameStackuser)
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
                                    pole1 = data["car"].toString()
                                    if (pole != null && pole1!= null) {
                                        println(pole)
                                        username.text = "$pole"
                                        println(pole1)
                                        car_info.text = "$pole1"
                                    }
                                }
                            } else {
                                println("Dokument nie istnieje")
                                username.text = "Nie udalo sie zaladowac nicku"
                                car_info.text = "Nie udalo sie zaladowac informacji o aucie"
                            }
                        }
                        .addOnFailureListener { e ->
                            println("Błąd pobierania dokumentu: $e")
                            username.text = "Nie udalo sie zaladowac nicku"
                            car_info.text = "Nie udalo sie zaladowac informacji o aucie"
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
