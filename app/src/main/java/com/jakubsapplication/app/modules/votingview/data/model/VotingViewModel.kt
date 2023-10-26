package com.jakubsapplication.app.modules.votingview.`data`.model

import com.jakubsapplication.app.R
import com.jakubsapplication.app.appcomponents.di.MyApp
import kotlin.String

data class VotingViewModel(
  /**
   * TODO Replace with dynamic value
   */
  var txtGosowanie: String? = MyApp.getInstance().resources.getString(R.string.lbl_g_osowanie)
  ,
  /**
   * TODO Replace with dynamic value
   */
  var txtCzychceciespo: String? =
      MyApp.getInstance().resources.getString(R.string.msg_czy_chcecie_spo)

)
