package com.jakubsapplication.app.modules.chatview.ui

import android.os.Bundle
import androidx.fragment.app.viewModels
import com.jakubsapplication.app.R
import com.jakubsapplication.app.appcomponents.base.BaseFragment
import com.jakubsapplication.app.databinding.FragmentChatViewBinding
import com.jakubsapplication.app.modules.chatview.`data`.viewmodel.ChatViewVM
import kotlin.String
import kotlin.Unit

class ChatViewFragment : BaseFragment<FragmentChatViewBinding>(R.layout.fragment_chat_view) {
  private val viewModel: ChatViewVM by viewModels<ChatViewVM>()

  override fun onInitialized(): Unit {
    viewModel.navArguments = arguments
    binding.chatViewVM = viewModel
  }

  override fun setUpClicks(): Unit {
  }

  companion object {
    const val TAG: String = "CHAT_VIEW_FRAGMENT"


    fun getInstance(bundle: Bundle?): ChatViewFragment {
      val fragment = ChatViewFragment()
      fragment.arguments = bundle
      return fragment
    }
  }
}
