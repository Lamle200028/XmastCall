package com.qrcode.ai.app.ui.fakecall.view

import android.app.Activity
import android.content.Context
import android.media.Ringtone
import android.net.Uri
import android.os.Handler
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import com.ads.control.ads.AperoAd
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.source.DefaultMediaSourceFactory
import com.google.android.exoplayer2.ui.StyledPlayerView
import com.qrcode.ai.app.BuildConfig
import com.qrcode.ai.app.R
import com.qrcode.ai.app.ui.fakecall.FakeCallActivity
import com.qrcode.ai.app.ui.main.ui.setupfakecall.SetupFakeCallViewModel
import com.qrcode.ai.app.utils.BasePrefers
import com.qrcode.ai.app.utils.CameraUtils

class ViewVideoSpeaking : BaseCallView {
    constructor(context: Context?) : this(context, null)
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context, attrs, defStyleAttr
    ) {
        setupUI()
        setupListener()
    }

    private var timerHandler: Handler? = null
    private var timerRunnable: Runnable? = null
    private val playerView: StyledPlayerView by lazy {
        layout.findViewById(R.id.video_player)
    }
    var player: ExoPlayer? = null
    var ringtoneAlarm: Ringtone? = null
    private var counter = 0
    var direction: Int = SetupFakeCallViewModel.TYPE_INCOMING
    private var isMute = false

    override val layoutId: Int
        get() = R.layout.layout_view_video_speaking

    private fun setupUI() {
        showBanner()
        CameraUtils.init(layout.findViewById(R.id.previewView))
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

    fun start() {
        if (direction == SetupFakeCallViewModel.TYPE_INCOMING) {
            play()
        } else {
            Handler().postDelayed({
                play()
            }, 5000)
        }
    }

    fun stop() {
        timerRunnable?.let { timerHandler?.removeCallbacks(it) }
        player?.stop()
    }

    private fun play() {
        val cardView = findViewById<CardView>(R.id.card_camera)
        val params: ConstraintLayout.LayoutParams by lazy {
            val paramsSetting = ConstraintLayout.LayoutParams(
                resources.getDimensionPixelSize(R.dimen._90sdp),
                resources.getDimensionPixelSize(R.dimen._130sdp)
            )
            paramsSetting.setMargins(
                0,
                resources.getDimensionPixelSize(R.dimen._20sdp),
                resources.getDimensionPixelSize(R.dimen._20sdp),
                0
            )
            paramsSetting.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
            paramsSetting.rightToRight = ConstraintLayout.LayoutParams.PARENT_ID
            return@lazy paramsSetting
        }
        cardView.layoutParams = params
        cardView.requestLayout()
        player?.play()
    }

    private fun setupListener() {
        findViewById<ConstraintLayout>(R.id.button_1).setOnClickListener {
            if (findViewById<CardView>(R.id.card_camera).visibility == View.VISIBLE) {
                findViewById<CardView>(R.id.card_camera).visibility = View.GONE
                findViewById<ImageView>(R.id.icon_1)?.visibility = View.GONE
                findViewById<ImageView>(R.id.icon_1_2)?.visibility = View.VISIBLE
            } else {
                findViewById<CardView>(R.id.card_camera).visibility = View.VISIBLE
                findViewById<ImageView>(R.id.icon_1)?.visibility = View.VISIBLE
                findViewById<ImageView>(R.id.icon_1_2)?.visibility = View.GONE
            }
        }
        findViewById<ConstraintLayout>(R.id.button_2).setOnClickListener {
            CameraUtils.flipCamera()
        }
        findViewById<ConstraintLayout>(R.id.button_3).setOnClickListener {
            if (!isMute) {
                isMute = true
                findViewById<ImageView>(R.id.icon_3_2).isVisible = true
                findViewById<ImageView>(R.id.icon_3).isVisible = false
            } else {
                isMute = false
                findViewById<ImageView>(R.id.icon_3_2).isVisible = false
                findViewById<ImageView>(R.id.icon_3).isVisible = true
            }
        }
    }

    fun initializePlayer(url: String) {
        player = ExoPlayer.Builder(context).build()
        playerView.player = player

        val mediaItem = MediaItem.fromUri(Uri.parse(url))
        val mediaSourceFactory = DefaultMediaSourceFactory(context)
        val mediaSource = mediaSourceFactory.createMediaSource(mediaItem)

        player?.setMediaSource(mediaSource)
        player?.prepare()
        player?.addListener(object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                when (isPlaying) {
                    true -> {
                        findViewById<TextView>(R.id.profile_text_speaking).visibility = View.VISIBLE
                        findViewById<LinearLayout>(R.id.layout_profile_calling).visibility =
                            View.GONE
                        findViewById<CardView>(R.id.card_camera).visibility = View.VISIBLE
                        ringtoneAlarm?.stop()
                        playerView.visibility = View.VISIBLE
                        timerHandler = Handler()
                        timerRunnable = object : Runnable {
                            override fun run() {
                                val minutes = counter / 60
                                val seconds = counter % 60
                                findViewById<TextView>(R.id.profile_text_speaking).text =
                                    String.format("%02d:%02d", minutes, seconds)
                                counter++
                                timerHandler?.postDelayed(this, 1000)
                            }
                        }
                        timerHandler?.postDelayed(timerRunnable as Runnable, 0)
                        if (FakeCallActivity.checkreject) {
                            timerHandler!!.removeCallbacks(timerRunnable as Runnable)
                            FakeCallActivity.checkreject = false
                        }
                    }

                    false -> playerView.visibility = View.INVISIBLE
                }
            }
        })
    }
}
