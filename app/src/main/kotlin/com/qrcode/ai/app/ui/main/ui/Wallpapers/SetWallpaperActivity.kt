package com.qrcode.ai.app.ui.main.ui.Wallpapers

import android.app.WallpaperManager
import android.graphics.Bitmap
import android.widget.TextView
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.ads.control.ads.AperoAd
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.qrcode.ai.app.BuildConfig
import com.qrcode.ai.app.R
import com.qrcode.ai.app.databinding.ActivitySetWallBinding
import com.qrcode.ai.app.platform.BaseActivity
import com.qrcode.ai.app.utils.BasePrefers
import java.io.IOException

class SetWallpaperActivity : BaseActivity<ActivitySetWallBinding>() {
    override val layoutId: Int = R.layout.activity_set_wall

    private var bottomSheetDialog: BottomSheetDialog? = null
    private var tvLockScreen: TextView? = null
    private var tvHomeScreen: TextView? = null
    private var tvHomeAndLockScreen: TextView? = null
    private var tvCancel: TextView? = null
    private var bitmap: Bitmap? = null

    companion object {
        var isSetWallpaperSuccess = false
    }

    override fun setupUI() {
        super.setupUI()
        showBanner()
    }

    override fun setupListener() {
        binding.icBack.setOnClickListener {
            finish()
        }

        val imageUrl = intent.getStringExtra("imageUrl")
        if (imageUrl != null) {
            Glide.with(this)
                .asBitmap()
                .load(imageUrl)
                .into(object : SimpleTarget<Bitmap>() {
                    override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                        binding.bgImg.setImageBitmap(resource)
                        bitmap = resource
                        binding.btnSet.setOnClickListener {
                            showChooseWppTypeDialog()
                        }
                    }
                })
        }
    }

    private fun showChooseWppTypeDialog() {
        if (bottomSheetDialog == null) {
            bottomSheetDialog = BottomSheetDialog(this)
            bottomSheetDialog?.setContentView(R.layout.layout_wpp_bottom_sheet)
            tvCancel = bottomSheetDialog?.findViewById(R.id.tv_cancel)
            tvLockScreen = bottomSheetDialog?.findViewById(R.id.tv_lock_screen)
            tvHomeScreen = bottomSheetDialog?.findViewById(R.id.tv_home_screen)
            tvHomeAndLockScreen = bottomSheetDialog?.findViewById(R.id.tv_home_and_lock_screen)

            tvCancel?.setOnClickListener {
                bottomSheetDialog?.dismiss()
            }
            tvLockScreen?.setOnClickListener {
                setImageWallpaper(bitmap, WallpaperManager.FLAG_LOCK)
            }
            tvHomeScreen?.setOnClickListener {
                setImageWallpaper(bitmap, WallpaperManager.FLAG_SYSTEM)
            }
            tvHomeAndLockScreen?.setOnClickListener {
                setImageWallpaper(bitmap)
            }
        }
        bottomSheetDialog?.show()
    }

    /**
     * @param imageBitmap: bitmap tobe wallpaper
     * @param type: either [WallpaperManager.FLAG_SYSTEM] : for Home Screen,
     * [WallpaperManager.FLAG_LOCK] :for Lock Screen or nothing for both
     */
    private fun setImageWallpaper(imageBitmap: Bitmap?, type: Int? = null) {
        val myWallpaperManager = WallpaperManager
            .getInstance(applicationContext)
        if (type == null) {
            myWallpaperManager.setBitmap(imageBitmap)
        } else {
            myWallpaperManager.setBitmap(imageBitmap, null, true, type)
        }
        Toast.makeText(this, "set wallpaper successfully", Toast.LENGTH_SHORT).show()
        finish()
    }

    private fun showBanner() {
        if (BasePrefers.getPrefsInstance().banner) {
            binding.frAdsParent.visibility = View.VISIBLE
            AperoAd.getInstance().loadBannerFragment(
                this, BuildConfig.Banner, binding.root
            )
        } else {
            binding.frAdsParent.visibility = View.GONE
        }
    }
}

