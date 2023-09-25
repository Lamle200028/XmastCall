package com.qrcode.ai.app.ui.main.ui.Wallpapers.api

import com.google.gson.annotations.SerializedName

data class Wallpaper(
    @SerializedName("id") val id: Int?,
    @SerializedName("wallpapper") val wallpaper: String?,
    @SerializedName("category_id") val categoryID: Int?,
    @SerializedName("url") val url: String?
)