package com.jakubsapplication.app.modules.jointogroupview.ui

import com.google.firebase.auth.FirebaseAuth
import androidx.activity.viewModels
import com.jakubsapplication.app.R
import com.jakubsapplication.app.appcomponents.base.BaseActivity
import com.jakubsapplication.app.databinding.ActivityJoinToGroupViewBinding
import com.jakubsapplication.app.modules.jointogroupview.`data`.viewmodel.JoinToGroupViewVM
import kotlin.String
import kotlin.Unit
import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.zxing.Result
import me.dm7.barcodescanner.zxing.ZXingScannerView

@Suppress("DEPRECATION")
class JoinToGroupViewActivity :
    BaseActivity<ActivityJoinToGroupViewBinding>(R.layout.activity_join_to_group_view),
    ZXingScannerView.ResultHandler {

    private lateinit var scannerView: ZXingScannerView
    private val cameraPermissionCode = 101
    private val viewModel: JoinToGroupViewVM by viewModels<JoinToGroupViewVM>()

    override fun onCreate(savedInstanceState: Bundle?) {
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN or
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_join_to_group_view)

        val cameraPreview = findViewById<LinearLayout>(R.id.camera_preview)
        scannerView = ZXingScannerView(this)
        cameraPreview.addView(scannerView)

        // Sprawdź i udziel uprawnienia do aparatu
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CAMERA),
                cameraPermissionCode
            )
        }
    }

    override fun onResume() {
        super.onResume()
        scannerView.setResultHandler(this)
        scannerView.startCamera()
    }

    override fun onPause() {
        super.onPause()
        scannerView.stopCamera()
    }

    override fun handleResult(result: Result) {
        val auth = FirebaseAuth.getInstance()
        val user = auth.currentUser
        val providers = user?.providerData
        data class Account(
            val adres_email: String,
            val name: String
        )


        if (providers != null) {
            for (userInfo in providers) {
                val db = FirebaseFirestore.getInstance()
                val collectionReference = db.collection("Ban") // Określ kolekcję
                // Utwórz zapytanie, aby znaleźć dokumenty z danym adresem e-mail
                val email = user.email
                val emailToSearch = email
                println(emailToSearch)
                val query = collectionReference.whereEqualTo("adres_email", emailToSearch)
                if (userInfo.providerId == "google.com") {
                    query.get()
                        .addOnSuccessListener { querySnapshot ->
                            if (!querySnapshot.isEmpty) {
                                // Adres e-mail istnieje w bazie danych
                                Toast.makeText(
                                    getApplicationContext(),
                                    "Twoje konto zostało zablokowane, skontaktuj się z administratorem", Toast.LENGTH_SHORT
                                ).show();
                                println("Adres e-mail istnieje w bazie danych i jest zablokowany przez administratora.")
                            } else {
                                db.collection("QR")
                                    .get()
                                    .addOnSuccessListener { documents ->
                                        for (document in documents) {
                                            val data = document.data
                                            val pole1 = data["QR"]
                                            println("Pole 1: $pole1")
                                            if (pole1 == result.text) {
                                                Log.d("QRScanner", "Kod QR zgadza się!")
                                                val user = Firebase.auth.currentUser
                                                val displayName = user?.displayName
                                                    val nameParts = displayName!!.split(" ")
                                                    val firstName = nameParts[0]
                                                val email = user?.email
                                                val collectionReference = db.collection("QRAuth")
                                                val data = hashMapOf("adres_email" to email)
                                                val account = email?.let { Account(it,firstName) }
                                                if (account != null) {
                                                    collectionReference.add(account)
                                                        .addOnSuccessListener { documentReference ->
                                                            println("Adres e-mail został zapisany z sukcesem.")

                                                            val intent = Intent(
                                                                this,
                                                                com.jakubsapplication.app.modules.homeview.ui.HomeViewActivity::class.java
                                                            )
                                                            startActivity(intent)
                                                        }
                                                        .addOnFailureListener { exception ->
                                                            println("Błąd podczas zapisywania adresu e-mail: $exception")
                                                        }
                                                }
                                            } else {
                                                Log.d("QRScanner", "Kod QR nie zgadza sie! :(")
                                                Toast.makeText(
                                                    getApplicationContext(),
                                                    "Kod QR nie jest zgodny, zapytaj organizatora o nowy", Toast.LENGTH_SHORT
                                                ).show();
                                            }
                                        }
                                    }
                                    .addOnFailureListener { exception ->
                                        // Obsłuż błąd
                                        Log.d("QRScanner", "Błąd :(")
                                    }
                                println("Adres e-mail nie jest zablokowany.")
                            }
                        }
                        .addOnFailureListener { exception ->
                            println("Błąd podczas sprawdzania adresu e-mail: $exception")
                        }
                    println("Użytkownik jest połączony z kontem Google.")
                }
            }
        }


        // Ten kod zostanie wywołany po zeskanowaniu kodu QR.
        Log.d("QRScanner", "Wartość zeskanowanego kodu: ${result.text}")

        // Dodaj opóźnienie, jeśli chcesz kontynuować skanowanie po udanym skanowaniu
        Handler(Looper.getMainLooper()).postDelayed({
            scannerView.resumeCameraPreview(this)
        }, 2000) // Po 2 sekundach
    }

    override fun onInitialized(): Unit {
        viewModel.navArguments = intent.extras?.getBundle("bundle")
        binding.joinToGroupViewVM = viewModel
    }

    override fun setUpClicks(): Unit {
    }

    companion object {
        const val TAG: String = "JOIN_TO_GROUP_VIEW_ACTIVITY"

    }
}
