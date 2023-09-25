package com.qrcode.ai.app.ui.splash.ui.onboarding

import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.navigation.Navigation
import androidx.viewpager2.widget.ViewPager2
import com.ads.control.admob.Admob
import com.ads.control.ads.AperoAd
import com.ads.control.ads.AperoAdCallback
import com.ads.control.ads.wrapper.ApAdError
import com.ads.control.ads.wrapper.ApInterstitialAd
import com.ads.control.ads.wrapper.ApNativeAd
import com.qrcode.ai.app.BuildConfig
import com.qrcode.ai.app.R
import com.qrcode.ai.app.databinding.FragmentOnboardingBinding
import com.qrcode.ai.app.platform.BaseFragment
import com.qrcode.ai.app.ui.splash.ui.onboarding.screen.OnboardFirstFragment
import com.qrcode.ai.app.ui.splash.ui.onboarding.screen.OnboardSecondFragment
import com.qrcode.ai.app.ui.splash.ui.onboarding.screen.OnboardThirdFragment
import com.qrcode.ai.app.ui.splash.ui.onboarding.screen.ViewPagerAdapter
import com.qrcode.ai.app.utils.BasePrefers
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OnboardingFragment : BaseFragment<FragmentOnboardingBinding>() {

    private var isViewCreated = false
    private var native: ApNativeAd? = null
    private var mInterOnboard: ApInterstitialAd? = null

    override val layoutId: Int
        get() = R.layout.fragment_onboarding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val fragmentList = arrayListOf(
            OnboardFirstFragment(), OnboardSecondFragment(), OnboardThirdFragment()
        )
        val adapter = ViewPagerAdapter(
            fragmentList, requireActivity().supportFragmentManager, lifecycle
        )
        binding.viewpagerOnboard.adapter = adapter
        binding.viewpagerOnboard.offscreenPageLimit = 3
        binding.viewpagerOnboard.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                if (position == 2){
                    binding.continueButton.setText(R.string.get_start)
                }else{
                    binding.continueButton.setText(R.string.next)
                }
            }
        })
        binding.IndicatorViewBanner.attachTo(binding.viewpagerOnboard)
    }
    override fun setupUI() {
        super.setupUI()

        isViewCreated = true

        setupNativeOnboard()
    }

    private fun setupNativeOnboard() {

        if (BasePrefers.getPrefsInstance().nativeOnboard) {
            loadNativeOnboard()
        } else {
            binding.frNativeAdsActivity.visibility = View.INVISIBLE
        }
    }

    override fun setupListener() {
        super.setupListener()

        binding.continueButton.setOnClickListener {
            if (binding.viewpagerOnboard.currentItem < 2) {
                binding.viewpagerOnboard.currentItem =
                    binding.viewpagerOnboard.currentItem + 1
            } else {
                if (BasePrefers.getPrefsInstance().interOnboard) {
                    activity?.let {
                        Admob.getInstance().setOpenActivityAfterShowInterAds(true)
                        AperoAd.getInstance().forceShowInterstitial(
                            it, mInterOnboard, object : AperoAdCallback() {
                                override fun onNextAction() {
                                    super.onNextAction()
                                    onGotoMain()
                                }
                            }, true
                        )
                    }
                } else {
                    onGotoMain()
                }
            }
        }
    }




    private fun onGotoMain() {
        BasePrefers.getPrefsInstance().openboard = true
        activity?.let {
            Navigation.findNavController(
                it, R.id.nav_host_splash_fragment
            ).navigate(R.id.openMain)
            Handler().postDelayed({
                it.finish()
            }, 1000)
        }
    }

    private fun loadNativeOnboard() {
        AperoAd.getInstance().loadNativeAdResultCallback(activity,
            BuildConfig.Native_Onboard,
            R.layout.native_onboarding_ads,
            object : AperoAdCallback() {
                override fun onNativeAdLoaded(nativeAd: ApNativeAd) {
                    onAdLoaded(nativeAd)
                }

                override fun onAdFailedToLoad(adError: ApAdError?) {
                    onNativeAdFailed()
                }
            })
    }

    fun onAdLoaded(native: ApNativeAd) {
        if (isViewCreated) {
            addNativeAds(native)
        } else {
            this.native = native
        }
    }

    fun onNativeAdFailed() {
        if (isViewCreated) {
            removeNativeAds()
        }
    }

    private fun addNativeAds(native: ApNativeAd?) {
        binding.shimmerContainerNative1.stopShimmer()
        binding.shimmerContainerNative1.visibility = View.INVISIBLE
        binding.flAdplaceholderActivity.visibility = View.VISIBLE
        AperoAd.getInstance().populateNativeAdView(
            activity, native, binding.flAdplaceholderActivity, binding.shimmerContainerNative1
        )
    }

    private fun removeNativeAds() {
        binding.shimmerContainerNative1.stopShimmer()
        binding.shimmerContainerNative1.visibility = View.INVISIBLE
        binding.flAdplaceholderActivity.visibility = View.INVISIBLE
    }

    override fun onDestroy() {
        isViewCreated = false
        super.onDestroy()
    }
}
