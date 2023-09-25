package com.qrcode.ai.base.platform

import android.os.SystemClock
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppManager @Inject constructor() {

    companion object {

        private const val THRESHOLD_FINISH_TIME = 2000
    }

    private var backPressedTime = 0L

    val isBackPressFinish: Boolean
        get() {
            // preventing finish, using threshold of 2000 ms
            if (backPressedTime + THRESHOLD_FINISH_TIME > SystemClock.elapsedRealtime()) {
                return true
            }

            backPressedTime = SystemClock.elapsedRealtime()
            return false
        }

}
