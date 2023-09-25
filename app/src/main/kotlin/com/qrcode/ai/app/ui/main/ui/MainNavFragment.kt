package com.qrcode.ai.app.ui.main.ui

import android.graphics.Color
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.ads.control.admob.AppOpenManager
import com.ads.control.ads.AperoAd
import com.qrcode.ai.app.BuildConfig
import com.qrcode.ai.app.R
import com.qrcode.ai.app.databinding.FragmentMainNavBinding
import com.qrcode.ai.app.platform.BaseFragment
import com.qrcode.ai.app.ui.main.MainActivity
import com.qrcode.ai.app.ui.main.ui.home.HomeFragment
import com.qrcode.ai.app.ui.main.ui.setting.SettingFragment
import com.qrcode.ai.app.utils.BasePrefers
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainNavFragment : BaseFragment<FragmentMainNavBinding>() {

    override val layoutId: Int
        get() = R.layout.fragment_main_nav

    private var mHomeFragment: HomeFragment? = null
    private var mSettingFragment: SettingFragment? = null

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

    override fun setupUI() {
        super.setupUI()

        AppOpenManager.getInstance().enableAppResume()
//        showBanner()

        val pagerAdapter = ScreenSlidePagerAdapter(requireActivity())
        binding.viewPager2.offscreenPageLimit = 2
        binding.viewPager2.adapter = pagerAdapter
    }

    override fun setupListener() {
        super.setupListener()

        binding.apply {
            btnCenter.setOnClickListener {

            }
        }

        binding.containerHistory.setOnClickListener {

        }

        binding.containerSettings.setOnClickListener {
            clickMenuTools()
        }

        binding.viewPager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                when (position) {
                    0 -> {
                        clickMenuHome()
                    }

                    1 -> {
                        clickMenuTools()
                    }
                }
            }
        })
    }

    private fun clickMenuHome() {
        binding.icHistory.setImageResource(R.drawable.ic_menu_history_selected)
        binding.icSettings.setImageResource(R.drawable.ic_menu_settings_unselected)
        binding.txtHistory.setTextColor(Color.parseColor("#FFFFFF"))
        binding.txtSettings.visibility = View.GONE
        binding.txtHistory.visibility = View.VISIBLE

        binding.viewPager2.currentItem = 0
    }

    private fun clickMenuTools() {
        binding.icHistory.setImageResource(R.drawable.ic_menu_history_unselected)
        binding.icSettings.setImageResource(R.drawable.ic_menu_settings_selected)
        binding.txtSettings.setTextColor(Color.parseColor("#FFFFFF"))
        binding.txtSettings.visibility = View.VISIBLE
        binding.txtHistory.visibility = View.GONE

        binding.viewPager2.currentItem = 1
    }

    private inner class ScreenSlidePagerAdapter(fa: FragmentActivity) : FragmentStateAdapter(fa) {
        override fun getItemCount() = 2
        override fun createFragment(position: Int): Fragment =
            handleOnNavigationItemSelected(position)
    }

    private fun handleOnNavigationItemSelected(itemId: Int) = when (itemId) {
        1 -> getFragmentForIndex(1)
        else -> getFragmentForIndex(0)
    }

    private fun initFragmentAt(position: Int): Fragment {
        when (position) {
            1 -> mSettingFragment = SettingFragment.newInstance()
            else -> mHomeFragment = HomeFragment.newInstance()
        }
        return handleOnNavigationItemSelected(position)
    }

    private fun getFragmentForIndex(index: Int) = when (index) {
        1 -> mSettingFragment ?: initFragmentAt(index)
        else -> mHomeFragment ?: initFragmentAt(index)
    }
}
