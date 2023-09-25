package com.qrcode.ai.app.ui.main.ui.setting.language

import androidx.lifecycle.ViewModel
import com.qrcode.ai.app.ui.splash.ui.first_language.Language
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LanguageViewModel @Inject constructor(): ViewModel() {
    var chosenLanguage: Language? = null
}