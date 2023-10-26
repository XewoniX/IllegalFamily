package com.jakubsapplication.app.modules.jointogroupview.`data`.model

import com.jakubsapplication.app.R
import com.jakubsapplication.app.appcomponents.di.MyApp
import kotlin.String

data class JoinToGroupViewModel(
  /**
   * TODO Replace with dynamic value
   */
  var txtIllegalnightr: String? =
      MyApp.getInstance().resources.getString(R.string.msg_illegal_night_r)
  ,
  /**
   * TODO Replace with dynamic value
   */
  var txtAbydoczydo: String? = MyApp.getInstance().resources.getString(R.string.msg_aby_do_czy_do)
  ,
  /**
   * TODO Replace with dynamic value
   */
  var etGotoscanbuttValue: String? = null
)
