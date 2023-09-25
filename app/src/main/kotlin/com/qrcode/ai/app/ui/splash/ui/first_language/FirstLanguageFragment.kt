package com.qrcode.ai.app.ui.splash.ui.first_language

import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ads.control.admob.AppOpenManager
import com.ads.control.ads.AperoAd
import com.ads.control.ads.AperoAdCallback
import com.ads.control.ads.wrapper.ApAdError
import com.ads.control.ads.wrapper.ApNativeAd
import com.qrcode.ai.app.BuildConfig
import com.qrcode.ai.app.R
import com.qrcode.ai.app.databinding.FragmentFirstLanguageBinding
import com.qrcode.ai.app.platform.BaseFragment
import com.qrcode.ai.app.ui.main.ui.setting.language.LanguageViewModel
import com.qrcode.ai.app.ui.main.ui.setting.settings.language.LanguageItem
import com.qrcode.ai.app.utils.*
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import java.util.Locale

data class Language(
    val id: Int, val name: String, val code: String, val image: Int
)

class FirstLanguageFragment : BaseFragment<FragmentFirstLanguageBinding>(),
    LanguageItem.OnLanguageListener {

    private lateinit var groupLanguage: GroupAdapter<GroupieViewHolder>
    private lateinit var locales: ArrayList<Language>

    private val languageViewModel: LanguageViewModel by viewModels()
    private var newLocale: String? = null
    private var isViewCreated = false
    private var native: ApNativeAd? = null

    override fun setupData() {
        super.setupData()

        groupLanguage = GroupAdapter()
        groupLanguage.clear()
        initLanguage()
    }

    override fun setupUI() {
        super.setupUI()

        isViewCreated = true

        val ct = context
        if (ct != null) {
            initView(ct)
        }

        if (BasePrefers.getPrefsInstance().nativeLanguage && isShowNativeLanguage()) {
            loadNativeFirstLanguage()
        } else {
            binding.frNativeAdsActivity.visibility = View.GONE
        }
    }

    override fun setupListener() {
        super.setupListener()

        binding.choseBtn.setOnClickListener {
            newLocale = languageViewModel.chosenLanguage?.code ?: Locale.getDefault().displayLanguage
            if (newLocale != null) {
                BasePrefers.getPrefsInstance().locale = newLocale ?: Locale.getDefault().displayLanguage
                val wrapContext = MyContextWrapper.wrap(
                    requireContext(), newLocale!!
                )
                resources.updateConfiguration(
                    wrapContext.resources.configuration, wrapContext.resources.displayMetrics
                )
            }
            goToOnboarding()
//            goToMain()
        }
    }

    override fun onResume() {
        super.onResume()
        AppOpenManager.getInstance().enableAppResume()
    }

    private fun initView(context: Context) {
        initRecyclerView(context)
    }

    private fun goToOnboarding() {
        activity?.let {
            BasePrefers.getPrefsInstance().newUser = false
            val navController = Navigation.findNavController(it, R.id.nav_host_splash_fragment)
            navController.navigate(R.id.openOnboarding)
        }
    }

    private fun goToMain() {
        activity?.let {
            BasePrefers.getPrefsInstance().newUser = false
            val navController = Navigation.findNavController(it, R.id.nav_host_splash_fragment)
            navController.navigate(R.id.openMain)
        }
    }

    private fun initRecyclerView(context: Context) {
        binding.languageSettingRcv.apply {
            setHasFixedSize(false)
            layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            adapter = groupLanguage
        }

    }

    private fun initLanguage() {
        locales = ContextUtils.getLocalesListFirt(resources)
        locales.forEach {
            groupLanguage.add(LanguageItem(it, this, languageViewModel))
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onChooseLanguage(language: Language, position: Int) {
        languageViewModel.chosenLanguage = language
        groupLanguage.notifyDataSetChanged()
    }

    private fun loadNativeFirstLanguage() {
        AperoAd.getInstance().loadNativeAdResultCallback(activity,
            BuildConfig.Native_Language,
            R.layout.native_language_first_open,
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

    override val layoutId: Int
        get() = R.layout.fragment_first_language
}
