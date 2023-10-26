package com.jakubsapplication.app.modules.jointogroupview.`data`.viewmodel

import android.os.Bundle
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.jakubsapplication.app.modules.jointogroupview.`data`.model.JoinToGroupViewModel
import org.koin.core.KoinComponent

class JoinToGroupViewVM : ViewModel(), KoinComponent {
  val joinToGroupViewModel: MutableLiveData<JoinToGroupViewModel> =
      MutableLiveData(JoinToGroupViewModel())

  var navArguments: Bundle? = null
}
