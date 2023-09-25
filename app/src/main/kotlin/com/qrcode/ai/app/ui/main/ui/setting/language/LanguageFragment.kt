package com.qrcode.ai.app.ui.main.ui.setting.settings.language

import android.annotation.SuppressLint
import android.content.Intent
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ads.control.admob.AppOpenManager
import com.ads.control.ads.AperoAd
import com.qrcode.ai.app.BuildConfig
import com.qrcode.ai.app.R
import com.qrcode.ai.app.databinding.FragmentLanguageBinding
import com.qrcode.ai.app.platform.BaseFragment
import com.qrcode.ai.app.ui.main.MainActivity
import com.qrcode.ai.app.ui.main.ui.setting.language.LanguageViewModel
import com.qrcode.ai.app.ui.splash.ui.first_language.Language
import com.qrcode.ai.app.utils.BasePrefers
import com.qrcode.ai.app.utils.Constants
import com.qrcode.ai.app.utils.ContextUtils
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder

class LanguageFragment : BaseFragment<FragmentLanguageBinding>(), LanguageItem.OnLanguageListener {

    override val layoutId: Int
        get() = R.layout.fragment_language

    private lateinit var groupLanguage: GroupAdapter<GroupieViewHolder>
    private lateinit var locales: ArrayList<Language>

    private val languageViewModel: LanguageViewModel by viewModels()

    private var newLocale: String? = null

    override fun setupUI() {
        super.setupUI()

        binding.rvLanguage.apply {
            setHasFixedSize(false)
            layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            adapter = groupLanguage
        }
        AppOpenManager.getInstance().enableAppResume()
    }

    override fun setupListener() {
        super.setupListener()

        binding.choseBtn.setOnClickListener {
            newLocale = languageViewModel.chosenLanguage?.code
            val locale = BasePrefers.getPrefsInstance().locale
            if (locale != newLocale) {
                BasePrefers.getPrefsInstance().locale = newLocale ?: Constants.en
            }
            refreshLayout()
        }

        binding.icBack.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    override fun setupData() {
        super.setupData()


        groupLanguage = GroupAdapter()
        groupLanguage.clear()

        locales = ContextUtils.getLocalesList(resources)
        languageViewModel.chosenLanguage = locales.firstOrNull {
            language -> language.code == BasePrefers.getPrefsInstance().locale
        }
        locales.forEach {
            groupLanguage.add(LanguageItem(it, this, languageViewModel))
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onChooseLanguage(language: Language, position: Int) {
        languageViewModel.chosenLanguage = language
        groupLanguage.notifyDataSetChanged()
    }

    private fun refreshLayout() {
        val intent = requireActivity().intent
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
        requireActivity().finish()
        startActivity(intent)
    }
}

