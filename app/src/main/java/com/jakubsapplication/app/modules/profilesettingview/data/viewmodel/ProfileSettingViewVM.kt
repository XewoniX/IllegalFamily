package com.jakubsapplication.app.modules.profilesettingview.`data`.viewmodel

import android.os.Bundle
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.jakubsapplication.app.modules.profilesettingview.`data`.model.ProfileSettingViewModel
import org.koin.core.KoinComponent

class ProfileSettingViewVM : ViewModel(), KoinComponent {
  val profileSettingViewModel: MutableLiveData<ProfileSettingViewModel> =
      MutableLiveData(ProfileSettingViewModel())

  var navArguments: Bundle? = null
}
