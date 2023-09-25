package com.qrcode.ai.app.platform

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout

abstract class BaseCustomViewLinearLayout: LinearLayout {
    constructor(context: Context?) : this(context, null)
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        setLayout()
    }

    protected abstract val layoutId: Int
    protected lateinit var layout: View

    private fun setLayout() {
        val inflater = LayoutInflater.from(context)
        layout = inflater.inflate(layoutId, this, true)
    }
}
