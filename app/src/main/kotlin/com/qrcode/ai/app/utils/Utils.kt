package com.qrcode.ai.app.utils

import android.animation.ValueAnimator
import android.view.View
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.qrcode.ai.app.utils.Constants.Inter_Splash
import com.qrcode.ai.app.utils.Constants.Native_Language
import com.qrcode.ai.app.utils.Constants.Native_Onboard


fun isShowNativeLanguage(): Boolean {
    val config = FirebaseRemoteConfig.getInstance()
    config.fetch(0)
    return config.getBoolean(Native_Language)
}

fun isShowInterSplash(onFinishFetchRemote: (Boolean) -> Unit) {
    val config = FirebaseRemoteConfig.getInstance()
    config.fetch(0).addOnCompleteListener {
        onFinishFetchRemote.invoke(config.getBoolean(Inter_Splash))
    }
}

fun isShowNativeOnboard(): Boolean {
    val config = FirebaseRemoteConfig.getInstance()
    config.fetch(0)
    return config.getBoolean(Native_Onboard)
}

fun isShowBanner(): Boolean {
    val config = FirebaseRemoteConfig.getInstance()
    config.fetch(0)
    return config.getBoolean(Constants.Banner)
}

fun isShowAppResume(): Boolean {
    val config = FirebaseRemoteConfig.getInstance()
    config.fetch(0)
    return config.getBoolean(Constants.Ads_resume)
}

fun isShowInterOnboard(): Boolean {
    val config = FirebaseRemoteConfig.getInstance()
    config.fetch(0)
    return config.getBoolean(Constants.Inter_Onboard)
}

fun View.playAnimationPulse(){
    this.post {
        YoYo.with(Techniques.Pulse).duration(700).repeat(YoYo.INFINITE)
            .repeatMode(ValueAnimator.RESTART).playOn(this)
    }
}

fun View.playAnimationOutUp(onStart: (() -> Unit?)? = null, onEnd: (() -> Unit?)? = null) {
    this.post {
        YoYo.with(Techniques.ZoomOutUp)
            .duration(2700)
            .onStart {
                onStart?.invoke()
            }
            .onEnd {
                onEnd?.invoke()
            }
            .playOn(this)
    }
}

