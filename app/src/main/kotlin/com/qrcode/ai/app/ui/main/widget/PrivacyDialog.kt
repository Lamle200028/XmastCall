package com.qrcode.ai.app.ui.main.widget

import android.content.DialogInterface
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.ads.control.admob.AppOpenManager
import com.qrcode.ai.app.databinding.LayoutDialogPrivacyBinding
import com.qrcode.ai.app.utils.Constants
import kotlin.math.min

class PrivacyDialog : DialogFragment() {
    private var binding: LayoutDialogPrivacyBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = LayoutDialogPrivacyBinding.inflate(inflater, container, false)
        isCancelable = false
        setupEvent()
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.post {
            val screenWidth = Resources.getSystem().displayMetrics.widthPixels
            val screenHeight = Resources.getSystem().displayMetrics.heightPixels
            val dialogWidth = min(screenWidth, screenHeight) * 0.95f
            dialog?.window?.setLayout(dialogWidth.toInt(), ViewGroup.LayoutParams.WRAP_CONTENT)
            dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }

        binding?.let {
            it.webview.webViewClient = (object : WebViewClient() {
                override fun onPageFinished(view: WebView, url: String) {
                    it.loading.visibility = View.GONE
                }

                override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                    super.onPageStarted(view, url, favicon)
                    it.loading.visibility = View.VISIBLE
                }
            })

            it.webview.loadUrl(Constants.PRIVACY_POLICY_LINK)
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
        binding?.closeBtn?.setOnClickListener {
            dismissAllowingStateLoss()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        AppOpenManager.getInstance().enableAppResume()
    }

    override fun onCancel(dialog: DialogInterface) {
        super.onCancel(dialog)
    }
}