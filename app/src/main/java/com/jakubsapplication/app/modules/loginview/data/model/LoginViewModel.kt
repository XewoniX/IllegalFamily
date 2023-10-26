package com.jakubsapplication.app.modules.loginview.`data`.model

import com.jakubsapplication.app.R
import com.jakubsapplication.app.appcomponents.di.MyApp
import kotlin.String

data class LoginViewModel(
  /**
   * TODO Replace with dynamic value
   */
  var txtIllegalnightr: String? =
      MyApp.getInstance().resources.getString(R.string.msg_illegal_night_r)



)
