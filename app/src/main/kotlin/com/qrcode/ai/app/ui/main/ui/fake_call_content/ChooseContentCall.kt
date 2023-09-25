package com.qrcode.ai.app.ui.main.ui.fake_call_content

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.ads.control.admob.AppOpenManager
import com.ads.control.ads.AperoAd
import com.ads.control.ads.AperoAdCallback
import com.ads.control.ads.wrapper.ApAdError
import com.ads.control.ads.wrapper.ApNativeAd
import com.qrcode.ai.app.BuildConfig
import com.qrcode.ai.app.R
import com.qrcode.ai.app.databinding.FragmentChooseContentCallBinding
import com.qrcode.ai.app.platform.BaseFragment
import com.qrcode.ai.app.ui.main.ui.setupfakecall.SetupFakeCallViewModel
import com.qrcode.ai.app.utils.BasePrefers
import com.qrcode.ai.app.utils.Constants
import com.qrcode.ai.app.utils.api.ApiState
import com.qrcode.ai.app.utils.api.FakeVideoData
import com.qrcode.ai.app.utils.repo.DataViewModel
import com.qrcode.ai.app.utils.repo.Repository
import com.qrcode.ai.app.utils.repo.ViewModelFactory

class ChooseContentCall : BaseFragment<FragmentChooseContentCallBinding>(),
    ContentAdapter.OnClickItemListener {
    private lateinit var adapter: ContentAdapter
    private val viewModel: SetupFakeCallViewModel by activityViewModels()

    private var isViewCreated = false
    private var native: ApNativeAd? = null
    private var isNativeLoaded = false

    companion object {
        var stype : String? = ""
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.url = null
    }

    override fun onClickItem(item: FakeVideoData) {
        viewModel.url = item.video
    }

    override val layoutId: Int
        get() = R.layout.fragment_choose_content_call

    override fun setupUI() {
        isViewCreated = true
        if (!isNativeLoaded) {
            loadNative()
        }

        AppOpenManager.getInstance().enableAppResume()
        val myDataVM = ViewModelProvider(
            this, ViewModelFactory(Repository())
        )[DataViewModel::class.java]
        myDataVM.getDataList()
        lifecycleScope.launchWhenCreated {
            myDataVM.myDataList.collect {
                when (it) {
                    is ApiState.Empty -> {
                        println("Empty...")
                    }

                    is ApiState.Loading -> {

                    }

                    is ApiState.Failure -> {
                        it.e.printStackTrace()
                    }

                    is ApiState.Success -> {
                        val list = it.data as ArrayList<FakeVideoData>
                        adapter = ContentAdapter(list, this@ChooseContentCall)
                        adapter.mCheckPosition = list.indexOf(list.firstOrNull { item -> item.video == viewModel.url } ?: list[0])
                        binding.rcvContent.adapter = adapter
                    }
                }
            }
        }
    }

    override fun setupListener() {
        binding.icBack.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.done.setOnClickListener {
            findNavController().navigate(
                R.id.openSetupFakeCall, bundleOf(
                    Constants.KEY_TYPE to arguments?.getString(Constants.KEY_TYPE)
                )
            )
        }
    }

    private fun loadNative() {
        if (BasePrefers.getPrefsInstance().nativeChooseSanta) {
            AperoAd.getInstance().loadNativeAdResultCallback(activity,
                BuildConfig.Native_ChooseSanta,
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
