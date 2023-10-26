package com.jakubsapplication.app.modules.chatviewcontainer.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.viewModels
import com.jakubsapplication.app.R
import com.jakubsapplication.app.appcomponents.base.BaseActivity
import com.jakubsapplication.app.databinding.ActivityChatViewContainerBinding
import com.jakubsapplication.app.extensions.loadFragment
import com.jakubsapplication.app.modules.chatview.ui.ChatViewFragment
import com.jakubsapplication.app.modules.chatviewcontainer.`data`.viewmodel.ChatViewContainerVM
import com.jakubsapplication.app.modules.homeview.ui.HomeViewActivity
import com.jakubsapplication.app.modules.mapview.ui.MapViewActivity
import com.jakubsapplication.app.modules.profilesettingview.ui.ProfileSettingViewActivity
import com.jakubsapplication.app.modules.votingview.ui.VotingViewActivity
import kotlin.String
import kotlin.Unit

class ChatViewContainerActivity :
    BaseActivity<ActivityChatViewContainerBinding>(R.layout.activity_chat_view_container) {
  private val viewModel: ChatViewContainerVM by viewModels<ChatViewContainerVM>()

  override fun onInitialized(): Unit {
    viewModel.navArguments = intent.extras?.getBundle("bundle")
    binding.chatViewContainerVM = viewModel
    val destFragment = ChatViewFragment.getInstance(null)
    this.loadFragment(
      R.id.frameStackbackground,
      destFragment,
      bundle = destFragment.arguments,
      tag = ChatViewFragment.TAG,
      addToBackStack = false,
      add = false,
      enter = null,
      exit = null,
    )
    }

    override fun setUpClicks(): Unit {
      binding.frameCheckringligh.setOnClickListener {
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