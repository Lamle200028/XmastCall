package com.qrcode.ai.app.ui.main.ui.writeletter

import android.view.View
import androidx.navigation.fragment.findNavController
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import com.qrcode.ai.app.R
import com.qrcode.ai.app.databinding.LayoutSusscessBinding
import com.qrcode.ai.app.platform.BaseFullDialogFragment

class SuccessSendFragment : BaseFullDialogFragment<LayoutSusscessBinding>() {
    override val layoutId = R.layout.layout_susscess
    override fun setupUI() {
        super.setupUI()
        isCancelable = false
        setupEvent()
        binding.apply {
            imgSuscess.post {
                YoYo.with(Techniques.ZoomInDown).duration(2700).onEnd {
                    if (this@SuccessSendFragment.isResumed) homeBackSend?.visibility = View.VISIBLE
                }.playOn(imgSuscess)
            }
        }
    }

    private fun setupEvent() {
        binding.apply {
            homeBackSend?.setOnClickListener() {
                dismiss()
                findNavController().popBackStack(R.id.homeFragment, false)
                WriteLetterFragment.page = 0
            }
        }

    }
}