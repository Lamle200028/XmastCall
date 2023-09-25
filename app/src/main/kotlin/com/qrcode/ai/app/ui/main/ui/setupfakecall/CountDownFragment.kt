package com.qrcode.ai.app.ui.main.ui.setupfakecall

import android.os.Handler
import android.view.View
import androidx.fragment.app.viewModels
import com.ads.control.admob.AppOpenManager
import com.qrcode.ai.app.R
import com.qrcode.ai.app.databinding.FragmentCountdownBinding
import com.qrcode.ai.app.platform.BaseFragment
import com.qrcode.ai.app.utils.BasePrefers
import com.qrcode.ai.app.utils.Constants
import java.util.Calendar

class CountDownFragment(private val onCountdownFinished: () -> Unit) : BaseFragment<FragmentCountdownBinding>() {
    override val layoutId: Int
        get() = R.layout.fragment_countdown
    var time: Long = 0
    private val viewModel: SetupFakeCallViewModel by viewModels()
    private lateinit var timerHandler: Handler
    private lateinit var timerRunnable: Runnable

    override fun setupUI() {
        binding.apply {
            val now = Calendar.getInstance().timeInMillis
            val date = BasePrefers.getPrefsInstance().scheduled
            time = (date - now).div(1000)
            if (time < 0) time = 0
            timerHandler = Handler()
            timerRunnable = object : Runnable {
                override fun run() {
                    if (time >= 0) {
                        val minutes = time / 60
                        val seconds = time % 60
                        countdown.text = String.format("%02d:%02d", minutes, seconds)
                        time--
                        timerHandler.postDelayed(this, 1000)
                    } else {
                        onCountdownFinished.invoke()
                    }
                }
            }
            timerHandler.postDelayed(timerRunnable, 0)
        }
    }

    override fun setupListener() {
        binding.apply {
            close.setOnClickListener {
                timerHandler.removeCallbacks(timerRunnable)
                viewModel.cancelWorker(requireActivity(), Constants.WORKER_TAG)
                BasePrefers.getPrefsInstance().scheduled = 0
                binding.countdown.text = "00:00"
                binding.ctnCountDown.visibility = View.GONE
                onCountdownFinished.invoke()
            }
        }
    }
}
