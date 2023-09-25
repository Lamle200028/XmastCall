package com.qrcode.ai.app.ui.main.ui.Wallpapers

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.qrcode.ai.app.ui.main.ui.Wallpapers.api.RequestInterface
import com.qrcode.ai.app.ui.main.ui.Wallpapers.api.RetrofitClient
import com.qrcode.ai.app.ui.main.ui.Wallpapers.api.Wallpaper
import dagger.hilt.android.lifecycle.HiltViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class WallpaperViewModel @Inject constructor() : ViewModel() {
    private var wallpaperRepo: RequestInterface? = null
    val onSuccessGetData = MutableLiveData<List<Wallpaper>>()
    val onErrorGetData = MutableLiveData<Throwable>()

    fun startFetchAllWallpaper() {
        if (wallpaperRepo == null) wallpaperRepo = RetrofitClient.getClient().create(RequestInterface::class.java)

        wallpaperRepo?.getAllWallpaper()?.enqueue(object : Callback<List<Wallpaper>> {
            override fun onResponse(call: Call<List<Wallpaper>>, response: Response<List<Wallpaper>>) {
                if (response.isSuccessful) {
                    onSuccessGetData.postValue(response.body())
                } else {
                    onErrorGetData.postValue(Throwable("get all wallpaper failed"))
                }
            }

            override fun onFailure(call: Call<List<Wallpaper>>, t: Throwable) {
                onErrorGetData.postValue(t)
            }
        })
    }
}