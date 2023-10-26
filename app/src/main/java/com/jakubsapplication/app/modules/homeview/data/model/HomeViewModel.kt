package com.jakubsapplication.app.modules.homeview.`data`.model

import com.jakubsapplication.app.R
import com.jakubsapplication.app.appcomponents.di.MyApp
import kotlin.String

data class HomeViewModel(
  /**
   * TODO Replace with dynamic value
   */
  var txtWitajUsernam: String? = MyApp.getInstance().resources.getString(R.string.msg_witaj_usernam)
  ,
  /**
   * TODO Replace with dynamic value
   */
  var txtDescription: String? =
      MyApp.getInstance().resources.getString(R.string.msg_nocna_jazda_sta)
  ,
  /**
   * TODO Replace with dynamic value
   */
  var txtDescriptionOne: String? =
      MyApp.getInstance().resources.getString(R.string.msg_uwaga_konkurs)
  ,
  /**
   * TODO Replace with dynamic value
   */
  var txtZapraszamdoza: String? =
      MyApp.getInstance().resources.getString(R.string.msg_zapraszam_do_za)

)
