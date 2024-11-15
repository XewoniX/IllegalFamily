package com.jakubsapplication.app.modules.homeview.ui


import ItemAdapter
import ItemModel
import android.Manifest
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.WindowManager
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
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.jakubsapplication.app.modules.mapview.ui.REQUEST_LOCATION_PERMISSION


class HomeViewActivity : BaseActivity<ActivityHomeViewBinding>(R.layout.activity_home_view) {
    private val viewModel: HomeViewVM by viewModels<HomeViewVM>()

    private lateinit var recyclerView: RecyclerView
    private lateinit var itemAdapter: ItemAdapter
    private lateinit var firestore: FirebaseFirestore
    override fun onInitialized(): Unit {
        viewModel.navArguments = intent.extras?.getBundle("bundle")
        binding.homeViewVM = viewModel

        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN or
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)


        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            REQUEST_LOCATION_PERMISSION
        )
        val auth = FirebaseAuth.getInstance()
        val button = findViewById<FrameLayout>(R.id.buttonDelete)
        button.setOnClickListener {
            showX()
        }
        val username = findViewById<TextView>(R.id.txtWitajUsernam)
        val db = FirebaseFirestore.getInstance()
        val user: FirebaseUser? = auth.currentUser
        val email = user?.email
        val kolekcjaRef = db.collection("QRAuth")
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
                                    val pole = data["name"]
                                    if (pole != null) {
                                        println(pole)
                                        username.text = "$pole, witaj w Illegal Family Brodnica!\n" +
                                                "Zapoznaj się z ogłoszeniami"
                                    }
                                }
                            } else {
                                println("Dokument nie istnieje")
                                username.text = "Witaj w Illegal Family Brodnica!\n" +
                                        "Zapoznaj się z ogłoszeniami"
                            }
                        }
                        .addOnFailureListener { e ->
                            println("Błąd pobierania dokumentu: $e")
                            username.text = "Witaj w Illegal Family Brodnica!\n" +
                                    "Zapoznaj się z ogłoszeniami"
                        }
                    println("ID dokumentu: $idDokumentu")
                }
            }
            .addOnFailureListener { e ->
                println("Błąd podczas wykonywania zapytania: $e")
            }


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
            finish()
        }
        binding.imageLineOne.setOnClickListener {
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


