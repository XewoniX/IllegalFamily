package com.jakubsapplication.app.modules.votingview.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.activity.viewModels
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

  override fun onInitialized(): Unit {
    viewModel.navArguments = intent.extras?.getBundle("bundle")
    binding.votingViewVM = viewModel

    window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN or
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE
    window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
  }

  override fun setUpClicks(): Unit {
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
