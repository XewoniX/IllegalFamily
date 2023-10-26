package com.jakubsapplication.app.modules.scanqrcodeview.ui

import androidx.activity.viewModels
import com.jakubsapplication.app.R
import com.jakubsapplication.app.appcomponents.base.BaseActivity
import com.jakubsapplication.app.databinding.ActivityScanQrCodeViewBinding
import com.jakubsapplication.app.modules.scanqrcodeview.`data`.viewmodel.ScanQrCodeViewVM
import kotlin.String
import kotlin.Unit

class ScanQrCodeViewActivity :
    BaseActivity<ActivityScanQrCodeViewBinding>(R.layout.activity_scan_qr_code_view) {
  private val viewModel: ScanQrCodeViewVM by viewModels<ScanQrCodeViewVM>()

  override fun onInitialized(): Unit {
    viewModel.navArguments = intent.extras?.getBundle("bundle")
    binding.scanQrCodeViewVM = viewModel
  }

  override fun setUpClicks(): Unit {
  }

  companion object {
    const val TAG: String = "SCAN_QR_CODE_VIEW_ACTIVITY"

  }
}
