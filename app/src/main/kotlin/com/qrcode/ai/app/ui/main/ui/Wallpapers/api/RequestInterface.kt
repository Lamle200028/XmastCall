package com.qrcode.ai.app.ui.main.ui.Wallpapers.api

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface RequestInterface {
    @GET("${ApiConstants.BASE_URL}wallpaper")
    fun getAllWallpaper(@Query("category_id") categoryId: Int = 1009): Call<List<Wallpaper>>
}