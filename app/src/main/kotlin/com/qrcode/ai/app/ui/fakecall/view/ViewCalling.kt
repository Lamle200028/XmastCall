package com.qrcode.ai.app.ui.fakecall.view

import android.app.Activity
import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import com.ads.control.ads.AperoAd
import com.qrcode.ai.app.BuildConfig
import com.qrcode.ai.app.R
import com.qrcode.ai.app.platform.BaseCustomViewLinearLayout
import com.qrcode.ai.app.utils.BasePrefers

class ViewCalling : BaseCallView {
    constructor(context: Context?) : this(context, null)
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context, attrs, defStyleAttr
    ){
        showBanner()
    }

    override val layoutId: Int
        get() = R.layout.layout_view_calling

    private fun showBanner() {
        if (BasePrefers.getPrefsInstance().banner) {
            findViewById<View>(R.id.fr_ads_parent).visibility = View.VISIBLE
            findViewById<View>(R.id.view).visibility = View.VISIBLE
            AperoAd.getInstance().loadBannerFragment(
                context as Activity?, BuildConfig.Banner, rootView
            )
        } else {
            findViewById<View>(R.id.fr_ads_parent).visibility = View.GONE
            findViewById<View>(R.id.view).visibility = View.GONE
        }
    }

}
