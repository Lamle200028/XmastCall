package com.qrcode.ai.app.ui.fakecall

import android.media.Ringtone
import android.media.RingtoneManager
import android.net.Uri
import android.view.ViewGroup
import android.widget.LinearLayout
import com.ads.control.admob.AppOpenManager
import com.google.android.exoplayer2.ExoPlayer
import com.qrcode.ai.app.R
import com.qrcode.ai.app.databinding.LayoutActivityCallBinding
import com.qrcode.ai.app.platform.BaseActivity
import com.qrcode.ai.app.ui.fakecall.view.*
import com.qrcode.ai.app.ui.main.ui.setupfakecall.SetupFakeCallViewModel
import com.qrcode.ai.app.utils.BasePrefers

class FakeCallActivity : BaseActivity<LayoutActivityCallBinding>() {
    override val layoutId: Int = R.layout.layout_activity_call
    private lateinit var alarmTone: Uri
    private var ringtoneAlarm: Ringtone? = null
    private var player: ExoPlayer? = null
    private var viewCall: BaseCallView? = null

    companion object {
        var checkreject: Boolean = false
        var isBackFromCallActivity = false
    }

    private val params: LinearLayout.LayoutParams by lazy {
        val paramsSetting = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT
        )
        paramsSetting.setMargins(0, 0, 0, 0)
        return@lazy paramsSetting
    }

    private val viewCalling: ViewCalling by lazy {
        ViewCalling(context = this)
    }

    private val viewSpeaking: ViewSpeaking by lazy {
        ViewSpeaking(context = this)
    }

    private val viewVideoCalling: ViewVideoCalling by lazy {
        ViewVideoCalling(context = this)
    }

    private val viewVideoSpeaking: ViewVideoSpeaking by lazy {
        ViewVideoSpeaking(context = this)
    }

    private val viewCallEnd: ViewCallEnd by lazy {
        ViewCallEnd(context = this)
    }

    private var type: String? = null
    private var name: String? = null
    private var vid: String? = null
    private var direction: Int = SetupFakeCallViewModel.TYPE_INCOMING
    private var uri: Uri? = null
    override fun setupData() {
        type = intent.getStringExtra("type")
        val image = intent.getStringExtra("image")
        if (image != null) uri = Uri.parse(image)
        name = intent.getStringExtra("name")
        vid = intent.getStringExtra("vid")
        direction = intent.getIntExtra("direction", SetupFakeCallViewModel.TYPE_INCOMING)
        ViewCallEnd.time = "00:00"
        checkreject = false
        BasePrefers.getPrefsInstance().scheduled = 0
    }

    override fun setupUI() {
        AppOpenManager.getInstance().enableAppResume()
        // Play alarm
        alarmTone = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
        ringtoneAlarm = RingtoneManager.getRingtone(this, alarmTone)
        ringtoneAlarm?.play()

        if (direction == SetupFakeCallViewModel.TYPE_INCOMING) {
            if (type == "video") {
                setupCall(CallState.VIDEO_CALLING)
            } else {
                setupCall(CallState.CALLING)
            }
        } else {
            if (type == "video") {
                setupCall(CallState.VIDEO_SPEAKING)
            } else {
                setupCall(CallState.SPEAKING)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        ringtoneAlarm?.stop()
        player?.stop()
        player?.release()
    }

    override fun onResume() {
        super.onResume()
        if (BasePrefers.getPrefsInstance().openResume) {
            AppOpenManager.getInstance().enableAppResumeWithActivity(FakeCallActivity::class.java)
        }

        (viewCall as? ViewCallEnd)?.startPopup()
    }

    private fun setupCall(state: CallState) {
        viewCall = filterCallState(state)

        viewCall?.apply {
            layoutParams = params
            addViewToContent(this)
            if (this is ViewVideoSpeaking) {
                if (vid == null) this.initializePlayer("https://mobiles-videos.ap-south-1.linodeobjects.com/Merry%20X_mas.mp4")
                else this.initializePlayer(vid!!)
                this@FakeCallActivity.player = this.player
                this.direction = this@FakeCallActivity.direction
                this.ringtoneAlarm = this@FakeCallActivity.ringtoneAlarm
                this.start()

            }

            if (this is ViewSpeaking) {
                if (vid != null) this.initializePlayer(vid!!)
                else (this@FakeCallActivity.ringtoneAlarm?.stop())
                this.ringtoneAlarm = this@FakeCallActivity.ringtoneAlarm
                this.direction = this@FakeCallActivity.direction
                this@FakeCallActivity.player = this.player
                this.start()

            }


            if (uri != null) this.loadImage(uri!!)
        }
        viewCall?.onAccept = {
            if (viewCall is ViewCalling) setupCall(CallState.SPEAKING)
            else setupCall(CallState.VIDEO_SPEAKING)
        }
        viewCall?.onReject = {
            (viewCall as? ViewSpeaking)?.stop()
            (viewCall as? ViewVideoSpeaking)?.stop()
            ringtoneAlarm?.stop()
            setupCall(CallState.END)
        }
    }

    private fun filterCallState(state: CallState): BaseCallView {
        return when (state) {
            CallState.CALLING -> viewCalling
            CallState.SPEAKING -> viewSpeaking
            CallState.VIDEO_CALLING -> viewVideoCalling
            CallState.VIDEO_SPEAKING -> viewVideoSpeaking
            CallState.END -> viewCallEnd
        }
    }

    private fun addViewToContent(layout: LinearLayout) {
        binding.content.removeAllViews()
        val parent = layout.parent as ViewGroup?
        parent?.removeAllViews()
        binding.content.addView(layout)
    }

    override fun onPause() {
        super.onPause()
        (viewCall as? ViewCallEnd)?.timer?.cancel()
    }
}
