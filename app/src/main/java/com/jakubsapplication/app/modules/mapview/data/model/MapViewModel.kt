package com.jakubsapplication.app.modules.mapview.`data`.model

import com.jakubsapplication.app.R
import com.jakubsapplication.app.appcomponents.di.MyApp
import kotlin.String

data class MapViewModel(
  /**
   * TODO Replace with dynamic value
   */
  var txtTUbdziemapa: String? = MyApp.getInstance().resources.getString(R.string.lbl_tu_b_dzie_mapa)

)
