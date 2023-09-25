package com.qrcode.ai.app.utils

import android.content.Context
import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics

object TrackingManager {
    private var firebaseAnalytics: FirebaseAnalytics? = null

    fun init(context: Context) {
        firebaseAnalytics = FirebaseAnalytics.getInstance(context)
    }

    fun tracking(trackingName: String, bundle: Bundle) {
        firebaseAnalytics?.logEvent(trackingName, bundle)
    }

    fun trackingString(trackingName: String, trackingParam: String, value: String) {
        val bundle = Bundle()
        bundle.putString(trackingParam, value)
        firebaseAnalytics?.logEvent(trackingName, bundle)
    }
}