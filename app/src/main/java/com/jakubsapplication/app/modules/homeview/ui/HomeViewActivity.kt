package com.jakubsapplication.app.modules.homeview.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
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

class HomeViewActivity : BaseActivity<ActivityHomeViewBinding>(R.layout.activity_home_view) {
  private val viewModel: HomeViewVM by viewModels<HomeViewVM>()

  override fun onInitialized(): Unit {
    viewModel.navArguments = intent.extras?.getBundle("bundle")
    binding.homeViewVM = viewModel

    val user = Firebase.auth.currentUser
    val username = findViewById<TextView>(R.id.txtWitajUsernam)

        // Sprawdź, czy użytkownik jest zalogowany
    if (user != null) {
      val displayName = user.displayName
      if (displayName != null) {
        // Rozdziel imię i nazwisko (jeśli dostępne)
        val nameParts = displayName.split(" ")
        val firstName = nameParts[0]
        username.text = "$firstName, witaj w Illegal Family Brodnica!"
      } else {
        username.text = "Witaj w Illegal Family Brodnica!"
      }
    }
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
