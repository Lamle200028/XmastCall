package com.qrcode.ai.app.utils.repo

import com.qrcode.ai.app.utils.api.FakeVideoData
import com.qrcode.ai.app.utils.api.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class Repository {

    fun getDataList(): Flow<List<FakeVideoData>> = flow {
        val r = RetrofitClient.retrofit.getDataList()
        emit(r)
    }.flowOn(Dispatchers.IO)

}
