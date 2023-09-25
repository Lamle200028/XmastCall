package com.qrcode.ai.app.ui.main.ui.Wallpapers

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.ads.control.ads.AperoAd
import com.qrcode.ai.app.BuildConfig
import com.qrcode.ai.app.R
import com.qrcode.ai.app.databinding.FragmentWallpapersBinding
import com.qrcode.ai.app.platform.BaseFragment
import com.qrcode.ai.app.utils.BasePrefers

class WallpapersFragment : BaseFragment<FragmentWallpapersBinding>() {

    private val viewModel: WallpaperViewModel by viewModels()
    private lateinit var adapter: WallAdapter

    override val layoutId: Int
        get() = R.layout.fragment_wallpapers

    override fun setupUI() {
        super.setupUI()
        showBanner()
    }

    override fun setupData() {
        adapter = WallAdapter(onItemClickListener = { item ->
            val intent = Intent(context, SetWallpaperActivity::class.java)
            intent.putExtra("imageUrl", item.url)
            startActivity(intent)
        })
        binding.rcvWallPaper.adapter = adapter
        binding.rcvWallPaper.setHasFixedSize(true)
        val layoutManager = GridLayoutManager(requireContext(), 3, GridLayoutManager.VERTICAL, false)
        binding.rcvWallPaper.layoutManager = layoutManager

        viewModel.onSuccessGetData.observe(viewLifecycleOwner) {
            adapter.setData(it)
        }

        viewModel.startFetchAllWallpaper()
    }

    override fun setupListener() {
        super.setupListener()
        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun showBanner() {
        if (BasePrefers.getPrefsInstance().banner) {
            binding.frAdsParent.visibility = View.VISIBLE
            AperoAd.getInstance().loadBannerFragment(
                context as Activity, BuildConfig.Banner, binding.root
            )
        } else {
            binding.frAdsParent.visibility = View.GONE
        }
    }
}