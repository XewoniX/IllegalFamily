package com.jakubsapplication.app.modules.votingview.`data`.viewmodel

import android.os.Bundle
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.jakubsapplication.app.modules.votingview.`data`.model.VotingViewModel
import org.koin.core.KoinComponent

class VotingViewVM : ViewModel(), KoinComponent {
  val votingViewModel: MutableLiveData<VotingViewModel> = MutableLiveData(VotingViewModel())

  var navArguments: Bundle? = null
}
