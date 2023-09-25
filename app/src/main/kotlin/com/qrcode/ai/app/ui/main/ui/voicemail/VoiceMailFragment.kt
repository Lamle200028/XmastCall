package com.qrcode.ai.app.ui.main.ui.voicemail

import android.content.Context
import android.media.AudioManager
import android.media.Ringtone
import android.media.ToneGenerator
import android.net.Uri
import android.os.Handler
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.widget.GridLayout
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.source.DefaultMediaSourceFactory
import com.qrcode.ai.app.R
import com.qrcode.ai.app.databinding.LayoutVoicemailBinding
import com.qrcode.ai.app.platform.BaseFragment
import kotlinx.coroutines.launch


class VoiceMailFragment : BaseFragment<LayoutVoicemailBinding>() {
    override val layoutId: Int
        get() = R.layout.layout_voicemail

    private var ringtoneAlarm: Ringtone? = null
    private var player: ExoPlayer? = null
    private var timerHandler: Handler? = null
    private var timerRunnable: Runnable? = null
    private var isMute = false
    private var checkBack = false
    private var reject = false
    private var click = false
    private var resetVoice = false
    private var isKeyPad = false
    var counter = 0
    private lateinit var toneGenerator: ToneGenerator

    override fun setupData() {
        super.setupData()
        PopUpWarningVolum(
            onDismissed = {
                if (it) {
                    binding.apply {
                        enableVoice.visibility = View.VISIBLE
                        initializePlayer("https://mobiles-videos.ap-south-1.linodeobjects.com/Merry%20X_mas.mp4")
                        start()
                    }
                }
            }
        ).show(parentFragmentManager, null)
    }

    fun animateView(mImageView: GridLayout) {
        mImageView.visibility = View.VISIBLE

        mImageView.scaleY = 0f
        mImageView.alpha = 0f

        val animation = mImageView.animate()
            .scaleY(1f)
            .alpha(1f)
            .setDuration(1000)
            .setInterpolator(AccelerateInterpolator())
        animation.start()
    }

    override fun setupListener() {
        super.setupListener()

        binding.apply {
            backVoicemail.setOnClickListener{
                findNavController().popBackStack()
                checkBack = true
            }
            rejectCallMail.setOnClickListener{
                reject = true
                bgVoiceCall.setBackgroundResource(R.drawable.callend)
                enableVoice.visibility = View.GONE
                callend.visibility = View.VISIBLE
                backVoicemail.visibility = View.VISIBLE
                text2VoicemailCallend.text = text2Voicemail.text
                stop()
                Handler().postDelayed({
                    if (!checkBack) {
                        findNavController().popBackStack()
                    }
                }, 5000)

            }
            btnKeypad.setOnClickListener {
                isKeyPad = true
                animateView(gifSelectNumber)
                gifSelect.visibility = View.GONE
                gifSelectNumber.visibility = View.VISIBLE
            }
            btnMute.setOnClickListener {
                if (!isMute) {
                    isMute = true
                    imgMute.setBackgroundResource(R.drawable.unmute)
                    textMute.text = getString(R.string.unmute)
                } else {
                    isMute = false
                    imgMute.setBackgroundResource(R.drawable.mute)
                }
            }
            speaker.setOnClickListener{
                val audio = context?.getSystemService(Context.AUDIO_SERVICE) as AudioManager
                audio.adjustStreamVolume(
                    AudioManager.STREAM_MUSIC,
                    AudioManager.ADJUST_SAME,
                    AudioManager.FLAG_SHOW_UI,
                )
            }
            setClick(root,R.id.btn_number0, ToneGenerator.TONE_DTMF_0)
            setClick(root,R.id.btn_number1, ToneGenerator.TONE_DTMF_1)
            setClick(root,R.id.btn_number2, ToneGenerator.TONE_DTMF_2)
            setClick(root,R.id.btn_number3, ToneGenerator.TONE_DTMF_3)
            setClick(root,R.id.btn_number4, ToneGenerator.TONE_DTMF_4)
            setClick(root,R.id.btn_number5, ToneGenerator.TONE_DTMF_5)
            setClick(root,R.id.btn_number6, ToneGenerator.TONE_DTMF_6)
            setClick(root,R.id.btn_number7, ToneGenerator.TONE_DTMF_7)
            setClick(root,R.id.btn_number8, ToneGenerator.TONE_DTMF_8)
            setClick(root,R.id.btn_number9, ToneGenerator.TONE_DTMF_9)
            setClick(root,R.id.btn_star, ToneGenerator.TONE_DTMF_A)
            setClick(root,R.id.btn_thang, ToneGenerator.TONE_DTMF_B)
        }
    }
    fun start() {
        if (player != null) player?.play()
        else {
            timerHandler = Handler()
            timerRunnable = object : Runnable {
                override fun run() {
                    val minutes = counter / 60
                    val seconds = counter % 60
                    binding.text2Voicemail.text =
                        String.format("%02d:%02d", minutes, seconds)
                    counter++
                    timerHandler?.postDelayed(this, 1000)
                    if (counter == 10) {
                        binding.apply {
                            if (!isKeyPad) {
                                animateView(gifSelectNumber)
                                gifSelect.visibility = View.GONE
                                gifSelectNumber.visibility = View.VISIBLE
                            }
                        }
                    }
                }
            }
            timerHandler?.postDelayed(timerRunnable as Runnable, 0)
        }
    }

