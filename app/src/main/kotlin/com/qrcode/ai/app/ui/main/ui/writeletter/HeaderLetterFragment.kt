package com.qrcode.ai.app.ui.main.ui.writeletter

import AdapterHeader
import android.view.View
import android.widget.ImageView
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.qrcode.ai.app.R
import com.qrcode.ai.app.databinding.LayoutHeaderletterBinding
import com.qrcode.ai.app.platform.BaseFragment
import com.qrcode.ai.app.utils.playAnimationOutUp

class HeaderLetterFragment : BaseFragment<LayoutHeaderletterBinding>(){

    var listheader  = ArrayList<Int>()
    lateinit var adapter : AdapterHeader
    override val layoutId: Int
        get() = R.layout.layout_headerletter

    override fun setupData() {
        super.setupData()
        listheader.add(R.drawable.header1)
        listheader.add(R.drawable.header2)
        listheader.add(R.drawable.header3)
        listheader.add(R.drawable.header4)
        listheader.add(R.drawable.header5)
        listheader.add(R.drawable.header6)
        listheader.add(R.drawable.header7)
        listheader.add(R.drawable.header8)
        adapter = AdapterHeader(listheader)

        binding.apply {
            rvHeader.adapter = adapter
            rvHeader.layoutManager =
                GridLayoutManager(requireContext(), 4, GridLayoutManager.VERTICAL, false)
            adapter.notifyDataSetChanged()
            adapter.setOnClickItemHeader { id ->
                viewHearderLetter.setImageResource(id)
                adapter.notifyDataSetChanged()
            }
        }
    }

    override fun setupListener() {
        super.setupListener()
        binding.apply {
            next.setOnClickListener(){
                rvHeader.visibility = View.INVISIBLE
                textFavorite.visibility = View.INVISIBLE
                ctrTitle.visibility = View.INVISIBLE
                btn.visibility = View.INVISIBLE
                headerView.playAnimationOutUp(
                    onEnd = {
                        if (this@HeaderLetterFragment.isResumed) SuccessSendFragment().show(parentFragmentManager, null)
                    }
                )
            }
            back.setOnClickListener(){
                findNavController().popBackStack()
            }
            backWriteLetter.setOnClickListener(){
                findNavController().popBackStack(R.id.homeFragment, false)
                WriteLetterFragment.page = 0
            }
        }
    }
}