package com.qrcode.ai.app.ui.splash.ui

import android.view.View
import androidx.fragment.app.activityViewModels
import com.qrcode.ai.app.R
import com.qrcode.ai.app.databinding.FragmentSplashNavBinding
import com.qrcode.ai.app.platform.BaseFragment
import com.qrcode.ai.app.ui.splash.SplashViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SplashNavFragment : BaseFragment<FragmentSplashNavBinding>() {

    private val viewModel: SplashViewModel by activityViewModels()
    override val layoutId: Int
        get() = R.layout.fragment_splash_nav

    override fun setupUI() {
        super.setupUI()
        viewModel.isShowInterSplash.observeForever {
            if (it) binding.cover.visibility = View.VISIBLE
        }
    }
}
