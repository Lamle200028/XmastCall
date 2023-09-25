package com.qrcode.ai.app.ui.fakecall.view

import android.content.Context
import android.net.Uri
import android.util.AttributeSet
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.google.android.exoplayer2.ExoPlayer
import com.qrcode.ai.app.R
import com.qrcode.ai.app.platform.BaseCustomViewLinearLayout

abstract class BaseCallView : BaseCustomViewLinearLayout {
    constructor(context: Context?) : this(context, null)
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ){
        init()
    }

    var onAccept: (() -> Unit)? = null
    var onReject: (() -> Unit)? = null

    private fun init(){
        findViewById<ImageView>(R.id.btn_accept)?.setOnClickListener {
            onAccept?.invoke()
        }
        findViewById<ImageView>(R.id.btn_reject)?.setOnClickListener {
            if(findViewById<TextView>(R.id.profile_text_speaking) != null){
                ViewCallEnd.time = findViewById<TextView>(R.id.profile_text_speaking).text.toString()
            }
            onReject?.invoke()
        }
    }

    fun loadImage(uri: Uri){
        Glide.with(context).load(uri).into(findViewById(R.id.background))
        Glide.with(context).load(uri).into(findViewById(R.id.profile_pic_1))
    }
}
