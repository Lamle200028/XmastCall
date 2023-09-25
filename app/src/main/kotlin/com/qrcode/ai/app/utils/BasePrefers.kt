package com.qrcode.ai.app.utils

import android.content.Context
import androidx.core.content.edit
import androidx.preference.PreferenceManager
import java.util.Calendar

class BasePrefers(context: Context) {

    private val prefsNewUser = "prefsNewUser"
    private val prefsLocale = "prefsLocale"
    private val prefsOnBoard = "prefsOnBoard"
    private val prefsTimer = "prefsTimer"

    private val prefsBanner = "prefsBanner"
    private val prefsInterSplash = "prefsInterSplash"
    private val prefsInterSetup = "prefsInterSetup"
    private val prefsInterVideoCall = "prefsInterVideoCall"
    private val prefsInterChatVideoCall = "prefsInterChatVideoCall"
    private val prefsNativeLanguage = "prefsNativeLanguage"
    private val prefsNativeHome = "prefsNativeHome"
    private val prefsNativeSetupCall = "prefsNativeSetupCall"
    private val prefsNativeChooseSanta = "prefsNativeChooseSanta"
    private val prefsNativeCountdown = "prefsNativeCountdown"
    private val prefsNativeOnboard = "prefsNativeOnboard"
    private val prefsInterOnboard = "prefsInterOnboard"
    private val prefsOpenResume = "prefsOpenResume"
    private val ContentPosition = "ContentPosition"

    private val mPrefs = PreferenceManager.getDefaultSharedPreferences(context)

    var newUser
        get() = mPrefs.getBoolean(prefsNewUser, true)
        set(value) = mPrefs.edit { putBoolean(prefsNewUser, value) }

    var openboard
        get() = mPrefs.getBoolean(prefsOnBoard, false)
        set(value) = mPrefs.edit { putBoolean(prefsOnBoard, value) }

    var scheduled
        get() = mPrefs.getLong(prefsTimer, Calendar.getInstance().timeInMillis)
        set(value) = mPrefs.edit { putLong(prefsTimer, value) }

    var locale
        get() = mPrefs.getString(prefsLocale, "en")
        set(value) = mPrefs.edit { putString(prefsLocale, value) }

    var banner
        get() = mPrefs.getBoolean(prefsBanner, true)
        set(value) = mPrefs.edit { putBoolean(prefsBanner, value) }

    var interSplash
        get() = mPrefs.getBoolean(prefsInterSplash, true)
        set(value) = mPrefs.edit { putBoolean(prefsInterSplash, value) }
    var interSetup
        get() = mPrefs.getBoolean(prefsInterSetup, true)
        set(value) = mPrefs.edit { putBoolean(prefsInterSetup, value) }
    var interVideoCall
        get() = mPrefs.getBoolean(prefsInterVideoCall, true)
        set(value) = mPrefs.edit { putBoolean(prefsInterVideoCall, value) }
    var interChatVideoCall
        get() = mPrefs.getBoolean(prefsInterChatVideoCall, true)
        set(value) = mPrefs.edit { putBoolean(prefsInterChatVideoCall, value) }

    var nativeLanguage
        get() = mPrefs.getBoolean(prefsNativeLanguage, true)
        set(value) = mPrefs.edit { putBoolean(prefsNativeLanguage, value) }
    var nativeHome
        get() = mPrefs.getBoolean(prefsNativeHome, true)
        set(value) = mPrefs.edit { putBoolean(prefsNativeHome, value) }
    var nativeSetupCall
        get() = mPrefs.getBoolean(prefsNativeSetupCall, true)
        set(value) = mPrefs.edit { putBoolean(prefsNativeSetupCall, value) }
    var nativeChooseSanta
        get() = mPrefs.getBoolean(prefsNativeChooseSanta, true)
        set(value) = mPrefs.edit { putBoolean(prefsNativeChooseSanta, value) }
    var nativeCountdown
        get() = mPrefs.getBoolean(prefsNativeCountdown, true)
        set(value) = mPrefs.edit { putBoolean(prefsNativeCountdown, value) }

    var nativeOnboard
        get() = mPrefs.getBoolean(prefsNativeOnboard, true)
        set(value) = mPrefs.edit { putBoolean(prefsNativeOnboard, value) }

    var interOnboard
        get() = mPrefs.getBoolean(prefsInterOnboard, true)
        set(value) = mPrefs.edit { putBoolean(prefsInterOnboard, value) }

    var openResume
        get() = mPrefs.getBoolean(prefsOpenResume, true)
        set(value) = mPrefs.edit { putBoolean(prefsOpenResume, value) }
    var contentPosition
        get() = mPrefs.getInt(ContentPosition,-1)
        set(value) = mPrefs.edit().putInt(ContentPosition, value).apply()


    companion object {
        @Volatile
        private var INSTANCE: BasePrefers? = null

        fun initPrefs(context: Context): BasePrefers {
            return INSTANCE ?: synchronized(this) {
                val instance = BasePrefers(context)
                INSTANCE = instance
                // return instance
                instance
            }
        }

        fun getPrefsInstance(): BasePrefers {
            return INSTANCE ?: error("GoPreferences not initialized!")
        }
    }
}