    fun stop() {
        timerRunnable?.let { timerHandler?.removeCallbacks(it) }
        timerHandler?.removeCallbacksAndMessages(timerRunnable)
        ringtoneAlarm?.stop()
        player?.stop()
        player?.release()
    }

    private fun initializePlayer(url: String) {
        player = ExoPlayer.Builder(requireContext()).build()

        val mediaItem = MediaItem.fromUri(Uri.parse(url))
        val mediaSourceFactory = DefaultMediaSourceFactory(requireContext())
        val mediaSource = mediaSourceFactory.createMediaSource(mediaItem)

        player?.setMediaSource(mediaSource)
        player?.prepare()
        player?.addListener(object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                binding.apply {
                    text2Voicemail.visibility = View.VISIBLE
                    ringtoneAlarm?.stop()
                    timerHandler = Handler()
                    timerRunnable = object : Runnable {
                        override fun run() {
                            val minutes = counter / 60
                            val seconds = counter % 60
                            text2Voicemail.text =
                                String.format("%02d:%02d", minutes, seconds)
                            counter++
                            timerHandler?.postDelayed(this, 1000)
                            if (counter == 10) {
                                binding.apply {
                                    if (!isKeyPad) {
                                        animateView(gifSelectNumber)
                                        gifSelect.visibility = View.GONE
                                        gifSelectNumber.visibility = View.VISIBLE
                                    }
                                }
                            }
                        }
                    }
                    timerHandler?.postDelayed(timerRunnable as Runnable, 0)
                }
                if (reject || click) {
                    timerRunnable?.let { timerHandler?.removeCallbacks(it) }
                }
            }
        })
    }
    private fun setClick(view: View, id: Int, toneId : Int) {
        toneGenerator = ToneGenerator(AudioManager.STREAM_MUSIC, ToneGenerator.MAX_VOLUME)
        val targetView = view.findViewById<View>(id)
        lifecycleScope.launch{
            targetView.setOnClickListener{
                if (!resetVoice){
                    click = true
                    player?.stop()
                    stop()
                    initializePlayer("https://mobiles-videos.ap-south-1.linodeobjects.com/Merry%20X_mas.mp4")
                    player?.play()

                }
                toneGenerator.startTone(toneId, 150)
                resetVoice = true
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        ringtoneAlarm?.stop()
        player?.stop()
        player?.release()
        stop()
        toneGenerator.release()
    }
}