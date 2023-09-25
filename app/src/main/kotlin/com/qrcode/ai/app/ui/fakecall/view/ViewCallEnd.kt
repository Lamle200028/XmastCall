package com.qrcode.ai.app.ui.fakecall.view

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.CountDownTimer
import android.util.AttributeSet
import android.view.View
import android.widget.TextView
import com.ads.control.ads.AperoAd
import com.qrcode.ai.app.BuildConfig
import com.qrcode.ai.app.R
import com.qrcode.ai.app.ui.main.MainActivity
import com.qrcode.ai.app.utils.BasePrefers

class ViewCallEnd : BaseCallView {
    constructor(context: Context?) : this(context, null)
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context, attrs, defStyleAttr
    ) {
        showBanner()
        setupListener()
    }
    var timer : CountDownTimer? = null
    private var timeInterval : Long = 4000
    private var timeDelay: Long = 3000
    companion object{
        var iscallend : Boolean = false
        var time: String = "00:00"
    }

    override val layoutId: Int
        get() = R.layout.layout_view_call_end


    private fun setupListener() {
        findViewById<TextView>(R.id.call_time).text = time
        startPopup()
    }
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
    fun startPopup() {
        timer?.cancel()
        timer = object : CountDownTimer(timeDelay, timeInterval) {
            override fun onTick(millisUntilFinished: Long) {}
            override fun onFinish() {
                iscallend = true
                context.startActivity(Intent(context, MainActivity::class.java))
                (context as Activity).finish()
            }
        }
        timer?.start()
    }
}
