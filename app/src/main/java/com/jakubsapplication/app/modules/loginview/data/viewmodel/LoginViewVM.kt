package com.jakubsapplication.app.modules.loginview.`data`.viewmodel

import android.os.Bundle
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.jakubsapplication.app.modules.loginview.`data`.model.LoginViewModel
import org.koin.core.KoinComponent

class LoginViewVM : ViewModel(), KoinComponent {
  val loginViewModel: MutableLiveData<LoginViewModel> = MutableLiveData(LoginViewModel())

  var navArguments: Bundle? = null


}
