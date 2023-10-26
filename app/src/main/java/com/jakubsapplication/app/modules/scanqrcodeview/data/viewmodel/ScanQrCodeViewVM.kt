package com.jakubsapplication.app.modules.scanqrcodeview.`data`.viewmodel

import android.os.Bundle
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.jakubsapplication.app.modules.scanqrcodeview.`data`.model.ScanQrCodeViewModel
import org.koin.core.KoinComponent

class ScanQrCodeViewVM : ViewModel(), KoinComponent {
  val scanQrCodeViewModel: MutableLiveData<ScanQrCodeViewModel> =
      MutableLiveData(ScanQrCodeViewModel())

  var navArguments: Bundle? = null
}
