package com.jakubsapplication.app.modules.profilesettingview.`data`.model

import com.jakubsapplication.app.R
import com.jakubsapplication.app.appcomponents.di.MyApp
import kotlin.String

data class ProfileSettingViewModel(
  /**
   * TODO Replace with dynamic value
   */
  var txtUsername: String? = MyApp.getInstance().resources.getString(R.string.lbl_username)
  ,
  /**
   * TODO Replace with dynamic value
   */
  var txtDodatkowefunkc: String? =
      MyApp.getInstance().resources.getString(R.string.msg_dodatkowe_funkc)
  ,
  /**
   * TODO Replace with dynamic value
   */
  var txtDodatkowefunkcOne: String? =
      MyApp.getInstance().resources.getString(R.string.msg_dodatkowe_funkc)
  ,
  /**
   * TODO Replace with dynamic value
   */
  var txtDodatkowefunkcTwo: String? =
      MyApp.getInstance().resources.getString(R.string.msg_dodatkowe_funkc)
  ,
  /**
   * TODO Replace with dynamic value
   */
  var txtDodatkowefunkcThree: String? =
      MyApp.getInstance().resources.getString(R.string.msg_dodatkowe_funkc)
  ,
  /**
   * TODO Replace with dynamic value
   */
  var txtDodatkowefunkcFour: String? =
      MyApp.getInstance().resources.getString(R.string.msg_dodatkowe_funkc)
  ,
  /**
   * TODO Replace with dynamic value
   */
  var txtDodatkowefunkcFive: String? =
      MyApp.getInstance().resources.getString(R.string.msg_dodatkowe_funkc)
  ,
  /**
   * TODO Replace with dynamic value
   */
  var txtDodatkowefunkcSix: String? =
      MyApp.getInstance().resources.getString(R.string.msg_dodatkowe_funkc)

)
