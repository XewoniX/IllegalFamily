package com.jakubsapplication.app.modules.homeview.`data`.viewmodel

import android.os.Bundle
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.jakubsapplication.app.modules.homeview.`data`.model.HomeViewModel
import org.koin.core.KoinComponent

class HomeViewVM : ViewModel(), KoinComponent {
  val homeViewModel: MutableLiveData<HomeViewModel> = MutableLiveData(HomeViewModel())

  var navArguments: Bundle? = null
}
