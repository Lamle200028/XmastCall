package com.qrcode.ai.app.ui.main.ui.home

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.ads.control.admob.AppOpenManager
import com.ads.control.ads.AperoAd
import com.ads.control.ads.AperoAdCallback
import com.ads.control.ads.wrapper.ApAdError
import com.ads.control.ads.wrapper.ApInterstitialAd
import com.ads.control.ads.wrapper.ApNativeAd
import com.qrcode.ai.app.BuildConfig
import com.qrcode.ai.app.R
import com.qrcode.ai.app.databinding.FragmentHomeBinding
import com.qrcode.ai.app.platform.BaseFragment
import com.qrcode.ai.app.ui.fakecall.view.ViewCallEnd
import com.qrcode.ai.app.ui.main.ui.fake_call_content.ChooseContentCall
import com.qrcode.ai.app.ui.main.widget.RatingDialog
import com.qrcode.ai.app.utils.BasePrefers
import com.qrcode.ai.app.utils.Constants
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding>() {

    private var isViewCreated = false
    private var native: ApNativeAd? = null
    private var isNativeLoaded = false

    override val layoutId: Int
        get() = R.layout.fragment_home
    var PERMISSION_CAMERA1 = 1
    private var isRequestingPermission = false

    companion object {
        var isShowDialog = false
        fun newInstance(): HomeFragment {
            val args = Bundle()
            val fragment = HomeFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onResume() {
        super.onResume()
        if (BasePrefers.getPrefsInstance().openResume && !isShowDialog) {
            AppOpenManager.getInstance().enableAppResume()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (ViewCallEnd.iscallend) {
            RatingDialog(
                onAccept = {
                    if (it) {
                        ViewCallEnd.iscallend = false
                    }
                }
            ).show(parentFragmentManager, null)

            ViewCallEnd.iscallend = false
            isShowDialog = true
        }
        isViewCreated = true
        if (!isNativeLoaded) {
            loadNative()
        }
    }

    override fun setupListener() {
        binding.apply {
            btnFakeCall.setOnClickListener {
                ChooseContentCall.stype = "voice"
                findNavController().navigate(
                    R.id.openSetupFakeCall, bundleOf(
                        Constants.KEY_TYPE to Constants.VOICE
                    )
                )
            }
            btnFakeVideoCall.setOnClickListener {
                if (ContextCompat.checkSelfPermission(
                        requireContext(),
                        Manifest.permission.CAMERA
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    if (!isRequestingPermission) {
                        isRequestingPermission = true
                        ActivityCompat.requestPermissions(
                            requireActivity(),
                            arrayOf(Manifest.permission.CAMERA),
                            PERMISSION_CAMERA1
                        )
                    } else {
                        val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
                        builder.setMessage(R.string.popup_per)
                        builder.setPositiveButton("Yes") { _, _ ->
                            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                            val uri = Uri.fromParts("package", requireActivity().packageName, null)
                            intent.data = uri
                            AppOpenManager.getInstance().disableAppResume()
                            startActivity(intent)
                        }
                        builder.setNegativeButton("No") { dialog, _ ->
                            dialog.dismiss()
                        }
                        builder.show()
                    }
                } else {
                    ChooseContentCall.stype = "video"
                    if (BasePrefers.getPrefsInstance().interVideoCall) {
                        loadAndShowInterAds {
                            findNavController().navigate(
                                R.id.gotoContent, bundleOf(
                                    Constants.KEY_TYPE to Constants.VIDEO
                                )
                            )
                        }
                    } else {
                        findNavController().navigate(
                            R.id.gotoContent, bundleOf(
                                Constants.KEY_TYPE to Constants.VIDEO
                            )
                        )
                    }
                }
            }
            btnFakeMessage.setOnClickListener {
                findNavController().navigate(R.id.openMessager)
            }
            btnMenu.setOnClickListener {
                findNavController().navigate(R.id.settingFragment)
            }
            btnVoicemail.setOnClickListener{
                findNavController().navigate(R.id.voiceMail)
            }
            btnWriteLetter.setOnClickListener{
                findNavController().navigate(R.id.writeLetter)
            }
        }


    }

    private fun loadAndShowInterAds(callback: () -> Unit) {
        AppOpenManager.getInstance().disableAppResume()
        AperoAd.getInstance().getInterstitialAds(
            activity,
            BuildConfig.Inter_VideoCall,
            loadAdCallback.apply { nextScreen = callback })
    }

    private val loadAdCallback = object : AperoAdCallback() {

        var nextScreen = {}

        override fun onInterstitialLoad(interstitialAd: ApInterstitialAd?) {
            nextScreen.invoke()
            AperoAd.getInstance().forceShowInterstitial(
                requireActivity(),
                interstitialAd,
                adListener.apply { nextScreen2 = nextScreen },
                true
            )
        }

        override fun onAdFailedToLoad(adError: ApAdError?) {
            nextScreen.invoke()
        }
    }

    private val adListener = object : AperoAdCallback() {
        var nextScreen2 = {}

        override fun onAdFailedToShow(adError: ApAdError?) {
            nextScreen2.invoke()
        }
    }

    private fun loadNative() {
        if (BasePrefers.getPrefsInstance().nativeHome) {
            AperoAd.getInstance().loadNativeAdResultCallback(activity,
                BuildConfig.Native_Home,
                R.layout.native_language_first_open,
                object : AperoAdCallback() {
                    override fun onNativeAdLoaded(nativeAd: ApNativeAd) {
                        isNativeLoaded = true
                        onAdLoaded(nativeAd)
                    }

                    override fun onAdFailedToLoad(adError: ApAdError?) {
                        onNativeAdFailed()
                    }
                })
        } else {
            binding.frNativeAdsActivity.visibility = View.GONE
        }
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
        binding.shimmerContainerNative1.visibility = View.GONE
        binding.flAdplaceholderActivity.visibility = View.VISIBLE
        AperoAd.getInstance().populateNativeAdView(
            activity, native, binding.flAdplaceholderActivity, binding.shimmerContainerNative1
        )
    }

    private fun removeNativeAds() {
        binding.shimmerContainerNative1.stopShimmer()
        binding.shimmerContainerNative1.visibility = View.GONE
        binding.flAdplaceholderActivity.visibility = View.GONE
    }

    override fun onDestroy() {
        isViewCreated = false
        super.onDestroy()
    }
}
