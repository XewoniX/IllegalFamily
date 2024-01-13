package com.jakubsapplication.app.modules.votingview.ui

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.widget.AppCompatButton
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.jakubsapplication.app.R
import com.jakubsapplication.app.appcomponents.base.BaseActivity
import com.jakubsapplication.app.databinding.ActivityVotingViewBinding
import com.jakubsapplication.app.modules.chatviewcontainer.ui.ChatViewContainerActivity
import com.jakubsapplication.app.modules.homeview.ui.HomeViewActivity
import com.jakubsapplication.app.modules.mapview.ui.MapViewActivity
import com.jakubsapplication.app.modules.profilesettingview.ui.ProfileSettingViewActivity
import com.jakubsapplication.app.modules.votingview.`data`.viewmodel.VotingViewVM
import kotlin.String
import kotlin.Unit


class VotingViewActivity : BaseActivity<ActivityVotingViewBinding>(R.layout.activity_voting_view) {
    private val viewModel: VotingViewVM by viewModels<VotingViewVM>()
    var test = 0
    var suma = 1
    var tak = 0
    var nie = 0
    override fun onInitialized(): Unit {
        viewModel.navArguments = intent.extras?.getBundle("bundle")
        binding.votingViewVM = viewModel

        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN or
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        val btnTak = findViewById<AppCompatButton>(R.id.btnTak)
        val btnNie = findViewById<AppCompatButton>(R.id.btnNie)
        val titletxt = findViewById<TextView>(R.id.txtCzychceciespo)
        val firestore = FirebaseFirestore.getInstance()
        val documentPath = "Vote/Title"
        firestore.document(documentPath).get()
            .addOnCompleteListener(OnCompleteListener { task ->
                if (task.isSuccessful) {
                    val document = task.result
                    if (document.exists()) {
                        // Odczytanie danych z dokumentu
                        val title = document.getString("title")
                        println("Odczytano wartość Title: $title")
                        titletxt.text = "$title"
                    } else {
                        println("Dokument nie istnieje.")
                    }
                } else {
                    println("Wystąpił błąd: ${task.exception?.message}")
                }
            })

        val auth = FirebaseAuth.getInstance()
        val user: FirebaseUser? = auth.currentUser
        val email = user?.email
        // Nazwa kolekcji, w której znajdują się dokumenty z emailami
        val collectionName = "Voted"

        // Email do sprawdzenia
        val emailToCheck = email

        // Kwerenda sprawdzająca, czy email istnieje w bazie danych
        val query = firestore.collection(collectionName)
            .whereEqualTo("email", emailToCheck)
        // Wykonanie kwerendy
        query.get().addOnCompleteListener(OnCompleteListener<QuerySnapshot> { task ->
            if (task.isSuccessful) {
                val querySnapshot = task.result

                // Sprawdzenie, czy istnieją jakieś dokumenty pasujące do kwerendy
                if (querySnapshot != null && !querySnapshot.isEmpty) {
                    println("Email $emailToCheck istnieje w bazie danych.")
                    liczenie()
                    test = 1
                } else {
                    println("Email $emailToCheck nie istnieje w bazie danych.")
                    btnTak.text = "Tak"
                    btnNie.text = "Nie"
                    test = 0
                }
            } else {
                println("Wystąpił błąd: ${task.exception?.message}")
            }
        })

    }
    fun liczenie(){
        val btnTak = findViewById<AppCompatButton>(R.id.btnTak)
        val btnNie = findViewById<AppCompatButton>(R.id.btnNie)

        val firestore = FirebaseFirestore.getInstance()
        val collectionName = "Voted"
        val query = firestore.collection(collectionName)

        val targetVoteValue1 = 1
        val targetVoteValue0 = 0
        // Kwerenda z warunkiem
        val query1 = firestore.collection(collectionName)
            .whereEqualTo("Vote", targetVoteValue1)
        val query2 = firestore.collection(collectionName)
            .whereEqualTo("Vote", targetVoteValue0)
        // Wykonanie kwerendy

        query.get().addOnCompleteListener(OnCompleteListener<QuerySnapshot> { task ->
            if (task.isSuccessful) {
                val querySnapshot = task.result
                // Zliczenie ilości dokumentów
                val documentCount = querySnapshot?.size() ?: 0
                println("Ilość dokumentów w kolekcji $collectionName: $documentCount")
                suma = documentCount

                query1.get().addOnCompleteListener(OnCompleteListener<QuerySnapshot> { task ->
                    if (task.isSuccessful) {
                        val querySnapshot = task.result

                        // Zliczenie ilości dokumentów spełniających warunek
                        val documentCount = querySnapshot?.size() ?: 0
                        println("Ilość dokumentów w kolekcji $collectionName, gdzie pole 'Vote' ma wartość $targetVoteValue1: $documentCount")
                        tak = documentCount
                        query2.get().addOnCompleteListener(OnCompleteListener<QuerySnapshot> { task ->
                            if (task.isSuccessful) {
                                val querySnapshot = task.result

                                // Zliczenie ilości dokumentów spełniających warunek
                                val documentCount = querySnapshot?.size() ?: 0
                                println("Ilość dokumentów w kolekcji $collectionName, gdzie pole 'Vote' ma wartość $targetVoteValue0: $documentCount")
                                nie = documentCount
                            } else {
                                println("Wystąpił błąd: ${task.exception?.message}")
                            }
                        })
                    } else {
                        println("Wystąpił błąd: ${task.exception?.message}")
                    }
                })

            } else {
                println("Wystąpił błąd: ${task.exception?.message}")
            }
        })
        println("LICZENIE")
        val procentTak = 100*tak/suma
        val procentNie = 100*nie/suma

        btnTak.text = procentTak.toString() + "%"
        btnNie.text = procentNie.toString() + "%"

    }
    override fun setUpClicks(): Unit {
        binding.btnTak.setOnClickListener {
            if(test == 1)
            {
                println("Oddales juz glos")
            }
            else {
                val firestore = FirebaseFirestore.getInstance()
                val auth = FirebaseAuth.getInstance()
                val user: FirebaseUser? = auth.currentUser
                val email = user?.email


                // Nazwa kolekcji, w której chcesz dodać dokument
                val collectionName = "Voted"
                // Dane do zapisania
                val newData = hashMapOf(
                    "Vote" to 1,
                    "email" to email
                )
                tak+=1
                // Dodanie nowego dokumentu do kolekcji
                firestore.collection(collectionName).add(newData)
                    .addOnSuccessListener { documentReference ->
                        println("Nowy dokument został pomyślnie dodany. ID dokumentu: ${documentReference.id}")
                    }
                    .addOnFailureListener { e ->
                        println("Wystąpił błąd: ${e.message}")
                    }
                liczenie()
                test = 1
            }
        }
        binding.btnNie.setOnClickListener {
            if(test == 1)
            {
                println("Oddales juz glos")
            }
            else
            {
                val firestore = FirebaseFirestore.getInstance()
                val auth = FirebaseAuth.getInstance()
                val user: FirebaseUser? = auth.currentUser
                val email = user?.email
                // Nazwa kolekcji, w której chcesz dodać dokument
                val collectionName = "Voted"

                // Dane do zapisania
                val newData = hashMapOf(
                    "Vote" to 0,
                    "email" to email
                )
                nie+=1
                // Dodanie nowego dokumentu do kolekcji
                firestore.collection(collectionName).add(newData)
                    .addOnSuccessListener { documentReference ->
                        println("Nowy dokument został pomyślnie dodany. ID dokumentu: ${documentReference.id}")
                    }
                    .addOnFailureListener { e ->
                        println("Wystąpił błąd: ${e.message}")
                    }
                liczenie()
                test = 1
            }
        }
        binding.imageMenu.setOnClickListener {
            val destIntent = ChatViewContainerActivity.getIntent(this, null)
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
        const val TAG: String = "VOTING_VIEW_ACTIVITY"


        fun getIntent(context: Context, bundle: Bundle?): Intent {
            val destIntent = Intent(context, VotingViewActivity::class.java)
            destIntent.putExtra("bundle", bundle)
            return destIntent
        }
    }
}
