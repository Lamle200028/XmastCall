package com.qrcode.ai.app.ui.main.ui.Wallpapers

import com.qrcode.ai.app.R

class DataImage {
    fun loadAffimations(callback: (List<Affirmation>) -> Unit) {
        val imageUrlList = listOf(
            "https://camnang24h.net/wp-content/uploads/2021/06/hinh-nen-clb-bong-da-barcelona-fc-31.jpg",
            "https://w0.peakpx.com/wallpaper/585/86/HD-wallpaper-iu-lee-ji-eun-idol-korea-kpop-leejiuen-song-white.jpg",
            "https://w0.peakpx.com/wallpaper/585/86/HD-wallpaper-iu-lee-ji-eun-idol-korea-kpop-leejiuen-song-white.jpg",
            "https://w0.peakpx.com/wallpaper/585/86/HD-wallpaper-iu-lee-ji-eun-idol-korea-kpop-leejiuen-song-white.jpg",
            "https://camnang24h.net/wp-content/uploads/2021/06/hinh-nen-clb-bong-da-barcelona-fc-31.jpg",
            "https://camnang24h.net/wp-content/uploads/2021/06/hinh-nen-clb-bong-da-barcelona-fc-31.jpg",
            "https://w0.peakpx.com/wallpaper/585/86/HD-wallpaper-iu-lee-ji-eun-idol-korea-kpop-leejiuen-song-white.jpg",
            "https://w0.peakpx.com/wallpaper/585/86/HD-wallpaper-iu-lee-ji-eun-idol-korea-kpop-leejiuen-song-white.jpg",
            "https://camnang24h.net/wp-content/uploads/2021/06/hinh-nen-clb-bong-da-barcelona-fc-31.jpg",
            "https://camnang24h.net/wp-content/uploads/2021/06/hinh-nen-clb-bong-da-barcelona-fc-31.jpg",
            "https://camnang24h.net/wp-content/uploads/2021/06/hinh-nen-clb-bong-da-barcelona-fc-31.jpg",
            "https://camnang24h.net/wp-content/uploads/2021/06/hinh-nen-clb-bong-da-barcelona-fc-31.jpg",
            "https://camnang24h.net/wp-content/uploads/2021/06/hinh-nen-clb-bong-da-barcelona-fc-31.jpg",
            "https://camnang24h.net/wp-content/uploads/2021/06/hinh-nen-clb-bong-da-barcelona-fc-31.jpg",
            "https://camnang24h.net/wp-content/uploads/2021/06/hinh-nen-clb-bong-da-barcelona-fc-31.jpg",
            "https://camnang24h.net/wp-content/uploads/2021/06/hinh-nen-clb-bong-da-barcelona-fc-31.jpg",
            "https://camnang24h.net/wp-content/uploads/2021/06/hinh-nen-clb-bong-da-barcelona-fc-31.jpg",
            "https://camnang24h.net/wp-content/uploads/2021/06/hinh-nen-clb-bong-da-barcelona-fc-31.jpg"

        )

        val affirmations = imageUrlList.map { url -> Affirmation(url) }
        callback(affirmations)
    }
}
