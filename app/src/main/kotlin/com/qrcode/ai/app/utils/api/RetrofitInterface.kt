package com.qrcode.ai.app.utils.api

import retrofit2.http.GET

interface RetrofitInterface {

    @GET(AllApi.DATA_LIST)
    suspend fun getDataList(): List<FakeVideoData>

}
