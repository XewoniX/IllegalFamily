package com.jakubsapplication.app.modules.chatview.`data`.viewmodel

import android.os.Bundle
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.jakubsapplication.app.modules.chatview.`data`.model.ChatViewModel
import org.koin.core.KoinComponent

class ChatViewVM : ViewModel(), KoinComponent {
  val chatViewModel: MutableLiveData<ChatViewModel> = MutableLiveData(ChatViewModel())

  var navArguments: Bundle? = null
}
