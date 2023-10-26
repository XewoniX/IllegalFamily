package com.jakubsapplication.app.modules.loginview.ui


import android.content.Intent
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.Toast
import androidx.activity.viewModels
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.jakubsapplication.app.R
import com.jakubsapplication.app.appcomponents.base.BaseActivity
import com.jakubsapplication.app.databinding.ActivityLoginViewBinding
import com.jakubsapplication.app.modules.loginview.`data`.viewmodel.LoginViewVM
import kotlin.String
import kotlin.Unit

@Suppress("DEPRECATION")
class LoginViewActivity : BaseActivity<ActivityLoginViewBinding>(R.layout.activity_login_view) {
    private val viewModel: LoginViewVM by viewModels()
    private val RC_SIGN_IN = 12312123 // Możesz użyć dowolnej liczby całkowitej
    private val auth = FirebaseAuth.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN or
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        val mGoogleSignInClient = GoogleSignIn.getClient(this, gso)

        val button = findViewById<Button>(R.id.btnZalogujPrzezGoogle)
        button.setOnClickListener {
            val signInIntent = mGoogleSignInClient.signInIntent
            val user: FirebaseUser? = auth.currentUser

            if (user != null) {
                // Użytkownik jest zalogowany
                // Sprawdź dostawców uwierzytelnienia, czy jest połączony z kontem Google
                val providers = user.providerData

                for (userInfo in providers) {
                    val db = FirebaseFirestore.getInstance()
                    val collectionReference = db.collection("QRAuth") // Określ kolekcję
                    // Utwórz zapytanie, aby znaleźć dokumenty z danym adresem e-mail
                    val email = user.email
                    val emailToSearch = email
                    println(emailToSearch)
                    val query = collectionReference.whereEqualTo("adres_email", emailToSearch)
                    if (userInfo.providerId == "google.com") {
                        // Użytkownik jest połączony z kontem Google
                        // Możesz podjąć odpowiednie działania
                        query.get()
                            .addOnSuccessListener { querySnapshot ->
                                if (!querySnapshot.isEmpty) {
                                    // Adres e-mail istnieje w bazie danych
                                    println("Adres e-mail istnieje w bazie danych.")
                                    val intent = Intent(this, com.jakubsapplication.app.modules.homeview.ui.HomeViewActivity::class.java)
                                    startActivity(intent)
                                } else {
                                    val intent = Intent(this, com.jakubsapplication.app.modules.jointogroupview.ui.JoinToGroupViewActivity::class.java)
                                    startActivity(intent)
                                    // Adres e-mail nie istnieje w bazie danych
                                    println("Adres e-mail nie istnieje w bazie danych.")
                                }
                            }
                            .addOnFailureListener { exception ->
                                println("Błąd podczas sprawdzania adresu e-mail: $exception")
                            }
                        println("Użytkownik jest połączony z kontem Google.")
                    }
                }
            } else {
                // Użytkownik nie jest zalogowany
                startActivityForResult(signInIntent, RC_SIGN_IN)
                println("Użytkownik nie jest zalogowany.")
            }
        }
    }
    override fun onInitialized(): Unit {
        viewModel.navArguments = intent.extras?.getBundle("bundle")
        binding.loginViewVM = viewModel
    }
    override fun setUpClicks(): Unit {
    }
    companion object {
        const val TAG: String = "LOGIN_VIEW_ACTIVITY"
        fun getIntent(context: Context, bundle: Bundle?): Intent {
            val destIntent = Intent(context, LoginViewActivity::class.java)
            destIntent.putExtra("bundle", bundle)
            return destIntent
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)
                account.idToken?.let { firebaseAuthWithGoogle(it) }
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
            }
        }
    }
    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, /*accessToken=*/ null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    val db = FirebaseFirestore.getInstance()
                    val collectionReference = db.collection("QRAuth") // Określ kolekcję
                    // Utwórz zapytanie, aby znaleźć dokumenty z danym adresem e-mail
                    val email = user?.email
                    val emailToSearch = email
                    println(emailToSearch)
                    val query = collectionReference.whereEqualTo("adres_email", emailToSearch)
                    query.get()
                        .addOnSuccessListener { querySnapshot ->
                            if (!querySnapshot.isEmpty) {
                                println("Adres e-mail istnieje w bazie danych.")
                                val intent = Intent(this, com.jakubsapplication.app.modules.homeview.ui.HomeViewActivity::class.java)
                                startActivity(intent)
                            } else {
                                val intent = Intent(this, com.jakubsapplication.app.modules.jointogroupview.ui.JoinToGroupViewActivity::class.java)
                                startActivity(intent)
                            }
                        }
                        .addOnFailureListener { exception ->
                            println("Błąd podczas sprawdzania adresu e-mail: $exception")
                        }
                  //  val intent = Intent(this, com.jakubsapplication.app.modules.jointogroupview.ui.JoinToGroupViewActivity::class.java)
                   // startActivity(intent)
                } else {
                    // If sign in fails, display a message to the user.
                }
            }
    }

}
