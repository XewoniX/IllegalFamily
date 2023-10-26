package com.jakubsapplication.app.modules.chatview.`data`.model

import com.jakubsapplication.app.R
import com.jakubsapplication.app.appcomponents.di.MyApp
import kotlin.String

data class ChatViewModel(
  /**
   * TODO Replace with dynamic value
   */
  var txtCzatgrupowy: String? = MyApp.getInstance().resources.getString(R.string.lbl_czat_grupowy)
  ,
  /**
   * TODO Replace with dynamic value
   */
  var txtTextBorder: String? = MyApp.getInstance().resources.getString(R.string.msg_tak_spocik_zacz)
  ,
  /**
   * TODO Replace with dynamic value
   */
  var txtTekstCounter: String? = MyApp.getInstance().resources.getString(R.string.lbl_tekst)

)
