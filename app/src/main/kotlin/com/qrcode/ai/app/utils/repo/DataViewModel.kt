package com.qrcode.ai.app.utils.repo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.qrcode.ai.app.utils.api.ApiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class DataViewModel(private var repository: Repository) : ViewModel() {

    val myDataList: MutableStateFlow<ApiState> = MutableStateFlow(ApiState.Empty)

    fun getDataList() = viewModelScope.launch {
        myDataList.value = ApiState.Loading
        repository.getDataList()
            .catch { e ->
                myDataList.value = ApiState.Failure(e)
            }.collect { data ->
                myDataList.value = ApiState.Success(data)
            }
    }

}
