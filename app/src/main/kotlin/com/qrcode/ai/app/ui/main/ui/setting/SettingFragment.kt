package com.qrcode.ai.app.ui.main.ui.setting

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat.getColor
import androidx.navigation.Navigation
import com.ads.control.admob.AppOpenManager
import com.ads.control.ads.AperoAd
import com.qrcode.ai.app.BuildConfig
import com.qrcode.ai.app.R
import com.qrcode.ai.app.databinding.FragmentSettingBinding
import com.qrcode.ai.app.platform.BaseFragment
import com.qrcode.ai.app.ui.main.MainActivity
import com.qrcode.ai.app.ui.main.widget.PrivacyDialog
import com.qrcode.ai.app.ui.main.widget.RatingDialog
import com.qrcode.ai.app.utils.BasePrefers
import com.qrcode.ai.app.utils.Constants
import com.qrcode.ai.app.utils.Constants.LINK_STORE
import com.qrcode.ai.app.utils.Constants.email_support
import com.qrcode.ai.app.utils.Constants.request_code_share
import com.qrcode.ai.app.utils.Constants.store_uri
import com.qrcode.ai.app.utils.Constants.type_plain_text
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class SettingFragment : BaseFragment<FragmentSettingBinding>() {

    override val layoutId: Int
        get() = R.layout.fragment_setting

    private var dialogPrivacy: PrivacyDialog? = null
    private var isBackFromOtherActivity = false
    private var locale = Constants.en

    companion object {

        fun newInstance(): SettingFragment {
            val args = Bundle()
            val fragment = SettingFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onResume() {
        super.onResume()
        if (isBackFromOtherActivity) {
            AppOpenManager.getInstance().enableAppResume()
            isBackFromOtherActivity = false
        }
    }

    override fun setupData() {
        super.setupData()

        locale = when (BasePrefers.getPrefsInstance().locale) {
            Constants.en -> getString(R.string.setting_en)
            Constants.vi -> getString(R.string.setting_vi)
            Constants.es -> getString(R.string.setting_spain)
            Constants.it -> getString(R.string.setting_it)
            Constants.hi -> getString(R.string.setting_hindi)
            else -> Locale.getDefault().displayLanguage
        }
    }

    override fun setupUI() {
        super.setupUI()

        activity?.let {
            it.window.statusBarColor = getColor(it, R.color.transparent)
        }

//        binding.txtLanguage.text = locale
        binding.txtVersion.text = BuildConfig.VERSION_NAME
//        AppOpenManager.getInstance().enableAppResume()
//        showBanner()
    }

    private fun showBanner() {
        if (BasePrefers.getPrefsInstance().banner) {
            binding.frAdsParent.visibility = View.VISIBLE
            AperoAd.getInstance().loadBannerFragment(
                activity as MainActivity, BuildConfig.Banner, binding.root
            )
        } else {
            binding.frAdsParent.visibility = View.GONE
        }
    }

    override fun setupListener() {
        binding.containerRate.setOnClickListener {
            openRate()
        }

        binding.containerPrivacy.setOnClickListener {
            AppOpenManager.getInstance().disableAppResume()
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(Constants.PRIVACY_POLICY_LINK)
            startActivity(intent)
        }

        binding.containerShare.setOnClickListener {
            openShare()
        }

        binding.icBack.setOnClickListener {
            activity?.let {
                val navController = Navigation.findNavController(
                    it, R.id.nav_host_main_fragment
                )
                navController.popBackStack()
            }
        }

        binding.containerLanguage.setOnClickListener {
            activity?.let {
                val navController = Navigation.findNavController(
                    it, R.id.nav_host_main_fragment
                )
                navController.navigate(R.id.openLanguage)
            }
        }
    }

    private fun openRate() {
        val rateDialog = RatingDialog(onAccept = { isReview ->
            AppOpenManager.getInstance().disableAppResume()
            if (isReview) {
                openAppInPlayStore()
                AppOpenManager.getInstance().disableAdResumeByClickAction()
            } else {
                goToFeedback()
            }
        })
        rateDialog.show(parentFragmentManager, "showRatingDialog")
    }

    private fun openShare() {
        AppOpenManager.getInstance().disableAppResume()
        val text = buildString {
            append(getString(R.string.your_friend))
            append(getString(R.string.app_name))
            append(getString(R.string.visit_app))
            append(LINK_STORE)
        }
        val intent = Intent(Intent.ACTION_SEND)
        intent.putExtra(Intent.EXTRA_TEXT, text)
        intent.type = type_plain_text
        startActivityForResult(
            Intent.createChooser(intent, getString(R.string.choose_way_share)), request_code_share
        )
    }

    private fun openPrivacy() {
        AppOpenManager.getInstance().disableAppResume()
        dialogPrivacy = PrivacyDialog()
        dialogPrivacy?.show(parentFragmentManager, null)
    }

    @Suppress("DEPRECATION")
    private fun goToFeedback() {
        AppOpenManager.getInstance().disableAppResume()
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = type_plain_text
            putExtra(Intent.EXTRA_EMAIL, arrayOf(email_support))
            putExtra(Intent.EXTRA_SUBJECT, buildString {
                append(getString(R.string.feedback))
                append(getString(R.string.app_name))
            })
            putExtra(Intent.EXTRA_TEXT, getString(R.string.feedback))
        }
        startActivityForResult(
            Intent.createChooser(intent, getString(R.string.feedback)), request_code_share
        )
    }

    private fun openAppInPlayStore() {
        AppOpenManager.getInstance().disableAppResume()
        val uri = Uri.parse(store_uri)
        val goToMarketIntent = Intent(Intent.ACTION_VIEW, uri)

        var flags = Intent.FLAG_ACTIVITY_NO_HISTORY or Intent.FLAG_ACTIVITY_MULTIPLE_TASK
        flags = flags or Intent.FLAG_ACTIVITY_NEW_DOCUMENT
        goToMarketIntent.addFlags(flags)

        try {
            startActivityForResult(goToMarketIntent, request_code_share)
        } catch (e: ActivityNotFoundException) {
            val intent = Intent(
                Intent.ACTION_VIEW, Uri.parse(LINK_STORE)
            )
            startActivityForResult(intent, request_code_share)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == request_code_share) {
            isBackFromOtherActivity = true
        }
    }

}
