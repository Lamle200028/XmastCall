package com.qrcode.ai.app.ui.main.ui.writeletter

import AdapterDesign
import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Handler
import android.view.View
import android.view.ViewAnimationUtils
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.ScaleAnimation
import android.view.animation.TranslateAnimation
import android.widget.FrameLayout
import androidx.core.view.marginLeft
import androidx.core.view.marginTop
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.qrcode.ai.app.R
import com.qrcode.ai.app.databinding.LayoutBgletterBinding
import com.qrcode.ai.app.platform.BaseFragment
import kotlinx.android.synthetic.main.layout_bgletter.*
import kotlin.properties.Delegates

class BackGroundLetterFragment : BaseFragment<LayoutBgletterBinding>() {

    var listItem = ArrayList<Int>()
    lateinit var adapter : AdapterDesign
    private var positionDesign : Int = -1
    private var isViewExpanded  = true
    private var isCreate = false
    override val layoutId: Int
        get() = R.layout.layout_bgletter

    override fun setupData() {
        Handler().postDelayed({
            binding.apply {
                moveAndScaleView(ctrLetter, ctrLetterLitter)
            }
        }, 2000)
        super.setupData()
        val args = arguments
        val nameWrite = args?.getString("name")
        val ageWrite = args?.getString("age")
        val wishWrite = args?.getString("wish")
        listItem.clear()
        listItem.add(R.drawable.design1)
        listItem.add(R.drawable.design2)
        listItem.add(R.drawable.design3)
        listItem.add(R.drawable.design4)
        listItem.add(R.drawable.design5)
        listItem.add(R.drawable.design6)
        listItem.add(R.drawable.design7)
        listItem.add(R.drawable.design8)
        listItem.add(R.drawable.design9)
        listItem.add(R.drawable.design10)

        adapter= AdapterDesign(listItem)
        if (!isCreate) {
            binding.apply {
                name.text = "My name is: $nameWrite"
                age.text = "and I am $ageWrite years old."
                wish.text = "For Christmas this year, I want to make a wish: $wishWrite"
                signName.text = "$nameWrite"
                bgLetter.adapter = adapter
                bgLetter.layoutManager = LinearLayoutManager(requireContext())
                adapter.notifyDataSetChanged()
                adapter.setOnClickItem {
                    positionDesign = it
                    when (it) {
                        R.drawable.design1 -> ctrLetter.setBackgroundResource(R.drawable.bg_letter)
                        R.drawable.design2 -> ctrLetter.setBackgroundResource(R.drawable.formdesign2)
                        R.drawable.design3 -> ctrLetter.setBackgroundResource(R.drawable.formdesign3)
                        R.drawable.design4 -> ctrLetter.setBackgroundResource(R.drawable.formdesign4)
                        R.drawable.design5 -> ctrLetter.setBackgroundResource(R.drawable.formdesign5)
                        R.drawable.design6 -> ctrLetter.setBackgroundResource(R.drawable.formdesign7)
                        R.drawable.design7 -> ctrLetter.setBackgroundResource(R.drawable.formdesign8)
                        R.drawable.design8 -> ctrLetter.setBackgroundResource(R.drawable.formdesign9)
                        R.drawable.design9 -> ctrLetter.setBackgroundResource(R.drawable.formdesign6)
                        R.drawable.design10 -> ctrLetter.setBackgroundResource(R.drawable.formdesign10)
                    }
                    adapter.notifyDataSetChanged()
                }
                isCreate = true
            }
        }
    }

    override fun setupListener() {
        super.setupListener()
        binding.apply {
            next.setOnClickListener(){
                findNavController().navigate(R.id.headerLetter)
            }
            back.setOnClickListener(){
                WriteLetterFragment.page --
                findNavController().popBackStack()
            }
            backWriteLetter.setOnClickListener(){
                findNavController().popBackStack(R.id.homeFragment, false)
                WriteLetterFragment.page = 0
            }
            textTitle.setOnClickListener{

            }
        }
    }
    private fun moveAndScaleView(sourceView: View, targetView: View) {

        //scale
        val sourceWidth = sourceView.width
        val sourceHeight = sourceView.height

        val targetWidth = targetView.width
        val targetHeight = targetView.height

        val scaleX = targetWidth.toFloat() / sourceWidth
        val scaleY = targetHeight.toFloat() / sourceHeight

        val moveX = (targetWidth - sourceWidth).toFloat()/2 + sourceView.marginLeft.toFloat()
        val moveY = (sourceHeight - targetHeight).toFloat()/2 - sourceView.marginTop.toFloat()

        val animDuration = 500L

        val moveAnimatorX = ObjectAnimator.ofFloat(sourceView, View.X, moveX)
        moveAnimatorX.duration = animDuration

        val moveAnimatorY = ObjectAnimator.ofFloat(sourceView, View.Y, moveY)
        moveAnimatorY.duration = animDuration

        val scaleXAnimator = ObjectAnimator.ofFloat(sourceView, View.SCALE_X, scaleX)
        scaleXAnimator.duration = animDuration

        val scaleYAnimator = ObjectAnimator.ofFloat(sourceView, View.SCALE_Y, scaleY)
        scaleYAnimator.duration = animDuration

        val animatorSet = AnimatorSet()
        animatorSet.playTogether(moveAnimatorX, moveAnimatorY, scaleXAnimator, scaleYAnimator)
        animatorSet.start()
    }
}
