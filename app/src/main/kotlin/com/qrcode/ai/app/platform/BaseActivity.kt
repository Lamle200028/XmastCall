package com.qrcode.ai.app.platform

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.qrcode.ai.app.utils.BasePrefers
import com.qrcode.ai.app.utils.Constants
import com.qrcode.ai.app.utils.ContextUtils
import java.util.Locale

abstract class BaseActivity<T : ViewDataBinding> : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initBinding()
        setupData()
        setupUI()
        setupListener()
    }

    protected abstract val layoutId: Int
    protected lateinit var binding: T
    protected open fun initBinding() {
        binding = createBinding()
    }

    protected open fun setupUI() {}
    protected open fun setupData() {}
    protected open fun setupListener() {}

    private fun createBinding(): T {
        return DataBindingUtil.setContentView(this, layoutId)
    }

    override fun attachBaseContext(newBase: Context?) {
        newBase?.let { ctx ->
            val preferences = BasePrefers.initPrefs(ctx)
            val locale = preferences.locale ?: Constants.en

            locale.let {
                val locale1 = Locale.forLanguageTag(it)
                val localeUpdatedContext = ContextUtils.updateLocale(ctx, locale1)
                super.attachBaseContext(localeUpdatedContext)
            }
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            hideSystemUI()
        }
    }

    private fun hideSystemUI() {
        WindowCompat.setDecorFitsSystemWindows(window, true)
        WindowInsetsControllerCompat(window, binding.root).let { controller ->
            controller.hide(WindowInsetsCompat.Type.systemBars())
            controller.systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    }
}
