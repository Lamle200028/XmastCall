package com.qrcode.ai.app.ui.main.ui.writeletter

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.navigation.fragment.findNavController
import com.qrcode.ai.app.R
import com.qrcode.ai.app.databinding.LayoutWriteletterBinding
import com.qrcode.ai.app.platform.BaseFragment
import com.qrcode.ai.app.utils.playAnimationPulse

class WriteLetterFragment : BaseFragment<LayoutWriteletterBinding>() {

    val bundle = Bundle()
    override val layoutId: Int
        get() = R.layout.layout_writeletter

    companion object {
        var page: Int = 0
    }

    override fun setupData() {
        super.setupData()
    }

    override fun setupUI() {
        super.setupUI()

    }

    override fun onResume() {
        super.onResume()
        binding.tapWrite.playAnimationPulse()
    }

    override fun setupListener() {
        super.setupListener()
        binding.apply {
            ctrWrite.setOnClickListener {
                ctrWrite.setBackgroundResource(R.drawable.bg_1writeletter)
                tapWrite.visibility = View.GONE
                bgCtrLetterWrite.visibility = View.VISIBLE
                iconBtn.visibility = View.VISIBLE
                iconViewPager.visibility = View.VISIBLE
            }
            next.setOnClickListener {
                if (next.alpha == 1f) {
                    page++
                    setViewPage(page)
                }
            }
            back.setOnClickListener {
                page--
                setViewPage(page)
                next.alpha = 1f
            }
            backWriteLetter.setOnClickListener {
                findNavController().popBackStack(R.id.homeFragment, false)
                page = 0
            }
            sizeText(userWrite, "30", sizeTextWriteName)
            sizeText(ageWrite, "2", sizeTextWriteAge)
            sizeText(wishWrite, "80", sizeTextWriteWish)
        }
    }

    private fun setViewPage(mPage: Int) {
        binding.apply {
            textTitleWrite.setLineSpacing(0f, 0.5f)
            when (mPage) {
                0 -> {
                    ctrSantaWrite.visibility = View.VISIBLE
                    back.visibility = View.GONE
                    page1.setImageResource(R.drawable.ic_viewselectage)
                    page2.setImageResource(R.drawable.ic_unselectpage)
                    page3.setImageResource(R.drawable.ic_unselectpage)
                    page4.setImageResource(R.drawable.ic_unselectpage)
                    page5.setImageResource(R.drawable.ic_unselectpage)
                    textTitleWrite.text = getString(R.string.write_your_name)
                    userWrite.visibility = View.VISIBLE
                    ageWrite.visibility = View.GONE
                    sizeTextWriteName.visibility = View.VISIBLE
                    sizeTextWriteAge.visibility = View.GONE
                    sizeTextWriteWish.visibility = View.GONE
                    if (userWrite.text.isNullOrEmpty()){
                        binding.next.alpha = 0.8f
                    }
                }
                1 -> {
                    ctrSantaWrite.visibility = View.VISIBLE
                    back.visibility = View.VISIBLE
                    page1.setImageResource(R.drawable.ic_susselectpage)
                    page2.setImageResource(R.drawable.ic_viewselectage)
                    page3.setImageResource(R.drawable.ic_unselectpage)
                    page4.setImageResource(R.drawable.ic_unselectpage)
                    page5.setImageResource(R.drawable.ic_unselectpage)
                    textTitleWrite.text = getString(R.string.age_user)
                    ageWrite.visibility = View.VISIBLE
                    wishWrite.visibility = View.GONE
                    userWrite.visibility = View.GONE
                    sizeTextWriteName.visibility = View.GONE
                    sizeTextWriteAge.visibility = View.VISIBLE
                    sizeTextWriteWish.visibility = View.GONE
                    if (ageWrite.text.isNullOrEmpty()){
                        binding.next.alpha = 0.8f
                    }
                }
                2 -> {
                    ctrSantaWrite.visibility = View.VISIBLE
                    back.visibility = View.VISIBLE
                    page1.setImageResource(R.drawable.ic_susselectpage)
                    page2.setImageResource(R.drawable.ic_susselectpage)
                    page3.setImageResource(R.drawable.ic_viewselectage)
                    page4.setImageResource(R.drawable.ic_unselectpage)
                    page5.setImageResource(R.drawable.ic_unselectpage)
                    textTitleWrite.text = getString(R.string.yourwish)
                    ageWrite.visibility = View.GONE
                    wishWrite.visibility = View.VISIBLE
                    sizeTextWriteName.visibility = View.GONE
                    sizeTextWriteAge.visibility = View.GONE
                    sizeTextWriteWish.visibility = View.VISIBLE
                    if (wishWrite.text.isNullOrEmpty()){
                        binding.next.alpha = 0.8f
                    }
                }
                3 -> {
                    bundle.apply {
                        putString("name", userWrite.text.toString())
                        putString("age", ageWrite.text.toString())
                        putString("wish", wishWrite.text.toString())
                    }
                    findNavController().navigate(R.id.backGroundLetter, bundle)
                }
            }
        }
    }

    private fun sizeText(edit: EditText, textMax: String, textSize: TextView) {
        edit.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                textSize.text = "${s?.length}/${textMax}"
                if (s?.isNullOrEmpty() != true) {
                    binding.next.alpha = 1f
                } else {
                    binding.next.alpha = 0.8f
                }
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })
    }
}