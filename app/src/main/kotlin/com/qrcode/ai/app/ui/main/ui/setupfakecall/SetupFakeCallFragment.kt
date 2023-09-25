package com.qrcode.ai.app.ui.main.ui.setupfakecall

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.ads.control.admob.AppOpenManager
import com.ads.control.ads.AperoAd
import com.ads.control.ads.AperoAdCallback
import com.ads.control.ads.wrapper.ApAdError
import com.ads.control.ads.wrapper.ApInterstitialAd
import com.ads.control.ads.wrapper.ApNativeAd
import com.qrcode.ai.app.BuildConfig
import com.qrcode.ai.app.R
import com.qrcode.ai.app.databinding.FragmentSetupFakeCallBinding
import com.qrcode.ai.app.platform.BaseFragment
import com.qrcode.ai.app.ui.fakecall.FakeCallActivity
import com.qrcode.ai.app.ui.main.ui.fake_call_content.ChooseContentCall
import com.qrcode.ai.app.ui.main.ui.setupfakecall.SetupFakeCallViewModel.Companion.TYPE_INCOMING
import com.qrcode.ai.app.ui.main.ui.setupfakecall.SetupFakeCallViewModel.Companion.TYPE_OUTGOING
import com.qrcode.ai.app.utils.BasePrefers
import com.qrcode.ai.app.utils.Constants
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_setup_fake_call.*


@Suppress("DEPRECATION")
@AndroidEntryPoint
class SetupFakeCallFragment : BaseFragment<FragmentSetupFakeCallBinding>() {
    private val viewModel: SetupFakeCallViewModel by viewModels()
    private lateinit var adapter: TimeAdapter
    private var isViewCreated = false
    private var native: ApNativeAd? = null
    private var reloadCount = 0
    private val maxReload = 2
    private var callScheduled = false

    override val layoutId: Int
        get() = R.layout.fragment_setup_fake_call

