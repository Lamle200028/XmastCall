package com.qrcode.ai.app.ui.main.ui.no_internet

import android.content.Intent
import android.net.wifi.WifiManager
import android.util.Log
import androidx.navigation.fragment.findNavController
import com.ads.control.admob.AppOpenManager
import com.qrcode.ai.app.R
import com.qrcode.ai.app.databinding.FragmentNoInternetBinding
import com.qrcode.ai.app.platform.BaseFragment
import com.qrcode.ai.app.utils.NetworkUtil


class NoInternetFragment : BaseFragment<FragmentNoInternetBinding>() {
    override fun onResume() {
        super.onResume()
        if (NetworkUtil.isInternetAvailable(requireContext())) {
            findNavController().popBackStack()
        }
        AppOpenManager.getInstance().disableAppResume()
    }

    override fun setupListener() {
        binding.btnConnect.setOnClickListener {
            startActivity(Intent(WifiManager.ACTION_PICK_WIFI_NETWORK))
            AppOpenManager.getInstance().disableAdResumeByClickAction()
        }
    }

    override val layoutId: Int = R.layout.fragment_no_internet

}
