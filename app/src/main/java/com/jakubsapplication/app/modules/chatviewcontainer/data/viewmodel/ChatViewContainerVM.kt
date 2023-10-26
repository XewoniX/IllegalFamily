package com.jakubsapplication.app.modules.chatviewcontainer.`data`.viewmodel

import android.os.Bundle
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.jakubsapplication.app.modules.chatviewcontainer.`data`.model.ChatViewContainerModel
import org.koin.core.KoinComponent

class ChatViewContainerVM : ViewModel(), KoinComponent {
  val chatViewContainerModel: MutableLiveData<ChatViewContainerModel> =
      MutableLiveData(ChatViewContainerModel())

  var navArguments: Bundle? = null
}
