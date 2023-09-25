package com.qrcode.ai.app.utils

import android.content.Context
import android.content.ContextWrapper
import android.content.res.Resources
import android.os.LocaleList
import com.qrcode.ai.app.R
import com.qrcode.ai.app.ui.splash.ui.first_language.Language
import java.util.Locale

class ContextUtils(base: Context) : ContextWrapper(base) {

    companion object {

        fun getLocalesListFirt(resources: Resources): ArrayList<Language> {
            val codes = resources.getStringArray(R.array.supported_locales_first)
            val names = resources.getStringArray(R.array.supported_locales_names_first)
            val image = arrayListOf<Int>()

            image.add(R.mipmap.ic_uk)
            image.add(R.mipmap.ic_german)
            image.add(R.mipmap.ic_portu)
            image.add(R.mipmap.ic_spain)
            image.add(R.mipmap.ic_french)
            image.add(R.mipmap.ic_hindi)
            image.add(R.mipmap.ic_it)
            image.add(R.mipmap.ic_china)
            image.add(R.mipmap.ic_vn)


            val localeList = arrayListOf<Language>()

            codes.forEachIndexed { index, _ ->
                localeList.add(Language(index, names[index], codes[index], image[index]))
            }

            return localeList
        }

        fun getLocalesList(resources: Resources): ArrayList<Language> {
            val codes = resources.getStringArray(R.array.supported_locales)
            val names = resources.getStringArray(R.array.supported_locales_names)
            val image = arrayListOf<Int>()

            image.add(R.mipmap.ic_uk)
            image.add(R.mipmap.ic_german)
            image.add(R.mipmap.ic_portu)
            image.add(R.mipmap.ic_spain)
            image.add(R.mipmap.ic_french)
            image.add(R.mipmap.ic_china)
            image.add(R.mipmap.ic_hindi)
            image.add(R.mipmap.ic_russian)
            image.add(R.mipmap.ic_bengal)
            image.add(R.mipmap.ic_japan)
            image.add(R.mipmap.ic_marathi)
            image.add(R.mipmap.ic_telugu)
            image.add(R.mipmap.ic_turkish)
            image.add(R.mipmap.ic_tamil)
            image.add(R.mipmap.ic_korea)
            image.add(R.mipmap.ic_it)
            image.add(R.mipmap.ic_thailand)

            val localeList = arrayListOf<Language>()

            codes.forEachIndexed { index, _ ->
                localeList.add(Language(index, names[index], codes[index], image[index]))
            }

            return localeList
        }

        fun updateLocale(ctx: Context, localeToSwitchTo: Locale): ContextWrapper {
            var context = ctx
            val resources = context.resources
            val configuration = resources.configuration
            val localeList = LocaleList(localeToSwitchTo)
            LocaleList.setDefault(localeList)
            configuration.setLocales(localeList)
            context = context.createConfigurationContext(configuration)
            return ContextUtils(context)
        }
    }
}
