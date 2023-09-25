package com.qrcode.ai.app.ui.main.ui.voicemail

import android.content.res.Resources
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.qrcode.ai.app.databinding.LayoutPopupWarningvolumeBinding
import kotlin.math.min


//private val onDismissed: ((isAllPermissionAccepted: Boolean) -> Unit)? = null,
class PopUpWarningVolum(private val onDismissed: ((isAllPermissionAccepted: Boolean) -> Unit)? = null) : DialogFragment() {
    private var binding: LayoutPopupWarningvolumeBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = LayoutPopupWarningvolumeBinding.inflate(inflater, container, false)
        isCancelable = false
        setupEvent()
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.post {
            val screenWidth = Resources.getSystem().displayMetrics.widthPixels
            val screenHeight = Resources.getSystem().displayMetrics.heightPixels
            val dialogWidth = min(screenWidth, screenHeight) * 1f
            dialog?.window?.setLayout(dialogWidth.toInt(), ViewGroup.LayoutParams.MATCH_PARENT)
            dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
    }

    override fun show(manager: FragmentManager, tag: String?) {
        manager.beginTransaction().add(this, tag).commitAllowingStateLoss()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    private fun setupEvent() {
        binding?.apply {
            gotIt.setOnClickListener{
                dismiss()
                onDismissed?.invoke(true)
            }
        }

    }
}