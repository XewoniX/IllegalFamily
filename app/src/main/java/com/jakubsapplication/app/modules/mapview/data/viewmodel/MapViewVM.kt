package com.jakubsapplication.app.modules.mapview.`data`.viewmodel

import android.os.Bundle
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.jakubsapplication.app.modules.mapview.`data`.model.MapViewModel
import org.koin.core.KoinComponent

class MapViewVM : ViewModel(), KoinComponent {
  val mapViewModel: MutableLiveData<MapViewModel> = MutableLiveData(MapViewModel())

  var navArguments: Bundle? = null
}
