package com.qrcode.ai.app.ui.main.widget

import android.app.ActionBar.LayoutParams
import android.content.ActivityNotFoundException
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.graphics.Insets
import android.graphics.Point
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.ads.control.admob.AppOpenManager
import com.qrcode.ai.app.R
import com.qrcode.ai.app.databinding.DialogRateAppBinding
import com.qrcode.ai.app.ui.main.ui.home.HomeFragment
import com.qrcode.ai.app.utils.Constants

class RatingDialog(private val onAccept: (Boolean) -> Unit) : DialogFragment() {
    private lateinit var binding: DialogRateAppBinding
    private var currentStars = 5

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.dialog_rate_app, container, false
        )
        AppOpenManager.getInstance().disableAppResume()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {

            btnRateUs.setOnClickListener {
                onAccept(currentStars > 3)
                openAppInPlayStore()
            }

            closePopUp.setOnClickListener {
                dismiss()
            }

            super.onResume()
            dialog?.window?.apply {
                setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            }

            binding.star1.setOnClickListener { setStars(1) }
            binding.star2.setOnClickListener { setStars(2) }
            binding.star3.setOnClickListener { setStars(3) }
            binding.star4.setOnClickListener { setStars(4) }
            binding.star5.setOnClickListener { setStars(5) }
        }
    }

    override fun show(manager: FragmentManager, tag: String?) {
        manager.beginTransaction().add(this, tag).commitAllowingStateLoss()
    }


    private fun setStars(numberOfStars: Int) {
        currentStars = numberOfStars
        binding.apply {
            when (numberOfStars) {
                1 -> {
                    iconFaceRate.setImageResource(R.drawable.downcast_face_with_sweat)
                    star1.setImageResource(R.drawable.ic_star_fill)
                    star2.setImageResource(R.drawable.ic_star_empty)
                    star3.setImageResource(R.drawable.ic_star_empty)
                    star4.setImageResource(R.drawable.ic_star_empty)
                    star5.setImageResource(R.drawable.ic_star_empty)
                }

                2 -> {
                    iconFaceRate.setImageResource(R.drawable.weary_face)
                    star1.setImageResource(R.drawable.ic_star_fill)
                    star2.setImageResource(R.drawable.ic_star_fill)
                    star3.setImageResource(R.drawable.ic_star_empty)
                    star4.setImageResource(R.drawable.ic_star_empty)
                    star5.setImageResource(R.drawable.ic_star_empty)
                }

                3 -> {
                    iconFaceRate.setImageResource(R.drawable.astonished_face)
                    star1.setImageResource(R.drawable.ic_star_fill)
                    star2.setImageResource(R.drawable.ic_star_fill)
                    star3.setImageResource(R.drawable.ic_star_fill)
                    star4.setImageResource(R.drawable.ic_star_empty)
                    star5.setImageResource(R.drawable.ic_star_empty)
                }

                4 -> {
                    iconFaceRate.setImageResource(R.drawable.winking_face)
                    star1.setImageResource(R.drawable.ic_star_fill)
                    star2.setImageResource(R.drawable.ic_star_fill)
                    star3.setImageResource(R.drawable.ic_star_fill)
                    star4.setImageResource(R.drawable.ic_star_fill)
                    star5.setImageResource(R.drawable.ic_star_empty)
                }

                5 -> {
                    iconFaceRate.setImageResource(R.drawable.smiling_face_with_heart_eyes)
                    star1.setImageResource(R.drawable.ic_star_fill)
                    star2.setImageResource(R.drawable.ic_star_fill)
                    star3.setImageResource(R.drawable.ic_star_fill)
                    star4.setImageResource(R.drawable.ic_star_fill)
                    star5.setImageResource(R.drawable.ic_star_fill)
                }
            }
        }
    }

    override fun onResume() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val windowMetrics = requireActivity().windowManager.currentWindowMetrics
            val insets: Insets =
                windowMetrics.windowInsets.getInsetsIgnoringVisibility(WindowInsets.Type.systemBars())
            val width = windowMetrics.bounds.width() - insets.left - insets.right
            val window = dialog!!.window
            if (window != null) {
                window.setLayout((width * 0.75).toInt(), LayoutParams.WRAP_CONTENT)
                window.setGravity(Gravity.CENTER)
            }
            super.onResume()
        } else {
            val window = dialog!!.window
            val size = Point()
            val display = window!!.windowManager.defaultDisplay
            display.getSize(size)
            window.setLayout(
                (size.x * 0.75).toInt(), ViewGroup.LayoutParams.WRAP_CONTENT
            )
            window.setGravity(Gravity.CENTER)
            super.onResume()
        }
    }


    private fun openAppInPlayStore() {
        val uri = Uri.parse(Constants.store_uri)
        val goToMarketIntent = Intent(Intent.ACTION_VIEW, uri)

        var flags = Intent.FLAG_ACTIVITY_NO_HISTORY or Intent.FLAG_ACTIVITY_MULTIPLE_TASK
        flags = flags or Intent.FLAG_ACTIVITY_NEW_DOCUMENT
        goToMarketIntent.addFlags(flags)

        try {
            startActivityForResult(goToMarketIntent, Constants.request_code_share)
        } catch (e: ActivityNotFoundException) {
            val intent = Intent(
                Intent.ACTION_VIEW, Uri.parse(Constants.LINK_STORE)
            )
            startActivityForResult(intent, Constants.request_code_share)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        dismiss()
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        AppOpenManager.getInstance().enableAppResume()
        HomeFragment.isShowDialog = false
    }
}