    private val overlayPermissionRequestCode = 123

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        context?.let { viewModel.cancelWorker(it, Constants.WORKER_TAG) }
    }

    override fun setupUI() {
        super.setupUI()
        isViewCreated = true
        loadNative()
        AppOpenManager.getInstance().enableAppResume()
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
        if (ChooseContentCall.stype == "video") {
            binding.title.setText(R.string.setup_video_call)
            binding.done.setImageResource(R.drawable.ic_videocall)
        }
        binding.lnInComing.alpha = 1f
        binding.radio2.alpha = 0.6f
    }

    override fun setupData() {
        super.setupData()
        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        adapter = TimeAdapter(onItemClickListener = {
            binding.tvTime.text = context?.let { it1 -> it.toString(it1) }
            viewModel.time = (it)
            updateUIChooseTime()
        })
        binding.rvTime.adapter = adapter
        adapter.setData(
            listOf(
                Time(0),
                Time(15 * 1000),
                Time(30 * 1000),
                Time(1 * 60 * 1000),
                Time(5 * 60 * 1000),
                Time(30 * 60 * 1000),
                Time(1 * 60 * 60 * 1000),
                Time(2 * 60 * 60 * 1000),
            )
        )
    }

    override fun setupListener() {
        super.setupListener()
        viewModel.callStarted = {
            enableUI()
            callScheduled = false
        }
        binding.apply {
            radio1.setOnClickListener {
                binding.lnInComing.alpha = 1f
                binding.radio2.alpha = 0.6f
                binding.radio2.isChecked = false
                imgChooseTime.isEnabled = true
                tvTime.isEnabled = true
                this@SetupFakeCallFragment.viewModel.selectDirection(TYPE_INCOMING)
            }

            radio2.setOnClickListener {
                binding.lnInComing.alpha = 0.6f
                binding.radio2.alpha = 1f
                binding.radio1.isChecked = false
                imgChooseTime.isEnabled = false
                tvTime.isEnabled = false
                chooseTimeContainer.visibility = View.GONE
                this@SetupFakeCallFragment.viewModel.cancelWorker(requireActivity(), Constants.WORKER_TAG)
                this@SetupFakeCallFragment.viewModel.selectDirection(TYPE_OUTGOING)
            }

            imgChooseTime.setOnClickListener {
                if (!Settings.canDrawOverlays(context)) {
                    val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
                    builder.setMessage(R.string.pop_please_over)
                    builder.setPositiveButton("Yes") { _, _ ->
                        Toast.makeText(
                            context,
                            "Please Grant Overlay Permission",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                        val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
                        intent.data = Uri.parse("package:" + context?.packageName)
                        AppOpenManager.getInstance().disableAppResume()
                        startActivityForResult(intent, overlayPermissionRequestCode)
                    }
                    builder.setNegativeButton("No") { dialog, _ ->
                        dialog.dismiss()
                    }
                    builder.show()
                } else {
                    updateUIChooseTime()
                }
            }
            tvTime.setOnClickListener { imgChooseTime.performClick() }

            done.setOnClickListener {
                if (callScheduled) return@setOnClickListener
                callScheduled = true
                binding.chooseTimeContainer.visibility = View.GONE
                if (BasePrefers.getPrefsInstance().interSetup) {
                    disableUI()
                    loadAndShowInterAds()
                } else {
                    navigateNextScreen()
                }
            }

            icBack.setOnClickListener {
                findNavController().popBackStack()
            }
        }
    }

    private fun updateUIChooseTime() {
        binding.chooseTimeContainer.visibility =
            if (choose_time_container.visibility == View.GONE) View.VISIBLE else View.GONE
        binding.imgChooseTime.setImageResource(
            if (choose_time_container.visibility == View.GONE) R.drawable.ic_down else R.drawable.ic_up
        )
    }

    private fun addCountDown() {
        disableUI()
        AppOpenManager.getInstance().disableAppResume()
        binding.callContent.removeAllViews()

        val ft = childFragmentManager.beginTransaction()
        val frag = CountDownFragment(onCountdownFinished = {
            enableUI()
            callScheduled = false
            binding.callContent.removeAllViews()
            AppOpenManager.getInstance().enableAppResume()
        })
        ft.replace(R.id.call_content, frag)
        ft.commit()
    }

    private fun navigateNextScreen() {
        if (viewModel.time != null && viewModel.time?.timeInMillis != 0L) {
            addCountDown()
        }
        context?.let { it1 ->
            this@SetupFakeCallFragment.viewModel.schedulingCall(
                it1, arguments?.getString(Constants.KEY_TYPE)
            )
        }
    }

    private fun loadAndShowInterAds() {
        AppOpenManager.getInstance().disableAppResume()
        AperoAd.getInstance().getInterstitialAds(activity, BuildConfig.Inter_Setup, loadAdCallback)
    }

    private val loadAdCallback = object : AperoAdCallback() {

        override fun onInterstitialLoad(interstitialAd: ApInterstitialAd?) {
            AperoAd.getInstance().forceShowInterstitial(
                requireActivity(), interstitialAd, adListener, true
            )
        }
    }
    private val adListener = object : AperoAdCallback() {

        override fun onAdFailedToLoad(adError: ApAdError?) {
            navigateNextScreen()
        }

        override fun onAdClosed() {
            navigateNextScreen()
        }
    }


    private fun loadNative() {
        if (BasePrefers.getPrefsInstance().nativeSetupCall) {
            binding.frNativeAdsActivity.visibility = View.VISIBLE
            binding.shimmerContainerNative1.startShimmer()
            binding.shimmerContainerNative1.visibility = View.VISIBLE
            binding.flAdplaceholderActivity.visibility = View.GONE

            AperoAd.getInstance().loadNativeAdResultCallback(activity,
                BuildConfig.Native_SetupCall,
                R.layout.native_language_first_open,
                object : AperoAdCallback() {
                    override fun onNativeAdLoaded(nativeAd: ApNativeAd) {
                        onAdLoaded(nativeAd)
                    }

                    override fun onAdFailedToLoad(adError: ApAdError?) {
                        if (reloadCount < maxReload) {
                            loadNative()
                        } else {
                            onNativeAdFailed()
                        }
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

    private fun disableUI() {
        binding.radio1.isEnabled = false
        binding.radio2.isEnabled = false
        binding.imgChooseTime.isEnabled = false
        binding.tvTime.isEnabled = false
    }

    private fun enableUI() {
        binding.radio1.isEnabled = true
        binding.radio2.isEnabled = true
        binding.imgChooseTime.isEnabled = true
        binding.tvTime.isEnabled = true
    }

    override fun onResume() {
        super.onResume()
        if (!callScheduled) {
            enableUI()
        }
    }

    override fun onDestroy() {
        isViewCreated = false
        super.onDestroy()
        AppOpenManager.getInstance().enableAppResume()
    }
}
